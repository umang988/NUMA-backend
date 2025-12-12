package com.numa.file.service.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.numa.file.dao.UploadedFilesRepository;
import com.numa.file.entity.UploadedFiles;
import com.numa.file.service.FileDetailService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class FileDetailServiceImpl implements FileDetailService {

	@Autowired
	private UploadedFilesRepository uploadedFilesRepository;

	@Value("${azure.storage.connection-string}")
	private String azureStorageConnectionString;



	//<----------------------------- Server ----------------------------->
	@Override
	public ResponseEntity<HashMap<String, String>> uploadImage(MultipartFile file) throws IOException {
		try {
			String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

			BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
					.connectionString(azureStorageConnectionString)
					.buildClient();

			BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("images");
			containerClient.createIfNotExists();

			BlobClient blobClient = containerClient.getBlobClient(filename);
			blobClient.upload(file.getInputStream(), file.getSize(), true);

			// Return URL of the uploaded image
			String imageUrl = blobClient.getBlobUrl();

			HashMap<String, String> response = new HashMap<>();
			response.put("filename", filename);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			HashMap<String, String> error = new HashMap<>();
			error.put("error", "Failed to upload image: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
	}

	@Override
	public ResponseEntity<HashMap<String, String>> deleteImage(String filename) {
		try {
			BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
					.connectionString(azureStorageConnectionString)
					.buildClient();

			BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("images");
			BlobClient blobClient = containerClient.getBlobClient(filename);

			if (!blobClient.exists()) {
				HashMap<String, String> notFoundResponse = new HashMap<>();
				notFoundResponse.put("error", "File not found: " + filename);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
			}

			blobClient.delete();

			HashMap<String, String> successResponse = new HashMap<>();
			successResponse.put("message", "Successfully deleted: " + filename);
			return ResponseEntity.ok(successResponse);
		} catch (Exception e) {
			HashMap<String, String> errorResponse = new HashMap<>();
			errorResponse.put("error", "Failed to delete image: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}



	//<----------------------------- UploadedFiles ----------------------------->
	@Override
	public List<UploadedFiles> getUploadedFilesByTypeAndName(String name, String type) {
		return uploadedFilesRepository.findByTypeAndName(name, type);
	}

	@Override
	public UploadedFiles saveUploadedFile(UploadedFiles file) {
		return uploadedFilesRepository.save(file);
	}

	@Override
	public UploadedFiles updateUploadedFile(Long id, UploadedFiles file) {

		UploadedFiles existingFile = uploadedFilesRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("File not found"));

		if(file.getUrl() != null){deleteImage(file.getUrl());existingFile.setUrl(file.getUrl());}
		if(file.getType() != null){existingFile.setType(file.getType());}
		return uploadedFilesRepository.save(existingFile);
	}

	@Override
	public void deleteUploadedFile(Long id) {
		uploadedFilesRepository.deleteById(id);
	}
}