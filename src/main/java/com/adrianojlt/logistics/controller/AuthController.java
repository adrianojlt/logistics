package com.adrianojlt.logistics.controller;

import com.adrianojlt.logistics.dto.ApiResponse;
import com.adrianojlt.logistics.dto.LoginRequestDTO;
import com.adrianojlt.logistics.dto.LoginResponseDTO;
import com.adrianojlt.logistics.security.AppSecurityProperties;
import com.adrianojlt.logistics.service.AuthService;
import com.adrianojlt.logistics.util.WebUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AppSecurityProperties securityProperties;

    @GetMapping("/config")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> config() {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("loginEnabled", securityProperties.isEnableLogin())));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request,
            HttpServletRequest httpRequest) {

        String ip = WebUtil.resolveClientIp(httpRequest);

        try {
            LoginResponseDTO response = authService.login(request.getUsername(), request.getPassword());
            log.info("login_success username={} ip={}", request.getUsername(), ip);
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (BadCredentialsException e) {
            log.warn("login_failed username={} ip={}", request.getUsername(), ip);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid credentials"));
        }
    }
}
