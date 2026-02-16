package com.team.arium.admin.mileage;


import com.team.arium.admin.mileage.*;
import com.team.arium.admin.noncurr.dto.NoncurrProgramDTO;  // ✅ 기존 DTO 활용
import com.team.arium.admin.noncurr.repository.NcsPrgInfoRepository;
import com.team.arium.domain.Ncs_PrgInfo;
import com.team.arium.admin.noncurr.dto.ApplicantDTO;      // ✅ 기존 DTO 활용
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminMileageApiController {

    private final AdminMileageService adminMileageService;
    private final NcsPrgInfoRepository ncsPrgInfoRepository;
    /**
     * ✅ 기존 NoncurrProgramDTO를 활용한 마일리지 프로그램 목록 조회
     */
    @GetMapping("/mileage_programs")
    public ResponseEntity<Map<String, Object>> getMileagePrograms(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status) {
        
        log.info("마일리지 프로그램 목록 조회 API: 페이지={}, 크기={}, 검색={}, 상태={}", 
                page, size, search, status);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            // ✅ 기존 NoncurrProgramDTO 활용
            Page<NoncurrProgramDTO> programs = adminMileageService.getMileageProgramList(
                    search, status, pageable);
            
            // 페이징 정보
            Map<String, Object> pagination = new HashMap<>();
            pagination.put("currentPage", page);
            pagination.put("totalPages", programs.getTotalPages());
            pagination.put("totalElements", programs.getTotalElements());
            pagination.put("size", size);
            pagination.put("first", programs.isFirst());
            pagination.put("last", programs.isLast());
            pagination.put("hasNext", programs.hasNext());
            pagination.put("hasPrevious", programs.hasPrevious());
            
            response.put("success", true);
            response.put("programs", programs.getContent());
            response.put("pagination", pagination);
            
            log.info("마일리지 프로그램 목록 조회 성공: {} 건", programs.getContent().size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("마일리지 프로그램 목록 조회 실패: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "프로그램 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    
    /**
     * ✅ 기존 ApplicantDTO를 활용한 마일리지 지급 대상자 목록 조회 + 프로그램 정보
     */
    @GetMapping("/mileage_participants/{prgId}")
    public ResponseEntity<Map<String, Object>> getMileageParticipants(
            @PathVariable("prgId") Integer prgId) {
        
        log.info("마일리지 지급 대상자 조회 API: 프로그램ID={}", prgId);
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 1. 프로그램 정보 조회 (Service 통해서)
            Map<String, Object> programInfo = adminMileageService.getProgramBasicInfo(prgId);
            if (programInfo == null) {
                response.put("success", false);
                response.put("error", "프로그램을 찾을 수 없습니다.");
                return ResponseEntity.notFound().build();
            }
            
            // 2. 지급 대상자 목록 조회
            List<ApplicantDTO> participants = adminMileageService.getMileageParticipants(prgId);
            
            // 3. 통계 정보 계산
            int totalCount = participants.size();
            int paidCount = (int) participants.stream()
                .filter(p -> "success".equals(p.getStatusBadgeClass()))
                .count();
            int pendingCount = totalCount - paidCount;
            
            // 프로그램 정보에서 마일리지 점수 가져오기
            Integer mlgDefScore = (Integer) programInfo.get("mlgDefScore");
            if (mlgDefScore == null) mlgDefScore = 0;
            
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalParticipants", totalCount);
            statistics.put("paidCount", paidCount);
            statistics.put("pendingCount", pendingCount);
            statistics.put("mileagePerPerson", mlgDefScore);
            statistics.put("totalMileageAmount", totalCount * mlgDefScore);
            statistics.put("paidMileageAmount", paidCount * mlgDefScore);
            statistics.put("pendingMileageAmount", pendingCount * mlgDefScore);
            
            response.put("success", true);
            response.put("program", programInfo);  // ✅ 프로그램 정보 추가
            response.put("participants", participants);
            response.put("statistics", statistics);
            
            log.info("마일리지 지급 대상자 조회 성공: 프로그램={}, 전체 {} 명, 지급완료 {} 명", 
                    programInfo.get("prgNm"), totalCount, paidCount);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("마일리지 지급 대상자 조회 실패: 프로그램ID={}, 오류={}", prgId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "지급 대상자 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 마일리지 지급 처리 (기존 로직 유지)
     */
    @PostMapping("/mileage_payment")
    public ResponseEntity<Map<String, Object>> processMileagePayment(
            @RequestBody MileagePaymentRequestDTO request) {
        
        log.info("마일리지 지급 처리 API: 프로그램ID={}, 대상자수={}", 
                request.getPrgId(), request.getParticipantIds().size());
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 입력값 검증
            if (request.getPrgId() == null) {
                response.put("success", false);
                response.put("error", "프로그램 ID가 필요합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (request.getParticipantIds() == null || request.getParticipantIds().isEmpty()) {
                response.put("success", false);
                response.put("error", "지급 대상자를 선택해주세요.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 마일리지 지급 처리
            MileagePaymentResultDTO result = adminMileageService.processMileagePayment(request);
            
            response.put("success", result.getSuccess());
            response.put("message", result.getMessage());
            response.put("result", result);
            
            if (result.getSuccess()) {
                log.info("마일리지 지급 처리 성공: {}", result.getMessage());
                return ResponseEntity.ok(response);
            } else {
                log.warn("마일리지 지급 처리 부분 실패: {}", result.getMessage());
                return ResponseEntity.ok(response);
            }
            
        } catch (Exception e) {
            log.error("마일리지 지급 처리 실패: 프로그램ID={}, 오류={}", request.getPrgId(), e.getMessage(), e);
            response.put("success", false);
            response.put("error", "마일리지 지급 처리 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 마일리지 통계 조회
     */
    @GetMapping("/mileage_statistics")
    public ResponseEntity<Map<String, Object>> getMileageStatistics() {
        log.info("마일리지 통계 조회 API");
        Map<String, Object> response = new HashMap<>();
        
        try {
            MileageStatisticsDTO statistics = adminMileageService.getMileageStatistics();
            
            response.put("success", true);
            response.put("statistics", statistics);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("마일리지 통계 조회 실패: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "통계 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * ✅ 마일리지 지급 가능 여부 확인 (ApplicantDTO 활용)
     */
    @PostMapping("/mileage_validate")
    public ResponseEntity<Map<String, Object>> validateMileagePayment(
            @RequestBody MileagePaymentRequestDTO request) {
        
        log.info("마일리지 지급 가능 여부 확인 API: 프로그램ID={}", request.getPrgId());
        Map<String, Object> response = new HashMap<>();
        
        try {
            // ✅ 기존 ApplicantDTO 활용
            List<ApplicantDTO> participants = adminMileageService.getMileageParticipants(request.getPrgId());
            
            // 이미 지급된 대상자 필터링 (statusBadgeClass가 "success"인 경우)
            List<Integer> alreadyPaidIds = participants.stream()
                .filter(p -> "success".equals(p.getStatusBadgeClass()))
                .filter(p -> request.getParticipantIds().contains(p.getCmpId()))
                .map(ApplicantDTO::getCmpId)
                .collect(java.util.stream.Collectors.toList());
            
            // 지급 가능한 대상자 필터링
            List<Integer> validIds = request.getParticipantIds().stream()
                .filter(id -> !alreadyPaidIds.contains(id))
                .collect(java.util.stream.Collectors.toList());
            
            response.put("success", true);
            response.put("validIds", validIds);
            response.put("alreadyPaidIds", alreadyPaidIds);
            response.put("validCount", validIds.size());
            response.put("alreadyPaidCount", alreadyPaidIds.size());
            
            if (!alreadyPaidIds.isEmpty()) {
                response.put("warning", "일부 대상자는 이미 마일리지가 지급되어 제외됩니다.");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("마일리지 지급 검증 실패: 프로그램ID={}, 오류={}", request.getPrgId(), e.getMessage(), e);
            response.put("success", false);
            response.put("error", "지급 검증 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}