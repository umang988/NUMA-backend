package com.numa.um.controller;

import com.numa.um.requestDto.LoginRequest;
import com.numa.um.requestDto.ResetPasswordRequest;
import com.numa.um.requestDto.SendOtpRequest;
import com.numa.um.requestDto.Toggle2FARequest;
import com.numa.um.requestDto.ValidateOtpRequest;
import com.numa.um.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return loginService.verifyUser(loginRequest.getLogin(), loginRequest.getPassword());
    }

    @PostMapping("/toggle-2fa")
    public ResponseEntity<String> toggle2FA(@RequestBody Toggle2FARequest request) {
        return loginService.toggle2FA(request.getEmail(), request.isEnable());
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody SendOtpRequest request) {
        return loginService.sendOtp(request.getEmail(), request.getSubject());
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<String> validateOtp(@RequestBody ValidateOtpRequest request) {
        return loginService.validateOtp(request.getEmail(), request.getOtp());
    }

    @PostMapping("/validate2fa-otp")
    public ResponseEntity<?> validate2faOtp(@RequestBody ValidateOtpRequest request) {
        return loginService.validate2faOtp(request.getEmail(), request.getOtp());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        return loginService.resetPassword(request.getEmail(), request.getNewPassword(), request.getConfirmPassword());
    }
}