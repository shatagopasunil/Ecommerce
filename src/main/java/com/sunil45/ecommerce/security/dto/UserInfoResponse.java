package com.sunil45.ecommerce.security.dto;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserInfoResponse {
    private Long userId;
    private String jwtToken;
    private String username;
    private List<String> roles;
}
