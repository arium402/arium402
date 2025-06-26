package com.team.arium.admin.noncurr.config;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class TomcatConfig {

    private static final Logger log = LoggerFactory.getLogger(TomcatConfig.class);

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> {
            factory.addConnectorCustomizers(connector -> {
                // 🔧 모든 제한을 대폭 늘리기
                connector.setProperty("maxFileCount", "50000");
                connector.setProperty("maxParameterCount", "50000");
                connector.setProperty("maxHeaderCount", "50000");
                connector.setProperty("maxPostSize", "-1"); // 무제한
                connector.setProperty("maxSavePostSize", "-1"); // 무제한
                
                log.info("🔧 Tomcat 제한 대폭 완화 완료!");
            });
        };
    }
}