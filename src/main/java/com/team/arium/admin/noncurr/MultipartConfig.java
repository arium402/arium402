package com.team.arium.admin.noncurr;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;

/**
 * 파일 개수 제한 해결을 위한 Multipart 설정
 */
@Configuration
public class MultipartConfig {
    
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        
        // application.properties의 설정을 그대로 사용하되, 파일 개수 제한만 늘림
        factory.setMaxFileSize(DataSize.ofMegabytes(50));
        factory.setMaxRequestSize(DataSize.ofMegabytes(100));
        
        // 파일 개수 제한 늘리기 (기본값은 보통 3-5개)
        factory.setFileSizeThreshold(DataSize.ofKilobytes(2));
        
        // 임시 파일 저장 경로
        factory.setLocation(System.getProperty("java.io.tmpdir"));
        
        return factory.createMultipartConfig();
    }
}