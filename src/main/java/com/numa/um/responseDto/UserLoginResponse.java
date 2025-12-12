package com.numa.um.responseDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponse {
    //UsersEntity
    private Long userId;
    private String userName;
    private String userEmail;
    private String name;
    private String surname;
    private String country;
    private String mobileNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant dob;

    //userRoles
    private Long roleId;
    private String roleName;

    //userLoginDetail
    private int failedLoginAttempts;
    private Instant blockedUntil;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant lastLogin;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant otpExpiry;

    private Boolean is2FAEnabled;
    private String token;
}
