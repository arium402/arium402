package com.team.arium.student.noncurr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentBasicInfoDTO {
    private Integer stdId;
    private String stdNm;      // 학생명
    private String stdGender;  // 성별
    private String schYr;      // 학년 (예: "1학년")
    private String deptNm;     // 학과명
}