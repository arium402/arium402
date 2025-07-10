package com.team.arium.student.noncurr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin/program-completion")
public class ProgramCompletionApiController {
    
    @Autowired
    private ProgramCompletionService programCompletionService;
    
    /**
     * 이수 처리 가능한 프로그램 목록 조회 API
     */
    @GetMapping("/completable-programs")
    public ResponseEntity<Map<String, Object>> getCompletablePrograms() {
        try {
            List<CompletableProgramDTO> programs = programCompletionService.getCompletablePrograms();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("programs", programs);
            response.put("total", programs.size());
            response.put("message", programs.isEmpty() ? "처리 가능한 프로그램이 없습니다." : "조회 완료");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "프로그램 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 특정 프로그램 이수 처리 API
     */
    @PostMapping("/process/{prgId}")
    public ResponseEntity<Map<String, Object>> processProgram(@PathVariable Integer prgId) {
        try {
            System.out.println("=== 프로그램 이수 처리 API 호출 ===");
            System.out.println("프로그램 ID: " + prgId);
            
            CompletionResult result = programCompletionService.processProgramCompletion(prgId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", result.getSuccess());
            response.put("message", result.getMessage());
            response.put("processedCount", result.getProcessedCount());
            response.put("alreadyProcessedCount", result.getAlreadyProcessedCount());
            
            if (result.getSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                response.put("errorDetail", result.getErrorDetail());
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            System.err.println("이수 처리 API 오류: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "이수 처리 중 시스템 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 프로그램 이수 현황 조회 API
     */
    @GetMapping("/status/{prgId}")
    public ResponseEntity<Map<String, Object>> getProgramCompletionStatus(@PathVariable Integer prgId) {
        try {
            // 여기서는 간단히 현재 상태만 반환
            // 필요에 따라 더 상세한 정보를 반환할 수 있음
            List<CompletableProgramDTO> allPrograms = programCompletionService.getCompletablePrograms();
            
            CompletableProgramDTO targetProgram = allPrograms.stream()
                .filter(p -> p.getPrgId().equals(prgId))
                .findFirst()
                .orElse(null);
            
            Map<String, Object> response = new HashMap<>();
            if (targetProgram != null) {
                response.put("success", true);
                response.put("program", targetProgram);
                response.put("message", "상태 조회 완료");
            } else {
                response.put("success", false);
                response.put("message", "해당 프로그램은 이수 처리 대상이 아니거나 존재하지 않습니다.");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "상태 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 일괄 이수 처리 API (종료된 모든 프로그램)
     */
    @PostMapping("/process-all")
    public ResponseEntity<Map<String, Object>> processAllCompletablePrograms() {
        try {
            System.out.println("=== 일괄 이수 처리 시작 ===");
            
            List<CompletableProgramDTO> completablePrograms = programCompletionService.getCompletablePrograms();
            
            if (completablePrograms.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "처리할 프로그램이 없습니다.");
                response.put("processedPrograms", 0);
                return ResponseEntity.ok(response);
            }
            
            int successCount = 0;
            int failCount = 0;
            int totalProcessed = 0;
            
            for (CompletableProgramDTO program : completablePrograms) {
                try {
                    CompletionResult result = programCompletionService.processProgramCompletion(program.getPrgId());
                    if (result.getSuccess()) {
                        successCount++;
                        totalProcessed += result.getProcessedCount();
                        System.out.println("프로그램 " + program.getPrgNm() + " 이수 처리 완료");
                    } else {
                        failCount++;
                        System.err.println("프로그램 " + program.getPrgNm() + " 이수 처리 실패: " + result.getMessage());
                    }
                } catch (Exception e) {
                    failCount++;
                    System.err.println("프로그램 " + program.getPrgNm() + " 처리 중 예외: " + e.getMessage());
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "일괄 이수 처리 완료");
            response.put("totalPrograms", completablePrograms.size());
            response.put("successCount", successCount);
            response.put("failCount", failCount);
            response.put("totalProcessedStudents", totalProcessed);
            
            System.out.println("=== 일괄 이수 처리 완료 ===");
            System.out.println("성공: " + successCount + "개, 실패: " + failCount + "개, 총 처리 학생: " + totalProcessed + "명");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("일괄 이수 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "일괄 이수 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}