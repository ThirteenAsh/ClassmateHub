package com.thirteenash.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StudentProfileVO {
    private Long id;
    private Long clazzId;
    private BasicInfo basic;
    private ContactInfo contact;
    private String message;
    private String avatar;
    private LocalDateTime createTime;

    @Data
    public static class BasicInfo {
        private String name;
        private String gender;
        private String university;
        private LocalDate birthDate;
    }

    @Data
    public static class ContactInfo {
        private String phone;
        private String email;
        private String wechat;
        private String qq;
    }
}
