package com.quit.review.presentation.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.FileInputStream;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quit.review.application.dto.ReviewCreateDto;
import com.quit.review.application.service.ReviewServiceImpl;
import com.quit.review.presentation.request.ReviewCreateRequest;
import com.quit.review.presentation.request.RatingDetailsRequest;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ReviewServiceImpl reviewService;

	@Test
	@DisplayName("리뷰 생성 API")
	void create() throws Exception {
		// given
		UUID storeId = UUID.randomUUID();
		UUID reservationId = UUID.randomUUID();
		String userId = "1";
		UUID reviewId = UUID.randomUUID();
		ReviewCreateRequest request = ReviewCreateRequest.builder()
			.content("정말 맛있었습니다! 또 올게요!")
			.ratingDetails(new RatingDetailsRequest(5, 4, 3, 5))
			.build();

		MockMultipartFile reviewPart = new MockMultipartFile(
			"review",
			"",
			"application/json",
			new ObjectMapper().writeValueAsBytes(request)
		);

		MockMultipartFile filePart = new MockMultipartFile(
			"files",
			"image.jpg",
			"image/jpeg",
			new FileInputStream("src/test/resources/test.jpg")
		);

		given(reviewService.create(any(UUID.class), any(UUID.class), any(Long.class), any(ReviewCreateDto.class), anyList()))
			.willReturn(reviewId);

		// when & then
		mockMvc.perform(multipart("/api/reservations/{reservationId}/reviews", reservationId)
				.file(reviewPart)
				.file(filePart)
				.header("X-User-ID", userId)
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", "/api/reviews/"+ reviewId))
			.andExpect(jsonPath("$.message").value("Review Created"))
			.andExpect(jsonPath("$.code").value(201));

		then(reviewService).should().create(
			eq(storeId),
			eq(reservationId),
			eq(Long.parseLong(userId)),
			any(ReviewCreateDto.class),
			anyList()
		);
	}
}