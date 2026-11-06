package com.ry.shop.service;

import com.ry.shop.common.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp");

    @Value("${ry.upload.dir:uploads}")// 找yml里面的配置项, 冒号是缺省值
    private String uploadDir;

    @Value("${ry.upload.url-prefix:/uploads}")
    private String uploadUrlPrefix;

    public String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 提取后缀名，统一转小写
        String original = file.getOriginalFilename();
        if (original == null || !original.contains(".")) {
            throw new BusinessException("文件名不合法");
        }
        String ext = original.substring(original.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXT.contains(ext)) {
            throw new BusinessException("只允许上传图片：jpg/png/gif/webp");
        }

        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();// 相对路径转绝对路径, 也就是后台根目录
            Files.createDirectories(dir);  // 如果没有这个文件夹，就自动建一个

            // 改名（防重名覆盖）
            String filename = UUID.randomUUID().toString().replace("-", "") + "." + ext;

            // 存盘
            Path target = dir.resolve(filename);  // 拼出完整路径：D:/uploads/a1b2c3d4.jpg
            file.transferTo(target);  // 核心动作：把内存里的文件写入硬盘

            // 返回给前端存库的相对路径，例如 /uploads/xxx.jpg
            return uploadUrlPrefix + "/" + filename;
        } catch (IOException e) {
            throw new BusinessException("文件保存失败");
        }
    }
}