package com.example.urlshortner.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.urlshortner.DTO.ClickEventDto;
import com.example.urlshortner.DTO.UrlMappingDto;
import com.example.urlshortner.Entities.ClickEvent;
import com.example.urlshortner.Entities.UrlMapping;
import com.example.urlshortner.Entities.User;
import com.example.urlshortner.Repository.ClickEventRepository;
import com.example.urlshortner.Repository.UrlMappingRepository;

@Service
public class UrlService {

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @Autowired
    private ClickEventRepository clickEventRepository; 

    public UrlMappingDto createShortUrl(String originalUrl, User user) {
        String shortUrl=generateShortUrl();
        UrlMapping urlMapping=new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setUser(user);
        urlMapping.setCreatedDate(LocalDateTime.now());
        UrlMapping savedUrlMapping = urlMappingRepository.save(urlMapping);
        return convertToDto(savedUrlMapping);
    }

    private UrlMappingDto convertToDto(UrlMapping urlMapping) {
        UrlMappingDto dto = new UrlMappingDto();
        dto.setOriginalUrl(urlMapping.getOriginalUrl());
        dto.setShortUrl(urlMapping.getShortUrl());
        dto.setCreatedDate(urlMapping.getCreatedDate());
        dto.setId(urlMapping.getUser().getId());
        dto.setUsername(urlMapping.getUser().getUsername());
        dto.setClickCount(urlMapping.getClickCount());
        return dto;
    }

    private String generateShortUrl() {
        Random random= new Random();
        String characters="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder shortUrl=new StringBuilder(8);

        for(int i=0;i<=8;i++)
        {
            shortUrl.append(characters.charAt(random.nextInt(characters.length())));
        }
        return shortUrl.toString(); 
    }

    public List<UrlMappingDto> getUrlsByUser(User user) {
        return urlMappingRepository.findByUser(user).stream().map(this::convertToDto).toList();
    }

    public List<ClickEventDto> getClickEventsByDate(String shorturl, LocalDateTime start, LocalDateTime end) {
        
        UrlMapping urlMapping=urlMappingRepository.findByShortUrl(shorturl);
        if(urlMapping!=null)
        {
            return clickEventRepository.findByUrlMappingAndClickDateBetween(urlMapping, start, end).stream()
            .collect(Collectors.groupingBy(click->click.getClickDate().toLocalDate(),Collectors.counting())).entrySet().stream().map(entry->{
                ClickEventDto clickEventDto=new ClickEventDto();
                clickEventDto.setClickDate(entry.getKey());
                clickEventDto.setCount(entry.getValue());
                return clickEventDto;
            }).collect(Collectors.toList());
        }
        return null;
    }

    public Map<LocalDate, Long> getTotalClicksByUserAndDate(User user, LocalDate start, LocalDate end) {
        List<UrlMapping> urlMappings=urlMappingRepository.findByUser(user);
        List<ClickEvent> clickEvents=clickEventRepository.findByUrlMappingInAndClickDateBetween(urlMappings, start.atStartOfDay(), end.plusDays(1).atStartOfDay());
        return clickEvents.stream().collect(Collectors.groupingBy(click->click.getClickDate().toLocalDate(),Collectors.counting()));
    }

    public UrlMapping getOriginalUrl(String shortUrl) {
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
        if(urlMapping!=null)
        {
            urlMapping.setClickCount(urlMapping.getClickCount()+1);
            urlMappingRepository.save(urlMapping);

            ClickEvent clickEvent = new ClickEvent();
            clickEvent.setClickDate(LocalDateTime.now());
            clickEvent.setUrlMapping(urlMapping);
            clickEventRepository.save(clickEvent);
        }
        return urlMapping;
    }
    
}
