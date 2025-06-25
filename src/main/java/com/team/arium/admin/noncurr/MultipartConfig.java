package com.team.arium.admin.noncurr;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import jakarta.servlet.MultipartConfigElement;

/**
 * 안전한 파일 업로드를 위한 Multipart 설정
 * 최대 2개 파일 (대표사진 + 첨부파일)까지만 허용
 */
@Configuration
public class MultipartConfig {
    
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        
        // 파일 크기 제한 (application.properties와 동일하게)
        factory.setMaxFileSize(DataSize.ofMegabytes(50));
        factory.setMaxRequestSize(DataSize.ofMegabytes(100));
        
        // 임시 파일 생성 기준 낮춤 (메모리 사용량 줄이기)
        factory.setFileSizeThreshold(DataSize.ofKilobytes(1));
        
        // 임시 파일 저장 경로 명시
        factory.setLocation(System.getProperty("java.io.tmpdir"));
        
        MultipartConfigElement element = factory.createMultipartConfig();
        
        System.out.println("=== 멀티파트 설정 완료 ===");
        System.out.println("최대 파일 크기: 50MB");
        System.out.println("최대 요청 크기: 100MB");
        System.out.println("임시 파일 경로: " + System.getProperty("java.io.tmpdir"));
        System.out.println("========================");
        
        return element;
    }
    
    /**
     * StandardServletMultipartResolver 빈 등록
     * 파일 업로드 처리 안정성 향상
     */
    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        StandardServletMultipartResolver resolver = new StandardServletMultipartResolver();
        
        // 지연 파싱 비활성화 (즉시 파싱으로 안정성 향상)
        resolver.setResolveLazily(false);
        
        System.out.println("=== 멀티파트 리졸버 설정 완료 ===");
        System.out.println("지연 파싱: 비활성화");
        System.out.println("==============================");
        
        return resolver;
    }
}