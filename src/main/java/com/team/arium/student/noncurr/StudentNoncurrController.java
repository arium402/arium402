package com.team.arium.student.noncurr;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.team.arium.admin.admin_module;

@Controller
@RequestMapping("/student/noncurr")
public class StudentNoncurrController {
	
    @Autowired
    private StudentNoncurrService studentNoncurrService;
	
    @Autowired
    @Qualifier("admin_module")
    private admin_module adminModule;
    
    //  학생 보안 유틸리티 추가
    @Autowired
    private StudentSecurityUtil studentSecurityUtil;
    
    /**
     * 비교과 목록 페이지
     */
    @GetMapping("/list")
    public String listPage(Model model) {
        try {
            //  현재 로그인된 학생 정보 추가 (필요시 사용)
            Integer currentStudentId = studentSecurityUtil.getCurrentStudentId();
            String currentStudentName = studentSecurityUtil.getCurrentStudentName();
            
            model.addAttribute("currentStudentId", currentStudentId);
            model.addAttribute("currentStudentName", currentStudentName);
            
            // 페이징을 위해 빈 목록으로 시작 (JavaScript에서 로드)
            model.addAttribute("programs", new ArrayList<>());
            
            return "/student/noncurr/student_noncurr_list";
            
        } catch (Exception e) {
            // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
            return "redirect:/student/login?error=login_required";
        }
    }
	
    /**
     * 프로그램 상세 페이지
     */
    @GetMapping("/detail")
    public String detailPage(@RequestParam("prgId") Integer prgId, Model model) {
        try {
            //  하드코딩 제거: 현재 로그인된 학생 ID 사용
            Integer stdId = studentSecurityUtil.getCurrentStudentId();
            
            ProgramListDTO program = studentNoncurrService.getProgramDetail(prgId, stdId);
            model.addAttribute("program", program);
            
            return "/student/noncurr/student_noncurr_detail";
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("로그인")) {
                return "redirect:/student/login?error=login_required";
            }
            model.addAttribute("error", "프로그램 정보를 불러올 수 없습니다: " + e.getMessage());
            return "/student/noncurr/student_noncurr_list";
        }
    }
	
    /**
     * 신청 페이지
     */
    @GetMapping("/add")
    public String addPage(@RequestParam(value = "prgId", required = false) Integer prgId, Model model) {
        try {
            if (prgId != null) {
                //  하드코딩 제거: 현재 로그인된 학생 ID 사용
                Integer stdId = studentSecurityUtil.getCurrentStudentId();
                
                ProgramListDTO program = studentNoncurrService.getProgramDetail(prgId, stdId);
                model.addAttribute("program", program);
            }
            
            return "/student/noncurr/student_noncurr_add";
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("로그인")) {
                return "redirect:/student/login?error=login_required";
            }
            model.addAttribute("error", "프로그램 정보를 불러올 수 없습니다: " + e.getMessage());
            return "/student/noncurr/student_noncurr_list";
        }
    }
	
    /**
     * 신청 내역 페이지
     */
    @GetMapping("/addcheck")
    public String addcheckPage(Model model) {
        try {
            //  하드코딩 제거: 현재 로그인된 학생 ID 사용
            Integer stdId = studentSecurityUtil.getCurrentStudentId();
            
            List<ProgramListDTO> myApplications = studentNoncurrService.getMyApplications(stdId);
            model.addAttribute("applications", myApplications);
            
            //  아시아/서울 시간 기준 현재 날짜 추가
            String currentDate = adminModule.todays_module();
            model.addAttribute("currentDate", currentDate);
            
            //  현재 학생 정보 추가
            model.addAttribute("currentStudentName", studentSecurityUtil.getCurrentStudentName());
            model.addAttribute("currentStudentNumber", studentSecurityUtil.getCurrentStudentNumber());
            
            return "/student/noncurr/student_noncurr_addcheck";
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("로그인")) {
                return "redirect:/student/login?error=login_required";
            }
            model.addAttribute("error", "신청 내역을 불러올 수 없습니다: " + e.getMessage());
            return "/student/noncurr/student_noncurr_list";
        }
    }
	
    /**
     * 만족도 조사 페이지
     */
    @GetMapping("/survey")
    public String surveyPage(@RequestParam(value = "prgId", required = false) Integer prgId, Model model) {
        try {
            //  하드코딩 제거: 현재 로그인된 학생 ID 사용
            Integer stdId = studentSecurityUtil.getCurrentStudentId();
            
            if (prgId != null) {
                // DB에서 만족도 조사 문항들 가져오기
                SatisfactionQuestionResponseDTO surveyData = studentNoncurrService.getSatisfactionQuestions(prgId);
                model.addAttribute("surveyData", surveyData);
            }
            
            //  현재 로그인된 학생 기본 정보 가져오기 (하드코딩 제거)
            StudentBasicInfoDTO studentInfo = studentNoncurrService.getStudentBasicInfo(stdId);
            model.addAttribute("studentInfo", studentInfo);
            
            return "/student/noncurr/student_noncurr_survey";
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("로그인")) {
                return "redirect:/student/login?error=login_required";
            }
            model.addAttribute("error", "만족도 조사를 불러올 수 없습니다: " + e.getMessage());
            return "/student/noncurr/student_noncurr_list";
        }
    }
}