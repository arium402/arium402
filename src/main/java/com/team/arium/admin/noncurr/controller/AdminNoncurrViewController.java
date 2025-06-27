// 1. 관리자 비교과 화면 컨트롤러 (Admin View Controller)
package com.team.arium.admin.noncurr.controller;

import com.team.arium.admin.noncurr.dto.NoncurrProgramDTO;
import com.team.arium.admin.noncurr.service.AdminNoncurrProgramService;
import com.team.arium.domain.Core_CptInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminNoncurrViewController {

    private final AdminNoncurrProgramService adminNoncurrProgramService;

    /**
     * 비교과 목록 페이지
     */
    @GetMapping("/noncurr_list")
    public String noncurr_list(
            @RequestParam(name="page", defaultValue = "0") int page,
            @RequestParam(name="size", defaultValue = "10") int size,
            @RequestParam(name="search", required = false) String search,
            Model model) {
        
        log.info("비교과 목록 페이지 요청 - page: {}, size: {}, search: {}", page, size, search);
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("regDt").descending());
            Page<NoncurrProgramDTO> programs = adminNoncurrProgramService.getProgramList(search, pageable);
            
            model.addAttribute("programs", programs);
            model.addAttribute("search", search);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", programs.getTotalPages());
            model.addAttribute("totalElements", programs.getTotalElements());
            
            // 페이징 정보
            model.addAttribute("hasPrevious", programs.hasPrevious());
            model.addAttribute("hasNext", programs.hasNext());
            model.addAttribute("isFirst", programs.isFirst());
            model.addAttribute("isLast", programs.isLast());
            
            return "/admin/admin_noncurr_list";
            
        } catch (Exception e) {
            log.error("비교과 목록 조회 실패: {}", e.getMessage(), e);
            model.addAttribute("error", "목록을 불러오는 중 오류가 발생했습니다.");
            return "/admin/admin_noncurr_list";
        }
    }

    /**
     * 비교과 등록 페이지
     */
    @GetMapping("/noncurr_add")
    public String noncurr_add(Model model) {
        log.info("비교과 등록 폼 페이지 요청");
        
        try {
            // 빈 DTO 객체 생성
            NoncurrProgramDTO formDto = new NoncurrProgramDTO();
            model.addAttribute("formDto", formDto);
            
            // 핵심역량 목록 조회
            List<Core_CptInfo> competencies = adminNoncurrProgramService.getAllCompetencies();
            model.addAttribute("competencies", competencies);
            
            // 등록 모드 플래그
            model.addAttribute("isEditMode", false);
            
            log.info("핵심역량 목록 조회 완료: {} 개", competencies.size());
            
            return "/admin/admin_noncurr_list_add";
            
        } catch (Exception e) {
            log.error("비교과 등록 폼 페이지 로드 실패: {}", e.getMessage(), e);
            model.addAttribute("error", "페이지를 불러오는 중 오류가 발생했습니다.");
            return "redirect:/admin/noncurr_list";
        }
    }

    /**
     * 비교과 상세 페이지
     */
    @GetMapping("/noncurr_detail")
    public String noncurr_detail(
            @RequestParam Integer prgId,
            Model model, 
            RedirectAttributes redirectAttributes) {
        
        log.info("비교과 상세 페이지 요청: ID={}", prgId);
        
        try {
            NoncurrProgramDTO programDto = adminNoncurrProgramService.getProgramDetail(prgId);
            model.addAttribute("program", programDto);
            
            // 핵심역량 정보도 함께 전달
            if (programDto.getCompetencyIds() != null && !programDto.getCompetencyIds().isEmpty()) {
                List<Core_CptInfo> allCompetencies = adminNoncurrProgramService.getAllCompetencies();
                List<Core_CptInfo> selectedCompetencies = allCompetencies.stream()
                    .filter(comp -> programDto.getCompetencyIds().contains(comp.getCclId()))
                    .toList();
                model.addAttribute("selectedCompetencies", selectedCompetencies);
            }
            
            return "/admin/admin_noncurr_list_detail";
            
        } catch (Exception e) {
            log.error("비교과 상세 페이지 로드 실패: ID={}, 오류={}", prgId, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "프로그램 정보를 불러오는 중 오류가 발생했습니다.");
            return "redirect:/admin/noncurr_list";
        }
    }

    /**
     * 비교과 수정 페이지
     */
    @GetMapping("/noncurr_edit")
    public String noncurr_edit(
            @RequestParam Integer prgId,
            Model model, 
            RedirectAttributes redirectAttributes) {
        
        log.info("비교과 수정 폼 페이지 요청: ID={}", prgId);
        
        try {
            // 프로그램 상세 정보 조회
            NoncurrProgramDTO programDto = adminNoncurrProgramService.getProgramDetail(prgId);
            model.addAttribute("formDto", programDto);
            
            // 핵심역량 목록 조회
            List<Core_CptInfo> competencies = adminNoncurrProgramService.getAllCompetencies();
            model.addAttribute("competencies", competencies);
            
            // 수정 모드 플래그
            model.addAttribute("isEditMode", true);
            model.addAttribute("prgId", prgId);
            
            log.info("프로그램 수정 폼 로드 완료: ID={}, 이름={}", prgId, programDto.getPrgNm());
            
            return "/admin/admin_noncurr_list_edit";
            
        } catch (Exception e) {
            log.error("비교과 수정 폼 페이지 로드 실패: ID={}, 오류={}", prgId, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "프로그램 정보를 불러오는 중 오류가 발생했습니다.");
            return "redirect:/admin/noncurr_list";
        }
    }

    /**
     * 비교과 통계 페이지
     */
    @GetMapping("/noncurr_stat")
    public String noncurr_stat(Model model) {
        log.info("비교과 통계 페이지 요청");
        
        try {
            // 통계 데이터 조회 (추후 구현)
            // Map<String, Object> stats = adminNoncurrProgramService.getStatistics();
            // model.addAttribute("stats", stats);
            
            return "/admin/admin_noncurr_stat";
            
        } catch (Exception e) {
            log.error("비교과 통계 페이지 로드 실패: {}", e.getMessage(), e);
            model.addAttribute("error", "통계 데이터를 불러오는 중 오류가 발생했습니다.");
            return "/admin/admin_noncurr_stat";
        }
    }
}