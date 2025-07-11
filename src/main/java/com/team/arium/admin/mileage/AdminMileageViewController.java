package com.team.arium.admin.mileage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;


import com.team.arium.admin.admin_module;
import com.team.arium.admin.mileage.AdminMileageService;
import com.team.arium.admin.mileage.MileageStatisticsDTO;
import com.team.arium.admin.noncurr.dto.NoncurrProgramDTO;
import com.team.arium.admin.noncurr.dto.ApplicantDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMileageViewController {

    @Autowired
    private admin_module adminModule;
    private final AdminMileageService adminMileageService;

    /**
     * 마일리지 지급 관리 메인 페이지
     */
    @GetMapping("/admin_mileage_payment")
    public String adminMileagePayment(
            @RequestParam(value = "tab", defaultValue = "all") String tab,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "success", required = false) String success,
            Model model) {
        
        log.info("마일리지 지급 관리 페이지 요청 - 탭: {}, 페이지: {}, 검색: {}", tab, page, search);
        
        try {
            // 1. 기본 페이지 설정
            String currentDate = adminModule.todays_module();
            model.addAttribute("currentDate", currentDate);
            model.addAttribute("currentTab", tab);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("searchKeyword", search);
            
            // 2. 페이지 메타데이터
            model.addAttribute("pageTitle", "마일리지 지급 관리");
            model.addAttribute("pageDescription", "비교과 프로그램 이수 완료자에게 마일리지를 지급하는 페이지입니다.");
            model.addAttribute("breadcrumbItems", createBreadcrumbItems());
            
            // 3. 초기 통계 정보 로딩 (페이지 로딩 시 바로 표시)
            try {
                MileageStatisticsDTO statistics = adminMileageService.getMileageStatistics();
                model.addAttribute("initialStatistics", statistics);
                
                // 요약 정보
                Map<String, Object> summary = createSummaryInfo(statistics);
                model.addAttribute("summary", summary);
                
            } catch (Exception e) {
                log.warn("초기 통계 로딩 실패: {}", e.getMessage());
                model.addAttribute("statisticsError", "통계 정보를 불러올 수 없습니다.");
            }
            
            // 4. 상태 옵션 설정
            model.addAttribute("statusOptions", createStatusOptions());
            model.addAttribute("tabOptions", createTabOptions());
            
            // 5. JavaScript 설정값
            Map<String, Object> jsConfig = createJavaScriptConfig(tab, page, size, search);
            model.addAttribute("jsConfig", jsConfig);
            
            // 6. 알림 메시지 처리
            if (error != null) {
                model.addAttribute("errorMessage", getErrorMessage(error));
            }
            if (success != null) {
                model.addAttribute("successMessage", getSuccessMessage(success));
            }
            
            // 7. 권한 체크 (필요시)
            model.addAttribute("canProcessPayment", true); // 실제로는 권한 체크 로직
            model.addAttribute("canViewStatistics", true);
            
            log.info("마일리지 지급 관리 페이지 로드 완료: 탭={}, 페이지={}", tab, page);
            return "/admin/admin_mileage_payment"; // 기존 템플릿 경로
            
        } catch (Exception e) {
            log.error("마일리지 지급 관리 페이지 로드 실패: {}", e.getMessage(), e);
            model.addAttribute("error", "페이지를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
            return "/admin/admin_error";
        }
    }
    
    /**
     * 마일리지 지급 상세 페이지 (기존 HTML 파일명 기준)
     */
    @GetMapping("/admin_mileage_payment_add")
    public String adminMileagePaymentAdd(
            @RequestParam("prgId") Integer prgId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        log.info("마일리지 지급 상세 페이지 요청: 프로그램ID={}, 페이지={}", prgId, page);
        
        try {
            // 1. 입력값 검증
            if (prgId == null || prgId <= 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "유효하지 않은 프로그램 ID입니다.");
                return "redirect:/admin/admin_mileage_payment";
            }
            
            // 2. 기본 페이지 설정
            model.addAttribute("programId", prgId);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("currentDate", adminModule.todays_module());
            
            // 3. 페이지 메타데이터
            model.addAttribute("pageTitle", "마일리지 지급 상세");
            model.addAttribute("pageDescription", "프로그램별 마일리지 지급 대상자 목록 및 지급 처리");
            model.addAttribute("breadcrumbItems", createDetailBreadcrumbItems("프로그램 상세"));
            
            // 4. JavaScript 설정
            model.addAttribute("jsConfig", createDetailJavaScriptConfig(prgId, page, size));
            
            // 5. API 엔드포인트 설정
            model.addAttribute("apiEndpoints", Map.of(
                "participants", "/api/admin/mileage_participants",
                "payment", "/api/admin/mileage_payment",
                "validate", "/api/admin/mileage_validate"
            ));
            
            log.info("마일리지 지급 상세 페이지 로드 완료: 프로그램ID={}", prgId);
            return "/admin/admin_mileage_payment_add"; // 기존 HTML 파일명과 일치
            
        } catch (Exception e) {
            log.error("마일리지 지급 상세 페이지 로드 실패: 프로그램ID={}, 오류={}", prgId, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "상세 페이지를 불러오는 중 오류가 발생했습니다.");
            return "redirect:/admin/admin_mileage_payment";
        }
    }

    /**
     * 마일리지 지급 통계 페이지
     */
    @GetMapping("/mileage_statistics")
    public String mileageStatistics(
            @RequestParam(value = "period", defaultValue = "6months") String period,
            @RequestParam(value = "view", defaultValue = "summary") String view,
            Model model) {
        
        log.info("마일리지 지급 통계 페이지 요청: 기간={}, 뷰={}", period, view);
        
        try {
            // 1. 통계 데이터 조회
            MileageStatisticsDTO statistics = adminMileageService.getMileageStatistics();
            
            // 2. 차트 데이터 생성
            Map<String, Object> chartData = createChartData(statistics, period);
            
            // 3. 요약 카드 데이터
            Map<String, Object> summaryCards = createSummaryCards(statistics);
            
            // 4. 기간별 옵션
            Map<String, Object> periodOptions = createPeriodOptions();
            
            // 5. 모델에 데이터 추가
            model.addAttribute("statistics", statistics);
            model.addAttribute("chartData", chartData);
            model.addAttribute("summaryCards", summaryCards);
            model.addAttribute("periodOptions", periodOptions);
            model.addAttribute("selectedPeriod", period);
            model.addAttribute("selectedView", view);
            model.addAttribute("currentDate", adminModule.todays_module());
            
            // 6. 페이지 메타데이터
            model.addAttribute("pageTitle", "마일리지 지급 통계");
            model.addAttribute("pageDescription", "마일리지 지급 현황 및 통계 정보");
            model.addAttribute("breadcrumbItems", createStatisticsBreadcrumbItems());
            
            // 7. JavaScript 설정
            model.addAttribute("jsConfig", createStatisticsJavaScriptConfig(period, view));
            
            log.info("마일리지 지급 통계 페이지 로드 완료");
            return "/admin/admin_mileage_statistics";
            
        } catch (Exception e) {
            log.error("마일리지 지급 통계 페이지 로드 실패: {}", e.getMessage(), e);
            model.addAttribute("error", "통계 페이지를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
            return "/admin/admin_error";
        }
    }

    /**
     * 마일리지 지급 성공 페이지
     */
    @GetMapping("/mileage_payment_success")
    public String mileagePaymentSuccess(
            @RequestParam(value = "prgId", required = false) Integer prgId,
            @RequestParam(value = "successCount", required = false) Integer successCount,
            @RequestParam(value = "failCount", required = false) Integer failCount,
            @RequestParam(value = "totalCount", required = false) Integer totalCount,
            @RequestParam(value = "paymentDate", required = false) String paymentDate,
            Model model) {
        
        log.info("마일리지 지급 성공 페이지 요청: 프로그램ID={}, 성공={}, 실패={}", prgId, successCount, failCount);
        
        try {
            // 1. 결과 정보 설정
            model.addAttribute("programId", prgId);
            model.addAttribute("successCount", successCount != null ? successCount : 0);
            model.addAttribute("failCount", failCount != null ? failCount : 0);
            model.addAttribute("totalCount", totalCount != null ? totalCount : 0);
            model.addAttribute("paymentDate", paymentDate != null ? paymentDate : adminModule.todays_module());
            model.addAttribute("currentDate", adminModule.todays_module());
            
            // 2. 성공률 계산
            double successRate = totalCount != null && totalCount > 0 ? 
                (double) (successCount != null ? successCount : 0) / totalCount * 100 : 0;
            model.addAttribute("successRate", String.format("%.1f%%", successRate));
            
            // 3. 프로그램 정보 조회 (가능한 경우)
            if (prgId != null) {
                try {
                    List<ApplicantDTO> participants = adminMileageService.getMileageParticipants(prgId);
                    if (!participants.isEmpty()) {
                        // 프로그램명 추출 (첫 번째 참가자의 프로그램 정보에서)
                        model.addAttribute("programName", "프로그램 ID: " + prgId);
                    }
                } catch (Exception e) {
                    log.warn("프로그램 정보 조회 실패: {}", e.getMessage());
                }
            }
            
            // 4. 결과 메시지 생성
            String resultMessage = generateResultMessage(successCount, failCount, totalCount);
            model.addAttribute("resultMessage", resultMessage);
            
            // 5. 추천 액션
            model.addAttribute("recommendedActions", createRecommendedActions(successCount, failCount));
            
            // 6. 페이지 메타데이터
            model.addAttribute("pageTitle", "마일리지 지급 완료");
            model.addAttribute("pageDescription", "마일리지 지급 처리 결과");
            
            return "/admin/mileage_payment_success";
            
        } catch (Exception e) {
            log.error("마일리지 지급 성공 페이지 로드 실패: {}", e.getMessage(), e);
            return "redirect:/admin/admin_mileage_payment?error=success_page_error";
        }
    }

    // ========== 유틸리티 메서드들 ==========

    /**
     * 브레드크럼 아이템 생성
     */
    private List<Map<String, String>> createBreadcrumbItems() {
        return List.of(
            Map.of("name", "관리자", "url", "/admin"),
            Map.of("name", "비교과 관리", "url", "/admin/noncurr_list"),
            Map.of("name", "마일리지 지급", "url", "/admin/admin_mileage_payment")
        );
    }

    private List<Map<String, String>> createDetailBreadcrumbItems(String programName) {
        return List.of(
            Map.of("name", "관리자", "url", "/admin"),
            Map.of("name", "마일리지 지급", "url", "/admin/admin_mileage_payment"),
            Map.of("name", programName, "url", "#")
        );
    }

    private List<Map<String, String>> createStatisticsBreadcrumbItems() {
        return List.of(
            Map.of("name", "관리자", "url", "/admin"),
            Map.of("name", "마일리지 지급", "url", "/admin/admin_mileage_payment"),
            Map.of("name", "통계", "url", "/admin/mileage_statistics")
        );
    }

    /**
     * 요약 정보 생성
     */
    private Map<String, Object> createSummaryInfo(MileageStatisticsDTO statistics) {
        return Map.of(
            "totalPrograms", statistics.getTotalPrograms(),
            "pendingPrograms", statistics.getPendingPrograms(),
            "completedPrograms", statistics.getCompletedPrograms(),
            "totalMileagePaid", statistics.getTotalMileagePaid(),
            "completionRate", statistics.getTotalPrograms() > 0 ? 
                String.format("%.1f%%", (double) statistics.getCompletedPrograms() / statistics.getTotalPrograms() * 100) : "0.0%"
        );
    }

    /**
     * 상태 옵션 생성
     */
    private List<Map<String, String>> createStatusOptions() {
        return List.of(
            Map.of("value", "all", "label", "전체"),
            Map.of("value", "waiting", "label", "지급대기"),
            Map.of("value", "completed", "label", "지급완료")
        );
    }

    /**
     * 탭 옵션 생성
     */
    private List<Map<String, String>> createTabOptions() {
        return List.of(
            Map.of("value", "all", "label", "전체", "description", "모든 마일리지 지급 대상 프로그램"),
            Map.of("value", "waiting", "label", "대기", "description", "마일리지 지급 대기 중인 프로그램"),
            Map.of("value", "completed", "label", "완료", "description", "마일리지 지급이 완료된 프로그램")
        );
    }

    /**
     * JavaScript 설정 생성
     */
    private Map<String, Object> createJavaScriptConfig(String tab, int page, int size, String search) {
        return Map.of(
            "currentTab", tab,
            "currentPage", page,
            "pageSize", size,
            "searchKeyword", search != null ? search : "",
            "apiEndpoints", Map.of(
                "programs", "/api/admin/mileage_programs",
                "participants", "/api/admin/mileage_participants",
                "payment", "/api/admin/mileage_payment",
                "statistics", "/api/admin/mileage_statistics"
            ),
            "refreshInterval", 30000 // 30초마다 자동 새로고침
        );
    }

    private Map<String, Object> createDetailJavaScriptConfig(Integer prgId, int page, int size) {
        return Map.of(
            "programId", prgId,
            "currentPage", page,
            "pageSize", size,
            "maxSelectable", 50, // 한 번에 선택 가능한 최대 인원
            "confirmationRequired", true,
            "apiEndpoints", Map.of(
                "participants", "/api/admin/mileage_participants/" + prgId,
                "payment", "/api/admin/mileage_payment",
                "validate", "/api/admin/mileage_validate"
            )
        );
    }

    private Map<String, Object> createStatisticsJavaScriptConfig(String period, String view) {
        return Map.of(
            "period", period,
            "view", view,
            "chartOptions", Map.of(
                "responsive", true,
                "plugins", Map.of(
                    "legend", Map.of("display", true),
                    "tooltip", Map.of("enabled", true)
                )
            ),
            "refreshInterval", 60000 // 1분마다 새로고침
        );
    }

    /**
     * 상세 통계 계산
     */
    private Map<String, Object> calculateDetailStatistics(List<ApplicantDTO> participants, NoncurrProgramDTO program) {
        int total = participants.size();
        int paid = (int) participants.stream().filter(p -> "success".equals(p.getStatusBadgeClass())).count();
        int pending = total - paid;
        
        return Map.of(
            "totalParticipants", total,
            "paidCount", paid,
            "pendingCount", pending,
            "paymentRate", total > 0 ? String.format("%.1f%%", (double) paid / total * 100) : "0.0%",
            "mileagePerPerson", program.getMlgDefScore(),
            "totalMileageAmount", total * program.getMlgDefScore(),
            "paidMileageAmount", paid * program.getMlgDefScore(),
            "pendingMileageAmount", pending * program.getMlgDefScore()
        );
    }

    /**
     * 페이징 정보 생성
     */
    private Map<String, Object> createPaginationInfo(int page, int size, int total, int totalPages) {
        return Map.of(
            "currentPage", page,
            "pageSize", size,
            "totalElements", total,
            "totalPages", totalPages,
            "startRecord", total > 0 ? (page - 1) * size + 1 : 0,
            "endRecord", Math.min(page * size, total),
            "hasPrevious", page > 1,
            "hasNext", page < totalPages,
            "isFirst", page == 1,
            "isLast", page == totalPages
        );
    }

    /**
     * 지급 처리 가능 여부 확인
     */
    private boolean canProcessPayment(Map<String, Object> statistics) {
        Integer pendingCount = (Integer) statistics.get("pendingCount");
        return pendingCount != null && pendingCount > 0;
    }

    /**
     * 미지급자 존재 여부 확인
     */
    private boolean hasUnpaidParticipants(List<ApplicantDTO> participants) {
        return participants.stream().anyMatch(p -> !"success".equals(p.getStatusBadgeClass()));
    }

    /**
     * 에러 메시지 생성
     */
    private String getErrorMessage(String error) {
        switch (error) {
            case "invalid_program": return "유효하지 않은 프로그램입니다.";
            case "payment_failed": return "마일리지 지급 처리에 실패했습니다.";
            case "no_participants": return "지급 대상자가 없습니다.";
            case "access_denied": return "접근 권한이 없습니다.";
            default: return "알 수 없는 오류가 발생했습니다.";
        }
    }

    /**
     * 성공 메시지 생성
     */
    private String getSuccessMessage(String success) {
        switch (success) {
            case "payment_completed": return "마일리지 지급이 성공적으로 완료되었습니다.";
            case "data_updated": return "데이터가 성공적으로 업데이트되었습니다.";
            default: return "작업이 성공적으로 완료되었습니다.";
        }
    }

    /**
     * 결과 메시지 생성
     */
    private String generateResultMessage(Integer successCount, Integer failCount, Integer totalCount) {
        if (totalCount == null || totalCount == 0) {
            return "처리할 대상자가 없습니다.";
        }
        
        if (failCount == null || failCount == 0) {
            return String.format("총 %d명의 마일리지 지급이 모두 성공적으로 완료되었습니다.", successCount);
        } else {
            return String.format("총 %d명 중 %d명 성공, %d명 실패했습니다.", totalCount, successCount, failCount);
        }
    }

    /**
     * 추천 액션 생성
     */
    private List<Map<String, String>> createRecommendedActions(Integer successCount, Integer failCount) {
        List<Map<String, String>> actions = List.of(
            Map.of("label", "목록으로 돌아가기", "url", "/admin/admin_mileage_payment", "type", "primary"),
            Map.of("label", "통계 보기", "url", "/admin/mileage_statistics", "type", "secondary")
        );
        
        if (failCount != null && failCount > 0) {
            // 실패가 있는 경우 추가 액션 제공
            return List.of(
                Map.of("label", "실패 내역 확인", "url", "#", "type", "warning"),
                Map.of("label", "재시도", "url", "#", "type", "danger"),
                Map.of("label", "목록으로 돌아가기", "url", "/admin/admin_mileage_payment", "type", "primary")
            );
        }
        
        return actions;
    }

    /**
     * 차트 데이터 생성
     */
    private Map<String, Object> createChartData(MileageStatisticsDTO statistics, String period) {
        // 실제로는 기간에 따른 데이터 처리
        return Map.of(
            "monthlyData", statistics.getMonthlyStats() != null ? statistics.getMonthlyStats() : List.of(),
            "summaryData", Map.of(
                "completed", statistics.getCompletedPrograms(),
                "pending", statistics.getPendingPrograms(),
                "total", statistics.getTotalPrograms()
            )
        );
    }

    /**
     * 요약 카드 데이터 생성
     */
    private Map<String, Object> createSummaryCards(MileageStatisticsDTO statistics) {
        return Map.of(
            "totalPrograms", Map.of(
                "value", statistics.getTotalPrograms(),
                "label", "전체 프로그램",
                "icon", "fas fa-list",
                "color", "primary"
            ),
            "completedPrograms", Map.of(
                "value", statistics.getCompletedPrograms(),
                "label", "지급 완료",
                "icon", "fas fa-check-circle",
                "color", "success"
            ),
            "pendingPrograms", Map.of(
                "value", statistics.getPendingPrograms(),
                "label", "지급 대기",
                "icon", "fas fa-clock",
                "color", "warning"
            ),
            "totalMileage", Map.of(
                "value", statistics.getTotalMileagePaid(),
                "label", "총 지급 마일리지",
                "icon", "fas fa-coins",
                "color", "info"
            )
        );
    }

    /**
     * 기간 옵션 생성
     */
    private Map<String, Object> createPeriodOptions() {
        return Map.of(
            "options", List.of(
                Map.of("value", "1month", "label", "최근 1개월"),
                Map.of("value", "3months", "label", "최근 3개월"),
                Map.of("value", "6months", "label", "최근 6개월"),
                Map.of("value", "1year", "label", "최근 1년")
            )
        );
    }
}