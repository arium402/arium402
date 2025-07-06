package com.team.arium.student.noncurr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/student/noncurr")
public class StudentNoncurrApiController {
    
    @Autowired
    private StudentNoncurrService studentNoncurrService;

    
    /**
     * 프로그램 신청 API
     */
    @PostMapping("/apply")
    public ResponseEntity<Map<String, Object>> applyProgram(
            @RequestParam(value = "prgId") Integer prgId) { 
        try {
            Integer stdId = 1; 
            
            boolean success = studentNoncurrService.applyProgram(prgId, stdId);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "신청이 완료되었습니다.");
                
                // 업데이트된 프로그램 정보도 함께 반환
                ProgramListDTO updatedProgram = studentNoncurrService.getProgramDetail(prgId, stdId);
                response.put("program", updatedProgram);
            } else {
                response.put("success", false);
                response.put("message", "신청에 실패했습니다.");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 핵심역량 차트 데이터 조회 API
     */
    @GetMapping("/competency/{prgId}")
    public ResponseEntity<Map<String, Object>> getCompetencyData(@PathVariable("prgId") Integer prgId) {
        try {
            Integer stdId = 1; // 현재 하드코딩된 학생 ID
            
            CompetencyChartDTO competencyData = studentNoncurrService.getCompetencyChartData(prgId, stdId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", competencyData);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 프로그램 신청 취소 API
     */
    @DeleteMapping("/cancel-application/{prgId}")
    public ResponseEntity<Map<String, Object>> cancelApplicationNew(@PathVariable("prgId") Integer prgId) {
        try {
            Integer stdId = 1; // 현재 하드코딩된 학생 ID
            
            boolean success = studentNoncurrService.cancelApplication(prgId, stdId);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "신청이 취소되었습니다.");
                
                // 업데이트된 프로그램 정보도 함께 반환
                ProgramListDTO updatedProgram = studentNoncurrService.getProgramDetail(prgId, stdId);
                response.put("program", updatedProgram);
            } else {
                response.put("success", false);
                response.put("message", "취소에 실패했습니다.");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    
    /**
     * 프로그램 상세 정보 API
     */
   @GetMapping("/detail/{prgId}")
   public ResponseEntity<Map<String, Object>> getProgramDetail(@PathVariable Integer prgId) {
        try {
            Integer stdId = 1; 
            
            ProgramListDTO program = studentNoncurrService.getProgramDetail(prgId, stdId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("program", program);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 내 신청 내역 API
     */
    @GetMapping("/my-applications")
    public ResponseEntity<Map<String, Object>> getMyApplications() {
        try {
            Integer stdId = 1; 
            
            List<ProgramListDTO> applications = studentNoncurrService.getMyApplications(stdId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("applications", applications);
            response.put("total", applications.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 신청 취소 API
     */
    @DeleteMapping("/cancel/{prgId}")
    public ResponseEntity<Map<String, Object>> cancelApplication(@PathVariable Integer prgId) {
        try {
            Integer stdId = 1; 
            
            boolean success = studentNoncurrService.cancelApplication(prgId, stdId);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "신청이 취소되었습니다.");
            } else {
                response.put("success", false);
                response.put("message", "취소에 실패했습니다.");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchPrograms(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "dept", required = false) String dept,
            @RequestParam(value = "mileageFilter", required = false) String mileageFilter,
            @RequestParam(value = "statusFilter", required = false) String statusFilter,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "8") int size) {
        
        try {
            Integer stdId = 1;
            
            PagedProgramResponseDTO result = studentNoncurrService.searchProgramsWithPaging(
                keyword, dept, mileageFilter, statusFilter, sortBy, stdId, page, size);
            
            // 🔍 상세 로깅
            System.out.println("=== API 응답 데이터 상세 ===");
            result.getPrograms().forEach(program -> {
                System.out.println("Program: " + program.getPrgNm());
                System.out.println("  - dDayText: '" + program.getDDayText() + "'");
                System.out.println("  - dDay: " + program.getDDay());
                System.out.println("  - programStatus: " + program.getProgramStatus());
                System.out.println("  - applicationStatus: " + program.getApplicationStatus());
                System.out.println("================");
            });
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // 스택 트레이스도 출력
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
}