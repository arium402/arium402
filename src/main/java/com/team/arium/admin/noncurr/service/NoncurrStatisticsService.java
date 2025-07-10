package com.team.arium.admin.noncurr.service;

import org.springframework.web.bind.annotation.*;

import com.team.arium.admin.noncurr.repository.DgstfnEvalRepository;
import com.team.arium.admin.noncurr.repository.DgstfnQstRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class NoncurrStatisticsService {
    
    @Autowired
    private DgstfnEvalRepository dgstfnEvalRepository;
    
    @Autowired
    private DgstfnQstRepository dgstfnQstRepository;
    
    /**
     * 프로그램별 만족도 조사 통계 조회 (기존 Repository 메서드 활용)
     */
    public Map<String, Object> getSatisfactionStatistics(Integer prgId) {
        log.info("프로그램 만족도 통계 조회: prgId={}", prgId);
        
        // 1. 만족도 데이터 존재 여부 확인
        if (!dgstfnEvalRepository.existsSurveyDataByPrgId(prgId)) {
            log.info("만족도 조사 데이터가 없는 프로그램: prgId={}", prgId);
            return null;
        }
        
        // 2. 전체 응답자 수 조회
        Integer totalResponders = dgstfnEvalRepository.countTotalRespondersByPrgId(prgId);
        if (totalResponders == null || totalResponders == 0) {
            log.info("응답자가 없는 프로그램: prgId={}", prgId);
            return null;
        }
        
        // 3. 응답률 계산 (임시로 100%로 설정, 실제로는 참여자 대비 계산)
        String responseRate = "100.0%";
        
        // 4. 섹션별 통계 조회
        List<Map<String, Object>> sections = getSectionStatistics(prgId);
        
        // 5. 결과 맵 구성
        Map<String, Object> result = new HashMap<>();
        result.put("totalResponders", totalResponders);
        result.put("responseRate", responseRate);
        result.put("sections", sections);
        
        log.info("만족도 통계 조회 완료: prgId={}, 응답자수={}", prgId, totalResponders);
        return result;
    }
    
    /**
     * 섹션별 통계 조회
     */
    private List<Map<String, Object>> getSectionStatistics(Integer prgId) {
        List<Map<String, Object>> sections = new ArrayList<>();
        
        // 섹션별 평균 점수 조회
        List<Object[]> sectionStats = dgstfnEvalRepository.findSectionStatisticsByPrgId(prgId);
        
        for (Object[] sectionStat : sectionStats) {
            String sectionName = (String) sectionStat[0];
            Double sectionAverage = (Double) sectionStat[1];
            Integer sectionResponders = ((Number) sectionStat[2]).intValue();
            
            // 해당 섹션의 문항들 조회
            List<Map<String, Object>> questions = getQuestionsBySection(prgId, sectionName);
            
            Map<String, Object> section = new HashMap<>();
            section.put("sectionName", sectionName);
            section.put("questions", questions);
            section.put("sectionAverage", String.format("%.1f", sectionAverage != null ? sectionAverage : 0.0));
            section.put("sectionResponders", sectionResponders);
            
            sections.add(section);
        }
        
        return sections;
    }
    
    /**
     * 섹션별 문항 통계 조회
     */
    private List<Map<String, Object>> getQuestionsBySection(Integer prgId, String sectionName) {
        List<Map<String, Object>> questions = new ArrayList<>();
        
        // 전체 문항 통계 조회
        List<Object[]> questionStats = dgstfnEvalRepository.findQuestionStatisticsByPrgId(prgId);
        List<Object[]> surveyStats = dgstfnEvalRepository.findSurveyStatisticsByPrgId(prgId);
        
        // 점수별 응답 분포를 맵으로 변환
        Map<Integer, Map<Integer, Object[]>> scoreDistributionMap = new HashMap<>();
        for (Object[] stat : surveyStats) {
            Integer surId = (Integer) stat[0];
            Integer ansScore = (Integer) stat[3];
            
            scoreDistributionMap.putIfAbsent(surId, new HashMap<>());
            scoreDistributionMap.get(surId).put(ansScore, stat);
        }
        
        // 문항별 통계 처리
        for (Object[] questionStat : questionStats) {
            Integer surId = (Integer) questionStat[0];
            String surContent = (String) questionStat[1];
            Integer surOrd = (Integer) questionStat[2];
            Integer totalResponses = ((Number) questionStat[3]).intValue();
            Double averageScore = (Double) questionStat[4];
            
            // 섹션 필터링 (문항 순서로 구분)
            if (!isQuestionInSection(surOrd, sectionName)) {
                continue;
            }
            
            // 점수별 응답 분포 생성
            Map<Integer, Map<String, Object>> scoreStats = new HashMap<>();
            
            for (int score = 1; score <= 5; score++) {
                Object[] scoreStat = scoreDistributionMap.getOrDefault(surId, new HashMap<>()).get(score);
                
                int count = 0;
                double percentage = 0.0;
                
                if (scoreStat != null) {
                    count = ((Number) scoreStat[4]).intValue();
                    percentage = (Double) scoreStat[5];
                }
                
                Map<String, Object> stat = new HashMap<>();
                stat.put("count", count);
                stat.put("percentage", String.format("%.1f", percentage));
                
                scoreStats.put(score, stat);
            }
            
            Map<String, Object> question = new HashMap<>();
            question.put("surContent", surContent);
            question.put("scoreStats", scoreStats);
            question.put("totalResponses", totalResponses);
            question.put("averageScore", Double.parseDouble(String.format("%.1f", averageScore != null ? averageScore : 0.0)));
            
            questions.add(question);
        }
        
        return questions;
    }
    
    /**
     * 문항이 특정 섹션에 속하는지 확인
     */
    private boolean isQuestionInSection(Integer surOrd, String sectionName) {
        switch (sectionName) {
            case "1. 프로그램 종합 만족도":
                return surOrd >= 1 && surOrd <= 5;
            case "2. 프로그램 내용":
                return surOrd >= 6 && surOrd <= 8;
            case "3. 프로그램 강사":
                return surOrd >= 9 && surOrd <= 11;
            default:
                return false;
        }
    }
}