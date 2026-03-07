package com.thirteenash.service;

import com.thirteenash.dto.SquareStudentDTO;

import java.util.List;

public interface SquareService {
    List<SquareStudentDTO> getSquareStudents(Long userId);
}
