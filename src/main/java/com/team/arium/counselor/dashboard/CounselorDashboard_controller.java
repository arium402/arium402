package com.team.arium.counselor.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/counselor")
public class CounselorDashboard_controller {

	//상담원 대쉬보드
    @GetMapping("/dashboard")
    public String dashboard() {
        return "/counselor/counselor_dashboard_main";
    }
}
