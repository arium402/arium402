package com.team.arium.admin.noncurr;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

import java.io.File;

@Configuration
@Getter
public class FileUploadConfig {
    
    // 기본 설정값들 (application.properties 수정 없이)
    private String uploadBasePath = System.getProperty("user.home") + File.separator + "arium" + File.separator + "uploads";
    private String urlPrefix = "http://localhost:8080/uploads";
    private long maxImageSize = 5 * 1024 * 1024; // 5MB
    private long maxAttachmentSize = 10 * 1024 * 1024; // 10MB
    
    // 하위 디렉토리 경로
    private String programImagePath;
    private String attachmentPath;
    
    // 허용된 파일 확장자
    private String[] allowedImageExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
    private String[] allowedAttachmentExtensions = {".pdf", ".doc", ".docx", ".hwp", ".xls", ".xlsx", ".ppt", ".pptx", ".txt", ".jpg", ".jpeg", ".png"};
    
    @PostConstruct
    public void init() {
        // 하위 디렉토리 경로 설정
        this.programImagePath = uploadBasePath + File.separator + "program-images";
        this.attachmentPath = uploadBasePath + File.separator + "attachments";
        
        // 업로드 디렉토리 생성
        createDirectoryIfNotExists(uploadBasePath);
        createDirectoryIfNotExists(programImagePath);
        createDirectoryIfNotExists(attachmentPath);
    }
    
    /**
     * 웹 접근 가능한 URL 생성
     */
    public String buildWebUrl(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }
        
        String cleanPath = relativePath.replace("\\", "/");
        if (cleanPath.startsWith("/")) {
            cleanPath = cleanPath.substring(1);
        }
        
        return urlPrefix + "/" + cleanPath;
    }
    
    /**
     * 디렉토리 생성
     */
    private void createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("디렉토리 생성됨: " + directoryPath);
            } else {
                System.err.println("디렉토리 생성 실패: " + directoryPath);
            }
        }
    }
    
    /**
     * 파일 크기를 MB 단위로 변환
     */
    public String formatFileSize(long bytes) {
        return String.format("%.1f MB", bytes / 1024.0 / 1024.0);
    }
    
    /**
     * 이미지 파일 여부 확인
     */
    public boolean isImageExtension(String extension) {
        if (extension == null) return false;
        String lowerExt = extension.toLowerCase();
        for (String allowed : allowedImageExtensions) {
            if (allowed.equals(lowerExt)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 첨부파일 확장자 여부 확인
     */
    public boolean isAttachmentExtension(String extension) {
        if (extension == null) return false;
        String lowerExt = extension.toLowerCase();
        for (String allowed : allowedAttachmentExtensions) {
            if (allowed.equals(lowerExt)) {
                return true;
            }
        }
        return false;
    }
}