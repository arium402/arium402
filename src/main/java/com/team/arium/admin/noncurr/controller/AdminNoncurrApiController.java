package com.team.arium.admin.noncurr.controller;

import com.team.arium.admin.noncurr.dto.NoncurrProgramDTO;
import com.team.arium.admin.noncurr.service.NoncurrProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/noncurr")
@RequiredArgsConstructor
@Slf4j
public class AdminNoncurrApiController {
    
    private final NoncurrProgramService noncurrProgramService; // 기존 서비스 사용
    
    /**
     * 비교과 프로그램 등록 (등록 페이지용)
     */
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> addProgram(
            @RequestParam(value = "prgNm") String prgNm,
            @RequestParam(value = "prgDesc") String prgDesc,
            @RequestParam(value = "recruitStart") String recruitStart,
            @RequestParam(value = "recruitEnd") String recruitEnd,
            @RequestParam(value = "prgStDt") String prgStDt,
            @RequestParam(value = "prgEndDt") String prgEndDt,
            @RequestParam(value = "maxCnt") Integer maxCnt,
            @RequestParam(value = "department") String department,
            @RequestParam(value = "contact") String contact,
            @RequestParam(value = "surveyDt") String surveyDt,
            @RequestParam(value = "mlgDefScore") Integer mlgDefScore,
            @RequestParam(value = "selectedCompetencies") String selectedCompetencies, // "1,2,3" 형태
            @RequestParam(value = "competencyScores", required = false) String competencyScores, // "100,120,80" 형태
            @RequestParam(value = "programImage", required = false) MultipartFile programImage,
            @RequestParam(value = "attachmentFile", required = false) MultipartFile attachmentFile) {
        
        try {
            // 핵심역량 ID 파싱
            List<Integer> competencyIds = Arrays.stream(selectedCompetencies.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
            
            // 프로그램 코드 생성
            String prgCd = noncurrProgramService.generateProgramCode();
            
            // DTO 생성 (기존 NoncurrProgramDTO 사용)
            NoncurrProgramDTO programDTO = NoncurrProgramDTO.builder()
                .prgCd(prgCd)
                .prgNm(prgNm)
                .prgDesc(prgDesc)
                .recruitStartDt(recruitStart)
                .recruitEndDt(recruitEnd)
                .prgStDt(prgStDt)
                .prgEndDt(prgEndDt)
                .maxCnt(maxCnt)
                .deptCd(department)
                .contactTel(contact)
                .surveyDt(surveyDt)
                .mlgDefScore(mlgDefScore)
                .competencyIds(competencyIds)
                .imageFile(programImage)
                .attachmentFile(attachmentFile)
                .prgStatCd(1) // 기본 상태: 오픈
                .build();
            
            Map<String, Object> result = noncurrProgramService.registerProgram(programDTO);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("비교과 프로그램 등록 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "프로그램 등록 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 비교과 프로그램 목록 조회 (목록 페이지용)
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getProgramList(
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "statusCode", required = false) String statusFilter,
            @RequestParam(value = "periodFilter", required = false) String periodFilter,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        try {
            NoncurrProgramDTO searchDTO = NoncurrProgramDTO.builder()
                .searchKeyword(searchKeyword)
                .statusFilter(statusFilter)
                .periodFilter(periodFilter)
                .page(page)
                .size(size)
                .build();
            
            Map<String, Object> result = noncurrProgramService.getProgramList(searchDTO);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("비교과 프로그램 목록 조회 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 핵심역량 목록 조회 (등록 페이지용 - 간단한 형태)
     */
    @GetMapping("/competencies")
    public ResponseEntity<Map<String, Object>> getCompetencies() {
        try {
            List<Map<String, Object>> competencies = noncurrProgramService.getCompetencyList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", competencies);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("핵심역량 목록 조회 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "핵심역량 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 프로그램 상세 조회
     */
    @GetMapping("/{prgId}")
    public ResponseEntity<Map<String, Object>> getProgramDetail(@PathVariable Integer prgId) {
        try {
            Map<String, Object> result = noncurrProgramService.getProgramDetail(prgId);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("프로그램 상세 조회 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "상세 조회 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 프로그램 삭제
     */
    @DeleteMapping("/{prgId}")
    public ResponseEntity<Map<String, Object>> deleteProgram(@PathVariable Integer prgId) {
        try {
            Map<String, Object> result = noncurrProgramService.deleteProgram(prgId);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("프로그램 삭제 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "삭제 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 프로그램 코드 생성
     */
    @GetMapping("/generate-code")
    public ResponseEntity<Map<String, Object>> generateProgramCode() {
        try {
            String programCode = noncurrProgramService.generateProgramCode();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("code", programCode);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("프로그램 코드 생성 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "코드 생성 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 프로그램 통계 조회
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getProgramStatistics() {
        try {
            Map<String, Object> result = noncurrProgramService.getProgramStatistics();
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("프로그램 통계 조회 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "통계 조회 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}