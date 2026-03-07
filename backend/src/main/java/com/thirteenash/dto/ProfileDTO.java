package com.thirteenash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
    private Long id;
    private Long userId;
    private Long clazzId;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String phone;
    private String university;
    private String email;
    private String wechat;
    private String qq;
    private String message;
    private String avatar;
}
