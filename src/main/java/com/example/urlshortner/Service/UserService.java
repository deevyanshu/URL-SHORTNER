package com.example.urlshortner.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.urlshortner.DTO.LoginRequest;
import com.example.urlshortner.Entities.User;
import com.example.urlshortner.Repository.UserRepository;
import com.example.urlshortner.Security.JwtAuthenticationResponse;
import com.example.urlshortner.Security.JwtUtils;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserRepository repository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    public User registerUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return repository.save(user);
    }

    public JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest){
        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), 
                        loginRequest.getPassword()
                ));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails= (UserDetailsImpl) authentication.getPrincipal();

            String jwt= jwtUtils.generateToken(userDetails);
        
        return new JwtAuthenticationResponse(jwt);
    }

    public User findByUserName(String name) {
        return repository.findByUsername(name).get();
    }
    
}
