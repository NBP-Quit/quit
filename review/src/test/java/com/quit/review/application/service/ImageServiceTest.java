package com.quit.review.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import com.quit.review.common.CustomApiException;
import com.quit.review.domain.model.Review;
import com.quit.review.infrastructure.service.ImageUploader;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {
	
	@InjectMocks
	ImageServiceImpl imageService;

	@Mock
	private ImageUploader imageUploader;
	
	@Test
	@DisplayName("유효한 리뷰 ID와 이미지 파일이 주어지면 성공적으로 업로드 된다.")
	void create_success() throws IOException {
	    // given
		MockMultipartFile mockFile = new MockMultipartFile(
			"test",
			"test.jpg",
			"test/jpg",
			new FileInputStream("src/test/resources/test.jpg")
		);
		UUID reviewId = UUID.randomUUID();
		Review review = mock(Review.class);

		given(imageUploader.upload(any(), any())).willReturn("https://amzn-s3-quit-bucket/images/image.jpg");
		
		// when
		imageService.create(mockFile, review);
	    
	    // then
		assertThat(mockFile.getOriginalFilename()).isEqualTo("test.jpg");
		assertThat(mockFile.getContentType()).isEqualTo("test/jpg");

		then(review).should().addImage(any());
	}
	
	@Test
	@DisplayName("확장자를 찾을 수 없는 파일명이 주어지면 CustomApiException이 발생한다")
	void create_without_extension() throws IOException {
	    // given
		MockMultipartFile mockFile = new MockMultipartFile(
			"test",
			"test",
			"",
			new FileInputStream("src/test/resources/test")
		);

		Review review = mock(Review.class);

		// when & then
		assertThatThrownBy(() -> imageService.create(mockFile, review))
			.isInstanceOf(CustomApiException.class)
			.hasMessage("이미지 파일의 확장자를 찾을 수 없습니다.")
			.extracting("httpStatus")
			.isEqualTo(HttpStatus.BAD_REQUEST);
	}

	@Test
	@DisplayName("허용되지 않은 확장자일 경우 CustomApiException이 발생한다.")
	void create_with_invalid_extension() throws IOException {
	    // given
		MockMultipartFile mockFile = new MockMultipartFile(
			"test",
			"test.txt",
			"txt",
			new FileInputStream("src/test/resources/test.txt")
		);

		Review review = mock(Review.class);

		// when & then
		assertThatThrownBy(() -> imageService.create(mockFile, review))
			.isInstanceOf(CustomApiException.class)
			.hasMessage("이미지 파일만 업로드할 수 있습니다.")
			.extracting("httpStatus")
			.isEqualTo(HttpStatus.BAD_REQUEST);
	}
}