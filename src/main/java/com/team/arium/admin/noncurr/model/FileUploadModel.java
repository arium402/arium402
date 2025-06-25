package com.team.arium.admin.noncurr.model;

import com.team.arium.admin.noncurr.repository.CommonFileRepository;
import com.team.arium.domain.Common_File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileUploadModel {
    
    private final CommonFileRepository commonFileRepository;
    
    @Value("${file.upload.path:uploads}")
    private String uploadPath;
    
    @Value("${file.upload.noncurr.path:uploads/noncurr}")
    private String noncurrUploadPath;
    
    /**
     * 비교과 프로그램 파일 업로드
     * @param file 업로드할 파일
     * @param fileType 파일 타입 (image, attachment)
     * @return 저장된 파일 정보
     */
    public Common_File uploadNoncurrFile(MultipartFile file, String fileType) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        try {
            // 파일 저장 경로 생성
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fullUploadPath = noncurrUploadPath + fileType + "/" + datePath;
            
            // 디렉토리 생성
            createDirectoryIfNotExists(fullUploadPath);
            
            // 고유한 파일명 생성
            String originalFileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFileName);
            String saveFileName = generateUniqueFileName() + fileExtension;
            
            // 파일 저장
            Path filePath = Paths.get(fullUploadPath, saveFileName);
            Files.copy(file.getInputStream(), filePath);
            
            // DB에 파일 정보 저장
            Common_File commonFile = Common_File.builder()
                    .orgFileName(originalFileName)
                    .saveFileName(saveFileName)
                    .fileName(originalFileName)
                    .filePath(fullUploadPath + "/" + saveFileName)
                    .build();
            
            return commonFileRepository.save(commonFile);
            
        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage(), e);
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }
    
    /**
     * 이미지 파일 업로드
     * @param imageFile 이미지 파일
     * @return 저장된 파일 정보
     */
    public Common_File uploadImageFile(MultipartFile imageFile) {
        validateImageFile(imageFile);
        return uploadNoncurrFile(imageFile, "images");
    }
    
    /**
     * 첨부파일 업로드
     * @param attachmentFile 첨부파일
     * @return 저장된 파일 정보
     */
    public Common_File uploadAttachmentFile(MultipartFile attachmentFile) {
        validateAttachmentFile(attachmentFile);
        return uploadNoncurrFile(attachmentFile, "attachments");
    }
    
    /**
     * 파일 삭제
     * @param fileId 파일 ID
     * @return 삭제 성공 여부
     */
    public boolean deleteFile(Integer fileId) {
        try {
            Common_File commonFile = commonFileRepository.findById(fileId).orElse(null);
            if (commonFile == null) {
                return false;
            }
            
            // 실제 파일 삭제
            Path filePath = Paths.get(commonFile.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            
            // DB에서 파일 정보 삭제
            commonFileRepository.delete(commonFile);
            
            return true;
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 파일 다운로드 경로 생성
     * @param fileId 파일 ID
     * @return 다운로드 URL
     */
    public String getDownloadUrl(Integer fileId) {
        if (fileId == null) {
            return null;
        }
        return "/api/admin/noncurr/files/" + fileId + "/download";
    }
    
    /**
     * 이미지 미리보기 URL 생성
     * @param fileId 파일 ID
     * @return 미리보기 URL
     */
    public String getImagePreviewUrl(Integer fileId) {
        if (fileId == null) {
            return null;
        }
        return "/api/admin/noncurr/files/" + fileId + "/preview";
    }
    
    /**
     * 디렉토리 생성
     * @param path 생성할 경로
     */
    private void createDirectoryIfNotExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    
    /**
     * 고유한 파일명 생성
     * @return UUID 기반 파일명
     */
    private String generateUniqueFileName() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 파일 확장자 추출
     * @param fileName 파일명
     * @return 확장자 (.포함)
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
    
    /**
     * 이미지 파일 유효성 검사
     * @param file 검사할 파일
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 선택되지 않았습니다.");
        }
        
        // 파일 크기 검사 (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("이미지 파일 크기는 5MB를 초과할 수 없습니다.");
        }
        
        // 파일 형식 검사
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }
        
        // 허용된 이미지 형식 검사
        if (!contentType.equals("image/jpeg") && 
            !contentType.equals("image/jpg") && 
            !contentType.equals("image/png") && 
            !contentType.equals("image/gif")) {
            throw new IllegalArgumentException("JPG, PNG, GIF 형식의 이미지만 업로드 가능합니다.");
        }
    }
    
    /**
     * 첨부파일 유효성 검사
     * @param file 검사할 파일
     */
    private void validateAttachmentFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return; // 첨부파일은 선택사항
        }
        
        // 파일 크기 검사 (50MB)
        if (file.getSize() > 50 * 1024 * 1024) {
            throw new IllegalArgumentException("첨부파일 크기는 50MB를 초과할 수 없습니다.");
        }
        
        // 허용된 파일 형식 검사
        String fileName = file.getOriginalFilename();
        if (fileName != null) {
            String extension = getFileExtension(fileName).toLowerCase();
            if (!extension.matches("\\.(pdf|doc|docx|hwp|jpg|jpeg|png|gif)$")) {
                throw new IllegalArgumentException("PDF, DOC, DOCX, HWP, 이미지 파일만 업로드 가능합니다.");
            }
        }
    }
}