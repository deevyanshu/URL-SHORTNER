package com.example.urlshortner.Security;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.urlshortner.Service.UserDetailsImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {
    
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    public String getJwtFromHeader(HttpServletRequest request)
    {
        String bearerToken= request.getHeader("Authorization");
        if(bearerToken!=null && bearerToken.startsWith("Bearer "))
        {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String generateToken(UserDetailsImpl userDetails)
    {
        String username= userDetails.getUsername();
        String roles=userDetails.getAuthorities().stream().map(authority-> authority.getAuthority()).collect(Collectors.joining(","));
        return Jwts.builder().setSubject(username).claim("roles", roles).issuedAt(new Date()).expiration(new Date((new Date().getTime()+jwtExpirationMs))).signWith(key()).compact();
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    String getUserNameFromJwtToken(String token)
    {
        return Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(String authToken){
        Jwts.parser().verifyWith((SecretKey) key()).build().parseClaimsJws(authToken);
        return true;
    }
}
