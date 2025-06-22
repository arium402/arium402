package com.team.arium.student.notice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student/notice")
public class StudentNoticeController {
	
	@GetMapping("/asklist")
	public String asklistPage() {
		
		return "/student/notice/student_notice_asklist.html";	// 문의 게시판
	}
	
	@GetMapping("/askcheck")
	public String askcheckPage() {
		
		return "/student/notice/student_notice_askcheck.html";	// 문의 게시판 상세
	}
	
	@GetMapping("/askadd")
	public String askaddPage() {
		
		return "/student/notice/student_notice_askadd.html";	// 문의 게시판 신청
	}
	
	@GetMapping("/fq")
	public String fqPage() {
		
		return "/student/notice/student_notice_fq.html";	// 자주 묻는 질문
	}
	
	@GetMapping("/ann")
	public String annPage() {
		
		return "/student/notice/student_notice_ann.html";	// 공지사항
	}
	
	@GetMapping("/anndetail")
	public String anndetailPage() {
		
		return "/student/notice/student_notice_anndetail.html";	// 공지사항 상세
	}
}