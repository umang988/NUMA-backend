package com.numa.file.service;

import com.numa.file.entity.UploadedFiles;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface FileDetailService {

    //<----------------------------- Server ----------------------------->
    ResponseEntity<HashMap<String, String>> uploadImage(MultipartFile file) throws IOException;
    ResponseEntity<HashMap<String, String>> deleteImage(String filename);


    //<----------------------------- UploadedFiles ----------------------------->
    List<UploadedFiles> getUploadedFilesByTypeAndName(String name, String type);
    UploadedFiles saveUploadedFile(UploadedFiles file);
    UploadedFiles updateUploadedFile(Long id, UploadedFiles file);
    void deleteUploadedFile(Long id);
}