package com.team.arium.student.noncurr;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/student/noncurr")
public class StudentNoncurrController {
	
    @Autowired
    private StudentNoncurrService studentNoncurrService;
	
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
    public String detailPage(@RequestParam Integer prgId, Model model) {
        Integer stdId = 1; 
        
        ProgramListDTO program = studentNoncurrService.getProgramDetail(prgId, stdId);
        model.addAttribute("program", program);
        
        return "/student/noncurr/student_noncurr_detail";
    }
	
    /**
     * 신청 페이지
     */
    @GetMapping("/add")
    public String addPage(@RequestParam(required = false) Integer prgId, Model model) {
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
        
        return "/student/noncurr/student_noncurr_addcheck";
    }
	
    /**
     * 만족도 조사 페이지
     */
    @GetMapping("/survey")
    public String surveyPage(@RequestParam(required = false) Integer prgId, Model model) {
        
        return "/student/noncurr/student_noncurr_survey";
    }
}