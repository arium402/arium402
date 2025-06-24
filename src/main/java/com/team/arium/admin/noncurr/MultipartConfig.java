package com.team.arium.admin.noncurr;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;

/**
 * 멀티파트 파일 업로드 설정
 * properties 파일 대신 자바 코드로 설정
 */
@Configuration
@RequiredArgsConstructor
public class MultipartConfig {
    
    private final FileUploadConfig fileUploadConfig;
    
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        
        // 최대 파일 크기 설정 (첨부파일 기준으로 설정)
        factory.setMaxFileSize(DataSize.ofBytes(fileUploadConfig.getMaxAttachmentSize()));
        
        // 최대 요청 크기 설정 (파일 + 폼 데이터)
        factory.setMaxRequestSize(DataSize.ofBytes(fileUploadConfig.getMaxAttachmentSize()));
        
        // 임시 파일 저장 경로 설정
        factory.setLocation(System.getProperty("java.io.tmpdir"));
        
        return factory.createMultipartConfig();
    }
}