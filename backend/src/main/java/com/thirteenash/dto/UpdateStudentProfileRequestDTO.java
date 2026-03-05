package com.thirteenash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStudentProfileRequestDTO {
    private BasicInfoDTO basic;

    private ContactInfoDTO contact;

    private Long clazzId;
    private String message;
    private String avatar;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BasicInfoDTO {
        private String name;
        private String gender;
        private LocalDate birthDate;
        private String university;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ContactInfoDTO {
        private String phone;
        private String email;
        private String wechat;
        private String qq;
    }
}
