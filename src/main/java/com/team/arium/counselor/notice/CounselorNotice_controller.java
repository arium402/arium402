package com.team.arium.counselor.notice;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/counselor")
public class CounselorNotice_controller {

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
