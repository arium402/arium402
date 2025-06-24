package com.team.arium.admin.noncurr.service;

import com.team.arium.admin.noncurr.dto.*;
import com.team.arium.admin.noncurr.repository.*;
import com.team.arium.admin.noncurr.exception.NoncurrException;
import com.team.arium.competence.StudentCompetenceDTO;
import com.team.arium.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NoncurrService {
    
    private final NcsPrgInfoRepository ncsPrgInfoRepository;
    private final CoreCptInfoRepository coreCptInfoRepository;
    private final NcsCclRelRepository ncsCclRelRepository;
    private final CommonCodeRepository commonCodeRepository;
    private final FileUploadService fileUploadService;
    
    /**
     * 비교과 프로그램 등록 (등록 페이지용)
     */
    @Transactional
    public Integer addProgram(NoncurrAddRequestDto requestDto) {
        try {
            log.info("비교과 프로그램 등록 시작 - 프로그램명: {}", requestDto.getProgramName());
            
            // 1. 프로그램 코드 생성
            String programCode = generateProgramCode();
            
            // 2. 파일 업로드 처리
            Common_File programImageFile = null;
            Common_File attachmentFile = null;
            
            if (requestDto.getProgramImage() != null && !requestDto.getProgramImage().isEmpty()) {
                programImageFile = fileUploadService.uploadProgramImage(requestDto.getProgramImage());
            }
            
            if (requestDto.getAttachmentFile() != null && !requestDto.getAttachmentFile().isEmpty()) {
                attachmentFile = fileUploadService.uploadAttachmentFile(requestDto.getAttachmentFile());
            }
            
            // 3. 프로그램 상태 코드 조회 (오픈 상태로 설정)
            Common_Code statusCode = getOrCreateDefaultStatusCode();
            
            // 4. 프로그램 정보 생성
            Ncs_PrgInfo program = Ncs_PrgInfo.builder()
                .prgCd(programCode)
                .prgNm(requestDto.getProgramName())
                .prgDesc(requestDto.getDescription())
                .prgStDt(requestDto.getOperationStart())
                .prgEndDt(requestDto.getOperationEnd())
                .maxCnt(requestDto.getCapacity())
                .mlgDefScore(requestDto.getMileagePoints())
                .surveyDt(requestDto.getSurveyDeadline())
                .comFile(programImageFile)
                .prgStatCd(statusCode)
                .build();
            
            // 5. 프로그램 저장
            Ncs_PrgInfo savedProgram = ncsPrgInfoRepository.save(program);
            
            // 6. 핵심역량 연결 저장
            if (requestDto.getSelectedCompetencyIds() != null && !requestDto.getSelectedCompetencyIds().isEmpty()) {
                saveCompetencyRelations(savedProgram.getPrgId(), 
                    requestDto.getSelectedCompetencyIds(), 
                    requestDto.getCompetencyScores());
            }
            
            log.info("비교과 프로그램 등록 완료 - ID: {}, 이름: {}", savedProgram.getPrgId(), savedProgram.getPrgNm());
            
            return savedProgram.getPrgId();
            
        } catch (Exception e) {
            log.error("비교과 프로그램 등록 실패", e);
            if (e instanceof NoncurrException) {
                throw e;
            }
            throw new NoncurrException("프로그램 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 비교과 프로그램 목록 조회 (목록 페이지용)
     */
    public Page<NoncurrListResponseDto> getProgramList(String searchKeyword, String department, 
                                                     Integer statusCode, Pageable pageable) {
        try {
            log.debug("프로그램 목록 조회 - 키워드: {}, 부서: {}, 상태: {}", searchKeyword, department, statusCode);
            
            Page<Ncs_PrgInfo> programs = ncsPrgInfoRepository.findBySearchConditions(
                searchKeyword, department, statusCode, pageable);
            
            Page<NoncurrListResponseDto> result = programs.map(this::convertToListDto);
            
            log.debug("프로그램 목록 조회 완료 - 총 {}건", result.getTotalElements());
            return result;
            
        } catch (Exception e) {
            log.error("프로그램 목록 조회 실패", e);
            throw new NoncurrException("프로그램 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 활성 핵심역량 목록 조회 (등록 페이지용 - 기존 StudentCompetenceDTO 사용)
     */
    public List<StudentCompetenceDTO> getActiveCompetencies() {
        try {
            log.debug("활성 핵심역량 목록 조회 시작");
            
            List<StudentCompetenceDTO> competencies = coreCptInfoRepository.findActiveCompetencies()
                .stream()
                .map(competency -> StudentCompetenceDTO.builder()
                    .cclId(competency.getCclId())
                    .cclNm(competency.getCclNm())
                    .cclDesc(competency.getCclDesc())
                    .build())
                .collect(Collectors.toList());
            
            log.debug("활성 핵심역량 목록 조회 완료 - {}개", competencies.size());
            return competencies;
            
        } catch (Exception e) {
            log.error("핵심역량 목록 조회 실패", e);
            throw new NoncurrException("핵심역량 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // ================= Private 메서드들 =================
    
    /**
     * 프로그램 코드 생성 (년도 + 순번)
     */
    private String generateProgramCode() {
        String year = String.valueOf(LocalDate.now().getYear());
        long count = ncsPrgInfoRepository.count();
        return year + String.format("%04d", count + 1);
    }
    
    /**
     * 기본 상태 코드 조회 또는 생성
     */
    private Common_Code getOrCreateDefaultStatusCode() {
        return commonCodeRepository.findByCodeTypeAndCode("PRG_STATUS", "OPEN")
            .orElse(Common_Code.builder()
                .codeId(1001)
                .codeType("PRG_STATUS")
                .code("OPEN")
                .codeDesc("오픈")
                .build());
    }
    
    /**
     * 핵심역량 연결 저장
     */
    @Transactional
    public void saveCompetencyRelations(Integer programId, List<Integer> competencyIds, List<Integer> scores) {
        try {
            log.debug("핵심역량 연결 저장 시작 - 프로그램ID: {}, 역량개수: {}", programId, competencyIds.size());
            
            // 기존 연결 삭제
            ncsCclRelRepository.deleteByPrgId(programId);
            
            // 새로운 연결 저장
            IntStream.range(0, competencyIds.size())
                .forEach(i -> {
                    Integer competencyId = competencyIds.get(i);
                    Integer score = (scores != null && i < scores.size()) ? scores.get(i) : 100; // 기본값 100
                    
                    // 핵심역량 존재 여부 확인
                    Core_CptInfo competency = coreCptInfoRepository.findByCclId(competencyId);
                    if (competency == null) {
                        log.warn("존재하지 않는 핵심역량 ID: {}", competencyId);
                        return;
                    }
                    
                    Ncs_CclRel relation = Ncs_CclRel.builder()
                        .prgId(programId)
                        .cclId(competencyId)
                        .cclScore(score)
                        .build();
                    
                    ncsCclRelRepository.save(relation);
                    log.debug("핵심역량 연결 저장 - 역량ID: {}, 점수: {}", competencyId, score);
                });
            
            log.debug("핵심역량 연결 저장 완료 - 프로그램ID: {}", programId);
            
        } catch (Exception e) {
            log.error("핵심역량 연결 저장 실패 - 프로그램ID: {}", programId, e);
            throw new NoncurrException("핵심역량 연결 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 엔티티를 목록 DTO로 변환
     */
    private NoncurrListResponseDto convertToListDto(Ncs_PrgInfo program) {
        try {
            // 현재 신청자 수 계산 (일단 0으로 - 나중에 추가)
            int currentCapacity = 0;
            
            return NoncurrListResponseDto.builder()
                .prgId(program.getPrgId())
                .prgCd(program.getPrgCd())
                .prgNm(program.getPrgNm())
                .department("운영부서") // TODO: 실제 부서 정보로 변경 필요
                .recruitmentPeriod(formatPeriod(program.getPrgStDt(), program.getPrgEndDt()))
                .operationPeriod(formatPeriod(program.getPrgStDt(), program.getPrgEndDt()))
                .currentCapacity(currentCapacity)
                .maxCapacity(program.getMaxCnt())
                .status(getStatusName(program.getPrgStatCd().getCode()))
                .prgStatCd(program.getPrgStatCd().getCode())
                .mlgDefScore(program.getMlgDefScore())
                .regDt(program.getRegDt())
                .build();
        } catch (Exception e) {
            log.error("DTO 변환 실패 - 프로그램ID: {}", program.getPrgId(), e);
            throw new NoncurrException("데이터 변환 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * 날짜 형식 변환 (String -> String 포맷팅)
     */
    private String formatPeriod(String startDate, String endDate) {
        try {
            if (startDate == null || endDate == null) {
                return "";
            }
            return startDate + "~" + endDate;
        } catch (Exception e) {
            log.warn("날짜 포맷 변환 실패 - 시작일: {}, 종료일: {}", startDate, endDate);
            return startDate + "~" + endDate;
        }
    }
    
    /**
     * 상태 코드를 한글명으로 변환
     */
    private String getStatusName(String statusCode) {
        if (statusCode == null) {
            return "알 수 없음";
        }
        
        switch (statusCode.toUpperCase()) {
            case "OPEN": return "오픈";
            case "PROGRESS": return "진행";
            case "COMPLETED": return "완료";
            case "FULL": return "인원 마감";
            default: return "알 수 없음";
        }
    }
}