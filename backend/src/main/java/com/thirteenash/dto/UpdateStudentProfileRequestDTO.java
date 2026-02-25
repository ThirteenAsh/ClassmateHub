package com.thirteenash.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateStudentProfileRequestDTO {
    private BasicInfoDTO basic;

    private ContactInfoDTO contact;

    private Long clazzId;
    private String message;
    private String avatar;

    @Data
    public static class BasicInfoDTO {
        private String name;
        private String gender;
        private LocalDate birthDate;
        private String university;
    }

    @Data
    public static class ContactInfoDTO {
        private String phone;
        private String email;
        private String wechat;
        private String qq;
    }
}
