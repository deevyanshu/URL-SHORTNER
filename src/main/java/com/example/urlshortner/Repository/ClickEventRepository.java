package com.example.urlshortner.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.urlshortner.Entities.ClickEvent;
import com.example.urlshortner.Entities.UrlMapping;

@Repository
public interface ClickEventRepository extends JpaRepository<ClickEvent,Long>{
    List<ClickEvent> findByUrlMappingAndClickDateBetween(UrlMapping urlMapping,LocalDateTime start,LocalDateTime end);
    List<ClickEvent> findByUrlMappingInAndClickDateBetween(List<UrlMapping> urlMappings,LocalDateTime start,LocalDateTime end);
}
