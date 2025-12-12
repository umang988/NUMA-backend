package com.numa.um.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Toggle2FARequest {
    private String email;
    private boolean enable;
}
