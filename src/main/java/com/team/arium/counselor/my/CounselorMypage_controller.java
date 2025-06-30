package com.team.arium.counselor.my;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/counselor")
public class CounselorMypage_controller {

    @GetMapping("/mypage")
    public String mypage(Model model) {
        
        return "/counselor/counselor_mypage";
    }
}
