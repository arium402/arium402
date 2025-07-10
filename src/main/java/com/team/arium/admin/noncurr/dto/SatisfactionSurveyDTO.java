package com.team.arium.admin.noncurr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SatisfactionSurveyDTO {
    
    // 프로그램 기본 정보
    private Integer prgId;
    private String prgNm;
    private String prgDept;
    private String prgPeriod;
    private Integer totalParticipants;  // 전체 참여인원
    private Integer totalResponders;    // 응답자 수
    private String responseRate;        // 응답률 (예: "93.3%")
    
    // 섹션별 통계
    private List<SectionStatDTO> sections;
    
    // 전체 통계
    private Double overallAverage;      // 전체 평균 만족도
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionStatDTO {
        private String sectionName;        // "1. 프로그램 종합 만족도"
        private List<QuestionStatDTO> questions;
        private Double sectionAverage;     // 섹션 평균
        private Integer sectionResponders; // 섹션 응답자 수
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionStatDTO {
        private Integer surId;
        private String surContent;         // 문항 내용
        private Integer surOrd;            // 문항 순서
        private Map<Integer, ScoreStatDTO> scoreStats; // 1~5점별 통계
        private Integer totalResponses;    // 해당 문항 응답자 수
        private Double averageScore;       // 해당 문항 평균 점수
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoreStatDTO {
        private Integer score;          // 점수 (1~5)
        private Integer count;          // 해당 점수 응답자 수
        private Double percentage;      // 비율 (예: 28.6)
    }
}