package org.example.urlshortner.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "app.auth")
@Data
public class AppAuthProperties {
    private String email;
    private String name;
    private String rollNo;
    private String accessCode;
    private String clientID;
    private String clientSecret;
}
