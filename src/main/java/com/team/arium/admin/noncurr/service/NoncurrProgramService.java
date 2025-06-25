package com.team.arium.admin.noncurr.service;

import com.team.arium.admin.noncurr.dto.NoncurrProgramDTO;
import com.team.arium.admin.noncurr.repository.*;
import com.team.arium.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoncurrProgramService {
	
    private final NcsPrgInfoRepository ncsPrgInfoRepository;
    private final NcsCclRelRepository ncsCclRelRepository;
    private final CoreCptInfoRepository coreCptInfoRepository;
    private final CommonCodeRepository commonCodeRepository;
    private final CommonFileRepository commonFileRepository;
    private final FileUploadServiceImpl fileUploadService;
    
    /**
     * 비교과 프로그램 등록 (임시 버전 - 대표사진만)
     */
    @Transactional
    public Map<String, Object> registerProgram(NoncurrProgramDTO programDTO) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 프로그램 코드 중복 체크
            if (ncsPrgInfoRepository.findByPrgCd(programDTO.getPrgCd()).isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 프로그램 코드입니다.");
            }
            
            // 2. 대표사진 파일 업로드 처리
            Common_File imageFile = null;
            if (programDTO.getImageFile() != null && !programDTO.getImageFile().isEmpty()) {
                try {
                    imageFile = fileUploadService.uploadProgramImage(programDTO.getImageFile());
                    log.info("대표사진 업로드 성공: {}", imageFile.getOrgFileName());
                } catch (Exception e) {
                    log.error("대표사진 업로드 실패", e);
                    // 이미지 업로드 실패해도 프로그램 등록은 계속 진행
                }
            }
            
            // 3. 첨부파일 처리 제거됨 (임시)
            // Common_File attachmentFile = null;
            // if (programDTO.getAttachmentFile() != null && !programDTO.getAttachmentFile().isEmpty()) {
            //     try {
            //         attachmentFile = fileUploadService.uploadAttachmentFile(programDTO.getAttachmentFile());
            //         log.info("첨부 파일 업로드 성공: {}", attachmentFile.getOrgFileName());
            //     } catch (Exception e) {
            //         log.error("첨부 파일 업로드 실패", e);
            //     }
            // }
            
            // 4. 프로그램 상태 코드 조회 (기본값: 51 - 오픈)
            Common_Code statusCode = commonCodeRepository.findById(51)
                    .orElse(Common_Code.builder()
                            .codeId(51)
                            .codeType("prg_stat_cd")
                            .code("오픈")
                            .codeDesc("프로그램 신청 오픈")
                            .build());
            
            // 5. 비교과 프로그램 정보 저장 (기존 file_id 필드 사용)
            Ncs_PrgInfo ncsProgram = Ncs_PrgInfo.builder()
                    .prgCd(programDTO.getPrgCd())
                    .prgNm(programDTO.getPrgNm())
                    .prgDesc(buildProgramDescription(programDTO))
                    .prgStDt(programDTO.getPrgStDt())
                    .prgEndDt(programDTO.getPrgEndDt())
                    .maxCnt(programDTO.getMaxCnt())
                    .mlgDefScore(programDTO.getMlgDefScore())
                    .surveyDt(programDTO.getSurveyDt())
                    .comFile(imageFile)  // 기존 comFile 필드에 대표사진만 저장
                    .prgStatCd(statusCode)
                    .build();
            
            Ncs_PrgInfo savedProgram = ncsPrgInfoRepository.save(ncsProgram);
            log.info("프로그램 정보 저장 완료: {}", savedProgram.getPrgId());
            
            // 6. 핵심역량 연결 저장
            if (programDTO.getCompetencyIds() != null && !programDTO.getCompetencyIds().isEmpty()) {
                try {
                    saveCompetencyRelations(savedProgram.getPrgId(), programDTO.getCompetencyIds());
                    log.info("핵심역량 연결 완료: {} 개", programDTO.getCompetencyIds().size());
                } catch (Exception e) {
                    log.error("핵심역량 연결 실패", e);
                    // 핵심역량 연결 실패해도 프로그램 등록은 완료로 처리
                }
            }
            
            result.put("success", true);
            result.put("message", "비교과 프로그램이 성공적으로 등록되었습니다. (첨부파일은 추후 별도 추가 가능)");
            result.put("prgId", savedProgram.getPrgId());
            result.put("prgCd", savedProgram.getPrgCd());
            
            if (imageFile != null) {
                result.put("imageUrl", fileUploadService.getImagePreviewUrl(imageFile.getFileId()));
                result.put("imageFileName", imageFile.getOrgFileName());
            }
            
            // 첨부파일 관련 정보 제거
            // if (attachmentFile != null) {
            //     result.put("attachmentUrl", fileUploadService.getDownloadUrl(attachmentFile.getFileId()));
            //     result.put("attachmentFileName", attachmentFile.getOrgFileName());
            // }
            
            log.info("비교과 프로그램 등록 완료: {} ({})", savedProgram.getPrgNm(), savedProgram.getPrgCd());
            log.info("업로드된 파일: 대표사진={}", 
                    imageFile != null ? imageFile.getOrgFileName() : "없음");
            log.info("⚠️ 임시 버전: 첨부파일 업로드 생략됨");
            
        } catch (Exception e) {
            log.error("비교과 프로그램 등록 실패: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "등록 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 비교과 프로그램 목록 조회 (페이징)
     */
    public Map<String, Object> getProgramList(NoncurrProgramDTO searchDTO) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 페이징 설정
            Pageable pageable = PageRequest.of(
                searchDTO.getPage(), 
                searchDTO.getSize(), 
                Sort.by("regDt").descending()
            );
            
            // 검색 조건에 따른 조회
            Page<Ncs_PrgInfo> programPage = searchPrograms(searchDTO, pageable);
            
            // DTO 변환
            List<NoncurrProgramDTO> programList = programPage.getContent().stream()
                    .map(this::convertToListDTO)
                    .collect(Collectors.toList());
            
            result.put("success", true);
            result.put("programs", programList);
            result.put("totalElements", programPage.getTotalElements());
            result.put("totalPages", programPage.getTotalPages());
            result.put("currentPage", programPage.getNumber());
            result.put("size", programPage.getSize());
            result.put("first", programPage.isFirst());
            result.put("last", programPage.isLast());
            
        } catch (Exception e) {
            log.error("프로그램 목록 조회 실패: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            result.put("programs", Collections.emptyList());
        }
        
        return result;
    }
    
    /**
     * 핵심역량 목록 조회 (간단한 형태로)
     */
    public List<Map<String, Object>> getCompetencyList() {
        try {
            List<Core_CptInfo> competencies = coreCptInfoRepository.findActiveCompetencies();
            
            return competencies.stream()
                    .map(competency -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("cclId", competency.getCclId());
                        map.put("cclNm", competency.getCclNm());
                        map.put("cclDesc", competency.getCclDesc());
                        return map;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("핵심역량 목록 조회 실패: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 프로그램 코드 생성
     */
    public String generateProgramCode() {
        try {
            String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String prefix = "PRG" + today;
            
            List<String> existingCodes = ncsPrgInfoRepository.findLatestPrgCdWithPrefix(prefix);
            
            int nextSequence = 1;
            if (!existingCodes.isEmpty()) {
                String latestCode = existingCodes.get(0);
                try {
                    String sequencePart = latestCode.substring(latestCode.length() - 3);
                    nextSequence = Integer.parseInt(sequencePart) + 1;
                } catch (Exception e) {
                    log.warn("기존 코드 파싱 실패, 기본값 사용: {}", latestCode);
                }
            }
            
            return prefix + String.format("%03d", nextSequence);
            
        } catch (Exception e) {
            log.error("프로그램 코드 생성 실패: {}", e.getMessage(), e);
            return "PRG" + System.currentTimeMillis();
        }
    }
    
    /**
     * 프로그램 설명 구성
     */
    private String buildProgramDescription(NoncurrProgramDTO dto) {
        StringBuilder description = new StringBuilder();
        description.append(dto.getPrgDesc());
        
        if (dto.getDeptCd() != null || dto.getContactTel() != null || 
            dto.getRecruitStartDt() != null || dto.getRecruitEndDt() != null) {
            
            description.append("\n\n### 추가 정보 ###\n");
            
            if (dto.getDeptCd() != null) {
                description.append("운영부서: ").append(dto.getDeptCd()).append("\n");
            }
            
            if (dto.getContactTel() != null) {
                description.append("문의전화: ").append(dto.getContactTel()).append("\n");
            }
            
            if (dto.getRecruitStartDt() != null && dto.getRecruitEndDt() != null) {
                description.append("모집기간: ").append(dto.getRecruitStartDt())
                          .append(" ~ ").append(dto.getRecruitEndDt()).append("\n");
            }
        }
        
        return description.toString();
    }
    
    /**
     * 부서코드로 부서명 조회
     */
    private String getDepartmentName(String deptCd) {
        if (deptCd == null || deptCd.trim().isEmpty()) {
            return "미지정";
        }
        
        // 하드코딩된 부서명 매핑
        Map<String, String> deptMap = Map.of(
            "교양학부", "교양학부",
            "취업지원처", "취업지원처", 
            "정보과학과", "정보과학과",
            "학생처", "학생처",
            "창업지원센터", "창업지원센터",
            "학습지원센터", "학습지원센터",
            "사회봉사센터", "사회봉사센터",
            "연구처", "연구처",
            "컴퓨터과학과", "컴퓨터과학과"
        );
        
        return deptMap.getOrDefault(deptCd, deptCd);
    }
    
    /**
     * 핵심역량 연결 정보 저장
     */
    @Transactional
    public void saveCompetencyRelations(Integer prgId, List<Integer> competencyIds) {
        try {
            // 기존 연결 정보 삭제
            ncsCclRelRepository.deleteByPrgId(prgId);
            
            // 새로운 연결 정보 저장
            for (Integer cclId : competencyIds) {
                Core_CptInfo competency = coreCptInfoRepository.findByCclId(cclId);
                if (competency != null) {
                    Ncs_CclRel relation = Ncs_CclRel.builder()
                            .prgId(prgId)
                            .cclId(cclId)
                            .cclScore(100) // 기본 점수
                            .build();
                    
                    ncsCclRelRepository.save(relation);
                    log.debug("핵심역량 연결 저장: prgId={}, cclId={}", prgId, cclId);
                } else {
                    log.warn("존재하지 않는 핵심역량 ID: {}", cclId);
                }
            }
            
            log.info("핵심역량 연결 정보 저장 완료: 프로그램ID={}, 연결된역량수={}", prgId, competencyIds.size());
            
        } catch (Exception e) {
            log.error("핵심역량 연결 정보 저장 실패: {}", e.getMessage(), e);
            throw new RuntimeException("핵심역량 연결 정보 저장에 실패했습니다.", e);
        }
    }
    
    /**
     * 검색 조건에 따른 프로그램 조회
     */
    private Page<Ncs_PrgInfo> searchPrograms(NoncurrProgramDTO searchDTO, Pageable pageable) {
        String searchKeyword = searchDTO.getSearchKeyword();
        Integer statusFilter = null;
        String yearFilter = null;
        
        // 상태 필터 처리
        if (searchDTO.getStatusFilter() != null && !searchDTO.getStatusFilter().isEmpty()) {
            statusFilter = getStatusCodeId(searchDTO.getStatusFilter());
        }
        
        // 연도 필터 처리
        if (searchDTO.getPeriodFilter() != null && !searchDTO.getPeriodFilter().isEmpty()) {
            try {
                Integer.parseInt(searchDTO.getPeriodFilter());
                yearFilter = searchDTO.getPeriodFilter();
            } catch (NumberFormatException e) {
                log.warn("잘못된 연도 필터 값: {}", searchDTO.getPeriodFilter());
            }
        }
        
        // 검색 실행
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            return ncsPrgInfoRepository.findByAllSearchConditions(
                searchKeyword.trim(), statusFilter, yearFilter, pageable);
        } else {
            return ncsPrgInfoRepository.findByAllSearchConditions(
                null, statusFilter, yearFilter, pageable);
        }
    }
    
    /**
     * 상태 문자열을 코드ID로 변환 (수정된 매핑)
     */
    private Integer getStatusCodeId(String statusString) {
        // 공통코드 테이블 이미지 기준으로 수정
        Map<String, Integer> statusMap = Map.of(
            "open", 51,      // 오픈
            "pro", 52,       // 진행  
            "completed", 53, // 종료
            "full", 51       // 임시로 오픈으로 설정
        );
        
        return statusMap.get(statusString);
    }
    
    /**
     * Entity를 목록용 DTO로 변환
     */
    private NoncurrProgramDTO convertToListDTO(Ncs_PrgInfo program) {
        // 부서명 추출 (프로그램 설명에서 추출)
        String deptNm = extractDepartmentFromDescription(program.getPrgDesc());
        String recruitPeriod = extractRecruitPeriodFromDescription(program.getPrgDesc());
        
        return NoncurrProgramDTO.builder()
                .prgId(program.getPrgId())
                .prgCd(program.getPrgCd())
                .prgNm(program.getPrgNm())
                .prgDesc(program.getPrgDesc())
                .prgStDt(program.getPrgStDt())
                .prgEndDt(program.getPrgEndDt())
                .maxCnt(program.getMaxCnt())
                .currentCnt(0) // 신청자 수는 0으로 설정
                .mlgDefScore(program.getMlgDefScore())
                .surveyDt(program.getSurveyDt())
                .deptNm(deptNm) // 추출된 부서명
                .recruitStartDt(extractStartDateFromPeriod(recruitPeriod))
                .recruitEndDt(extractEndDateFromPeriod(recruitPeriod))
                .prgStatNm(program.getPrgStatCd() != null ? program.getPrgStatCd().getCodeDesc() : "알 수 없음")
                .regDt(program.getRegDt())
                .updDt(program.getUpdDt())
                .imageUrl(program.getComFile() != null ? 
                    fileUploadService.getImagePreviewUrl(program.getComFile().getFileId()) : null)
                .build();
    }
    
    /**
     * 프로그램 설명에서 부서명 추출
     */
    private String extractDepartmentFromDescription(String description) {
        if (description == null) return "미지정";
        
        String[] lines = description.split("\n");
        for (String line : lines) {
            if (line.startsWith("운영부서:")) {
                return line.replace("운영부서:", "").trim();
            }
        }
        return "미지정";
    }
    
    /**
     * 프로그램 설명에서 모집기간 추출
     */
    private String extractRecruitPeriodFromDescription(String description) {
        if (description == null) return "";
        
        String[] lines = description.split("\n");
        for (String line : lines) {
            if (line.startsWith("모집기간:")) {
                return line.replace("모집기간:", "").trim();
            }
        }
        return "";
    }
    
    /**
     * 모집기간에서 시작일 추출
     */
    private String extractStartDateFromPeriod(String period) {
        if (period == null || !period.contains(" ~ ")) return "";
        return period.split(" ~ ")[0].trim();
    }
    
    /**
     * 모집기간에서 종료일 추출
     */
    private String extractEndDateFromPeriod(String period) {
        if (period == null || !period.contains(" ~ ")) return "";
        String[] parts = period.split(" ~ ");
        return parts.length > 1 ? parts[1].trim() : "";
    }
    
    /**
     * 비교과 프로그램 상세 조회
     */
    public Map<String, Object> getProgramDetail(Integer prgId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<Ncs_PrgInfo> programOpt = ncsPrgInfoRepository.findById(prgId);
            if (programOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "프로그램을 찾을 수 없습니다.");
                return result;
            }
            
            Ncs_PrgInfo program = programOpt.get();
            
            Integer currentCnt = 0;
            
            List<Object[]> competencyRels = ncsCclRelRepository.findCompetenciesWithDetailsByPrgId(prgId);
            List<NoncurrProgramDTO.CompetencyRelDTO> competencies = competencyRels.stream()
                    .map(rel -> {
                        Ncs_CclRel relation = (Ncs_CclRel) rel[0];
                        Core_CptInfo competency = (Core_CptInfo) rel[1];
                        
                        return NoncurrProgramDTO.CompetencyRelDTO.builder()
                                .prgId(relation.getPrgId())
                                .cclId(relation.getCclId())
                                .cclScore(relation.getCclScore())
                                .cclNm(competency.getCclNm())
                                .cclDesc(competency.getCclDesc())
                                .build();
                    })
                    .collect(Collectors.toList());
            
            NoncurrProgramDTO programDTO = NoncurrProgramDTO.builder()
                    .prgId(program.getPrgId())
                    .prgCd(program.getPrgCd())
                    .prgNm(program.getPrgNm())
                    .prgDesc(program.getPrgDesc())
                    .prgStDt(program.getPrgStDt())
                    .prgEndDt(program.getPrgEndDt())
                    .maxCnt(program.getMaxCnt())
                    .currentCnt(currentCnt)
                    .mlgDefScore(program.getMlgDefScore())
                    .surveyDt(program.getSurveyDt())
                    .prgStatNm(program.getPrgStatCd() != null ? program.getPrgStatCd().getCodeDesc() : "알 수 없음")
                    .regDt(program.getRegDt())
                    .updDt(program.getUpdDt())
                    .competencies(competencies)
                    .build();
            
            if (program.getComFile() != null) {
                programDTO.setFileId(program.getComFile().getFileId());
                programDTO.setImageUrl(fileUploadService.getImagePreviewUrl(program.getComFile().getFileId()));
                programDTO.setOrgFileName(program.getComFile().getOrgFileName());
            }
            
            result.put("success", true);
            result.put("program", programDTO);
            
            log.info("프로그램 상세 조회 완료: {} ({})", program.getPrgNm(), program.getPrgCd());
            
        } catch (Exception e) {
            log.error("프로그램 상세 조회 실패: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "상세 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 비교과 프로그램 삭제
     */
    @Transactional
    public Map<String, Object> deleteProgram(Integer prgId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<Ncs_PrgInfo> programOpt = ncsPrgInfoRepository.findById(prgId);
            if (programOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "삭제할 프로그램을 찾을 수 없습니다.");
                return result;
            }
            
            Ncs_PrgInfo program = programOpt.get();
            
            ncsCclRelRepository.deleteByPrgId(prgId);
            
            if (program.getComFile() != null) {
                fileUploadService.deleteFile(program.getComFile());
            }
            
            ncsPrgInfoRepository.delete(program);
            
            result.put("success", true);
            result.put("message", "프로그램이 성공적으로 삭제되었습니다.");
            
            log.info("프로그램 삭제 완료: {} ({})", program.getPrgNm(), program.getPrgCd());
            
        } catch (Exception e) {
            log.error("프로그램 삭제 실패: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "삭제 중 오류가 발생했습니다: " + e.getMessage());
            throw new RuntimeException("프로그램 삭제에 실패했습니다.", e);
        }
        
        return result;
    }
    
    /**
     * 프로그램 상태별 통계 조회
     */
    public Map<String, Object> getProgramStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            long totalCount = ncsPrgInfoRepository.count();
            
            Map<String, Long> statusCounts = new HashMap<>();
            
            // 실제 공통코드 ID 매핑
            Map<String, Integer> statusCodeMap = Map.of(
                "open", 51,      // 오픈
                "progress", 52,  // 진행
                "completed", 53, // 종료
                "full", 51       // 임시로 오픈으로 설정
            );
            
            for (Map.Entry<String, Integer> entry : statusCodeMap.entrySet()) {
                try {
                    Long count = ncsPrgInfoRepository.countByPrgStatCd(entry.getValue());
                    statusCounts.put(entry.getKey(), count);
                } catch (Exception e) {
                    log.warn("상태별 통계 조회 오류 ({}): {}", entry.getKey(), e.getMessage());
                    statusCounts.put(entry.getKey(), 0L);
                }
            }
            
            Map<String, Long> statistics = new HashMap<>();
            statistics.put("total", totalCount);
            statistics.put("open", statusCounts.getOrDefault("open", 0L));
            statistics.put("progress", statusCounts.getOrDefault("progress", 0L));
            statistics.put("completed", statusCounts.getOrDefault("completed", 0L));
            statistics.put("full", statusCounts.getOrDefault("full", 0L));
            
            result.put("success", true);
            result.put("statistics", statistics);
            
        } catch (Exception e) {
            log.error("프로그램 통계 조회 실패: {}", e.getMessage(), e);
            
            Map<String, Long> statistics = new HashMap<>();
            statistics.put("total", 0L);
            statistics.put("open", 0L);
            statistics.put("progress", 0L);
            statistics.put("completed", 0L);
            statistics.put("full", 0L);
            
            result.put("success", true);
            result.put("statistics", statistics);
        }
        
        return result;
    }
}