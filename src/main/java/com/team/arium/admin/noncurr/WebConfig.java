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
        registry.addResourceHandler(fileUploadConfig.getUrlPrefix() + "/**")
                .addResourceLocations("file:" + fileUploadConfig.getUploadBasePath() + "/");
    }
}