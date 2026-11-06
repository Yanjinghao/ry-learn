package com.ry.shop.controller;

import com.ry.shop.common.Result;
import com.ry.shop.service.FileStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

import com.ry.shop.dto.file.UploadResponse;

@Tag(name = "文件")
@RestController
@RequestMapping("/api/file")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    // @PostMapping("/upload")
    // public Result<Map<String, String>> upload(@RequestPart("file") MultipartFile file) {
    //     String url = fileStorageService.storeImage(file);
    //     Map<String, String> data = new HashMap<>();
    //     data.put("url", url);
    //     return Result.ok(data);
    // }

    @PostMapping("/upload")
    public Result<UploadResponse> upload(@RequestPart("file") MultipartFile file) {
        String url = fileStorageService.storeImage(file);
        return Result.ok(new UploadResponse(url));
    }
}