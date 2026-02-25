// 2. 관리자 비교과 API 컨트롤러 (Admin API Controller)
package com.team.arium.admin.noncurr.controller;

import com.team.arium.admin.mileage.StdMileageHistRepository;
import com.team.arium.admin.noncurr.dto.ApplicantDTO;
import com.team.arium.admin.noncurr.dto.NoncurrProgramDTO;
import com.team.arium.admin.noncurr.dto.SatisfactionSurveyDTO;
import com.team.arium.admin.noncurr.repository.CommonCodeRepository;
import com.team.arium.admin.noncurr.repository.NcsPrgInfoRepository;
import com.team.arium.admin.noncurr.service.AdminNoncurrProgramService;
import com.team.arium.admin.noncurr.service.NoncurrStatisticsService;
import com.team.arium.domain.Common_Code;
import com.team.arium.domain.Core_CptInfo;
import com.team.arium.domain.Std_MileageHist;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminNoncurrApiController {

    private final AdminNoncurrProgramService adminNoncurrProgramService;

    @Autowired
    private CommonCodeRepository commonCodeRepository; // 추가 필요
    
    @Autowired
    private NoncurrStatisticsService noncurrStatisticsService;
    
    @Autowired
    private StdMileageHistRepository stdMileHistRepo;
    
    @Autowired
    private NcsPrgInfoRepository ncsPrgInfoRepository;
    
    /*
    @PostMapping("/noncurr_add")
    public String noncurr_add(
            @ModelAttribute NoncurrProgramDTO dto,
            HttpServletRequest req
    		) {

    	//  Common_Code 객체 생성해서 상태 코드 설정
    	Common_Code statusCode = new Common_Code();
    	statusCode.setCodeId(51);
    	statusCode.setCodeType("prg_stat_cd");
    	statusCode.setCode("오픈");
    	statusCode.setCodeDesc("프로그램 신청 오픈");
    	dto.setPrgStatCd(statusCode);
    	
        log.info("비교과 프로그램 등록 API 요청: {}", dto.getPrgNm());
        
        try {
            log.info("=== 파일 업로드 디버깅 시작 (수정) ===");
            log.info("전체 파라미터 개수: {}", req.getParameterMap().size());
            log.info("이미지 파일 정보: {}", dto.getImageFile() != null ? dto.getImageFile().getOriginalFilename() : "null");
            
         //  문자열을 List로 변환 (DTO에서 자동 처리됨)
            log.info("선택된 핵심역량: {}", dto.getCompetencyIds());
            
            Collection<Part> parts = req.getParts();
            log.info("총 파트 개수: {}", parts.size());
            for (Part part : parts) {
                log.info("파트명: {}, 타입: {}, 크기: {}", 
                    part.getName(), 
                    part.getContentType(), 
                    part.getSize());
            }
            log.info("=== 파일 업로드 디버깅 끝 (수정) ===");
            
    	} catch (Exception debugE) {
    		log.error("디버깅 로그 실패", debugE);
    	}
        
        try { 
            // 입력값 검증
            String validationError = validateProgramDto(dto);
            if (validationError != null) {
                log.warn("프로그램 등록 검증 실패: {}", validationError);
                return "redirect:/admin/noncurr_add?error=validation_failed";
            }
     
    // 프로그램 등록
    Integer prgId = adminNoncurrProgramService.createProgram(dto);
    
    log.info("비교과 프로그램 등록 성공: ID={}, 이름={}", prgId, dto.getPrgNm());
    
    //  성공 시 목록 페이지로 리다이렉트 (success 파라미터 추가)
    return "redirect:/admin/noncurr_success";
    
} catch (Exception e) {
	log.error("비교과 프로그램 등록 실패: 이름={}, 오류={}", dto.getPrgNm(), e.getMessage(), e);
	return "redirect:/admin/noncurr_add?error=registration_failed";
}
}
     */
            
     

    /**
     * 비교과 프로그램 수정
     */
    @PostMapping("/noncurr_edit")
    public ResponseEntity<Map<String, Object>> noncurr_edit(
            @RequestParam Integer prgId,
            @ModelAttribute NoncurrProgramDTO dto) {
        
        log.info("비교과 프로그램 수정 API 요청: ID={}, 이름={}", prgId, dto.getPrgNm());
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 입력값 검증
            String validationError = validateProgramDto(dto);
            if (validationError != null) {
                log.warn("프로그램 수정 검증 실패: ID={}, 오류={}", prgId, validationError);
                response.put("success", false);
                response.put("error", validationError);
                return ResponseEntity.badRequest().body(response);
            }
            
            // 프로그램 수정
            adminNoncurrProgramService.updateProgram(prgId, dto);
            
            response.put("success", true);
            response.put("message", "비교과 프로그램이 성공적으로 수정되었습니다.");
            response.put("redirectUrl", "/admin/noncurr_list");
            
            log.info("비교과 프로그램 수정 성공: ID={}", prgId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("비교과 프로그램 수정 실패: ID={}, 오류={}", prgId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "프로그램 수정 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 비교과 프로그램 삭제
     */
    @PostMapping("/noncurr_delete")
    public ResponseEntity<Map<String, Object>> noncurr_delete(@RequestParam("prgId") Integer prgId) {
        log.info("비교과 프로그램 삭제 API 요청: ID={}", prgId);
        Map<String, Object> res = new HashMap<>();
        
        try {
            adminNoncurrProgramService.deleteProgram(prgId);
            
            res.put("success", true);
            res.put("message", "비교과 프로그램이 성공적으로 삭제되었습니다.");
            
            log.info("비교과 프로그램 삭제 성공: ID={}", prgId);
            return ResponseEntity.ok(res);
            
        } catch (Exception e) {
            log.error("비교과 프로그램 삭제 실패: ID={}, 오류={}", prgId, e.getMessage(), e);
            res.put("success", false);
            res.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(res);
        }
    }
    
    /**
     * ⚠️ 테스트용 - 삭제된 프로그램의 마일리지 내역 정리
     * 사용 후 삭제하거나 주석처리!
     @GetMapping("/cleanup_mileage")
    public ResponseEntity<Map<String, Object>> cleanupMileage() {
        log.warn("⚠️ 마일리지 정리 실행 - 테스트용!");
        
        Map<String, Object> res = new HashMap<>();
        
        try {
            // 1. 모든 마일리지 내역 조회
            List<Std_MileageHist> allHist = stdMileHistRepo.findAll();
            
            List<Std_MileageHist> toDelete = new ArrayList<>();
            
            for (Std_MileageHist hist : allHist) {
                // 2. 프로그램이 삭제되었거나 없는 경우
                Integer prgId = hist.getNcsCmpInfo().getNcsPrgInfo().getPrgId();
                boolean exists = ncsPrgInfoRepository.existsById(prgId);
                
                if (!exists) {
                    toDelete.add(hist);
                }
            }
            
            // 3. 삭제
            if (!toDelete.isEmpty()) {
                stdMileHistRepo.deleteAll(toDelete);
                log.warn("삭제된 프로그램의 마일리지 {} 건 정리 완료", toDelete.size());
            }
            
            res.put("success", true);
            res.put("message", "마일리지 정리 완료");
            res.put("deletedCount", toDelete.size());
            
            return ResponseEntity.ok(res);
            
        } catch (Exception e) {
            log.error("마일리지 정리 실패: {}", e.getMessage(), e);
            res.put("success", false);
            res.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(res);
        }
    }

     
     */
        

    /**
     * 프로그램 상세 정보 조회 (AJAX용) - 기존
     */
    @GetMapping("/noncurr_detail_ajax")
    public ResponseEntity<Map<String, Object>> noncurr_detail_ajax(@RequestParam("id") Integer prgId) {
        log.info("비교과 프로그램 상세 조회 API 요청: ID={}", prgId);
        Map<String, Object> response = new HashMap<>();
        
        try {
            NoncurrProgramDTO programDto = adminNoncurrProgramService.getProgramDetail(prgId);
            
            response.put("success", true);
            response.put("data", programDto);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("비교과 프로그램 상세 조회 실패: ID={}, 오류={}", prgId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "프로그램 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     *  신청자 목록 조회 API (새로 추가)
     */
    @GetMapping("/noncurr_applicants")
    public ResponseEntity<Map<String, Object>> getApplicants(
            @RequestParam("prgId") Integer prgId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        log.info("신청자 목록 조회 API 요청: 프로그램ID={}, 페이지={}, 크기={}", prgId, page, size);
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Pageable 객체 생성
            Pageable pageable = PageRequest.of(page, size);
            
            // 신청자 목록 조회 (서비스에 메서드 추가 필요)
            Page<ApplicantDTO> applicants = adminNoncurrProgramService.getApplicantList(prgId, pageable);
            
            // 페이징 정보
            Map<String, Object> pagination = new HashMap<>();
            pagination.put("currentPage", page);
            pagination.put("totalPages", applicants.getTotalPages());
            pagination.put("totalElements", applicants.getTotalElements());
            pagination.put("size", size);
            pagination.put("first", applicants.isFirst());
            pagination.put("last", applicants.isLast());
            pagination.put("hasNext", applicants.hasNext());
            pagination.put("hasPrevious", applicants.hasPrevious());
            
            response.put("success", true);
            response.put("applicants", applicants.getContent());
            response.put("pagination", pagination);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("신청자 목록 조회 실패: 프로그램ID={}, 오류={}", prgId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "신청자 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    
    /**
     *  통합 상세 정보 조회 API (프로그램 정보 + 신청자 목록)
     */
    @GetMapping("/noncurr_detail_full")
    public ResponseEntity<Map<String, Object>> getFullProgramDetail(
            @RequestParam("id") Integer prgId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        log.info("통합 상세 정보 조회 API 요청: ID={}, 페이지={}", prgId, page);
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 1. 프로그램 상세 정보
            NoncurrProgramDTO programDto = adminNoncurrProgramService.getProgramDetail(prgId);
            
            // 2. 신청자 목록 (페이징)
            Pageable pageable = PageRequest.of(page, size);
            Page<ApplicantDTO> applicants = adminNoncurrProgramService.getApplicantList(prgId, pageable);
            List<Core_CptInfo> selectedCompetencies = null;
            /*
            // 3. 핵심역량 정보
            if (programDto.getCompetencyIds() != null && !programDto.getCompetencyIds().isEmpty()) {
                List<Core_CptInfo> allCompetencies = adminNoncurrProgramService.getAllCompetencies();
                selectedCompetencies = allCompetencies.stream()
                    .filter(comp -> programDto.getCompetencyIds().contains(comp.getCclId()))
                    .collect(Collectors.toList());
            }
            */
            // 4. 응답 데이터 구성
            response.put("success", true);
            response.put("program", programDto);
            response.put("applicants", applicants.getContent());
            response.put("competencies", selectedCompetencies);
            
            // 페이징 정보
            Map<String, Object> pagination = new HashMap<>();
            pagination.put("currentPage", page);
            pagination.put("totalPages", applicants.getTotalPages());
            pagination.put("totalElements", applicants.getTotalElements());
            pagination.put("size", size);
            pagination.put("startRecord", page * size + 1);
            pagination.put("endRecord", Math.min((page + 1) * size, (int) applicants.getTotalElements()));
            
            response.put("pagination", pagination);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("통합 상세 정보 조회 실패: ID={}, 오류={}", prgId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "정보 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 이미지 파일 업로드 검증
     */
    @PostMapping("/noncurr_validate_image")
    public ResponseEntity<Map<String, Object>> noncurr_validate_image(@RequestParam(name="imageFile", required = false) MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        System.out.println(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("error", "파일이 선택되지 않았습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 파일 크기 검증 (5MB)
            if (file.getSize() > 5242880) {
                response.put("success", false);
                response.put("error", "파일 크기가 5MB를 초과합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 파일 타입 검증
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("success", false);
                response.put("error", "이미지 파일만 업로드 가능합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            response.put("success", true);
            response.put("message", "파일 검증 완료");
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", file.getSize());
            response.put("fileType", contentType);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("이미지 파일 검증 실패: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "파일 검증 중 오류가 발생했습니다.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 입력값 검증
     */
    private String validateProgramDto(NoncurrProgramDTO dto) {
        if (dto.getPrgNm() == null || dto.getPrgNm().trim().isEmpty()) {
            return "프로그램명은 필수입니다.";
        }
        
        if (dto.getRecruitStDt() == null || dto.getRecruitStDt().trim().isEmpty()) {
            return "모집 시작일은 필수입니다.";
        }
        
        if (dto.getRecruitEndDt() == null || dto.getRecruitEndDt().trim().isEmpty()) {
            return "모집 마감일은 필수입니다.";
        }
        
        if (dto.getPrgStDt() == null || dto.getPrgStDt().trim().isEmpty()) {
            return "운영 시작일은 필수입니다.";
        }
        
        if (dto.getPrgEndDt() == null || dto.getPrgEndDt().trim().isEmpty()) {
            return "운영 종료일은 필수입니다.";
        }
        
        if (dto.getMaxCnt() == null || dto.getMaxCnt() <= 0) {
            return "모집인원은 1명 이상이어야 합니다.";
        }
        
        if (dto.getPrgDept() == null || dto.getPrgDept().trim().isEmpty()) {
            return "운영부서는 필수입니다.";
        }
        
        if (dto.getPrgTel() == null || dto.getPrgTel().trim().isEmpty()) {
            return "문의 전화번호는 필수입니다.";
        }
        
        if (dto.getMlgDefScore() == null || dto.getMlgDefScore() < 0) {
            return "마일리지 점수는 0 이상이어야 합니다.";
        }
        
        if (dto.getSurveyDt() == null || dto.getSurveyDt().trim().isEmpty()) {
            return "만족도조사 마감일은 필수입니다.";
        }
        
        if (dto.getPrgDesc() == null || dto.getPrgDesc().trim().isEmpty()) {
            return "프로그램 설명은 필수입니다.";
        }
        
        if (dto.getCompetencyIds() == null || dto.getCompetencyIds().isEmpty()) {
            return "핵심역량을 하나 이상 선택해주세요.";
        }
        
        // 프로그램명 길이 검증
        if (dto.getPrgNm().length() > 100) {
            return "프로그램명은 100자를 초과할 수 없습니다.";
        }
        
        // 설명 길이 검증  
        if (dto.getPrgDesc().length() > 2000) {
            return "프로그램 설명은 2000자를 초과할 수 없습니다.";
        }
        
        return null; // 검증 통과
    }
    
    
    
    /**
     * 신청자 이수여부/만족도조사 상태 업데이트
     */
    @PostMapping("/noncurr_applicant_status")
    public ResponseEntity<Map<String, Object>> updateApplicantStatus(
            @RequestParam("aplyId") Integer aplyId,
            @RequestParam("type") String type, // "completion" 또는 "survey"
            @RequestParam("status") boolean status) {
        
        log.info("신청자 상태 업데이트 API 요청: 신청ID={}, 타입={}, 상태={}", aplyId, type, status);
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = adminNoncurrProgramService.updateApplicantCompletionStatus(aplyId, type, status);
            
            if (success) {
                response.put("success", true);
                response.put("message", "상태가 성공적으로 업데이트되었습니다.");
            } else {
                response.put("success", false);
                response.put("error", "상태 업데이트에 실패했습니다.");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("신청자 상태 업데이트 실패: 신청ID={}, 오류={}", aplyId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "상태 업데이트 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    
    /**
     * 신청자 통계 조회 API
     */
    @GetMapping("/noncurr_applicant_stats")
    public ResponseEntity<Map<String, Object>> getApplicantStatistics(@RequestParam("prgId") Integer prgId) {
        log.info("신청자 통계 조회 API 요청: 프로그램ID={}", prgId);
        
        try {
            Map<String, Object> statistics = adminNoncurrProgramService.getApplicantStatistics(prgId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("신청자 통계 조회 실패: 프로그램ID={}, 오류={}", prgId, e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "통계 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    
    /**
     * 프로그램 상태 자동 업데이트 API (관리자용)
     */
    @PostMapping("/noncurr_update_status")
    public ResponseEntity<Map<String, Object>> updateProgramStatus() {
        log.info("프로그램 상태 자동 업데이트 API 요청");
        Map<String, Object> response = new HashMap<>();
        
        try {
            adminNoncurrProgramService.updateProgramStatus();
            
            response.put("success", true);
            response.put("message", "프로그램 상태가 성공적으로 업데이트되었습니다.");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("프로그램 상태 업데이트 실패: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "상태 업데이트 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/satisfaction-stats/{prgId}")
    public ResponseEntity<?> getSatisfactionStatistics(@PathVariable("prgId") Integer prgId) {
        log.info("프로그램 만족도 통계 조회 요청: prgId={}", prgId);
        
        try {
            //  실제 만족도 조사 통계 조회
            SatisfactionSurveyDTO statistics = adminNoncurrProgramService.getSatisfactionSurveyStatistics(prgId);
            
            log.info("만족도 조회 성공: 응답자수={}", statistics.getTotalResponders());
            return ResponseEntity.ok(statistics);
            
        } catch (RuntimeException e) {
            log.error("만족도 통계 조회 실패: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.ok().body(errorResponse);
        }
    }
    



    
    
    
}