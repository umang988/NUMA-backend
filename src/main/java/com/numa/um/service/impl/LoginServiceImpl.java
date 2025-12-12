package com.numa.um.service.impl;

import com.numa.generic.EmailService;
import com.numa.jwt.JwtUtil;
import com.numa.generic.OtpGenerator;
import com.numa.user.dao.UserInfoRepository;
import com.numa.user.dao.UserRoleRepository;
import com.numa.user.entity.User;
import com.numa.um.dao.UserLoginRepository;
import com.numa.um.entity.UserLogin;
import com.numa.um.responseDto.User2faLoginResponse;
import com.numa.um.responseDto.UserLoginResponse;
import com.numa.um.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final JwtUtil jwtUtil;
    private final UserInfoRepository userInfoRepository;
    private final UserLoginRepository userLoginRepository;
    private final UserRoleRepository userRoleRepository;
    private final EmailService emailService;
    private final OtpGenerator otpGenerator;



    //<----------------------------- Main Methods ----------------------------->

    @Override
    @Transactional
    public ResponseEntity<?> verifyUser(String login, String password) {
        User user = getUserByLogin(login);
        String email = user.getEmail();
        UserLogin loginDetail = getOrCreateLoginDetail(email);

        if (isAccountLocked(loginDetail)) {
            long remainingMinutes = Duration.between(Instant.now(), loginDetail.getBlockedUntil()).toMinutes();
            throw new LockedException("Account is locked. Try again after " + remainingMinutes + " minutes");
        }

        if (password.equals(user.getPassword())) {
            if (!Boolean.TRUE.equals(loginDetail.getIs2FAEnabled())) {
                resetFailedLoginAttempts(email);
                return ResponseEntity.ok(getUserLoginResponse(email));
            }
            sendOtp(email, "Your 2FA OTP Code for login");
            return ResponseEntity.ok(getUser2faLoginResponse(email));
        } else {
            handleFailedLogin(email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Password");
        }
    }


    @Override
    @Transactional
    public ResponseEntity<String> toggle2FA(String email, boolean enable) {
        UserLogin loginDetail = getOrCreateLoginDetail(email);
        loginDetail.setIs2FAEnabled(enable);
        userLoginRepository.save(loginDetail);
        return ResponseEntity.ok(enable ? "2FA enabled successfully" : "2FA disabled successfully");
    }


    @Override
    @Transactional
    public ResponseEntity<String> sendOtp(String email, String subject) {
        userLoginRepository.findByEmail(email).ifPresent(loginDetail -> {
            String otp = otpGenerator.generateOtp();
            loginDetail.setOtp(otp);
            loginDetail.setOtpExpiry(Instant.now().plus(10, ChronoUnit.MINUTES));
            userLoginRepository.save(loginDetail);
            // Sending OTP asynchronously
            sendOtpEmailAsync(email, subject, "Your OTP is: " + otp + ". It is valid for 10 minutes.");
        });
        return ResponseEntity.ok("OTP sent successfully.");
    }


    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<String> validateOtp(String email, String otp) {
        if(checkOtp(email,otp)){
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired OTP.");
        }
            return ResponseEntity.ok("OTP validated");
    }


    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> validate2faOtp(String email, String otp) {
        if(checkOtp(email,otp)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired OTP.");
        }
        resetFailedLoginAttempts(email);
        return ResponseEntity.ok(getUserLoginResponse(email));
    }


    @Override
    @Transactional
    public ResponseEntity<String> resetPassword(String email, String newPassword, String confirmPassword) {

        User user = getUserByLogin(email);

        if (validatePassword(newPassword) && validatePassword(confirmPassword)) {
            throw new IllegalArgumentException("Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Password and confirm password do not match.");
        }

        if (newPassword.equals(user.getPassword())) {
            throw new IllegalArgumentException("New password cannot be the same as the previous password.");
        }

        resetFailedLoginAttempts(email);
        user.setPassword(newPassword);
        userInfoRepository.save(user);

        String subject = "Password Reset Successfully";
        String body = String.format("Hello %s,\n\nYour password has been successfully reset.\n\nIf this was not you, please contact our support team immediately.", user.getName());
        sendEmailAsync(email, subject, body);
        return ResponseEntity.ok("Password reset successfully");
    }



    //<----------------------------- Helper Methods ----------------------------->

    private User getUserByLogin(String login) {
        if (isValidEmail(login)) {
            return userInfoRepository.findByEmail(login)
                    .orElseThrow(() -> new NoSuchElementException("User not found with email: " + login));
        } else {
            return userInfoRepository.findByUsername(login)
                    .orElseThrow(() -> new NoSuchElementException("User not found with username: " + login));
        }
    }

    private UserLogin getOrCreateLoginDetail(String email) {
        return userLoginRepository.findByEmail(email).orElseGet(() -> {
            UserLogin newLoginDetail = new UserLogin();
            newLoginDetail.setEmail(email);
            newLoginDetail.setLastLogin(Instant.now());
            newLoginDetail.setFailedLoginAttempts(0);
            userLoginRepository.save(newLoginDetail);
            return newLoginDetail;
        });
    }

    @Transactional(readOnly = true)
    private UserLoginResponse getUserLoginResponse(String email) {
        return userInfoRepository.findByEmail(email)
                .flatMap(user -> userLoginRepository.findByEmail(email).map(loginDetail -> {
                    UserLoginResponse response = new UserLoginResponse();
                    userRoleRepository.findById(user.getRoleId()).ifPresent(roleDetail -> {
                        response.setUserId(user.getId());
                        response.setUserEmail(user.getEmail());
                        response.setUserName(user.getUsername());
                        response.setName(user.getName());
                        response.setSurname(user.getSurname());
                        response.setCountry(user.getCountry());
                        response.setDob(user.getDob());
                        response.setMobileNumber(user.getMobileNumber());
                        response.setRoleId(roleDetail.getId());
                        response.setRoleName(roleDetail.getName());
                        response.setFailedLoginAttempts(loginDetail.getFailedLoginAttempts());
                        response.setBlockedUntil(loginDetail.getBlockedUntil());
                        response.setLastLogin(loginDetail.getLastLogin());
                        response.setOtpExpiry(loginDetail.getOtpExpiry());
                        response.setIs2FAEnabled(loginDetail.getIs2FAEnabled());
                        response.setToken(jwtUtil.generateToken(email));
                    });
                    return response;
                })).orElseThrow(() -> new NoSuchElementException("User login details not found."));
    }

    @Transactional(readOnly = true)
    private User2faLoginResponse getUser2faLoginResponse(String email) {
        return userLoginRepository.findByEmail(email)
                .map(loginDetail -> {
                    User2faLoginResponse response = new User2faLoginResponse();
                    response.setEmail(loginDetail.getEmail());
                    response.setIs2FAEnabled(loginDetail.getIs2FAEnabled());
                    response.setMessage("OTP sent successfully.");
                    return response;
                }).orElseThrow(() -> new NoSuchElementException("User login details not found."));
    }

    @Transactional
    private void handleFailedLogin(String email) {
        UserLogin loginDetail = getOrCreateLoginDetail(email);
        loginDetail.setFailedLoginAttempts(loginDetail.getFailedLoginAttempts() + 1);
        loginDetail.setLastLogin(Instant.now());
        if (loginDetail.getFailedLoginAttempts() >= 5) {
            lockUserAccount(email);
        } else {
            userLoginRepository.save(loginDetail);
        }
    }

    @Transactional
    private void lockUserAccount(String email) {
        UserLogin loginDetail = getOrCreateLoginDetail(email);
        Instant lockUntil = Instant.now().plus(15, ChronoUnit.MINUTES);
        loginDetail.setBlockedUntil(lockUntil);
        userLoginRepository.save(loginDetail);
        sendLockoutEmailAsync(email, lockUntil);
    }

    @Transactional
    private void resetFailedLoginAttempts(String email) {
        userLoginRepository.findByEmail(email).ifPresent(loginDetail -> {
            loginDetail.setFailedLoginAttempts(0);
            loginDetail.setLastLogin(Instant.now());
            loginDetail.setBlockedUntil(null);
            loginDetail.setOtp(null);
            loginDetail.setOtpExpiry(null);
            userLoginRepository.save(loginDetail);
        });
    }



    //<----------------------------- Sub Helper Methods ----------------------------->

    private boolean isAccountLocked(UserLogin loginDetail) {
        return loginDetail.getBlockedUntil() != null && loginDetail.getBlockedUntil().isAfter(Instant.now());
    }

    private boolean checkOtp(String email, String otp){
        return !userLoginRepository.findByEmail(email)
                .map(loginDetail -> otp.equals(loginDetail.getOtp()) && Instant.now().isBefore(loginDetail.getOtpExpiry()))
                .orElse(false);
    }

    private boolean isValidEmail(String identifier) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailRegex, identifier);
    }


    private boolean validatePassword(String password) {
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return !Pattern.matches(passwordRegex, password);
    }

    @Async
    private void sendLockoutEmailAsync(String email, Instant lockUntil) {
        emailService.sendAccountLockoutEmail(email, lockUntil);
    }

    @Async
    private void sendOtpEmailAsync(String email, String subject, String text) {
        emailService.sendOtp(email, subject, text);
    }

    @Async
    private void sendEmailAsync(String email, String subject, String body) {
        emailService.sendEmail(email, subject, body);
    }
}