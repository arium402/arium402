package com.team.arium.admin.noncurr.controller;

import com.team.arium.admin.noncurr.dto.*;
import com.team.arium.admin.noncurr.service.NoncurrService;
import com.team.arium.competence.StudentCompetenceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    
    private final NoncurrService noncurrService;
    
    /**
     * 비교과 프로그램 등록 (등록 페이지용)
     */
    @PostMapping("/add")
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
            
            // 핵심역량 점수 파싱
            List<Integer> scores = null;
            if (competencyScores != null && !competencyScores.trim().isEmpty()) {
                scores = Arrays.stream(competencyScores.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            }
            
            // DTO 생성 (DB 컬럼명 기준)
            NoncurrAddRequestDto requestDto = NoncurrAddRequestDto.builder()
                .prgNm(prgNm)
                .prgDesc(prgDesc)
                .recruitStart(recruitStart)
                .recruitEnd(recruitEnd)
                .prgStDt(prgStDt)
                .prgEndDt(prgEndDt)
                .maxCnt(maxCnt)
                .department(department)
                .contact(contact)
                .surveyDt(surveyDt)
                .mlgDefScore(mlgDefScore)
                .selectedCompetencyIds(competencyIds)
                .competencyScores(scores)
                .programImage(programImage)
                .attachmentFile(attachmentFile)
                .build();
            
            Integer programId = noncurrService.addProgram(requestDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "비교과 프로그램이 성공적으로 등록되었습니다.");
            response.put("programId", programId);
            
            return ResponseEntity.ok(response);
            
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
            @RequestParam(value = "statusCode", required = false) Integer statusCode,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "regDt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<NoncurrListResponseDto> programs = noncurrService.getProgramList(
                searchKeyword, department, statusCode, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("content", programs.getContent());
            response.put("currentPage", programs.getNumber());
            response.put("totalPages", programs.getTotalPages());
            response.put("totalElements", programs.getTotalElements());
            response.put("size", programs.getSize());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("비교과 프로그램 목록 조회 실패", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 핵심역량 목록 조회 (등록 페이지용 - 기존 StudentCompetenceDTO 사용)
     */
    @GetMapping("/competencies")
    public ResponseEntity<Map<String, Object>> getCompetencies() {
        try {
            List<StudentCompetenceDTO> competencies = noncurrService.getActiveCompetencies();
            
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
}