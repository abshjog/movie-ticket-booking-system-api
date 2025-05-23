package com.example.mdb.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app")
@Getter
@Setter
public class AppEnv {

    private Token token;

    @Getter
    @Setter
    public static class Token{
        private String secret;
        private int accessDuration;
        private int refreshDuration;
    }
}
