package com.team.arium.admin.noncurr;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 웹 설정 클래스 (개선된 버전)
 * 정적 파일 서빙을 위한 설정
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    
    private final FileUploadConfig fileUploadConfig;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            // 업로드된 파일을 웹에서 접근할 수 있도록 설정
            String uploadPath = "file:" + fileUploadConfig.getAbsoluteUploadPath() + "/";
            
            System.out.println("=== 정적 리소스 설정 ===");
            System.out.println("URL 패턴: /uploads/**");
            System.out.println("실제 경로: " + uploadPath);
            System.out.println("절대 경로: " + fileUploadConfig.getAbsoluteUploadPath());
            
            // 업로드된 파일 접근 설정
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations(uploadPath)
                    .setCachePeriod(3600) // 1시간 캐시
                    .resourceChain(true);
            
            System.out.println("✅ 업로드 파일 리소스 핸들러 등록 완료");
            
        } catch (Exception e) {
            System.err.println("❌ 업로드 파일 리소스 핸들러 등록 실패: " + e.getMessage());
            e.printStackTrace();
        }
        
        try {
            // 기본 정적 리소스 (CSS, JS, 이미지 등)
            registry.addResourceHandler("/static/**")
                    .addResourceLocations("classpath:/static/")
                    .setCachePeriod(3600)
                    .resourceChain(true);
            
            // CSS 파일
            registry.addResourceHandler("/css/**")
                    .addResourceLocations("classpath:/static/css/")
                    .setCachePeriod(3600);
            
            // JavaScript 파일
            registry.addResourceHandler("/js/**")
                    .addResourceLocations("classpath:/static/js/")
                    .setCachePeriod(3600);
            
            // 이미지 파일
            registry.addResourceHandler("/images/**")
                    .addResourceLocations("classpath:/static/images/")
                    .setCachePeriod(86400); // 24시간 캐시
            
            // 폰트 파일
            registry.addResourceHandler("/fonts/**")
                    .addResourceLocations("classpath:/static/fonts/")
                    .setCachePeriod(86400); // 24시간 캐시
            
            // favicon
            registry.addResourceHandler("/favicon.ico")
                    .addResourceLocations("classpath:/static/favicon.ico")
                    .setCachePeriod(86400);
            
            System.out.println("✅ 기본 정적 리소스 핸들러 등록 완료");
            System.out.println("======================");
            
        } catch (Exception e) {
            System.err.println("❌ 기본 정적 리소스 핸들러 등록 실패: " + e.getMessage());
        }
    }
}