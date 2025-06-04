package com.example.urlshortner.Controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.urlshortner.DTO.ClickEventDto;
import com.example.urlshortner.DTO.UrlMappingDto;
import com.example.urlshortner.Entities.User;
import com.example.urlshortner.Service.UrlService;
import com.example.urlshortner.Service.UserService;

@RestController
@RequestMapping("api/urls")
public class UrlController {

    @Autowired
    private UrlService urlService;

    @Autowired
    private UserService userService;

    @PostMapping("/shorten")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UrlMappingDto> createShortUrl(@RequestBody Map<String,String> request, Principal principal){
        String originalUrl= request.get("originalUrl");
        User user=userService.findByUserName(principal.getName());
        UrlMappingDto urlMappingDto=urlService.createShortUrl(originalUrl,user);
        return ResponseEntity.ok(urlMappingDto);
    }

    @GetMapping("/myurls")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<UrlMappingDto>> getUserUrls(Principal principal)
    {
        User user=userService.findByUserName(principal.getName());
        List<UrlMappingDto> userUrls=urlService.getUrlsByUser(user);
        return ResponseEntity.ok(userUrls);
    }

    @GetMapping("/analytics/{shorturl}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ClickEventDto>> getUrlAnalytics(@PathVariable String shorturl, @RequestParam("startDate")String startDate, @RequestParam("endDate")String endDate)
    {
        DateTimeFormatter formatter= DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime start=LocalDateTime.parse(startDate,formatter);
        LocalDateTime end=LocalDateTime.parse(endDate,formatter);
        List<ClickEventDto> clickEventDtos=urlService.getClickEventsByDate(shorturl,start,end);
        return ResponseEntity.ok(clickEventDtos);
    }

    @GetMapping("/totalclicks")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<LocalDate,Long>> getTotalClicksByDate(Principal principal,@RequestParam("startDate")String startDate, @RequestParam("endDate")String endDate){
        DateTimeFormatter formatter= DateTimeFormatter.ISO_LOCAL_DATE;
        User user=userService.findByUserName(principal.getName());
        LocalDate start=LocalDate.parse(startDate,formatter);
        LocalDate end=LocalDate.parse(endDate,formatter);
        Map<LocalDate,Long> totalClicks=urlService.getTotalClicksByUserAndDate(user,start,end);
        return ResponseEntity.ok(totalClicks);
    }
}
