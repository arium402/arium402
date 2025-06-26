package com.team.arium.admin.noncurr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class MultipartConfig {
    
    private static final Logger log = LoggerFactory.getLogger(MultipartConfig.class);

    @Bean
    public MultipartResolver multipartResolver() {
        log.info("🔧 CustomMultipartResolver Bean이 생성됩니다!");
        
        StandardServletMultipartResolver resolver = new StandardServletMultipartResolver();
        // 여기서는 StandardServletMultipartResolver가 기본적으로 서블릿 컨테이너 설정을 따름
        
        return resolver;
    }
}