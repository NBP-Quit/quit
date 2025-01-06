package com.quit.review.infrastructure.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.quit.review.common.CustomApiException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3Uploader implements ImageUploader {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private static final String DIRECTORY = "images/";

	@Override
	public String upload(MultipartFile file, String filename) {
		ObjectMetadata objectMetaData = getObjectMetaData(file);
		try {
			amazonS3.putObject(bucket, DIRECTORY + filename, file.getInputStream(), objectMetaData);
		} catch (IOException e) {
			throw new CustomApiException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패하였습니다 : " + e.getMessage());
		}
		return amazonS3.getUrl(bucket, DIRECTORY + filename).toString();
	}

	private ObjectMetadata getObjectMetaData(MultipartFile file) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(file.getSize());
		objectMetadata.setContentType(file.getContentType());
		return objectMetadata;
	}
}
