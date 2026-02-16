package com.team.arium.admin;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.team.arium.DTO.admin_counselor_DTO;
import com.team.arium.model.pageing;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;


@CrossOrigin(origins="*", allowedHeaders = "*")
@Controller
@RequestMapping("/admin")
public class admin_controller {
	PrintWriter pw = null;
	
	@Autowired
	public admin_counselor_repo admin_cnsl_repo;

	@Autowired
	public admin_service admin_svc;
	
	@Resource(name="pageing")
	pageing m_pg;
	
	@Resource(name="admin_module")
	admin_module admin_module;
	
	@Resource(name="admin_counselor_DTO")
	admin_counselor_DTO admindto;
	
	List<String> list = null; 
	Map<String, String> map = null;
	String url = "";
	String msg = "";
	int result = 0;
	
	//상담사 리스트 
	@GetMapping("/admin_counselorList")
	public String admin_counselorList(Model m
									,@RequestParam(value = "empl_stat_cd", required = false, defaultValue = "") String empl_stat_cd
									,@RequestParam(value = "keyword", required = false) String keyword
									,@RequestParam(value="pageno", defaultValue="1", required=false) Integer pageno
									)  {
		List<admin_counselor_DTO> dtoList = null;
		int code  = 0;
		if(empl_stat_cd.equals("active") || empl_stat_cd.equals("inactive")) {
			if(empl_stat_cd.equals("active")) {
				code = 31;
			}
			else {
				code = 33;
			}
			dtoList = this.admin_svc.getCounselorDtoList(code);
		}
		else {
			dtoList = this.admin_svc.getCounselorDtoList();			
		}
		
//		List<Empl_Info> allCounselorList = this.admin_cnsl_repo.findAllByOrderByEmplId();
		System.out.println("allCounselorList : " + dtoList);
		
		Integer counselorTotal = dtoList.size();
		
		//페이징 관련 
		Map<String, Integer> pageinfo = this.m_pg.page_ea(pageno, counselorTotal);
		int bno = this.m_pg.serial_no(pageno, counselorTotal); 
				
		m.addAttribute("cslorList", dtoList);
		m.addAttribute("cslorTotal", counselorTotal);
		
		m.addAttribute("pageinfo", pageinfo);
		m.addAttribute("bno", bno);
		m.addAttribute("empl_stat_cd",empl_stat_cd);
		
		return "/admin/admin_counselorList.html";
	}
	
	
	//상담사등록 페이지로 이동 
	@GetMapping("/admin_counselorList_add")
	public String admin_counselorList_add() {
		return "/admin/admin_counselorList_add.html";
	}
		
//	@GetMapping("/check-role")
//	public ResponseEntity<?> checkRole() {
//	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//	    System.out.println("현재 사용자: " + auth.getName());
//	    System.out.println("권한: " + auth.getAuthorities());
//
//	    return ResponseEntity.ok("확인 완료");
//	}
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	//상담사 등록
	@PostMapping("/admin_counselorList_addOk")
	public String admin_counselorList_addOk(@RequestBody String emp_data, HttpServletResponse res) throws Exception {
		try {
			this.pw = res.getWriter();
			
			String today = this.admin_module.todays_module().replaceAll("-", "");
			String treecode = this.admin_module.code_random();
			String empl_no = "C"+today+treecode;
			this.admindto.setEmplNo(empl_no);
			
			JSONObject jo = new JSONObject(emp_data);
			this.admindto.setEmplName(String.valueOf(jo.get("emplName")));
			this.admindto.setCnslCd(String.valueOf(jo.get("cnslCd")));
			this.admindto.setEmplTellno(String.valueOf(jo.get("emplTellno")));
			this.admindto.setEmplEmlAddr(String.valueOf(jo.get("emplEmlAddr")));
			this.admindto.setEmplStatCd(String.valueOf(jo.get("emplStatCd")));
			String passwd = this.bcrypt.encode("1111");	//상담사 최초 가입시 1111로 지정함
			String roles = "ROLE_COUNSELOR";
			this.result = this.admin_cnsl_repo.mysql_insert(admindto);
			this.admin_cnsl_repo.user_insert(empl_no,passwd,roles);	//상담사 아이디, 패스워드, role 적용하는 repo			
			this.pw.print(this.result);
			
		}catch(Exception e) {
			System.out.println(e);
		}finally {
			this.pw.close();
		}
		return null;
	}
		
		
	//상담사관리 > 상담사 일정관리 
	@GetMapping("/admin_counselor_schedule")
	public String admin_counselor_schedule(HttpServletResponse res, Model m){

		return "/admin/admin_counselor_schedule";
	}
	
	
	//상담사 관리 > 상담사별 통계 
	@GetMapping("/admin_counselor_statistics")
	public String admin_counselor_statistics(HttpServletResponse res)  {
		
		
		return "/admin/admin_counselor_statistics";
	}
	
	
	
	
	
	//상담사 정보 
	@GetMapping("/admin_counselorList_detail")
	public String admin_counselorList_detail(@RequestParam(name="id", defaultValue = "")String id, Model m) throws Exception  {
		String msg = "error";
		try {
		Integer no = Integer.parseInt(id);
		List<admin_counselor_DTO> dtoList = null;
		dtoList = this.admin_svc.my_counselor_info(no);
			if(dtoList.size() <= 0) {
				m.addAttribute("msg", msg);
			}
			else {
				m.addAttribute("msg", "ok");
				m.addAttribute("cslorList", dtoList);
			}
		}catch (Exception e) {
			m.addAttribute("msg", msg);
		}
		return "/admin/admin_counselorList_detail";
	}
	
		
		
	//*******************************************************************************************//
	
	
	//상담사 관리 > 상담신청내역
	@GetMapping("/admin_counselor_studentApply")
	public String admin_counselor_studentApply(HttpServletResponse res)  {
		
		
		return "/admin/admin_counselor_studentApply";
	}
	
	
	//상담관리 > 상담 신청내역 
	@GetMapping("/admin_counselor_scheduleDetail")
	public String admin_counselor_scheduleDetail(HttpServletResponse res)  {
		
		
		return "/admin/admin_counselor_scheduleDetail";
	}
	
	
	//상담관리 > 상담 분야별 통계
	@GetMapping("/admin_counselingType_stats")
	public String admin_counselingType_stats(HttpServletResponse res)  {
		
		return "/admin/admin_counselingType_stats";
	}
	

	

}
