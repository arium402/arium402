package com.team.arium.admin.noncurr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantDTO {
    
    // 기본 신청 정보
    private Long id;                    // 신청 ID (aply_id)
    private Integer prgId;              // 프로그램 ID
    private Integer stdId;              // 학생 ID
    
    // 학생 기본 정보
    private String studentId;           // 학번 (std_no)
    private String name;                // 이름 (std_nm)
    private String department;          // 학과명 (dept_nm)
    private String college;             // 소속 대학 (college)
    private Integer schYr;              // 학년 (sch_yr)
    private String stdGender;           // 성별
    private String stdTellno;           // 전화번호
    private String stdEmlAddr;          // 이메일
    
    // 신청 관련 정보
    private String applyDate;           // 신청일 (aply_dt)
    private String status;              // 신청 상태 (aply_stat_cd 기반)
    private String statusCode;          // 신청 상태 코드
    
    // 이수 및 만족도 정보
    private Boolean completed;          // 이수 여부 (cmp_yn)
    private Boolean surveyCompleted;    // 만족도조사 여부 (survey_yn)
    private Integer cmpId;              // 이수 정보 ID (cmp_id)
    
    // 추가 정보
    private String regDt;               // 등록일
    private String updDt;               // 수정일
    
    // 화면 표시용
    private String appliedDateFormatted; // 포맷된 신청일
    private String statusBadgeClass;     // 상태 배지 CSS 클래스
    private Boolean canEdit;             // 수정 가능 여부
}