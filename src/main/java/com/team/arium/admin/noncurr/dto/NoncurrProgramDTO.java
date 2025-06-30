package com.team.arium.admin.noncurr.dto;

import com.team.arium.domain.Common_File;
import com.team.arium.domain.Common_Code;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoncurrProgramDTO {

    // 기본 프로그램 정보 (DB 컬럼명 매칭)
    private Integer prgId;              // 프로그램ID
    private String prgCd;               // 프로그램 코드
    private String prgNm;               // 프로그램명
    private String prgDesc;             // 프로그램 설명
    
    // 모집기간 (신규 추가)
    private String recruitStDt;         // 모집 시작일
    private String recruitEndDt;        // 모집 마감일
    
    // 운영기간 (기존)
    private String prgStDt;             // 운영기간 시작일
    private String prgEndDt;            // 운영기간 종료일
    
    private Integer maxCnt;             // 모집인원
    private String prgDept;             // 운영부서
    private String prgTel;              // 문의 전화번호
    private Integer mlgDefScore;        // 마일리지 점수
    private String surveyDt;            // 만족도 조사 마감일
    private Common_File comFile;        // 파일 객체 (엔티티와 동일하게)
    private Common_Code prgStatCd;      // 비교과 프로그램 상태 코드 객체
    private String regDt;               // 등록일
    private String updDt;               // 수정일

    // 추가 정보 (화면 표시용 - ID 값들)
    private Integer fileId;             // 파일 ID (화면 바인딩용)
    private String prgStatNm;           // 프로그램 상태명

    // 핵심역량 관련
    private List<Integer> competencyIds;        // 선택된 핵심역량 ID 목록
    private List<CompetencyRelDTO> competencies; // 핵심역량 상세 정보

    // 파일 관련
    private MultipartFile imageFile;    // 대표 이미지 파일
    private String imageUrl;            // 이미지 URL
    private String orgFileName;         // 원본 파일명
    private String saveFileName;        // 저장 파일명

    // 현재 신청자 수 (목록에서 사용)
    private Integer currentCnt;         // 현재 신청자 수
    private String applicationStatus;   // 신청 상태 (오픈/마감/진행중 등)

    // 검색 및 필터링용
    private String searchType;          // 검색 유형
    private String searchKeyword;       // 검색 키워드
    private String periodFilter;        // 기간 필터
    private String statusFilter;        // 상태 필터

    // 페이징 정보
    private int page = 0;               // 페이지 번호
    private int size = 10;              // 페이지 크기


    // ✅ 문자열로 받을 새 필드 추가
    private String competencyIdsStr;
    // ✅ 새로 추가: 핵심역량 점수 정보
    private String competencyScoresStr;                    // "1:100,2:95,3:80" 형태
    private Map<Integer, Integer> competencyScores;        // ID -> 점수 매핑
    
    // ✅ 문자열을 List로 변환하는 메서드 추가
    public void setCompetencyIdsStr(String competencyIdsStr) {
        this.competencyIdsStr = competencyIdsStr;
        if (competencyIdsStr != null && !competencyIdsStr.trim().isEmpty()) {
            this.competencyIds = Arrays.stream(competencyIdsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        }
    }
    
    // ✅ 문자열을 Map으로 변환하는 메서드 추가
    public void setCompetencyScoresStr(String competencyScoresStr) {
        this.competencyScoresStr = competencyScoresStr;
        this.competencyScores = new HashMap<>();
        
        if (competencyScoresStr != null && !competencyScoresStr.trim().isEmpty()) {
            String[] pairs = competencyScoresStr.split(",");
            for (String pair : pairs) {
                String[] parts = pair.trim().split(":");
                if (parts.length == 2) {
                    try {
                        Integer competencyId = Integer.parseInt(parts[0].trim());
                        Integer score = Integer.parseInt(parts[1].trim());
                        this.competencyScores.put(competencyId, score);
                    } catch (NumberFormatException e) {
                        // 파싱 오류 무시하고 계속 진행
                    }
                }
            }
        }
    }
    
    // ✅ 특정 핵심역량의 점수 조회 메서드
    public Integer getCompetencyScore(Integer competencyId) {
        return competencyScores != null ? competencyScores.getOrDefault(competencyId, null) : null;
    }
    
    // ✅ Getter/Setter
    public String getCompetencyScoresStr() {
        return competencyScoresStr;
    }
    
    public Map<Integer, Integer> getCompetencyScores() {
        return competencyScores;
    }
    
    public void setCompetencyScores(Map<Integer, Integer> competencyScores) {
        this.competencyScores = competencyScores;
    }
    
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetencyRelDTO {
        private Integer prgId;          // 프로그램ID
        private Integer cclId;          // 핵심 역량ID
        private Integer cclScore;       // 핵심역량 점수
        private String cclNm;           // 핵심역량명
        private String cclDesc;         // 핵심역량 설명
    }
}