package com.team.arium.counselor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/counselor")
public class counselor_controller {
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "/counselor/counselor_dashboard_main";
    }
    
    @GetMapping("/applicants")
    public String applicants(Model model) {
        // 필요한 경우 신청자 데이터를 모델에 추가
        // List<Applicant> applicants = applicantService.getAllApplicants();
        // model.addAttribute("applicants", applicants);
        
        return "/counselor/counselor_applicants";
    }
    
    @GetMapping("/clients")
    public String clients(Model model) {
        // 필요한 경우 내담자 데이터를 모델에 추가
        // List<Client> clients = clientService.getAllClients();
        // model.addAttribute("clients", clients);
        
        return "/counselor/counselor_client-management";
    }
    
    
    @GetMapping("/clients/advice_detail")
    public String clientsAdviceDetail(Model model) {
        // 내담자 상세 데이터를 모델에 추가
        return "/counselor/counselor_client_advice_detail";
    }
    
    @GetMapping("/clients/study_detail")
    public String clientStudyDetail(Model model) {
        // 학습 컨설팅 내담자 상세 데이터
        return "/counselor/counselor_client_study_detail";
    }
    
    @GetMapping("/clients/job_detail")
    public String clientsJobDetail(Model model) {
        // 취업/진로 상담 내담자 상세 데이터
        return "/counselor/counselor_client_job_detail";
    }
    
    @GetMapping("/clients/diary")
    public String diary(Model model) {
        // 취업/진로 상담 내담자 상세 데이터
        return "/counselor/counselor_diary";
    }
    
    @GetMapping("/schedule_check")
    public String schedule_check(Model model) {
        
        return "/counselor/counselor_schedule_check";
    }
    
    @GetMapping("/counseling_schedule")
    public String counseling_schedule(Model model) {
        
        return "/counselor/counselor_counseling_schedule";
    }
    
    
    @GetMapping("/schedule_registration")
    public String schedule_registration(Model model) {
        
        return "/counselor/counselor_schedule_registration";
    }
    
    
    @GetMapping("/mypage")
    public String mypage(Model model) {
        
        return "/counselor/counselor_mypage";
    }
    
    @GetMapping("/notice")
    public String notice(Model model) {
        
        return "/counselor/counselor_notice";
    }
    
    @GetMapping("/noticeClick")
    public String noticeClick(Model model) {
        
        return "/counselor/counselor_noticeClick";
    }
    
    @GetMapping("/faq")
    public String faq(Model model) {
        
        return "/counselor/counselor_F&Q";
    }
    
    @GetMapping("/QnA")
    public String QnA(Model model) {
        
        return "/counselor/counselor_QnA";
    }
    
    @GetMapping("/QnA_emergency")
    public String QnA_emergency(Model model) {
        
        return "/counselor/counselor_QnA_emergency";
    }
    
    @GetMapping("/QnA_waiting")
    public String QnA_waiting(Model model) {
        
        return "/counselor/counselor_QnA_waiting";
    }
    
    @GetMapping("/QnA_comment")
    public String QnA_comment(Model model) {
        
        return "/counselor/counselor_QnA_comment";
    }
    
    
    
}