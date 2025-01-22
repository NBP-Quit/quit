package com.quit.store;

import com.quit.store.application.service.ReservationSlotService;
import com.quit.store.domain.entity.Category;
import com.quit.store.domain.entity.ReservationSlot;
import com.quit.store.domain.entity.Store;
import com.quit.store.domain.repository.ReservationSlotRepository;
import com.quit.store.domain.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DistributedLockComparisonTest {

    @Autowired
    private ReservationSlotService reservationSlotService;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    private ReservationSlotRepository reservationSlotRepository;

    private UUID slotId;

    @BeforeEach
    void setUp() {
        Store store = Store.of(
                "파스타 하우스",
                "신선한 재료로 만든 정통 이탈리안 파스타와 와인을 즐길 수 있는 레스토랑입니다.",
                "서울특별시 강남구 테헤란로 123",
                "010-9876-5432",
                10000,
                LocalTime.of(10, 0),
                LocalTime.of(20, 0),
                LocalTime.of(19, 30),
                Category.WESTERN,
                "user7@email.com");
        storeRepository.save(store);

        ReservationSlot slot = ReservationSlot.of(
                LocalDate.of(2025,1,25),
                LocalTime.of(10, 0),
                50,
                store);
        reservationSlotRepository.save(slot);
        slotId = slot.getId();
    }

    @Test
    public void increaseCapacityTestWithoutLock() throws InterruptedException {
        int decrement = 1;
        int threadCount = 50;

        // 멀티 스레드 실행
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    reservationSlotService.increaseCapacity(slotId, decrement);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        ReservationSlot updatedSlot = reservationSlotRepository.findById(slotId).orElse(null);
        assert updatedSlot != null;
        System.out.println("Without Lock - Final Capacity: " + updatedSlot.getCurrentCapacity());
    }

    @Test
    public void increaseCapacityTestWithLock() throws InterruptedException {
        int decrement = 1;
        int threadCount = 50;

        // 멀티 스레드 실행
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    reservationSlotService.increaseCapacity(slotId, decrement);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        ReservationSlot updatedSlot = reservationSlotRepository.findById(slotId).orElse(null);
        System.out.println("With Lock - Final Capacity: " + updatedSlot.getCurrentCapacity());
        assertEquals(threadCount, updatedSlot.getCurrentCapacity());
    }

    @Test
    public void restoreCapacityTestWithoutLock() throws InterruptedException {
        ReservationSlot slot = reservationSlotRepository.findById(slotId).orElse(null);
        assert slot != null;
        slot.increaseCapacity(50);
        reservationSlotRepository.save(slot);

        int increment = 1;
        int threadCount = 50;

        // 멀티 스레드 실행
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    reservationSlotService.restoreCapacity(slotId, increment);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        ReservationSlot updatedSlot = reservationSlotRepository.findById(slotId).orElse(null);
        assert updatedSlot != null;
        System.out.println("Without Lock - Final Capacity: " + updatedSlot.getCurrentCapacity());
    }

    @Test
    public void restoreCapacityTestWithLock() throws InterruptedException {
        ReservationSlot slot = reservationSlotRepository.findById(slotId).orElse(null);
        assert slot != null;
        slot.increaseCapacity(50);
        reservationSlotRepository.save(slot);

        int increment = 1;
        int threadCount = 50;

        // 멀티 스레드 실행
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    reservationSlotService.restoreCapacity(slotId, increment);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        ReservationSlot updatedSlot = reservationSlotRepository.findById(slotId).orElse(null);
        System.out.println("With Lock - Final Capacity: " + updatedSlot.getCurrentCapacity());
        assertEquals(0, updatedSlot.getCurrentCapacity());
    }

}
