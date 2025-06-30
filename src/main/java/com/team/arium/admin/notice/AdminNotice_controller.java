package com.team.arium.admin.notice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin")
public class AdminNotice_controller {
	
	//공지사항 리스트 
	@GetMapping("/notice")
	public String notice(HttpServletResponse res) {
		return "/admin/admin_notice";
	}
	
	
	//공지사항 상세
	@GetMapping("/notice_detail")
	public String notice_detail(HttpServletResponse res) {
		return "/admin/admin_notice_detail";
	}
	
	
	//공지사항 등록
	@GetMapping("/notice_add")
	public String notice_add(HttpServletResponse res) {
		return "/admin/admin_notice_add";
	}
	
	//공지사항 수정 
	@GetMapping("/notice_modify")
	public String admin_modify(HttpServletResponse res) {
		return "/admin/admin_notice_modify";
	}
	
	
	//F&Q 관리페이지 
	@GetMapping("/faq")
	public String faq(HttpServletResponse res) {
		return "/admin/admin_F&Q";
	}
	
	//F&Q 문의문답 하나의 상세 페이지 
	@GetMapping("/faq_detail")
	public String faq_detail(HttpServletResponse res) {
		return "/admin/admin_F&Q_detail";
	}
		
	//F&Q 문의문답 등록 페이지 
	@GetMapping("/faq_add")
	public String faq_add(HttpServletResponse res) {
		return "/admin/admin_F&Q_add";
	}		
		
	//F&Q 문의문답 등록 페이지 
	@GetMapping("/faq_modify")
	public String faq_modify(HttpServletResponse res) {
		return "/admin/admin_F&Q_modify";
	}	
	
	//F&Q 리스트 상세 페이지 
	@GetMapping("/faq_list_detail")
	public String faq_list_detail(HttpServletResponse res) {
		return "/admin/admin_F&Q_list_detail";
	}
	
	//F&Q 리스트 등록 페이지 
	@GetMapping("/faq_list_add")
	public String faq_list_add(HttpServletResponse res) {
		return "/admin/admin_F&Q_list_add";
	}

	//F&Q 리스트 상세 수정
	@GetMapping("/faq_list_modify")
	public String faq_list_modify(HttpServletResponse res) {
		return "/admin/admin_F&Q_list_detail_modify";
	}

	
	//문의 게시판 페이지
	@GetMapping("/qna")
	public String qna(HttpServletResponse res) {
		return "/admin/admin_Q&A";
	}
	
	//문의게시판 상세 - 답변 완료된 페이지
	@GetMapping("/qna_detail_answered")
	public String qna_detail_answered(HttpServletResponse res) {
		return "/admin/admin_Q&A_detail_answered";
	}
	
	//문의게시판 상세 - 답변 완료된 페이지
	@GetMapping("/qna_detail_waiting_asw")
	public String qna_detail_waiting_asw(HttpServletResponse res) {
		return "/admin/admin_Q&A_detail_waitingAnswer";
	}
	
	
}
