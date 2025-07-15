package org.example.urlshortner.dto.request;

import lombok.Data;

@Data
public class UrlRequest {
    private String url;
    private Integer validity;
    private String shortcode;
}
