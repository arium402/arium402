package com.team.arium.counselor.dashboard;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.team.arium.DTO.admin_counselor_DTO;
import com.team.arium.admin.admin_service;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/counselor")
public class CounselorDashboard_controller {
	
	static String userid = "anonymousUser";
	List<admin_counselor_DTO> dtoList = null;
	@Autowired
	public admin_service admin_svc;

	@GetMapping("/log_outs")
	public String log_outs(Model m) {
		m.addAttribute("message", "자동 로그아웃 처리 되었습니다.");
        m.addAttribute("redirectUrl", "/student/login");
		
        return "/counselor/logout_alert";
	}
	//상담원 대쉬보드
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {    	
    	this.userid = SecurityContextHolder.getContext().getAuthentication().getName();
    	if(this.userid.equals("anonymousUser")) {
    		return "redirect:/counselor/log_outs";
    	}
    	else {
    		this.dtoList = this.admin_svc.my_counselor_info2(this.userid);
    		session.setAttribute("UserNo", this.dtoList.get(0).emplId);
    		session.setAttribute("UserId", this.userid);
    		session.setAttribute("UserNm", this.dtoList.get(0).emplName);
    		session.setAttribute("UserIdx", this.dtoList.get(0).emplId);
    		session.setAttribute("UserEmail", this.dtoList.get(0).emplEmlAddr);	
    	}		
        return "/counselor/counselor_dashboard_main";
    }
}



