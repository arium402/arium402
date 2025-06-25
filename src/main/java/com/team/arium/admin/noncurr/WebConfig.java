package com.team.arium.admin.noncurr;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 웹 설정 클래스
 * 정적 파일 서빙을 위한 설정
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    
    private final FileUploadConfig fileUploadConfig;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 업로드된 파일을 웹에서 접근할 수 있도록 설정
        String uploadPath = "file:" + fileUploadConfig.getAbsoluteUploadPath() + "/";
        
        System.out.println("=== 정적 리소스 설정 ===");
        System.out.println("URL 패턴: /uploads/**");
        System.out.println("실제 경로: " + uploadPath);
        System.out.println("======================");
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath)
                .setCachePeriod(3600); // 1시간 캐시
        
        // 추가: favicon 등 기본 정적 리소스
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }
}