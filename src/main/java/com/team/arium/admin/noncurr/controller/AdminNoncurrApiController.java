// 2. 관리자 비교과 API 컨트롤러 (Admin API Controller)
package com.team.arium.admin.noncurr.controller;

import com.team.arium.admin.noncurr.dto.NoncurrProgramDTO;
import com.team.arium.admin.noncurr.service.AdminNoncurrProgramService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminNoncurrApiController {

    private final AdminNoncurrProgramService adminNoncurrProgramService;

    /**
     * 비교과 프로그램 등록
     */
    @PostMapping("/noncurr_add")
    public ResponseEntity<Map<String, Object>> noncurr_add(
            @ModelAttribute NoncurrProgramDTO dto,
            HttpServletRequest req
    		) {
    	
        
        log.info("비교과 프로그램 등록 API 요청: {}", dto.getPrgNm());
        
        try {
            log.info("=== 파일 업로드 디버깅 시작 (수정) ===");
            log.info("전체 파라미터 개수: {}", req.getParameterMap().size());
            log.info("이미지 파일 정보: {}", dto.getImageFile() != null ? dto.getImageFile().getOriginalFilename() : "null");
            
         // ✅ 문자열을 List로 변환 (DTO에서 자동 처리됨)
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
        
        
        
        Map<String, Object> response = new HashMap<>();
        
        try { 

            // 입력값 검증
            String validationError = validateProgramDto(dto);
            if (validationError != null) {
                log.warn("프로그램 등록 검증 실패: {}", validationError);
                response.put("success", false);
                response.put("error", validationError);
                return ResponseEntity.badRequest().body(response);
            }
            
            // 프로그램 등록
            Integer prgId = adminNoncurrProgramService.createProgram(dto);
            
            response.put("success", true);
            response.put("message", "비교과 프로그램이 성공적으로 등록되었습니다.");
            response.put("prgId", prgId);
            response.put("redirectUrl", "/admin/noncurr_list");
            
            log.info("비교과 프로그램 등록 성공: ID={}, 이름={}", prgId, dto.getPrgNm());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("비교과 프로그램 등록 실패: 이름={}, 오류={}", dto.getPrgNm(), e.getMessage(), e);
            response.put("success", false);
            response.put("error", "프로그램 등록 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

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
    public ResponseEntity<Map<String, Object>> noncurr_delete(@RequestParam Integer prgId) {
        log.info("비교과 프로그램 삭제 API 요청: ID={}", prgId);
        Map<String, Object> response = new HashMap<>();
        
        try {
            adminNoncurrProgramService.deleteProgram(prgId);
            
            response.put("success", true);
            response.put("message", "비교과 프로그램이 성공적으로 삭제되었습니다.");
            
            log.info("비교과 프로그램 삭제 성공: ID={}", prgId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("비교과 프로그램 삭제 실패: ID={}, 오류={}", prgId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "프로그램 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 프로그램 상세 정보 조회 (AJAX용)
     */
    @GetMapping("/noncurr_detail_ajax")
    public ResponseEntity<Map<String, Object>> noncurr_detail_ajax(@RequestParam Integer prgId) {
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
     * 이미지 파일 업로드 검증
     */
    @PostMapping("/noncurr_validate_image")
    public ResponseEntity<Map<String, Object>> noncurr_validate_image(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
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
}