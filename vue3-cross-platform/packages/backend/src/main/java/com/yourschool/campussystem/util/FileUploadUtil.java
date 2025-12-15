package com.yourschool.campussystem.util;

import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传工具类
 */
public class FileUploadUtil {

    // 允许的图片格式
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    
    // 允许的文件格式
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList("pdf", "doc", "docx", "xls", "xlsx", "txt");
    
    // 最大文件大小（10MB）
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 验证图片文件
     */
    public static void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_IMAGE_TYPES.contains(extension.toLowerCase())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
    }

    /**
     * 验证文件
     */
    public static void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_FILE_TYPES.contains(extension.toLowerCase())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
    }

    /**
     * 保存文件
     */
    public static String saveFile(MultipartFile file, String basePath, String subDir) throws IOException {
        // 创建目录
        Path uploadDir = Paths.get(basePath, subDir);
        Files.createDirectories(uploadDir);

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String fileName = UUID.randomUUID().toString() + "." + extension;

        // 保存文件
        Path filePath = uploadDir.resolve(fileName);
        file.transferTo(filePath.toFile());

        return subDir + "/" + fileName;
    }

    /**
     * 删除文件
     */
    public static void deleteFile(String basePath, String filePath) {
        try {
            Path path = Paths.get(basePath, filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // 记录日志，但不抛出异常
            System.err.println("删除文件失败: " + filePath);
        }
    }

    /**
     * 获取文件扩展名
     */
    private static String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    /**
     * 获取文件大小（MB）
     */
    public static double getFileSizeInMB(long sizeInBytes) {
        return sizeInBytes / (1024.0 * 1024.0);
    }
}
