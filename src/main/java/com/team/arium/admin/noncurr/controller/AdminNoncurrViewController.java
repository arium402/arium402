// 1. 관리자 비교과 화면 컨트롤러 (Admin View Controller)
package com.team.arium.admin.noncurr.controller;

import com.team.arium.admin.admin_module;
import com.team.arium.admin.noncurr.dto.ApplicantDTO;
import com.team.arium.admin.noncurr.dto.NoncurrProgramDTO;
import com.team.arium.admin.noncurr.service.AdminNoncurrProgramService;
import com.team.arium.domain.Common_Code;
import com.team.arium.domain.Core_CptInfo;
import com.team.arium.domain.Ncs_CclRel;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminNoncurrViewController {

	@Autowired
	admin_module adminModule;
    private final AdminNoncurrProgramService adminNoncurrProgramService;
    
    /**
     * 비교과 목록 페이지
     */
    @GetMapping("/noncurr_list")
    public String noncurr_list(
            @RequestParam(name="page", defaultValue = "0") int page,
            @RequestParam(name="size", defaultValue = "10") int size,
            @RequestParam(name="search", required = false) String search,
            @RequestParam(name="period", required = false) String period,    // ← 추가
            @RequestParam(name="status", required = false) String status,    // ← 추가
            @RequestParam(name="searchType", required = false) String searchType, // ← 추가
            Model model) {
        
        log.info("비교과 목록 페이지 요청 - page: {}, size: {}, search: {}, period: {}, status: {}, searchType: {}", 
                page, size, search, period, status, searchType);
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("regDt").descending());
            Page<NoncurrProgramDTO> programs = adminNoncurrProgramService.getProgramList(
                    search, period, status, searchType, pageable);
            
            model.addAttribute("programs", programs);
            model.addAttribute("search", search);
            model.addAttribute("period", period);      // ← 추가
            model.addAttribute("status", status);      // ← 추가  
            model.addAttribute("searchType", searchType); // ← 추가
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
     * 비교과 프로그램 등록 처리
     */
    @PostMapping("/noncurr_add")
    public String noncurr_add_post(
            @ModelAttribute NoncurrProgramDTO dto,
            HttpServletRequest req,
            RedirectAttributes redirectAttributes,
            Model model
    		) {
        
        log.info("비교과 프로그램 등록 처리: {}", dto.getPrgNm());
        
        try {
            // Common_Code 객체 생성해서 상태 코드 설정
            Common_Code statusCode = new Common_Code();
            statusCode.setCodeId(51);
            statusCode.setCodeType("prg_stat_cd");
            statusCode.setCode("오픈");
            statusCode.setCodeDesc("프로그램 신청 오픈");
            dto.setPrgStatCd(statusCode);
            
            // 프로그램 등록
            Integer prgId = adminNoncurrProgramService.createProgram(dto);
            
            log.info("비교과 프로그램 등록 성공: ID={}, 이름={}", prgId, dto.getPrgNm());
            
            // 성공 시 성공 페이지로 리다이렉트
            return "redirect:/admin/noncurr_success";
            
        } catch (RuntimeException e) {
            log.error("비교과 프로그램 등록 실패: 이름={}, 오류={}", dto.getPrgNm(), e.getMessage(), e);
            
            // ✅ 중복 오류인지 확인 (메시지와 원인 모두 확인)
            String errorMessage;
            String fullMessage = e.getMessage();
            
            // 원인(cause)이 있다면 그 메시지도 확인
            if (e.getCause() != null && e.getCause().getMessage() != null) {
                fullMessage += " | 원인: " + e.getCause().getMessage();
            }
            
            if (fullMessage.contains("이미 등록된 프로그램명입니다")) {
                errorMessage = "이미 등록된 프로그램명입니다. 다른 이름을 사용해주세요.";
            } else {
                errorMessage = "프로그램 등록 중 오류가 발생했습니다: " + e.getMessage();
            }
            
            // ✅ 에러 메시지와 함께 등록 페이지로 돌아가기
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("formDto", dto); // 입력했던 데이터 유지
            
            // 핵심역량 목록 다시 조회
            try {
                List<Core_CptInfo> competencies = adminNoncurrProgramService.getAllCompetencies();
                model.addAttribute("competencies", competencies);
            } catch (Exception ex) {
                log.error("핵심역량 조회 실패: {}", ex.getMessage());
            }
            
            return "/admin/admin_noncurr_list_add"; // 등록 페이지로 직접 반환
            
        } catch (Exception e) {
            log.error("비교과 프로그램 등록 예상치 못한 오류: 이름={}, 오류={}", dto.getPrgNm(), e.getMessage(), e);
            
            model.addAttribute("errorMessage", "시스템 오류가 발생했습니다. 관리자에게 문의하세요.");
            model.addAttribute("formDto", dto);
            
            try {
                List<Core_CptInfo> competencies = adminNoncurrProgramService.getAllCompetencies();
                model.addAttribute("competencies", competencies);
            } catch (Exception ex) {
                log.error("핵심역량 조회 실패: {}", ex.getMessage());
            }
            
            return "/admin/admin_noncurr_list_add";
        }
    }
    
    
    /**
     * 비교과 프로그램 등록 성공 페이지
     */
    @GetMapping("/noncurr_success")
    public String noncurr_success() {
        return "/admin/noncurr_success"; // 성공 페이지 템플릿 반환
    }

    /**
     * 비교과 상세 페이지 (HTML 렌더링)
     */
    @GetMapping("/noncurr_detail")
    public String noncurrDetail(
            @RequestParam("id") Integer prgId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
        
        log.info("비교과 상세 페이지 요청: 프로그램ID={}, 페이지={}", prgId, page);
        
        try {
            // 1. 프로그램 상세 정보 조회
            NoncurrProgramDTO program = adminNoncurrProgramService.getProgramDetail(prgId);
            
            // 2. 신청자 목록 조회 (페이징)
            Pageable pageable = PageRequest.of(page - 1, size); // 페이지는 1부터 시작하므로 -1
            Page<ApplicantDTO> applicantPage = adminNoncurrProgramService.getApplicantList(prgId, pageable);
            
            // 3. 핵심역량 정보 조회
            List<Core_CptInfo> allCompetencies = adminNoncurrProgramService.getAllCompetencies();
            // 핵심역량 이름만 조합 (기존)
            String competencyNames = getCompetencyNames(program.getCompetencyIds(), allCompetencies);
            
            // ✅ 핵심역량 이름과 점수를 함께 조합 (새로 추가)
            String competencyNamesWithScores = getCompetencyNamesWithScores(prgId, allCompetencies);

            // 4. 신청자 통계 조회
            Map<String, Object> statistics = adminNoncurrProgramService.getApplicantStatistics(prgId);
            
            // 5. 삭제 가능 여부 판단
            boolean canDelete = canDeleteProgram(program, applicantPage.getTotalElements());
            
            // 6. 페이징 정보 계산
            int totalPages = applicantPage.getTotalPages();
            long totalElements = applicantPage.getTotalElements();
            int startPage = Math.max(1, page - 2);
            int endPage = Math.min(totalPages, page + 2);
            int startRecord = (page - 1) * size + 1;
            int endRecord = Math.min(page * size, (int) totalElements);
            
            // 6. 모델에 데이터 추가
            model.addAttribute("program", program);
            model.addAttribute("applicantList", applicantPage.getContent());
            model.addAttribute("applicantCount", totalElements);
            model.addAttribute("competencyNames", competencyNames);
            model.addAttribute("competencyNamesWithScores", competencyNamesWithScores);  // ✅ 새로 추가 (점수 포함)            
            model.addAttribute("statistics", statistics);
            model.addAttribute("canDelete", canDelete);  // ✅ 삭제 가능 여부 추가
            
            // 페이징 정보
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalCount", totalElements);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            model.addAttribute("startRecord", startRecord);
            model.addAttribute("endRecord", endRecord);
            model.addAttribute("size", size);
            
            // 페이징 네비게이션 정보
            model.addAttribute("hasPrevious", applicantPage.hasPrevious());
            model.addAttribute("hasNext", applicantPage.hasNext());
            model.addAttribute("isFirst", applicantPage.isFirst());
            model.addAttribute("isLast", applicantPage.isLast());
            
            // 프로그램 ID (JavaScript에서 사용)
            model.addAttribute("programId", prgId);
            
            log.info("비교과 상세 페이지 데이터 준비 완료: 프로그램={}, 신청자={}, 페이지={}/{}, 삭제가능={}", 
                    program.getPrgNm(), totalElements, page, totalPages, canDelete);
            
            return "/admin/admin_noncurr_list_detail";
            
        } catch (Exception e) {
            log.error("비교과 상세 페이지 오류: 프로그램ID={}, 오류={}", prgId, e.getMessage(), e);
            model.addAttribute("error", "프로그램 정보를 불러오는 중 오류가 발생했습니다.");
            return "admin/admin_error";
        }
    }
    
    /**
     * ✅ 프로그램 삭제 가능 여부 판단
     * 조건: 1) 만족도 조사 기간까지 모두 끝났거나 2) 신청자가 없는 경우
     */
    private boolean canDeleteProgram(NoncurrProgramDTO program, long applicantCount) {
        try {
            // admin_module로 현재 한국 날짜 가져오기
            String today = adminModule.todays_module(); // "2025-06-30" 형태
            
            log.info("삭제 가능 여부 판단: 프로그램ID={}, 오늘날짜={}, 신청자수={}", 
                    program.getPrgId(), today, applicantCount);
            
            // 조건 1: 신청자가 없는 경우 (언제든 삭제 가능)
            if (applicantCount == 0) {
                log.info("삭제 가능: 신청자가 없음 (프로그램ID={})", program.getPrgId());
                return true;
            }
            
            // 조건 2: 만족도 조사까지 모두 끝난 경우
            if (program.getSurveyDt() != null && !program.getSurveyDt().trim().isEmpty()) {
                // 만족도 조사 마감일에서 날짜 부분만 추출 (YYYY-MM-DD)
                String surveyEndDate = program.getSurveyDt().length() >= 10 ? 
                    program.getSurveyDt().substring(0, 10) : program.getSurveyDt();
                
                // 문자열 비교로 날짜 비교 (YYYY-MM-DD 형태이므로 가능)
                if (today.compareTo(surveyEndDate) > 0) {
                    log.info("삭제 가능: 만족도 조사 기간 종료 (프로그램ID={}, 오늘={}, 조사마감={})", 
                            program.getPrgId(), today, surveyEndDate);
                    return true;
                }
            }
            
            log.info("삭제 불가: 조건 미충족 (프로그램ID={}, 신청자={}, 조사마감일={})", 
                    program.getPrgId(), applicantCount, program.getSurveyDt());
            return false;
            
        } catch (Exception e) {
            log.error("삭제 가능 여부 판단 오류: 프로그램ID={}, 오류={}", program.getPrgId(), e.getMessage(), e);
            return false; // 오류 시 안전하게 삭제 불가로 처리
        }
    }
    
    
    /**
     * ✅ 핵심역량 이름과 점수를 함께 조합하는 새로운 메서드
     */
    private String getCompetencyNamesWithScores(Integer prgId, List<Core_CptInfo> allCompetencies) {
        try {
            // 핵심역량 매핑 정보 조회 (점수 포함)
            List<Ncs_CclRel> competencyMappings = adminNoncurrProgramService.getCompetencyMappingsWithScores(prgId);
            
            if (competencyMappings == null || competencyMappings.isEmpty()) {
                return "설정된 핵심역량이 없습니다.";
            }
            
            return competencyMappings.stream()
                .map(mapping -> {
                    // 핵심역량 이름 찾기
                    String competencyName = allCompetencies.stream()
                        .filter(comp -> comp.getCclId().equals(mapping.getCclId()))
                        .map(Core_CptInfo::getCclNm)
                        .findFirst()
                        .orElse("알 수 없는 역량");
                    
                    // 점수 정보 추가 (예: "글로컬 리더역량(90)")
                    Integer score = mapping.getCclScore() != null ? mapping.getCclScore() : 100;
                    return competencyName + "(" + score + ")";
                })
                .collect(Collectors.joining(", "));
                
        } catch (Exception e) {
            log.error("핵심역량 점수 정보 조회 실패: 프로그램ID={}, 오류={}", prgId, e.getMessage(), e);
            return "핵심역량 정보 조회 실패";
        }
    }
    
    /**
     * 핵심역량 ID 목록을 이름 문자열로 변환
     */
    private String getCompetencyNames(List<Integer> competencyIds, List<Core_CptInfo> allCompetencies) {
        if (competencyIds == null || competencyIds.isEmpty()) {
            return "설정된 핵심역량이 없습니다.";
        }
        
        return allCompetencies.stream()
            .filter(comp -> competencyIds.contains(comp.getCclId()))
            .map(Core_CptInfo::getCclNm)
            .collect(Collectors.joining(", "));
    }

    

    /**
     * ✅ 비교과 수정 페이지 - 등록 페이지와 동일한 구조
     */
    @GetMapping("/noncurr_edit")
    public String noncurr_edit(
            @RequestParam("id") Integer prgId,
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
     * ✅ 비교과 프로그램 수정 처리 - 등록과 동일한 필드명 사용
     */
    @PostMapping("/noncurr_edit")
    public String noncurr_edit_post(
            @ModelAttribute NoncurrProgramDTO dto,
            HttpServletRequest req,
            RedirectAttributes redirectAttributes,
            Model model
            ) {
        
        log.info("비교과 프로그램 수정 처리: ID={}, 이름={}", dto.getPrgId(), dto.getPrgNm());
        
        try {
            // ✅ 상태 코드는 기존 값 유지 (수정 시 변경하지 않음)
            // Common_Code statusCode = ... (필요시 별도 처리)
            
            // 프로그램 수정
            adminNoncurrProgramService.updateProgram(dto.getPrgId(), dto);
            
            log.info("비교과 프로그램 수정 성공: ID={}, 이름={}", dto.getPrgId(), dto.getPrgNm());
            
            // 성공 시 상세 페이지로 리다이렉트
            redirectAttributes.addFlashAttribute("successMessage", "프로그램이 성공적으로 수정되었습니다.");
            return "redirect:/admin/noncurr_detail?id=" + dto.getPrgId();
            
        } catch (RuntimeException e) {
            log.error("비교과 프로그램 수정 실패: ID={}, 이름={}, 오류={}", dto.getPrgId(), dto.getPrgNm(), e.getMessage(), e);
            
            // ✅ 중복 오류 확인 및 처리
            String errorMessage;
            String fullMessage = e.getMessage();
            
            if (e.getCause() != null && e.getCause().getMessage() != null) {
                fullMessage += " | 원인: " + e.getCause().getMessage();
            }
            
            if (fullMessage.contains("이미 등록된 프로그램명입니다")) {
                errorMessage = "이미 등록된 프로그램명입니다. 다른 이름을 사용해주세요.";
            } else {
                errorMessage = "프로그램 수정 중 오류가 발생했습니다: " + e.getMessage();
            }
            
            // ✅ 에러 메시지와 함께 수정 페이지로 돌아가기
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("formDto", dto); // 입력했던 데이터 유지
            
            // 핵심역량 목록 다시 조회
            try {
                List<Core_CptInfo> competencies = adminNoncurrProgramService.getAllCompetencies();
                model.addAttribute("competencies", competencies);
            } catch (Exception ex) {
                log.error("핵심역량 조회 실패: {}", ex.getMessage());
            }
            
            model.addAttribute("isEditMode", true);
            model.addAttribute("prgId", dto.getPrgId());
            
            return "/admin/admin_noncurr_list_edit"; // 수정 페이지로 직접 반환
            
        } catch (Exception e) {
            log.error("비교과 프로그램 수정 예상치 못한 오류: ID={}, 이름={}, 오류={}", dto.getPrgId(), dto.getPrgNm(), e.getMessage(), e);
            
            model.addAttribute("errorMessage", "시스템 오류가 발생했습니다. 관리자에게 문의하세요.");
            model.addAttribute("formDto", dto);
            
            try {
                List<Core_CptInfo> competencies = adminNoncurrProgramService.getAllCompetencies();
                model.addAttribute("competencies", competencies);
            } catch (Exception ex) {
                log.error("핵심역량 조회 실패: {}", ex.getMessage());
            }
            
            model.addAttribute("isEditMode", true);
            model.addAttribute("prgId", dto.getPrgId());
            
            return "/admin/admin_noncurr_list_edit";
        }
    }

    /**
     * 비교과 통계 페이지
     */
    @GetMapping("/noncurr_stat")
    public String noncurr_stat(
            @RequestParam(name="page", defaultValue = "0") int page,
            @RequestParam(name="size", defaultValue = "10") int size,
            @RequestParam(name="search", required = false) String search,
            @RequestParam(name="period", required = false) String period,    
            @RequestParam(name="searchType", required = false) String searchType,
            Model model) {
        
        log.info("비교과 통계 페이지 요청 - page: {}, size: {}, search: {}, period: {}, searchType: {}", 
                page, size, search, period, searchType);
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            
            // ✅ 완료된 프로그램 통계 목록 조회 (만족도 조사 마감일이 지난 프로그램만)
            Page<NoncurrProgramDTO> completedPrograms = adminNoncurrProgramService.getCompletedProgramsForStats(
                    search, period, searchType, pageable);
            
            model.addAttribute("programs", completedPrograms);
            model.addAttribute("search", search);
            model.addAttribute("period", period);
            model.addAttribute("searchType", searchType);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", completedPrograms.getTotalPages());
            model.addAttribute("totalElements", completedPrograms.getTotalElements());
            
            // 페이징 정보
            model.addAttribute("hasPrevious", completedPrograms.hasPrevious());
            model.addAttribute("hasNext", completedPrograms.hasNext());
            model.addAttribute("isFirst", completedPrograms.isFirst());
            model.addAttribute("isLast", completedPrograms.isLast());
            
            // 페이지 범위 계산
            int totalPages = completedPrograms.getTotalPages();
            int startPage = Math.max(1, page - 1); // 0-based에서 1-based로 변환
            int endPage = Math.min(totalPages, page + 3);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            
            return "/admin/admin_noncurr_stat";
            
        } catch (Exception e) {
            log.error("비교과 통계 페이지 로드 실패: {}", e.getMessage(), e);
            model.addAttribute("error", "통계 데이터를 불러오는 중 오류가 발생했습니다.");
            return "/admin/admin_noncurr_stat";
        }
    }
}