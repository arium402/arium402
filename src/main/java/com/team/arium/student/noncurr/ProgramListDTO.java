package com.team.arium.student.noncurr;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramListDTO {
    private Integer prgId;
    private String prgCd;
    private String prgNm;
    private String prgDesc;
    private String recruitStDt;
    private String recruitEndDt;
    private String prgStDt;
    private String prgEndDt;
    private String surveyDt; //만족도 조사 마감일
    private Integer maxCnt;
    private Integer mlgDefScore;
    private String prgDept;
    private String prgTel;
    
    // 계산된 필드들
    private Integer currentApplicants;
    private String applicationStatus;
    private String programStatus;
    private Boolean canCancel; 
    private Boolean canApply;           // ✅ 신청 가능 여부 추가
    private String cancelReasonMessage; // ✅ 취소 불가 이유 메시지 추가
    private String applicationPeriodStatus;  // ✅ 신청 기간 상태 추가 (BEFORE_PERIOD, DURING_PERIOD, AFTER_PERIOD)
    
    // ✅ 신청 내역용 추가 필드들
    private String applicationDate;        // 신청일자
    private String programProgressStatus;  // 프로그램 진행 상태 (신청/진행/완료)
    private Boolean isSurveyAvailable;     // 만족도 조사 가능 여부
    private Boolean isSurveyCompleted;     // 만족도 조사 완료 여부
    private String surveyDeadline;         // 만족도 조사 마감일
    private Integer applicationId;         // 신청 ID (취소용)
    
    // ✅ 만족도 조사 관련 필드 추가
    private Boolean surveyCompleted;        // 만족도 조사 완료 여부
    private String satisfactionStatus;      // none/pending/completed
    
    @JsonProperty("dDay")        // ← JSON에서 "dDay"로 출력
    private Integer dDay;
    
    @JsonProperty("dDayText")    // ← JSON에서 "dDayText"로 출력
    private String dDayText;
    
    // 이미지 관련
    private String imageUrl;
    private String orgFileName;
    
}