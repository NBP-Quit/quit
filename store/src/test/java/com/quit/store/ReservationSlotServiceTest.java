package com.quit.store;

import com.quit.store.application.dto.BatchReservationSlotsDto;
import com.quit.store.application.dto.ReservationSlotDto;
import com.quit.store.application.dto.UpdateReservationSlotDto;
import com.quit.store.application.dto.res.ReservationSlotResponse;
import com.quit.store.application.service.ReservationSlotService;
import com.quit.store.domain.entity.Category;
import com.quit.store.domain.entity.ReservationSlot;
import com.quit.store.domain.entity.Store;
import com.quit.store.domain.repository.ReservationSlotRepository;
import com.quit.store.domain.repository.StoreRepository;
import com.quit.store.presentation.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static com.quit.store.presentation.exception.ErrorType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class ReservationSlotServiceTest {

    @Autowired
    private ReservationSlotService reservationSlotService;

    @Autowired
    private ReservationSlotRepository reservationSlotRepository;

    @Autowired
    private StoreRepository storeRepository;

    private UUID storeId;
    private final String userId = "test-user@example.com";
    private final String userRole = "ROLE_OWNER";
    private final LocalDate date = LocalDate.of(2025, 1, 15);
    private final LocalTime time = LocalTime.of(18, 0);
    private final int maxCapacity = 10;

    @BeforeEach
    void setUp() {
        Store store = Store.of(
                "Test Store",
                "A test store description",
                "123 Test Street",
                "010-1234-5678",
                10000,
                LocalTime.of(10, 0),
                LocalTime.of(22, 0),
                LocalTime.of(21, 30),
                Category.CAFE,
                userId
        );
        storeId = storeRepository.save(store).getId();

        ReservationSlot slot = ReservationSlot.of(date, time, maxCapacity, store);
        reservationSlotRepository.save(slot);
    }

    @Test
    @DisplayName("예약 슬롯을 생성할 수 있다.")
    void createSingleSlot() {
        // given
        ReservationSlotDto requestDto = ReservationSlotDto.of(date, time, maxCapacity);

        // when
        ReservationSlotResponse response = reservationSlotService.createSingleSlot(storeId, requestDto, userId, userRole);

        // then
        ReservationSlot actualSlot = findSlotById(response.getSlotId());
        assertThat(actualSlot).isNotNull();
        assertThat(actualSlot.getDate()).isEqualTo(date);
        assertThat(actualSlot.getTime()).isEqualTo(time);
        assertThat(actualSlot.getMaxCapacity()).isEqualTo(maxCapacity);
    }

    @Test
    @DisplayName("권한이 없는 사용자가 슬롯을 생성하려고 하면 예외가 발생한다.")
    void createSingleSlotUnauthorized() {
        // given
        String invalidRole = "ROLE_USER";
        ReservationSlotDto requestDto = ReservationSlotDto.of(date, time, maxCapacity);

        // when & then
        assertThatThrownBy(() -> reservationSlotService.createSingleSlot(storeId, requestDto, userId, invalidRole))
                .isInstanceOf(CustomException.class)
                .hasMessage(USER_NOT_AUTHORIZED.getMessage());
    }

    @Test
    @DisplayName("예약 슬롯 배치를 생성할 수 있다.")
    void createBatchSlots() {
        // given
        BatchReservationSlotsDto requestDto = BatchReservationSlotsDto.of(
                LocalDate.of(2025, 1, 15),
                LocalDate.of(2025, 1, 15),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0),
                30,
                10
        );

        // when
        reservationSlotService.createBatchSlots(storeId, requestDto, userId, userRole);

        // then
        List<ReservationSlot> slots = reservationSlotRepository.findAllByStoreIdAndDateRange(
                storeId,
                requestDto.getStartDate(),
                requestDto.getEndDate()
        );
        assertThat(slots).isNotNull();
        assertThat(slots).hasSize(17);
        assertThat(slots.get(0).getMaxCapacity()).isEqualTo(10);
    }

    @Test
    @DisplayName("권한이 없는 사용자가 슬롯 배치를 생성하려고 하면 예외가 발생한다.")
    void createBatchSlotsUnauthorized() {
        // given
        String invalidRole = "ROLE_USER";
        BatchReservationSlotsDto requestDto = BatchReservationSlotsDto.of(
                LocalDate.of(2025, 1, 15),
                LocalDate.of(2025, 1, 15),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0),
                30,
                10
        );

        // when & then
        assertThatThrownBy(() -> reservationSlotService.createBatchSlots(storeId, requestDto, userId, invalidRole))
                .isInstanceOf(CustomException.class)
                .hasMessage(USER_NOT_AUTHORIZED.getMessage());
    }

    @Test
    @DisplayName("중복된 예약 슬롯은 생성되지 않는다.")
    void createBatchSlotsAvoidDuplicate() {
        // given
        LocalDate startDate = LocalDate.of(2025, 1, 15);
        LocalDate endDate = LocalDate.of(2025, 1, 15);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(18, 0);
        int interval = 30;
        int maxCapacity = 10;

        ReservationSlot existingSlot = ReservationSlot.of(
                startDate,
                LocalTime.of(10, 30),
                maxCapacity,
                storeRepository.findById(storeId).orElseThrow()
        );
        reservationSlotRepository.save(existingSlot);

        BatchReservationSlotsDto requestDto = BatchReservationSlotsDto.of(
                startDate, endDate, startTime, endTime, interval, maxCapacity
        );

        // when
        reservationSlotService.createBatchSlots(storeId, requestDto, userId, userRole);

        // then
        List<ReservationSlot> slots = reservationSlotRepository.findAllByStoreIdAndDateRange(storeId, startDate, endDate);

        assertThat(slots).isNotNull();
        assertThat(slots).hasSize(17);

    }

    @Test
    @DisplayName("예약 슬롯 정보를 수정할 수 있다.")
    void updateSlot() {
        // given
        UUID slotId = reservationSlotRepository.findAll().get(0).getId();
        LocalDate newDate = LocalDate.of(2025, 1, 16);
        LocalTime newTime = LocalTime.of(19, 0);
        int newMaxCapacity = 15;
        UpdateReservationSlotDto updateDto = UpdateReservationSlotDto.builder()
                .date(newDate)
                .time(newTime)
                .maxCapacity(newMaxCapacity)
                .isAvailable(true)
                .build();

        // when
        reservationSlotService.updateSlot(storeId, slotId, updateDto, userId, userRole);

        // then
        ReservationSlot updatedSlot = findSlotById(slotId);
        assertThat(updatedSlot).isNotNull();
        assertThat(updatedSlot.getDate()).isEqualTo(newDate);
        assertThat(updatedSlot.getTime()).isEqualTo(newTime);
        assertThat(updatedSlot.getMaxCapacity()).isEqualTo(newMaxCapacity);
    }

    @Test
    @DisplayName("가게 소유자가 아닌 사용자가 슬롯을 수정하려고 하면 예외가 발생한다.")
    void updateSlotUnauthorizedUser() {
        // given
        UUID slotId = reservationSlotRepository.findAll().get(0).getId();
        String invalidUserId = "other-user@example.com";
        UpdateReservationSlotDto updateDto = UpdateReservationSlotDto.builder()
                .date(LocalDate.of(2025, 1, 16))
                .time(LocalTime.of(19, 0))
                .maxCapacity(15)
                .isAvailable(true)
                .build();

        // when & then
        assertThatThrownBy(() -> reservationSlotService.updateSlot(storeId, slotId, updateDto, invalidUserId, userRole))
                .isInstanceOf(CustomException.class)
                .hasMessage(USER_NOT_SAME.getMessage());
    }

    @Test
    @DisplayName("스토어 ID로 예약 슬롯 목록을 조회할 수 있다.")
    void getSlotsByStore() {
        // when
        PageRequest pageable = PageRequest.of(0, 10);
        Page<ReservationSlotResponse> response = reservationSlotService.getSlotsByStore(storeId, pageable);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getDate()).isEqualTo(date);
    }

    @Test
    @DisplayName("존재하지 않는 스토어 ID로 슬롯 조회 시 예외가 발생한다.")
    void getSlotsByInvalidStore() {
        // when then
        UUID invalidStoreId = UUID.randomUUID();
        assertThatThrownBy(() -> reservationSlotService.getSlotsByStore(invalidStoreId, PageRequest.of(0, 10)))
                .isInstanceOf(CustomException.class)
                .hasMessage(STORE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("예약 슬롯을 삭제할 수 있다.")
    void deleteSlot() {
        // given
        UUID slotId = reservationSlotRepository.findAll().get(0).getId();

        // when
        reservationSlotService.deleteSlot(storeId, slotId, userId, userRole);

        // then
        assertThatThrownBy(() -> findSlotById(slotId))
                .isInstanceOf(CustomException.class)
                .hasMessage(RESERVATION_SLOT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("삭제 시 예약된 슬롯은 삭제할 수 없다.")
    void deleteSlotWithReservation() {
        // given
        UUID slotId = reservationSlotRepository.findAll().get(0).getId();
        ReservationSlot slot = findSlotById(slotId);
        slot.increaseCapacity(5);

        // when & then
        assertThatThrownBy(() -> reservationSlotService.deleteSlot(storeId, slotId, userId, userRole))
                .isInstanceOf(CustomException.class)
                .hasMessage(RESERVATION_SLOT_DELETE_NOT_ALLOWED.getMessage());
    }

    private ReservationSlot findSlotById(UUID slotId) {
        return reservationSlotRepository.findByIdAndIsDeletedFalse(slotId)
                .orElseThrow(() -> new CustomException(RESERVATION_SLOT_NOT_FOUND));
    }

}

