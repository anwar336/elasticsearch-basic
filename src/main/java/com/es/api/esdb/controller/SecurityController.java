package com.es.api.esdb.controller;


import com.es.api.esdb.model.LoginRequest;
import com.es.api.esdb.model.LoginResponse;
import com.es.api.esdb.service.SecurityService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class SecurityController {
    private final SecurityService securityService;
    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @PostMapping("/signin")
    public @ResponseBody
    LoginResponse login(@RequestBody LoginRequest request) {
        return securityService.login(request);
    }
}
