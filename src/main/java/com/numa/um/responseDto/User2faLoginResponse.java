package com.numa.um.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User2faLoginResponse {
    private String email;
    private Boolean is2FAEnabled;
    private String message;
}
