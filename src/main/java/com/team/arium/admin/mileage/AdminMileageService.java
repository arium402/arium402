// 완전히 구현된 마일리지 Service (모든 로직 포함)

package com.team.arium.admin.mileage;

import com.team.arium.admin.admin_module;

//✅ 마일리지 DTO import
import com.team.arium.admin.mileage.MileagePaymentRequestDTO;
import com.team.arium.admin.mileage.MileagePaymentResultDTO;
import com.team.arium.admin.mileage.MileageStatisticsDTO;

//✅ 마일리지 Repository import (StdMileageHistRepository만 사용)
import com.team.arium.admin.mileage.StdMileageHistRepository;

//✅ 기존 StdInfoRepository 사용 (경로 변경)
import com.team.arium.student.noncurr.StdInfoRepository;

//✅ 기존 비교과 Repository import
import com.team.arium.admin.noncurr.repository.*;

//✅ 기존 비교과 DTO import (재사용)
import com.team.arium.admin.noncurr.dto.NoncurrProgramDTO;
import com.team.arium.admin.noncurr.dto.ApplicantDTO;

//✅ Domain Entity import
import com.team.arium.domain.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMileageService {

    // Repository들
    private final NcsPrgInfoRepository ncsPrgInfoRepository;
    private final NcsCmpInfoRepository ncsCmpInfoRepository;
    private final CommonCodeRepository commonCodeRepository;
    private final StdMileageHistRepository stdMileageHistRepository;
    private final StdInfoRepository stdInfoRepository;
    private final NcsPrgAplyRepository ncsPrgAplyRepository;
    private final DgstfnEvalRepository dgstfnEvalRepository;
    private final admin_module adminModule;

    /**
     * 마일리지 지급 대상 프로그램 목록 조회 (페이징 + 필터링)
     */
    public Page<NoncurrProgramDTO> getMileageProgramList(String searchKeyword, String status, 
                                                        Pageable pageable) {
        log.info("마일리지 프로그램 목록 조회 - 검색어: {}, 상태: {}", searchKeyword, status);
        
        try {
            String currentDate = adminModule.todays_module();
            
            // 기존 Repository 활용하여 프로그램 조회
            List<Ncs_PrgInfo> allPrograms;
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                allPrograms = ncsPrgInfoRepository.findByPrgNmContainingIgnoreCase(searchKeyword.trim());
            } else {
                allPrograms = ncsPrgInfoRepository.findAll();
            }
            
            // 만족도 조사 완료된 프로그램만 필터링 (마일리지 지급 대상)
            List<Ncs_PrgInfo> completedPrograms = allPrograms.stream()
                .filter(program -> isSurveyCompleted(program, currentDate))
                .collect(Collectors.toList());
            
            // DTO 변환 및 마일리지 정보 추가
            List<NoncurrProgramDTO> allDtos = completedPrograms.stream()
                .map(this::convertToMileageNoncurrDto)
                .collect(Collectors.toList());
            
            // 상태 필터링 적용
            List<NoncurrProgramDTO> filteredDtos = allDtos.stream()
                .filter(program -> applyMileageStatusFilter(program, status))
                .sorted((p1, p2) -> {
                    if (p1.getRegDt() == null && p2.getRegDt() == null) return 0;
                    if (p1.getRegDt() == null) return 1;
                    if (p2.getRegDt() == null) return -1;
                    return p2.getRegDt().compareTo(p1.getRegDt());
                })
                .collect(Collectors.toList());
            
            // 수동 페이징 처리
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), filteredDtos.size());
            
            List<NoncurrProgramDTO> pagedContent = start <= filteredDtos.size() ? 
                filteredDtos.subList(start, end) : 
                Collections.emptyList();
            
            log.info("마일리지 프로그램 목록 조회 완료: 전체 {}, 필터링 후 {}", 
                    allPrograms.size(), filteredDtos.size());
            
            return new PageImpl<>(pagedContent, pageable, filteredDtos.size());
            
        } catch (Exception e) {
            log.error("마일리지 프로그램 목록 조회 실패: {}", e.getMessage(), e);
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
    }

    /**
     * 프로그램별 마일리지 지급 대상자 목록 조회
     */
    public List<ApplicantDTO> getMileageParticipants(Integer prgId) {
        log.info("마일리지 지급 대상자 조회: 프로그램ID={}", prgId);
        
        try {
            // 기존 Repository 메서드 활용 (신청자 상세 정보 조회)
            List<Object[]> rawData = ncsPrgAplyRepository.findApplicantDetailsByPrgId(prgId);
            
            List<ApplicantDTO> participants = rawData.stream()
                .map(this::convertToMileageApplicantDto)
                .filter(applicant -> applicant.getCompleted() && applicant.getSurveyCompleted()) // 이수+만족도 완료만
                .collect(Collectors.toList());
            
            log.info("마일리지 지급 대상자 조회 완료: 프로그램ID={}, 대상자수={}", prgId, participants.size());
            
            return participants;
                
        } catch (Exception e) {
            log.error("마일리지 지급 대상자 조회 실패: 프로그램ID={}, 오류={}", prgId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 마일리지 지급 처리
     */
    @Transactional
    public MileagePaymentResultDTO processMileagePayment(MileagePaymentRequestDTO request) {
        log.info("마일리지 지급 처리 시작: 프로그램ID={}, 대상자수={}", request.getPrgId(), 
                request.getParticipantIds().size());
        
        int successCount = 0;
        int failCount = 0;
        List<String> failedStudents = new ArrayList<>();
        
        try {
            String paymentDate = request.getPaymentDate() != null ? 
                request.getPaymentDate() : adminModule.todays_module();
            
            // 프로그램 정보 검증
            Ncs_PrgInfo program = ncsPrgInfoRepository.findById(request.getPrgId())
                .orElseThrow(() -> new RuntimeException("프로그램을 찾을 수 없습니다: " + request.getPrgId()));
            
            log.info("마일리지 지급 프로그램: {}, 기본점수: {}", program.getPrgNm(), program.getMlgDefScore());
            
            for (Integer cmpId : request.getParticipantIds()) {
                try {
                    // 이미 지급된 마일리지인지 확인
                    if (stdMileageHistRepository.existsMileageByCmpId(cmpId)) {
                        log.warn("이미 마일리지가 지급된 이수 ID: {}", cmpId);
                        continue; // 이미 지급된 경우 스킵
                    }
                    
                    // 개별 마일리지 지급 처리
                    String studentName = processSingleMileagePayment(cmpId, paymentDate);
                    successCount++;
                    
                    log.debug("마일리지 지급 성공: 이수ID={}, 학생={}", cmpId, studentName);
                    
                } catch (Exception e) {
                    log.error("개별 마일리지 지급 실패: cmpId={}, 오류={}", cmpId, e.getMessage());
                    failCount++;
                    failedStudents.add("이수ID: " + cmpId + " (" + e.getMessage() + ")");
                }
            }
            
            String message = String.format("마일리지 지급 완료: 성공 %d건, 실패 %d건", successCount, failCount);
            
            log.info("마일리지 지급 처리 완료: 프로그램ID={}, {}", request.getPrgId(), message);
            
            return MileagePaymentResultDTO.builder()
                .success(true)
                .message(message)
                .totalCount(request.getParticipantIds().size())
                .successCount(successCount)
                .failCount(failCount)
                .failedStudents(failedStudents)
                .paymentDate(paymentDate)
                .build();
                
        } catch (Exception e) {
            log.error("마일리지 지급 처리 실패: 프로그램ID={}, 오류={}", request.getPrgId(), e.getMessage(), e);
            
            return MileagePaymentResultDTO.builder()
                .success(false)
                .message("마일리지 지급 중 오류가 발생했습니다: " + e.getMessage())
                .totalCount(request.getParticipantIds().size())
                .successCount(successCount)
                .failCount(failCount)
                .failedStudents(failedStudents)
                .paymentDate(request.getPaymentDate())
                .build();
        }
    }

    /**
     * 개별 마일리지 지급 처리
     */
    private String processSingleMileagePayment(Integer cmpId, String paymentDate) {
        log.debug("개별 마일리지 지급 시작: 이수ID={}, 지급일={}", cmpId, paymentDate);
        
        // 1. 이수 정보 조회 및 검증
        Ncs_CmpInfo completion = ncsCmpInfoRepository.findById(cmpId)
            .orElseThrow(() -> new RuntimeException("이수 정보를 찾을 수 없습니다: " + cmpId));
        
        // 2. 이수 완료 및 만족도 조사 완료 확인
        if (!Yn.Y.equals(completion.getCmpYn())) {
            throw new RuntimeException("이수가 완료되지 않은 학생입니다");
        }
        
        if (!Yn.Y.equals(completion.getSurveyYn())) {
            throw new RuntimeException("만족도 조사가 완료되지 않은 학생입니다");
        }
        
        // 3. 프로그램 및 학생 정보 조회
        Ncs_PrgInfo program = completion.getNcsPrgInfo();
        Std_Info student = completion.getStdInfo();
        
        if (program == null || student == null) {
            throw new RuntimeException("프로그램 또는 학생 정보가 없습니다");
        }
        
        // 4. 마일리지 점수 조회
        Integer mileageScore = program.getMlgDefScore();
        if (mileageScore == null || mileageScore <= 0) {
            throw new RuntimeException("유효하지 않은 마일리지 점수입니다: " + mileageScore);
        }
        
        // 5. 현재 시각 생성
        String currentDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        // 6. 마일리지 가산 코드 조회
        Common_Code addCode = getOrCreateAdditionCode();
        
        // ✅ 7. 기존 만족도 조사 ID 조회 (수정된 부분)
        String surEvalId = findExistingSurveyEvalId(student.getStdId(), program.getPrgId());
        if (surEvalId == null) {
            throw new RuntimeException("만족도 조사 ID를 찾을 수 없습니다. 학생ID: " + student.getStdId() + ", 프로그램ID: " + program.getPrgId());
        }
        
        log.info("기존 만족도 조사 ID 사용: {}", surEvalId);
        
        // 8. 마일리지 지급 내역 생성 및 저장
        Std_MileageHist mileageHist = Std_MileageHist.builder()
            .stdInfo(student)
            .ncsCmpInfo(completion)
            .surEvalId(surEvalId)  // ✅ 기존 ID 사용
            .mlgScore(mileageScore)
            .mlgAddCd(addCode)
            .mlgDt(paymentDate)
            .regDt(currentDateTime)
            .build();
        
        stdMileageHistRepository.save(mileageHist);
        
        log.info("마일리지 지급 완료: 학생={}({}), 점수={}, 지급일={}", 
                student.getStdNm(), student.getStdNo(), mileageScore, paymentDate);
        
        return student.getStdNm();
    }

    // ✅ 3. 새로운 메서드 추가
    /**
     * 기존 만족도 조사 ID 조회
     */
    private String findExistingSurveyEvalId(Integer stdId, Integer prgId) {
        try {
            log.info("기존 만족도 조사 ID 조회: 학생ID={}, 프로그램ID={}", stdId, prgId);
            
            // dgstfn_eval 테이블에서 기존 만족도 조사 ID 조회
            String existingId = dgstfnEvalRepository.findSurEvalIdByStdIdAndPrgId(stdId, prgId);
            
            if (existingId != null && !existingId.trim().isEmpty()) {
                log.info("기존 만족도 조사 ID 발견: {}", existingId);
                return existingId;
            }
            
            log.warn("기존 만족도 조사 ID가 없습니다: 학생ID={}, 프로그램ID={}", stdId, prgId);
            return null;
            
        } catch (Exception e) {
            log.error("기존 만족도 조사 ID 조회 실패: stdId={}, prgId={}, 오류={}", stdId, prgId, e.getMessage());
            return null;
        }
    }
    
    
    /**
     * 기존 NoncurrProgramDTO로 변환 + 마일리지 정보 추가
     */
    private NoncurrProgramDTO convertToMileageNoncurrDto(Ncs_PrgInfo program) {
        try {
            // 이수 완료자 수 계산
            List<Ncs_CmpInfo> allCompletions = ncsCmpInfoRepository.findByPrgId(program.getPrgId());
            Integer completedCount = (int) allCompletions.stream()
                .filter(completion -> Yn.Y.equals(completion.getCmpYn()) && 
                                    Yn.Y.equals(completion.getSurveyYn()))
                .count();
            
            // 마일리지 지급 여부 확인
            Boolean mileagePaid = stdMileageHistRepository.existsMileageByPrgId(program.getPrgId());
            
            // 마일리지 상태 결정
            String mileageStatus = determineMileageStatus(program, completedCount, mileagePaid);
            
            // 기간 포맷팅
            String formattedPeriod = formatProgramPeriod(program.getPrgStDt(), program.getPrgEndDt());
            
            return NoncurrProgramDTO.builder()
                .prgId(program.getPrgId())
                .prgCd(program.getPrgCd())
                .prgNm(program.getPrgNm())
                .prgDesc(program.getPrgDesc())
                .prgStDt(program.getPrgStDt())
                .prgEndDt(program.getPrgEndDt())
                .surveyDt(program.getSurveyDt())
                .mlgDefScore(program.getMlgDefScore())
                .prgDept(program.getPrgDept())
                .prgTel(program.getPrgTel())
                .maxCnt(program.getMaxCnt())
                .currentCnt(completedCount) // 이수 완료자 수
                .regDt(program.getRegDt())
                .updDt(program.getUpdDt())
                .prgStatNm(mileageStatus) // 마일리지 상태
                .applicationStatus(mileageStatus) // 마일리지 상태
                .comFile(null)
                .prgStatCd(null)
                .imageUrl(null)
                .build();
                
        } catch (Exception e) {
            log.error("NoncurrProgramDTO 변환 실패: 프로그램ID={}, 오류={}", program.getPrgId(), e.getMessage(), e);
            
            // 기본값으로 설정
            return NoncurrProgramDTO.builder()
                .prgId(program.getPrgId())
                .prgNm(program.getPrgNm())
                .prgDesc("변환 오류")
                .currentCnt(0)
                .prgStatNm("오류")
                .build();
        }
    }

    /**
     * 기존 신청자 데이터를 ApplicantDTO로 변환 + 마일리지 정보 추가
     */
    private ApplicantDTO convertToMileageApplicantDto(Object[] row) {
        try {
            Integer cmpId = row[16] != null ? ((Number) row[16]).intValue() : null;
            String cmpYn = row[17] != null ? row[17].toString() : "N";
            String surveyYn = row[18] != null ? row[18].toString() : "N";
            
            // 마일리지 지급 여부 확인
            Boolean mileagePaid = cmpId != null ? 
                stdMileageHistRepository.existsMileageByCmpId(cmpId) : false;
            
            // 학생 계좌 정보 조회 (마일리지 지급을 위해 필요)
            Integer stdId = row[2] != null ? ((Number) row[2]).intValue() : null;
            String bankInfo = getBankInfoSummary(stdId);
            
            return ApplicantDTO.builder()
                .id(row[0] != null ? ((Number) row[0]).longValue() : null)
                .prgId(row[1] != null ? ((Number) row[1]).intValue() : null)
                .stdId(stdId)
                .studentId(row[6] != null ? row[6].toString() : "")
                .name(row[7] != null ? row[7].toString() : "")
                .schYr(row[8] != null ? ((Number) row[8]).intValue() : null)
                .stdGender(row[9] != null ? row[9].toString() : "")
                .stdTellno(row[10] != null ? row[10].toString() : "")
                .stdEmlAddr(row[11] != null ? row[11].toString() : "")
                .department(row[12] != null ? row[12].toString() : "")
                .college(row[13] != null ? row[13].toString() : "")
                .status(row[14] != null ? row[14].toString() : "")
                .statusCode(row[15] != null ? row[15].toString() : "")
                .cmpId(cmpId)
                .completed("Y".equals(cmpYn))
                .surveyCompleted("Y".equals(surveyYn))
                .applyDate(formatDate(row[3]))
                .regDt(formatDate(row[4]))
                .updDt(formatDate(row[5]))
                .appliedDateFormatted(formatDate(row[3]))
                .statusBadgeClass(mileagePaid ? "success" : "warning") // 마일리지 지급 상태
                .canEdit(!mileagePaid) // 지급 안된 경우만 처리 가능
                .build();
                
        } catch (Exception e) {
            log.error("ApplicantDTO 변환 실패: {}", e.getMessage(), e);
            return ApplicantDTO.builder()
                .name("변환 오류")
                .studentId("ERROR")
                .department("변환 실패")
                .completed(false)
                .surveyCompleted(false)
                .statusBadgeClass("danger")
                .canEdit(false)
                .build();
        }
    }

    /**
     * 마일리지 통계 조회 (완전 구현)
     */
    public MileageStatisticsDTO getMileageStatistics() {
        log.info("마일리지 통계 조회 시작");
        
        try {
            String currentDate = adminModule.todays_module();
            
            // 1. 전체 프로그램 조회
            List<Ncs_PrgInfo> allPrograms = ncsPrgInfoRepository.findAll();
            
            // 2. 만족도 조사 완료된 프로그램 필터링
            List<Ncs_PrgInfo> eligiblePrograms = allPrograms.stream()
                .filter(program -> isSurveyCompleted(program, currentDate))
                .collect(Collectors.toList());
            
            // 3. 기본 통계 계산
            int totalPrograms = eligiblePrograms.size();
            int completedPrograms = 0;
            int pendingPrograms = 0;
            int totalParticipants = 0;
            int totalMileageRecipients = 0;
            int totalMileagePaid = 0;
            
            for (Ncs_PrgInfo program : eligiblePrograms) {
                // 이수 완료자 수 계산
                List<Ncs_CmpInfo> completions = ncsCmpInfoRepository.findByPrgId(program.getPrgId());
                int completedCount = (int) completions.stream()
                    .filter(c -> Yn.Y.equals(c.getCmpYn()) && Yn.Y.equals(c.getSurveyYn()))
                    .count();
                
                if (completedCount > 0) {
                    totalParticipants += completedCount;
                    
                    // 마일리지 지급 여부 확인
                    Boolean mileagePaid = stdMileageHistRepository.existsMileageByPrgId(program.getPrgId());
                    
                    if (mileagePaid) {
                        completedPrograms++;
                        
                        // 실제 마일리지 지급 내역 조회
                        List<Std_MileageHist> mileageHistory = stdMileageHistRepository.findMileageHistoryByPrgId(program.getPrgId());
                        totalMileageRecipients += mileageHistory.size();
                        totalMileagePaid += mileageHistory.stream()
                            .mapToInt(hist -> hist.getMlgScore() != null ? hist.getMlgScore() : 0)
                            .sum();
                    } else {
                        pendingPrograms++;
                    }
                }
            }
            
            // 4. 월별 통계 계산 (최근 6개월)
            List<MileageStatisticsDTO.MonthlyStatDTO> monthlyStats = calculateMonthlyStats();
            
            MileageStatisticsDTO result = MileageStatisticsDTO.builder()
                .totalPrograms(totalPrograms)
                .completedPrograms(completedPrograms)
                .pendingPrograms(pendingPrograms)
                .totalMileagePaid(totalMileagePaid)
                .totalParticipants(totalParticipants)
                .totalMileageRecipients(totalMileageRecipients)
                .monthlyStats(monthlyStats)
                .build();
            
            log.info("마일리지 통계 조회 완료: 전체프로그램={}, 완료={}, 대기={}, 총마일리지={}", 
                    totalPrograms, completedPrograms, pendingPrograms, totalMileagePaid);
            
            return result;
                
        } catch (Exception e) {
            log.error("마일리지 통계 조회 실패: {}", e.getMessage(), e);
            return MileageStatisticsDTO.builder()
                .totalPrograms(0)
                .completedPrograms(0)
                .pendingPrograms(0)
                .totalMileagePaid(0)
                .totalParticipants(0)
                .totalMileageRecipients(0)
                .monthlyStats(Collections.emptyList())
                .build();
        }
    }

    /**
     * 프로그램 기본 정보 조회 (상세 페이지용)
     */
    public Map<String, Object> getProgramBasicInfo(Integer prgId) {
        log.info("프로그램 기본 정보 조회: 프로그램ID={}", prgId);
        
        try {
            Optional<Ncs_PrgInfo> programOpt = ncsPrgInfoRepository.findById(prgId);
            if (programOpt.isEmpty()) {
                return null;
            }
            
            Ncs_PrgInfo program = programOpt.get();
            
            Map<String, Object> programInfo = new HashMap<>();
            programInfo.put("prgId", program.getPrgId());
            programInfo.put("prgNm", program.getPrgNm());
            programInfo.put("prgDesc", program.getPrgDesc());
            programInfo.put("prgStDt", program.getPrgStDt());
            programInfo.put("prgEndDt", program.getPrgEndDt());
            programInfo.put("mlgDefScore", program.getMlgDefScore());
            programInfo.put("maxCnt", program.getMaxCnt());
            programInfo.put("prgDept", program.getPrgDept());
            programInfo.put("prgTel", program.getPrgTel());
            
            return programInfo;
            
        } catch (Exception e) {
            log.error("프로그램 기본 정보 조회 실패: 프로그램ID={}, 오류={}", prgId, e.getMessage(), e);
            return null;
        }
    }
    
    
    /**
     * 만족도 조사 완료 여부 확인
     */
    private boolean isSurveyCompleted(Ncs_PrgInfo program, String currentDate) {
        if (program.getSurveyDt() == null || program.getSurveyDt().trim().isEmpty()) {
            return false;
        }
        
        try {
            String surveyEndDate = program.getSurveyDt().length() >= 10 ? 
                program.getSurveyDt().substring(0, 10) : program.getSurveyDt();
            
            return currentDate.compareTo(surveyEndDate) > 0;
        } catch (Exception e) {
            log.warn("만족도 조사 마감일 비교 실패: 프로그램ID={}, 조사마감일={}", 
                    program.getPrgId(), program.getSurveyDt());
            return false;
        }
    }

    /**
     * 마일리지 상태 결정
     */
    private String determineMileageStatus(Ncs_PrgInfo program, Integer completedCount, Boolean mileagePaid) {
        String currentDate = adminModule.todays_module();
        
        // 만족도 조사가 끝나지 않았으면 대상 아님
        if (!isSurveyCompleted(program, currentDate)) {
            return "not_ready";
        }
        
        // 이수 완료자가 없으면 대상 아님
        if (completedCount == null || completedCount == 0) {
            return "no_participants";
        }
        
        // 마일리지 지급 여부에 따라 결정
        return mileagePaid ? "completed" : "waiting";
    }

    /**
     * 마일리지 상태 필터 적용
     */
    private boolean applyMileageStatusFilter(NoncurrProgramDTO program, String status) {
        if (status == null || status.trim().isEmpty() || "all".equals(status)) {
            // 전체: 지급 대상인 프로그램만 (waiting + completed)
            return "waiting".equals(program.getPrgStatNm()) || "completed".equals(program.getPrgStatNm());
        }
        
        return status.equals(program.getPrgStatNm());
    }

    /**
     * 프로그램 기간 포맷팅
     */
    private String formatProgramPeriod(String startDt, String endDt) {
        try {
            if (startDt == null || endDt == null) {
                return "기간 정보 없음";
            }
            
            String start = startDt.length() >= 10 ? startDt.substring(0, 10) : startDt;
            String end = endDt.length() >= 10 ? endDt.substring(0, 10) : endDt;
            return start + " ~ " + end;
        } catch (Exception e) {
            log.warn("프로그램 기간 포맷팅 실패: start={}, end={}", startDt, endDt);
            return startDt + " ~ " + endDt;
        }
    }

    /**
     * 날짜 포맷팅
     */
    private String formatDate(Object dateObj) {
        if (dateObj == null) return "";
        
        try {
            String dateStr = dateObj.toString();
            return dateStr.length() >= 10 ? dateStr.substring(0, 10) : dateStr;
        } catch (Exception e) {
            return dateObj.toString();
        }
    }

    /**
     * 마일리지 가산 코드 조회 또는 생성
     */
    private Common_Code getOrCreateAdditionCode() {
        // ✅ 실제 DB 코드 ID 71번 사용 ("적립" 코드)
        return commonCodeRepository.findById(71)
            .orElseGet(() -> {
                log.warn("마일리지 가산 코드(71)가 없어 기본값 사용");
                Common_Code defaultCode = new Common_Code();
                defaultCode.setCodeId(71);
                defaultCode.setCode("적립");
                defaultCode.setCodeDesc("마일리지 적립");
                return defaultCode;
            });
    }



    /**
     * 학생 계좌 정보 요약 조회
     */
    private String getBankInfoSummary(Integer stdId) {
        if (stdId == null) return "정보없음";
        
        try {
            Optional<Std_Info> studentOpt = stdInfoRepository.findById(stdId);
            if (studentOpt.isPresent()) {
                Std_Info student = studentOpt.get();
                if (student.getBankNm() != null && student.getBankAcnt() != null) {
                    return student.getBankNm() + " " + maskAccountNumber(student.getBankAcnt());
                }
            }
            return "계좌정보없음";
        } catch (Exception e) {
            log.warn("계좌 정보 조회 실패: 학생ID={}", stdId);
            return "조회실패";
        }
    }

    /**
     * 계좌번호 마스킹
     */
    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "****";
        }
        
        int len = accountNumber.length();
        if (len <= 6) {
            return accountNumber.substring(0, 2) + "****";
        } else {
            return accountNumber.substring(0, 3) + "****" + accountNumber.substring(len - 3);
        }
    }

    /**
     * 월별 통계 계산 (최근 6개월)
     */
    private List<MileageStatisticsDTO.MonthlyStatDTO> calculateMonthlyStats() {
        List<MileageStatisticsDTO.MonthlyStatDTO> monthlyStats = new ArrayList<>();
        
        try {
            LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
            
            for (int i = 5; i >= 0; i--) {
                LocalDate targetMonth = now.minusMonths(i);
                String monthStr = targetMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                
                // 해당 월의 시작일과 종료일
                String startDate = monthStr + "-01";
                String endDate = monthStr + "-" + targetMonth.lengthOfMonth();
                
                // 해당 월 마일리지 지급 내역 조회
                List<Std_MileageHist> monthlyHistory = stdMileageHistRepository.findByMlgDtBetween(startDate, endDate);
                
                int programCount = (int) monthlyHistory.stream()
                    .map(hist -> hist.getNcsCmpInfo().getNcsPrgInfo().getPrgId())
                    .distinct()
                    .count();
                
                int participantCount = monthlyHistory.size();
                
                int mileageAmount = monthlyHistory.stream()
                    .mapToInt(hist -> hist.getMlgScore() != null ? hist.getMlgScore() : 0)
                    .sum();
                
                MileageStatisticsDTO.MonthlyStatDTO monthStat = MileageStatisticsDTO.MonthlyStatDTO.builder()
                    .month(monthStr)
                    .programCount(programCount)
                    .participantCount(participantCount)
                    .mileageAmount(mileageAmount)
                    .build();
                
                monthlyStats.add(monthStat);
            }
            
        } catch (Exception e) {
            log.error("월별 통계 계산 실패: {}", e.getMessage(), e);
        }
        
        return monthlyStats;
    }
}