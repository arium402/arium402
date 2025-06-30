package com.team.arium.admin.noncurr.model;


import com.team.arium.admin.noncurr.dto.NoncurrProgramDTO;
import com.team.arium.admin.noncurr.service.AdminNoncurrProgramService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/noncurr")
@RequiredArgsConstructor
public class NoncurrProgramApiController {
    
    private final AdminNoncurrProgramService programService;
    
    /**
     * 비교과 프로그램 등록
     */
    @PostMapping("/add")
    public ResponseEntity<?> addProgram(
        @ModelAttribute NoncurrProgramDTO dto,
        RedirectAttributes redirectAttributes
    ) {
        try {
            log.info("비교과 프로그램 등록 요청 - 프로그램명: {}, 운영부서: {}", dto.getPrgNm(), dto.getPrgDept());
            
            // 입력값 검증
            if (dto.getPrgNm() == null || dto.getPrgNm().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "프로그램명은 필수입니다."));
            }
            
            if (dto.getPrgDept() == null || dto.getPrgDept().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "운영부서는 필수입니다."));
            }
            
            if (dto.getPrgTel() == null || dto.getPrgTel().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "문의 전화번호는 필수입니다."));
            }
            
            if (dto.getCompetencyIds() == null || dto.getCompetencyIds().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "핵심역량을 하나 이상 선택해주세요."));
            }
            
            // 프로그램 등록
            Integer prgId = programService.createProgram(dto);
            
            // 성공 응답 (Thymeleaf 페이지 리다이렉트)
            redirectAttributes.addFlashAttribute("successMessage", "비교과 프로그램이 성공적으로 등록되었습니다.");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("prgId", prgId);
            response.put("message", "프로그램이 성공적으로 등록되었습니다.");
            response.put("redirectUrl", "/admin/noncurr_list");
            
            return ResponseEntity
                .created(URI.create("/api/admin/noncurr/" + prgId))
                .body(response);
                
        } catch (Exception e) {
            log.error("비교과 프로그램 등록 실패", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "프로그램 등록 중 오류가 발생했습니다.");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * 핵심역량 목록 조회 (AJAX용)
     */
    @GetMapping("/competencies")
    public ResponseEntity<?> getCompetencies() {
        try {
            return ResponseEntity.ok(programService.getAllCompetencies());
        } catch (Exception e) {
            log.error("핵심역량 목록 조회 실패", e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", "핵심역량 목록을 불러오는데 실패했습니다."));
        }
    }
}