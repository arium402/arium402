package com.team.arium.admin.noncurr;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoncurrDetailResponseDto {
    private Integer prgId;
    private String prgCd;
    private String prgNm;
    private String prgDesc;
    private String prgStDt;
    private String prgEndDt;
    private Integer maxCnt;
    private Integer mlgDefScore;
    private String surveyDt;
    private String prgStatCd;
    private String statusName;
    private Integer currentApplicants;
    private String programImageUrl;
    private String attachmentFileUrl;
    private List<CompetencyInfo> competencies; // 간단한 내부 클래스 사용
    private String regDt;
    private String updDt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetencyInfo {
        private Integer cclId;
        private String cclNm;
        private Integer cclScore;
    }
}