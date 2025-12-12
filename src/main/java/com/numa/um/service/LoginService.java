package com.numa.um.service;

import org.springframework.http.ResponseEntity;

public interface LoginService {

    ResponseEntity<?> verifyUser(String email, String password);
    ResponseEntity<String> toggle2FA(String email, boolean enable);
    ResponseEntity<String> sendOtp(String email, String subject);
    ResponseEntity<String> validateOtp(String email, String otp);
    public ResponseEntity<?> validate2faOtp(String email, String otp);
    ResponseEntity<String> resetPassword(String email, String newPassword, String confirmPassword);

}
