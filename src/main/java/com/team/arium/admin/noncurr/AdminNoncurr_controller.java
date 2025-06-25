package com.team.arium.admin.noncurr;

import com.team.arium.admin.noncurr.service.NoncurrProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminNoncurr_controller {
    
    private final NoncurrProgramService noncurrProgramService;
    
    /**
     * 비교과 목록 페이지
     * @param model 모델
     * @return 비교과 목록 뷰
     */
    @GetMapping("/noncurr_list")
    public String noncurr_list(Model model) {
        try {
            // 페이지 초기화 데이터 전달
            model.addAttribute("pageTitle", "비교과 목록");
            model.addAttribute("currentMenu", "noncurr");
            model.addAttribute("currentSubMenu", "noncurr_list");
            
            log.info("비교과 목록 페이지 접근");
            
        } catch (Exception e) {
            log.error("비교과 목록 페이지 로딩 오류: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "페이지 로딩 중 오류가 발생했습니다.");
        }
        
        return "/admin/admin_noncurr_list";
    }
    
    /**
     * 비교과 등록 페이지
     * @param model 모델
     * @return 비교과 등록 뷰
     */
    @GetMapping("/noncurr_add")
    public String noncurr_add(Model model) {
        try {
            // 프로그램 코드 생성
            String programCode = noncurrProgramService.generateProgramCode();
            
            // 페이지 초기화 데이터 전달
            model.addAttribute("pageTitle", "비교과 등록");
            model.addAttribute("currentMenu", "noncurr");
            model.addAttribute("currentSubMenu", "noncurr_add");
            model.addAttribute("generatedPrgCd", programCode);
            
            log.info("비교과 등록 페이지 접근, 생성된 코드: {}", programCode);
            
        } catch (Exception e) {
            log.error("비교과 등록 페이지 로딩 오류: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "페이지 로딩 중 오류가 발생했습니다.");
            model.addAttribute("generatedPrgCd", "AUTO_GENERATED");
        }
        
        return "/admin/admin_noncurr_list_add";
    }
    
    /**
     * 비교과 상세 페이지
     * @param id 프로그램 ID
     * @param model 모델
     * @return 비교과 상세 뷰
     */
    @GetMapping("/noncurr_detail")
    public String noncurr_detail(@RequestParam(value = "id", required = false) Integer id, Model model) {
        try {
            if (id == null) {
                model.addAttribute("errorMessage", "프로그램 ID가 필요합니다.");
                return "redirect:/admin/noncurr_list";
            }
            
            // 프로그램 상세 정보 조회
            var result = noncurrProgramService.getProgramDetail(id);
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                model.addAttribute("program", result.get("program"));
                model.addAttribute("pageTitle", "비교과 상세");
                model.addAttribute("currentMenu", "noncurr");
                model.addAttribute("currentSubMenu", "noncurr_detail");
                model.addAttribute("prgId", id);
                
                log.info("비교과 상세 페이지 접근: ID={}", id);
            } else {
                model.addAttribute("errorMessage", result.get("message"));
                return "redirect:/admin/noncurr_list";
            }
            
        } catch (Exception e) {
            log.error("비교과 상세 페이지 로딩 오류: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "페이지 로딩 중 오류가 발생했습니다.");
            return "redirect:/admin/noncurr_list";
        }
        
        return "/admin/admin_noncurr_list_detail";
    }
    
    /**
     * 비교과 수정 페이지
     * @param id 프로그램 ID
     * @param model 모델
     * @return 비교과 수정 뷰
     */
    @GetMapping("/noncurr_edit")
    public String noncurr_edit(@RequestParam(value = "id", required = false) Integer id, Model model) {
        try {
            if (id == null) {
                model.addAttribute("errorMessage", "프로그램 ID가 필요합니다.");
                return "redirect:/admin/noncurr_list";
            }
            
            // 프로그램 상세 정보 조회
            var result = noncurrProgramService.getProgramDetail(id);
            
            if (Boolean.TRUE.equals(result.get("success"))) {
                model.addAttribute("program", result.get("program"));
                model.addAttribute("pageTitle", "비교과 수정");
                model.addAttribute("currentMenu", "noncurr");
                model.addAttribute("currentSubMenu", "noncurr_edit");
                model.addAttribute("prgId", id);
                model.addAttribute("isEditMode", true);
                
                log.info("비교과 수정 페이지 접근: ID={}", id);
            } else {
                model.addAttribute("errorMessage", result.get("message"));
                return "redirect:/admin/noncurr_list";
            }
            
        } catch (Exception e) {
            log.error("비교과 수정 페이지 로딩 오류: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "페이지 로딩 중 오류가 발생했습니다.");
            return "redirect:/admin/noncurr_list";
        }
        
        return "/admin/admin_noncurr_list_edit";
    }
    
    /**
     * 비교과 통계 페이지
     * @param model 모델
     * @return 비교과 통계 뷰
     */
    @GetMapping("/noncurr_stat")
    public String noncurr_stat(Model model) {
        try {
            // 통계 데이터 조회
            var statisticsResult = noncurrProgramService.getProgramStatistics();
            
            model.addAttribute("pageTitle", "비교과 통계");
            model.addAttribute("currentMenu", "noncurr");
            model.addAttribute("currentSubMenu", "noncurr_stat");
            
            if (Boolean.TRUE.equals(statisticsResult.get("success"))) {
                model.addAttribute("statistics", statisticsResult.get("statistics"));
            } else {
                model.addAttribute("errorMessage", "통계 데이터 로딩에 실패했습니다.");
            }
            
            log.info("비교과 통계 페이지 접근");
            
        } catch (Exception e) {
            log.error("비교과 통계 페이지 로딩 오류: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "페이지 로딩 중 오류가 발생했습니다.");
        }
        
        return "/admin/admin_noncurr_stat";
    }
}