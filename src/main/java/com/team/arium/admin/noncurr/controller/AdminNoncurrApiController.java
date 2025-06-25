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
    
    private final NoncurrProgramService noncurrProgramService;
    
    @PostMapping
    public ResponseEntity<?> registerProgram(
        @ModelAttribute NoncurrProgramDTO dto,
        @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
        @RequestParam(name = "attachmentFile", required = false) MultipartFile attachmentFile
    ) {
        log.info("DTO 프로그램명: {}", dto.getPrgNm());
        log.info("대표사진: {}", imageFile != null ? imageFile.getOriginalFilename() : "없음");
        log.info("첨부파일: {}", attachmentFile != null ? attachmentFile.getOriginalFilename() : "없음");

        dto.setImageFile(imageFile);
        dto.setAttachmentFile(attachmentFile);

        // 이후 서비스 로직 호출 (예시)
        Map<String, Object> result = noncurrProgramService.registerProgram(dto);

        return ResponseEntity.ok(result);
    }
    
    /**
     * 비교과 프로그램 목록 조회
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getProgramList(
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "statusFilter", required = false) String statusFilter,
            @RequestParam(value = "periodFilter", required = false) String periodFilter,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        try {
            log.info("프로그램 목록 조회 - 검색어: {}, 상태: {}, 기간: {}, 페이지: {}", 
                    searchKeyword, statusFilter, periodFilter, page);
            
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
            response.put("programs", List.of());
            response.put("totalElements", 0);
            response.put("totalPages", 1);
            response.put("currentPage", 0);
            response.put("size", size);
            
            return ResponseEntity.ok(response); // 목록 조회는 200으로 반환
        }
    }
    
    /**
     * 핵심역량 목록 조회
     */
    @GetMapping("/competencies")
    public ResponseEntity<Map<String, Object>> getCompetencies() {
        try {
            log.info("핵심역량 목록 조회 요청");
            
            List<Map<String, Object>> competencies = noncurrProgramService.getCompetencyList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", competencies);
            
            log.info("핵심역량 {}개 조회 완료", competencies.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("핵심역량 목록 조회 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "핵심역량 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            response.put("data", List.of());
            
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 프로그램 상세 조회
     */
    @GetMapping("/{prgId}")
    public ResponseEntity<Map<String, Object>> getProgramDetail(@PathVariable Integer prgId) {
        try {
            log.info("프로그램 상세 조회 - ID: {}", prgId);
            
            if (prgId == null || prgId <= 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "올바르지 않은 프로그램 ID입니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            Map<String, Object> result = noncurrProgramService.getProgramDetail(prgId);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("프로그램 상세 조회 실패 - ID: {}", prgId, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "상세 조회 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 프로그램 삭제
     */
    @DeleteMapping("/{prgId}")
    public ResponseEntity<Map<String, Object>> deleteProgram(@PathVariable Integer prgId) {
        try {
            log.info("프로그램 삭제 요청 - ID: {}", prgId);
            
            if (prgId == null || prgId <= 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "올바르지 않은 프로그램 ID입니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            Map<String, Object> result = noncurrProgramService.deleteProgram(prgId);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("프로그램 삭제 실패 - ID: {}", prgId, e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "삭제 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 프로그램 코드 생성
     */
    @GetMapping("/generate-code")
    public ResponseEntity<Map<String, Object>> generateProgramCode() {
        try {
            log.info("프로그램 코드 생성 요청");
            
            String programCode = noncurrProgramService.generateProgramCode();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("code", programCode);
            
            log.info("프로그램 코드 생성 완료: {}", programCode);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("프로그램 코드 생성 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "코드 생성 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 프로그램 통계 조회
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getProgramStatistics() {
        try {
            log.info("프로그램 통계 조회 요청");
            
            Map<String, Object> result = noncurrProgramService.getProgramStatistics();
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("프로그램 통계 조회 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "통계 조회 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 핵심역량 ID 파싱 (개선된 버전)
     */
    private List<Integer> parseCompetencyIds(String competencies) {
        try {
            if (competencies == null || competencies.trim().isEmpty()) {
                log.warn("핵심역량 데이터가 비어있음");
                return List.of();
            }
            
            log.debug("원본 핵심역량 문자열: {}", competencies);
            
            // JSON 배열 문자열 정리
            String cleaned = competencies.trim()
                    .replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .replace("'", "");
            
            if (cleaned.trim().isEmpty()) {
                log.warn("정리된 핵심역량 데이터가 비어있음");
                return List.of();
            }
            
            log.debug("정리된 핵심역량 문자열: {}", cleaned);
            
            List<Integer> result = Arrays.stream(cleaned.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .filter(s -> s.matches("\\d+")) // 숫자만 허용
                .map(Integer::parseInt)
                .distinct() // 중복 제거
                .collect(Collectors.toList());
            
            log.info("파싱된 핵심역량 ID 목록: {}", result);
            return result;
                
        } catch (NumberFormatException e) {
            log.error("핵심역량 ID 파싱 실패 - 숫자 형식 오류: {}", competencies, e);
            return List.of();
        } catch (Exception e) {
            log.error("핵심역량 ID 파싱 실패: {}", competencies, e);
            return List.of();
        }
    }
    
    /**
     * JSON으로 비교과 프로그램 등록 (테스트용)
     */
    @PostMapping(value = "/json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> addProgramJson(@RequestBody Map<String, Object> requestData) {
        
        try {
            log.info("JSON 등록 요청: {}", requestData.get("prgNm"));
            
            // JSON에서 데이터 추출
            String prgNm = (String) requestData.get("prgNm");
            String prgDesc = (String) requestData.get("prgDesc");
            String recruitStart = (String) requestData.get("recruitStart");
            String recruitEnd = (String) requestData.get("recruitEnd");
            String prgStDt = (String) requestData.get("prgStDt");
            String prgEndDt = (String) requestData.get("prgEndDt");
            Integer maxCnt = Integer.valueOf(requestData.get("maxCnt").toString());
            String department = (String) requestData.get("department");
            String contact = (String) requestData.get("contact");
            String surveyDt = (String) requestData.get("surveyDt");
            Integer mlgDefScore = Integer.valueOf(requestData.get("mlgDefScore").toString());
            String competencies = (String) requestData.get("competencies");
            
            // 핵심역량 JSON 파싱
            List<Integer> competencyIds = parseCompetencyIds(competencies);
            
            // 프로그램 코드 생성
            String prgCd = noncurrProgramService.generateProgramCode();
            
            // DTO 생성 (파일 없이)
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
                .prgStatCd(51) // 오픈 상태
                .build();
            
            Map<String, Object> result = noncurrProgramService.registerProgram(programDTO);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("JSON 등록 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "등록 실패: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}