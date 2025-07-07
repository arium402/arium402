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
    
    /**
     * 비교과 목록 페이지
     */
    @GetMapping("/list")
    public String listPage(Model model) {
        // 페이징을 위해 빈 목록으로 시작 (JavaScript에서 로드)
        model.addAttribute("programs", new ArrayList<>());
        
        return "/student/noncurr/student_noncurr_list";
    }
	
    /**
     * 프로그램 상세 페이지
     */
    @GetMapping("/detail")
    public String detailPage(@RequestParam("prgId") Integer prgId, Model model) {
        Integer stdId = 1; 
        
        ProgramListDTO program = studentNoncurrService.getProgramDetail(prgId, stdId);
        model.addAttribute("program", program);
        
        return "/student/noncurr/student_noncurr_detail";
    }
	
    /**
     * 신청 페이지
     */
    @GetMapping("/add")
    public String addPage(@RequestParam(value = "prgId", required = false) Integer prgId, Model model) {
        if (prgId != null) {
            Integer stdId = 1;
            ProgramListDTO program = studentNoncurrService.getProgramDetail(prgId, stdId);
            model.addAttribute("program", program);
        }
        
        return "/student/noncurr/student_noncurr_add";
    }
	
    /**
     * 신청 내역 페이지
     */
    @GetMapping("/addcheck")
    public String addcheckPage(Model model) {
        Integer stdId = 1;
        
        List<ProgramListDTO> myApplications = studentNoncurrService.getMyApplications(stdId);
        model.addAttribute("applications", myApplications);
        
        // ✅ 아시아/서울 시간 기준 현재 날짜 추가
        String currentDate = adminModule.todays_module();
        model.addAttribute("currentDate", currentDate);
        
        return "/student/noncurr/student_noncurr_addcheck";
    }
	
    /**
     * 만족도 조사 페이지
     */
    @GetMapping("/survey")
    public String surveyPage(@RequestParam(value = "prgId", required = false) Integer prgId, Model model) {
        
        return "/student/noncurr/student_noncurr_survey";
    }
}