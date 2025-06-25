package com.team.arium.admin.noncurr.service;

import com.team.arium.admin.noncurr.dto.NoncurrProgramDTO;
import com.team.arium.admin.noncurr.model.FileUploadModel;
import com.team.arium.admin.noncurr.repository.*;
import com.team.arium.competence.StudentCompetenceRepository;
import com.team.arium.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final StudentCompetenceRepository studentCompetenceRepository;
    private final CommonCodeRepository commonCodeRepository;
    private final CommonFileRepository commonFileRepository;
    private final FileUploadModel fileUploadModel;
    
    /**
     * 비교과 프로그램 등록
     */
    @Transactional
    public Map<String, Object> registerProgram(NoncurrProgramDTO programDTO) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 프로그램 코드 중복 체크
            if (ncsPrgInfoRepository.findByPrgCd(programDTO.getPrgCd()).isPresent()) {
                throw new IllegalArgumentException("이미 존재하는 프로그램 코드입니다.");
            }
            
            // 2. 파일 업로드 처리
            Common_File imageFile = null;
            if (programDTO.getImageFile() != null && !programDTO.getImageFile().isEmpty()) {
                imageFile = fileUploadModel.uploadImageFile(programDTO.getImageFile());
            }
            
            Common_File attachmentFile = null;
            if (programDTO.getAttachmentFile() != null && !programDTO.getAttachmentFile().isEmpty()) {
                attachmentFile = fileUploadModel.uploadAttachmentFile(programDTO.getAttachmentFile());
            }
            
            // 3. 공통코드 조회
            Common_Code statusCode = commonCodeRepository.findById(programDTO.getPrgStatCd())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 프로그램 상태 코드입니다."));
            
            // 4. 비교과 프로그램 정보 저장
            Ncs_PrgInfo ncsProgram = Ncs_PrgInfo.builder()
                    .prgCd(programDTO.getPrgCd())
                    .prgNm(programDTO.getPrgNm())
                    .prgDesc(buildProgramDescription(programDTO))
                    .prgStDt(programDTO.getPrgStDt())
                    .prgEndDt(programDTO.getPrgEndDt())
                    .maxCnt(programDTO.getMaxCnt())
                    .mlgDefScore(programDTO.getMlgDefScore())
                    .surveyDt(programDTO.getSurveyDt())
                    .comFile(imageFile)
                    .prgStatCd(statusCode)
                    .build();
            
            Ncs_PrgInfo savedProgram = ncsPrgInfoRepository.save(ncsProgram);
            
            // 5. 핵심역량 연결 저장
            if (programDTO.getCompetencyIds() != null && !programDTO.getCompetencyIds().isEmpty()) {
                saveCompetencyRelations(savedProgram.getPrgId(), programDTO.getCompetencyIds());
            }
            
            result.put("success", true);
            result.put("message", "비교과 프로그램이 성공적으로 등록되었습니다.");
            result.put("prgId", savedProgram.getPrgId());
            result.put("prgCd", savedProgram.getPrgCd());
            
            if (imageFile != null) {
                result.put("imageUrl", fileUploadModel.getImagePreviewUrl(imageFile.getFileId()));
            }
            if (attachmentFile != null) {
                result.put("attachmentUrl", fileUploadModel.getDownloadUrl(attachmentFile.getFileId()));
            }
            
            log.info("비교과 프로그램 등록 완료: {} ({})", savedProgram.getPrgNm(), savedProgram.getPrgCd());
            
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
            List<Core_CptInfo> competencies = studentCompetenceRepository.findByUpCclIdIsNotNullOrderByUpCclIdAscCclCdAsc();
            
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
                String deptName = getDepartmentName(dto.getDeptCd());
                description.append("운영부서: ").append(deptName).append("\n");
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
        try {
            Optional<Common_Code> deptCode = commonCodeRepository.findByCodeTypeAndCode("DEPT", deptCd);
            return deptCode.map(Common_Code::getCodeDesc).orElse(deptCd);
        } catch (Exception e) {
            return deptCd;
        }
    }
    
    /**
     * 핵심역량 연결 정보 저장
     */
    @Transactional
    public void saveCompetencyRelations(Integer prgId, List<Integer> competencyIds) {
        try {
            ncsCclRelRepository.deleteByPrgId(prgId);
            
            for (Integer cclId : competencyIds) {
                Core_CptInfo competency = studentCompetenceRepository.findByCclId(cclId);
                if (competency != null) {
                    Ncs_CclRel relation = Ncs_CclRel.builder()
                            .prgId(prgId)
                            .cclId(cclId)
                            .cclScore(100)
                            .build();
                    
                    ncsCclRelRepository.save(relation);
                }
            }
            
            log.info("핵심역량 연결 정보 저장 완료: 프로그램ID={}, 연결된역량수={}", prgId, competencyIds.size());
            
        } catch (Exception e) {
            log.error("핵심역량 연결 정보 저장 실패: {}", e.getMessage(), e);
            throw new RuntimeException("핵심역량 연결 정보 저장에 실패했습니다.", e);
        }
    }
    
    /**
     * 검색 조건에 따른 프로그램 조회 (수정)
     */
    private Page<Ncs_PrgInfo> searchPrograms(NoncurrProgramDTO searchDTO, Pageable pageable) {
        String searchKeyword = searchDTO.getSearchKeyword();
        Integer statusFilter = null;
        String yearFilter = null; // Integer에서 String으로 변경
        
        // 상태 필터 처리
        if (searchDTO.getStatusFilter() != null && !searchDTO.getStatusFilter().isEmpty()) {
            statusFilter = getStatusCodeId(searchDTO.getStatusFilter());
        }
        
        // 연도 필터 처리 (String으로 처리)
        if (searchDTO.getPeriodFilter() != null && !searchDTO.getPeriodFilter().isEmpty()) {
            try {
                // 연도 유효성 검증
                Integer.parseInt(searchDTO.getPeriodFilter());
                yearFilter = searchDTO.getPeriodFilter(); // String으로 그대로 사용
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
     * 상태 문자열을 코드ID로 변환
     */
    private Integer getStatusCodeId(String statusString) {
        Map<String, String> statusMap = Map.of(
            "open", "STAT01",
            "pro", "STAT02", 
            "completed", "STAT03",
            "full", "STAT04"
        );
        
        String statusCode = statusMap.get(statusString);
        if (statusCode != null) {
            Optional<Common_Code> code = commonCodeRepository.findByCodeTypeAndCode("PRG_STAT", statusCode);
            return code.map(Common_Code::getCodeId).orElse(null);
        }
        return null;
    }
    
    /**
     * Entity를 목록용 DTO로 변환
     */
    private NoncurrProgramDTO convertToListDTO(Ncs_PrgInfo program) {
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
                .prgStatNm(program.getPrgStatCd().getCodeDesc())
                .regDt(program.getRegDt())
                .updDt(program.getUpdDt())
                .imageUrl(program.getComFile() != null ? 
                    fileUploadModel.getImagePreviewUrl(program.getComFile().getFileId()) : null)
                .build();
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
            
            // 신청자 수는 0으로 설정
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
                    .prgStatNm(program.getPrgStatCd().getCodeDesc())
                    .regDt(program.getRegDt())
                    .updDt(program.getUpdDt())
                    .competencies(competencies)
                    .build();
            
            if (program.getComFile() != null) {
                programDTO.setFileId(program.getComFile().getFileId());
                programDTO.setImageUrl(fileUploadModel.getImagePreviewUrl(program.getComFile().getFileId()));
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
            
            // 신청자 수 체크 생략 (Ncs_Apply 엔티티가 없으므로)
            
            ncsCclRelRepository.deleteByPrgId(prgId);
            
            if (program.getComFile() != null) {
                fileUploadModel.deleteFile(program.getComFile().getFileId());
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
            
            Map<String, Integer> statusCodeMap = Map.of(
                "open", 1,
                "progress", 2,
                "completed", 3,
                "full", 4
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