package com.example.urlshortner.DTO;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UrlMappingDto {

    private Long id;
    private String originalUrl;
    private String shortUrl;
    private int clickCount;
    private LocalDateTime createdDate;
    private String username;

}
