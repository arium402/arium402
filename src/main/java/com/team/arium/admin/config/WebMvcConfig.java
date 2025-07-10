package com.team.arium.admin.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploads/** 로 들어오는 요청을 파일시스템 uploadDir 로 매핑
        registry.addResourceHandler("/uploads/noncurr/images/**")
                .addResourceLocations("file:" + uploadDir + "/")
                // 개발 중이라면 캐시 완전 꺼 두기 (0초)
                .setCachePeriod(0)
                // 추가 캐시 헤더 설정으로 브라우저 캐시도 방지
                .resourceChain(false)
                .addResolver(new org.springframework.web.servlet.resource.PathResourceResolver() {
                    @Override
                    protected org.springframework.core.io.Resource getResource(String resourcePath, 
                            org.springframework.core.io.Resource location) throws java.io.IOException {
                        org.springframework.core.io.Resource resource = super.getResource(resourcePath, location);
                        if (resource != null && resource.exists()) {
                            return resource;
                        }
                        return null;
                    }
                });
    }
}