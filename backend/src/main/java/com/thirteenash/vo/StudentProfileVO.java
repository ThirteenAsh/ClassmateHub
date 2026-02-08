package com.thirteenash.vo;

import com.thirteenash.entity.BasicInfo;
import com.thirteenash.entity.ContactInfo;
import com.thirteenash.entity.EducationInfo;
import com.thirteenash.entity.PersonalInfo;
import lombok.Data;

/**
 * 同学信息响应VO
 */
@Data
public class StudentProfileVO {
    private Long id;
    private Long userId;
    private BasicInfo basicInfo;
    private ContactInfo contactInfo;
    private EducationInfo educationInfo;
    private PersonalInfo personalInfo;
}
