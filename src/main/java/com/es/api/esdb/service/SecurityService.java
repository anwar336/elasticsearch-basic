/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.es.api.esdb.service;

import com.es.api.esdb.common.Utils;
import com.es.api.esdb.config.JwtUtil;
import com.es.api.esdb.model.LoginRequest;
import com.es.api.esdb.model.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

/**
 *
 * @author anwar
 */
@Service
public class SecurityService {
    @Value("${esdb.app.jwtExpirationDay}")
    private Long expiryTime;

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtils;
    
    public SecurityService(AuthenticationManager authenticationManager, JwtUtil jwtUtils){
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }
    
    public LoginResponse login(LoginRequest request) {
        if(request == null) {
            return new LoginResponse("Login request is required");
        }
        if(!Utils.isOk(request.getUserName())) {
            return new LoginResponse("User name is required");
        }
        if(!Utils.isOk(request.getPassword())) {
            return new LoginResponse("Password is required");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        User userDetails = (User) authentication.getPrincipal();
        return new LoginResponse(userDetails.getUsername(),
                jwt,expiryTime+"d");
    }
}
