package com.example.urlshortner.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.urlshortner.Entities.UrlMapping;
import com.example.urlshortner.Entities.User;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long>{

    UrlMapping findByShortUrl(String shortUrl);
    
    List<UrlMapping> findByUser(User user);
    
}
