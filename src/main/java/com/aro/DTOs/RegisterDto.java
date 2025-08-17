package com.aro.DTOs;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class RegisterDto {
    private String username;
    private String email;
    private String password;
    private String oauthProvider;
}
