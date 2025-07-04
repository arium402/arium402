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
    private Integer maxCnt;
    private Integer mlgDefScore;
    private String prgDept;
    private String prgTel;
    
    // 계산된 필드들
    private Integer currentApplicants;
    private String applicationStatus;
    private String programStatus;
    
    @JsonProperty("dDay")        // ← JSON에서 "dDay"로 출력
    private Integer dDay;
    
    @JsonProperty("dDayText")    // ← JSON에서 "dDayText"로 출력
    private String dDayText;
    
    // 이미지 관련
    private String imageUrl;
    private String orgFileName;
}