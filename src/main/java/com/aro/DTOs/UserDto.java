package com.aro.DTOs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class UserDto {
    private Long userId;
    private String email;
    private String username;
}
