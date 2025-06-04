package com.example.urlshortner.Controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.urlshortner.Entities.UrlMapping;
import com.example.urlshortner.Service.UrlService;

@RestController
public class RedirectController {
    
    @Autowired
    private UrlService urlService;

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl)
    {
        UrlMapping urlMapping = urlService.getOriginalUrl(shortUrl);
        if(urlMapping!=null)
        {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", urlMapping.getOriginalUrl());
            return ResponseEntity.status(302).headers(headers).build();
        }else
        {
            return ResponseEntity.notFound().build();
        }
    }
}
