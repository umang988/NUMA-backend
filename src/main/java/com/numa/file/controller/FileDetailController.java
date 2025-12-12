package com.numa.file.controller;

import com.numa.file.entity.UploadedFiles;
import com.numa.file.service.FileDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileDetailController {

    @Autowired
    FileDetailService fileDetailService;



    //<----------------------------- Server ----------------------------->
    @PostMapping(value = "/server/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<HashMap<String, String>> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return fileDetailService.uploadImage(file);
    }

    @DeleteMapping(value = "/server/delete")
    public ResponseEntity<HashMap<String, String>> deleteImage(@RequestParam("filename") String filename) {
        return fileDetailService.deleteImage(filename);
    }



    //<----------------------------- UploadedFiles ----------------------------->
    @GetMapping("/uploadedFiles")
    public ResponseEntity<List<UploadedFiles>> getUploadedFilesByJobIdAndTypeAndName(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type
    ) {
            return ResponseEntity.ok(fileDetailService.getUploadedFilesByTypeAndName(name, type));
    }

    @PostMapping("/uploadedFiles/create")
    public ResponseEntity<UploadedFiles> saveUploadedFile(@RequestBody UploadedFiles file) {
            return ResponseEntity.ok(fileDetailService.saveUploadedFile(file));
    }

    @PutMapping("/uploadedFiles/update/{id}")
    public ResponseEntity<UploadedFiles> updateUploadedFile(@PathVariable Long id, @RequestBody UploadedFiles file) {
            return ResponseEntity.ok(fileDetailService.updateUploadedFile(id,file));
    }

    @DeleteMapping("/uploadedFiles/delete/{id}")
    public ResponseEntity<Void> deleteUploadedFile(@PathVariable Long id) {
            fileDetailService.deleteUploadedFile(id);
            return ResponseEntity.noContent().build();
    }
}