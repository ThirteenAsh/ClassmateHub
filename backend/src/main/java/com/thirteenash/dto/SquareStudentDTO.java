package com.thirteenash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SquareStudentDTO {
    private String username;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String phone;
    private String qq;
    private String university;
}
