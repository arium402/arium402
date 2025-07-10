package com.team.arium.student.noncurr;

import com.team.arium.admin.admin_module;
import com.team.arium.admin.noncurr.repository.*;
import com.team.arium.domain.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgramCompletionService {
    
    @Autowired
    private NcsPrgInfoRepository ncsPrgInfoRepository;
    
    @Autowired
    private NcsPrgAplyRepository ncsPrgAplyRepository;
    
    @Autowired
    private NcsCmpInfoRepository ncsCmpInfoRepository;
    
    @Autowired
    private NcsCclRelRepository ncsCclRelRepository;
    
    @Autowired
    private StdCclScoreRepository stdCclScoreRepository;
    
    @Autowired
    private CommonCodeRepository commonCodeRepository;
    
    @Autowired
    @Qualifier("admin_module")
    private admin_module adminModule;
    
    /**
     * 특정 프로그램의 이수 처리 (수동 실행)
     */
    @Transactional
    public CompletionResult processProgramCompletion(Integer prgId) {
        try {
            System.out.println("=== 프로그램 이수 처리 시작 ===");
            System.out.println("프로그램 ID: " + prgId);
            
            // 1. 프로그램 정보 조회
            Ncs_PrgInfo program = ncsPrgInfoRepository.findById(prgId)
                .orElseThrow(() -> new RuntimeException("프로그램을 찾을 수 없습니다."));
            
            // 2. 현재 날짜 확인 (Asia/Seoul 기준)
            String todayStr = adminModule.todays_module();
            LocalDate today = LocalDate.parse(todayStr);
            LocalDate programEndDate = LocalDate.parse(program.getPrgEndDt());
            
            System.out.println("오늘 날짜: " + today);
            System.out.println("프로그램 종료일: " + programEndDate);
            
            // 3. 프로그램 종료 여부 확인
            if (today.isBefore(programEndDate) || today.isEqual(programEndDate)) {
                throw new RuntimeException("프로그램이 아직 진행 중입니다. 종료일: " + programEndDate);
            }
            
            // 4. 해당 프로그램의 신청자들 조회 (신청 완료 상태만)
            List<Ncs_PrgAply> applications = ncsPrgAplyRepository.findByPrgId(prgId).stream()
                .filter(app -> app.getAplyStatCd().getCodeId().equals(61)) // 61 = 신청완료
                .collect(Collectors.toList());
            
            System.out.println("처리할 신청자 수: " + applications.size());
            
            if (applications.isEmpty()) {
                return CompletionResult.builder()
                    .success(true)
                    .processedCount(0)
                    .message("처리할 신청자가 없습니다.")
                    .build();
            }
            
            // 5. 각 신청자에 대해 이수 처리
            int processedCount = 0;
            int alreadyProcessedCount = 0;
            
            for (Ncs_PrgAply application : applications) {
                try {
                    // 이미 이수 처리된 경우 스킵
                    if (ncsCmpInfoRepository.findByNcsPrgAply_AplyId(application.getAplyId()).isPresent()) {
                        alreadyProcessedCount++;
                        System.out.println("이미 처리됨 - 학생 ID: " + application.getStdInfo().getStdId());
                        continue;
                    }
                    
                    // 이수 정보 생성
                    createCompletionInfo(application);
                    
                    // 핵심역량 점수 반영
                    addCompetencyScores(application);
                    
                    processedCount++;
                    System.out.println("이수 처리 완료 - 학생 ID: " + application.getStdInfo().getStdId());
                    
                } catch (Exception e) {
                    System.err.println("학생 ID " + application.getStdInfo().getStdId() + " 처리 중 오류: " + e.getMessage());
                    // 개별 오류는 로그만 남기고 계속 진행
                }
            }
            
            System.out.println("=== 프로그램 이수 처리 완료 ===");
            System.out.println("새로 처리된 인원: " + processedCount);
            System.out.println("이미 처리된 인원: " + alreadyProcessedCount);
            
            return CompletionResult.builder()
                .success(true)
                .processedCount(processedCount)
                .alreadyProcessedCount(alreadyProcessedCount)
                .message("이수 처리가 완료되었습니다. (새로 처리: " + processedCount + "명, 이미 처리: " + alreadyProcessedCount + "명)")
                .build();
            
        } catch (Exception e) {
            System.err.println("프로그램 이수 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            
            return CompletionResult.builder()
                .success(false)
                .processedCount(0)
                .message("이수 처리 중 오류가 발생했습니다: " + e.getMessage())
                .build();
        }
    }
    
    /**
     * 이수 정보 생성
     */
    private void createCompletionInfo(Ncs_PrgAply application) {
        Ncs_CmpInfo completion = Ncs_CmpInfo.builder()
            .ncsPrgAply(application)
            .ncsPrgInfo(application.getNcsPrgInfo())
            .stdInfo(application.getStdInfo())
            .cmpYn(yn.Y)        // 이수 완료
            .surveyYn(yn.N)     // 만족도 조사 미완료 (추후 별도 처리)
            .build();
        
        ncsCmpInfoRepository.save(completion);
        System.out.println("이수 정보 생성 완료 - 신청 ID: " + application.getAplyId());
    }
    
    /**
     * 핵심역량 점수 반영
     */
    private void addCompetencyScores(Ncs_PrgAply application) {
        Integer prgId = application.getNcsPrgInfo().getPrgId();
        Integer stdId = application.getStdInfo().getStdId();
        
        // 1. 프로그램의 핵심역량 점수들 조회
        List<Ncs_CclRel> programCompetencies = ncsCclRelRepository.findByPrgId(prgId);
        
        System.out.println("프로그램 핵심역량 수: " + programCompetencies.size());
        
        // 2. 이수 정보 조회 (방금 생성된 것)
        Ncs_CmpInfo completionInfo = ncsCmpInfoRepository.findByNcsPrgAply_AplyId(application.getAplyId())
            .orElseThrow(() -> new RuntimeException("이수 정보를 찾을 수 없습니다."));
        
        // 3. 비교과 점수 타입 코드 조회 (42 = 비교과)
        Common_Code scoreType = commonCodeRepository.findById(42)
            .orElseThrow(() -> new RuntimeException("비교과 점수 타입 코드를 찾을 수 없습니다."));
        
        // 4. 각 핵심역량에 대해 점수 추가
        for (Ncs_CclRel rel : programCompetencies) {
            try {
                // 핵심역량 정보 조회
                Core_CptInfo competency = coreCptInfoRepository.findById(rel.getCclId())
                    .orElseThrow(() -> new RuntimeException("핵심역량 정보를 찾을 수 없습니다: " + rel.getCclId()));
                
                // 학생 핵심역량 점수 생성
                Std_CclScore score = Std_CclScore.builder()
                    .stdInfo(application.getStdInfo())
                    .coreCptInfo(competency)
                    .scoreType(scoreType)
                    .ncsCmpInfo(completionInfo)
                    .score(rel.getCclScore())
                    .build();
                
                stdCclScoreRepository.save(score);
                
                System.out.println("핵심역량 점수 추가 - 역량: " + competency.getCclNm() + ", 점수: " + rel.getCclScore());
                
            } catch (Exception e) {
                System.err.println("핵심역량 점수 추가 중 오류 - 역량 ID: " + rel.getCclId() + ", 오류: " + e.getMessage());
                // 개별 오류는 로그만 남기고 계속 진행
            }
        }
    }
    
    /**
     * 종료된 프로그램 목록 조회 (아직 이수 처리되지 않은 것들)
     */
    public List<CompletableProgramDTO> getCompletablePrograms() {
        try {
            String todayStr = adminModule.todays_module();
            LocalDate today = LocalDate.parse(todayStr);
            
            // 1. 종료된 프로그램들 조회
            List<Ncs_PrgInfo> programs = ncsPrgInfoRepository.findAll().stream()
                .filter(program -> {
                    try {
                        LocalDate endDate = LocalDate.parse(program.getPrgEndDt());
                        return today.isAfter(endDate); // 종료일이 지난 프로그램들
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
            
            // 2. 각 프로그램의 이수 처리 현황 확인
            return programs.stream()
                .map(program -> {
                    int totalApplicants = ncsPrgAplyRepository.countByPrgId(program.getPrgId());
                    int completedApplicants = ncsCmpInfoRepository.findByPrgId(program.getPrgId()).size();
                    
                    return CompletableProgramDTO.builder()
                        .prgId(program.getPrgId())
                        .prgNm(program.getPrgNm())
                        .prgEndDt(program.getPrgEndDt())
                        .totalApplicants(totalApplicants)
                        .completedApplicants(completedApplicants)
                        .needsProcessing(totalApplicants > completedApplicants)
                        .build();
                })
                .filter(CompletableProgramDTO::getNeedsProcessing) // 처리가 필요한 것만
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            System.err.println("완료 가능한 프로그램 조회 중 오류: " + e.getMessage());
            return List.of();
        }
    }
    
    // CoreCptInfoRepository 추가 (빠뜨린 것)
    @Autowired
    private CoreCptInfoRepository coreCptInfoRepository;
}