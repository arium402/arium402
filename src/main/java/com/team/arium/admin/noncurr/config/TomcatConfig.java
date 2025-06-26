package com.team.arium.admin.noncurr.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> {
            factory.addConnectorCustomizers(connector -> {
                // ✅ 파일 개수 제한을 무제한으로 설정
                connector.setProperty("maxFileCount", "-1");           // 무제한
                connector.setProperty("maxParameterCount", "-1");      // 무제한
                connector.setProperty("maxPostSize", "-1");            // 무제한
                connector.setProperty("maxSavePostSize", "-1");        // 무제한
                
                // 성능 관련 설정
                connector.setProperty("connectionTimeout", "60000");
                connector.setProperty("keepAliveTimeout", "60000");
                
                System.out.println("🔥🔥🔥 TomcatConfig 적용됨: maxFileCount=무제한");
            });
        };
    }
}