package com.numa.generic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpGenerator {
    private static final String DIGITS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Value("${otp.length}")
    private int otpLength;

    public String generateOtp() {
        StringBuilder otp = new StringBuilder(otpLength);
        for (int i = 0; i < otpLength; i++) {
            otp.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        }
        return otp.toString();
    }
}
