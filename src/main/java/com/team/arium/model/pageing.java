package com.team.arium.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository("pageing")
public class pageing {

	int post_ea = 10; 	//한페이지당 보여줄 게시물 개수
	int clickPage = 0;  //사용자가 클릭한 페이지번호에 맞는 게시글 순차번호 계산값
	int bno = 0;
	
	//게시물 일련번호 계산
	public int serial_no(int pageno, int total_ea)  {
		
		if(pageno == 1) {
			this.clickPage = 0;
			
		}else {  //1외의 다른 페이지를 클릭시
			this.clickPage = (pageno -1) * post_ea;
		}
		this.bno = total_ea - this.clickPage;
		return this.bno;
	}
	
	
	
	//페이지개수
	public Map<String, Integer> page_ea (Integer pageno, Integer total_post)  {
		//pageno : 현재 페이지 번호 , total_post : 전체게시물 개수  
		
		int page_ea = 5; //한페이지에 보여줄 페이지 박스의 갯수
		int page_ea_total;  //총 페이지 개수 
		int start_pg;
		int end_pg;
		
		//총 페이지 수 계산 
		page_ea_total = (int) Math.ceil((double) total_post / post_ea); 
		
		//요청한 페이지가 총 페이지보다 클 경우 조정
		if (pageno > page_ea_total) {
			pageno = page_ea_total;
		}
		
		//슬라이딩 페이지 계산 
		int half = page_ea / 2;
		start_pg = pageno - half;
		if (start_pg < 1) {
		    start_pg = 1;
		}
		end_pg = start_pg + page_ea - 1;
		if (end_pg > page_ea_total) {
		    end_pg = page_ea_total;
		    start_pg = Math.max(1, end_pg - page_ea + 1);
		    //ㄴstart_pg 조정: end_pg - (page_ea - 1) 가 1보다 작으면 1로 고정
		}
		
		
		Map<String, Integer> pageinfo = new HashMap<String, Integer>();
		//게시물이 없을 경우
		if (total_post == 0) {
			pageinfo.put("pageno", 1);
			pageinfo.put("page_ea", page_ea);
			pageinfo.put("page_ea_total", 1);
			pageinfo.put("start_pg", 1);
			pageinfo.put("end_pg", 1);
			pageinfo.put("post_ea", post_ea);
			
		}else {  //게시물이 있을 경우
			pageinfo.put("pageno" , pageno);
			pageinfo.put("page_ea" , page_ea);  
			pageinfo.put("page_ea_total" , page_ea_total);  
			pageinfo.put("start_pg" , start_pg);  
			pageinfo.put("end_pg" , end_pg); 
			pageinfo.put("post_ea" , post_ea);
		}
		
		return pageinfo;
	}
	
}
