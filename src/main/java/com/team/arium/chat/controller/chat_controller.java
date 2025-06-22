package com.team.arium.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chat")
public class chat_controller {
	
    @GetMapping("/student")
    public String dashboard() {
        return "/chat/chat_student";
    }
    
    @GetMapping("/counselor")
    public String counselor() {
        return "/chat/chat_counselor";
    }
	
}
