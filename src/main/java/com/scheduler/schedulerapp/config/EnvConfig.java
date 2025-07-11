package com.scheduler.schedulerapp.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.InitializingBean;

@Configuration
public class EnvConfig implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .filename(".env")
                .load();

        dotenv.entries().forEach(entry -> {
            if (System.getenv(entry.getKey()) == null) {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        });
    }
}