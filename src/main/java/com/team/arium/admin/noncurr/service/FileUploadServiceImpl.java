package com.team.arium.admin.noncurr.service;

import com.team.arium.admin.noncurr.repository.CommonFileRepository;
import com.team.arium.admin.noncurr.FileUploadConfig;
import com.team.arium.admin.noncurr.exception.NoncurrException;
import com.team.arium.domain.Common_File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {
    
    private final CommonFileRepository commonFileRepository;
    private final FileUploadConfig fileUploadConfig;
    
    @Override
    public Common_File uploadProgramImage(MultipartFile file) {
        return uploadFile(file, fileUploadConfig.getProgramImagePath(), "프로그램 이미지", true);
    }
    
    @Override
    public Common_File uploadAttachmentFile(MultipartFile file) {
        return uploadFile(file, fileUploadConfig.getAttachmentPath(), "첨부파일", false);
    }
    
    /**
     * 이미지 미리보기 URL 생성
     */
    public String getImagePreviewUrl(Integer fileId) {
        if (fileId == null) {
            return null;
        }
        return "/api/admin/noncurr/files/" + fileId + "/preview";
    }
    
    /**
     * 파일 다운로드 URL 생성
     */
    public String getDownloadUrl(Integer fileId) {
        if (fileId == null) {
            return null;
        }
        return "/api/admin/noncurr/files/" + fileId + "/download";
    }
    
    /**
     * 파일 업로드 공통 로직 (수정된 부분)
     */
    private Common_File uploadFile(MultipartFile file, String basePath, String fileType, boolean isImage) {
        try {
            // 파일 유효성 검사
            validateFile(file, fileType, isImage);
            
            // 업로드 디렉토리 생성 (날짜별 하위 디렉토리)
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path uploadDir = Paths.get(basePath, dateDir);
            createDirectoryIfNotExists(uploadDir);
            
            // 고유한 파일명 생성
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = generateUniqueFilename() + fileExtension;
            
            // 파일 저장
            Path filePath = uploadDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // 웹 접근용 상대 경로 생성 (수정된 부분)
            String relativePath = basePath.substring(fileUploadConfig.getUploadBasePath().length()) + "/" + dateDir + "/" + uniqueFilename;
            relativePath = relativePath.replace("\\", "/");
            if (relativePath.startsWith("/")) {
                relativePath = relativePath.substring(1);
            }
            
            // DB에 파일 정보 저장
            Common_File fileEntity = Common_File.builder()
                .orgFileName(originalFilename)
                .saveFileName(uniqueFilename)
                .fileName(originalFilename)
                .filePath(fileUploadConfig.buildWebUrl(relativePath))
                .build();
            
            Common_File savedFile = commonFileRepository.save(fileEntity);
            
            log.info("{} 업로드 완료 - 원본명: {}, 저장명: {}, 웹경로: {}", 
                    fileType, originalFilename, uniqueFilename, savedFile.getFilePath());
            
            return savedFile;
            
        } catch (IOException e) {
            log.error("{} 업로드 실패 - 파일명: {}", fileType, file.getOriginalFilename(), e);
            throw new NoncurrException(fileType + " 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 파일 유효성 검사
     */
    private void validateFile(MultipartFile file, String fileType, boolean isImage) {
        if (file == null || file.isEmpty()) {
            throw new NoncurrException("업로드할 파일이 없습니다.");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new NoncurrException("파일명이 올바르지 않습니다.");
        }
        
        // 파일 크기 검사
        long maxSize = isImage ? fileUploadConfig.getMaxImageSize() : fileUploadConfig.getMaxAttachmentSize();
        if (file.getSize() > maxSize) {
            throw new NoncurrException(fileType + " 파일 크기는 " + (maxSize / 1024 / 1024) + "MB 이하여야 합니다.");
        }
        
        // 파일 확장자 검사
        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        String[] allowedExtensions = isImage ? 
            fileUploadConfig.getAllowedImageExtensions() : 
            fileUploadConfig.getAllowedAttachmentExtensions();
        
        if (!Arrays.asList(allowedExtensions).contains(fileExtension)) {
            String extensionList = String.join(", ", allowedExtensions);
            throw new NoncurrException("허용되지 않는 파일 형식입니다. 허용 형식: " + extensionList);
        }
        
        // 이미지 파일의 경우 추가 검증
        if (isImage && !file.getContentType().startsWith("image/")) {
            throw new NoncurrException("올바른 이미지 파일이 아닙니다.");
        }
    }
    
    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
    
    /**
     * 고유한 파일명 생성 (UUID 기반)
     */
    private String generateUniqueFilename() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 디렉토리가 없으면 생성
     */
    private void createDirectoryIfNotExists(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
            log.debug("디렉토리 생성: {}", directory);
        }
    }
    
    /**
     * 파일 삭제 (물리적 파일 삭제) - 수정된 부분
     */
    public boolean deleteFile(Common_File file) {
        try {
            if (file != null && file.getFilePath() != null) {
                // 웹 URL을 실제 파일 경로로 변환
                String webPath = file.getFilePath();
                if (webPath.startsWith(fileUploadConfig.getUrlPrefix())) {
                    String relativePath = webPath.substring(fileUploadConfig.getUrlPrefix().length());
                    Path filePath = Paths.get(fileUploadConfig.getUploadBasePath(), relativePath);
                    
                    if (Files.exists(filePath)) {
                        Files.delete(filePath);
                        log.info("파일 삭제 완료: {}", filePath);
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", file.getFilePath(), e);
            return false;
        }
    }
    
    /**
     * 파일 존재 여부 확인
     */
    public boolean fileExists(Common_File file) {
        try {
            if (file != null && file.getFilePath() != null) {
                String webPath = file.getFilePath();
                if (webPath.startsWith(fileUploadConfig.getUrlPrefix())) {
                    String relativePath = webPath.substring(fileUploadConfig.getUrlPrefix().length());
                    Path filePath = Paths.get(fileUploadConfig.getUploadBasePath(), relativePath);
                    return Files.exists(filePath);
                }
            }
            return false;
        } catch (Exception e) {
            log.warn("파일 존재 여부 확인 실패: {}", file.getFilePath(), e);
            return false;
        }
    }
}