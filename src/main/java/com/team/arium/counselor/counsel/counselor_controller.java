package com.team.arium.counselor.counsel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.team.arium.DTO.counselor_cnlr_schd;
import com.team.arium.DTO.counselor_patient_DTO;
import com.team.arium.admin.admin_service;
import com.team.arium.domain.Empl_Info;
import com.team.arium.domain.Std_Info;
import com.team.arium.model.pageing;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/counselor")
public class counselor_controller {
    
	PrintWriter pw = null;
    
	@Autowired
	public counselor_repo cns_repo;
	
	@Autowired
	public counselor_service cns_svc;
	
	@Resource(name="pageing")
	pageing m_pg;
	
	@Resource(name="cnlr_schd_DTO")
	counselor_cnlr_schd schd;
	
	//근무일정 관리
	@PostMapping("/scheduleok")
	public String scheduleok(@RequestBody String data, HttpServletResponse res, 
			@SessionAttribute(name = "UserNo", required = false) String UserNo) throws Exception {
		this.pw = res.getWriter();			
		try {
			JSONObject jo = new JSONObject(data);
			//System.out.println(jo.get("emplno"));	//상담사 고유값
			JSONArray ja = (JSONArray)jo.get("workDays");	//근무요일			
			JSONArray ja2 = (JSONArray)jo.get("consultationTimes");	//상담가능 시간대
			this.schd.setEmpl_id(Integer.parseInt(jo.get("emplno").toString()));
			this.schd.setWork_year(jo.get("year").toString());
			this.schd.setWork_month(jo.get("month").toString());
			this.schd.setStart_time(jo.get("startTime").toString());
			this.schd.setEnd_time(jo.get("endTime").toString());
						
			int w = 0;
			while(w < ja.length()) {
				this.schd.setWork_day(Integer.parseInt(ja.get(w).toString()));
				this.cns_repo.schedule_insert(this.schd);	//DB 입력사항
				
				int wa = 0;
				while(wa < ja2.length()) {	
					/*
					System.out.println(ja2.get(wa).toString());
					System.out.println(jo.get("year").toString());
					System.out.println(jo.get("month").toString());
					System.out.println(jo.get("emplno").toString());
					*/
					this.cns_repo.schedule_insert(ja2.get(wa).toString(), jo.get("year").toString(), jo.get("month").toString(), jo.get("emplno").toString());
					wa++;
				}
				w++;
			}
			this.pw.print("ok");
		}catch(Exception e) {
			this.pw.print("error");
		}finally {
			this.pw.close();
		}
		return null;
	}
	
	
	//신청자 관리 
    @GetMapping("/applicants")
    public String applicants(Model model
    						,@RequestParam(value = "keyword", required = false) String keyword
    						,@RequestParam(value="pageno", defaultValue="1", required=false) Integer pageno) {
        // 필요한 경우 신청자 데이터를 모델에 추가
//         List<Applicant> applicants = applicantService.getAllApplicants();
        // model.addAttribute("applicants", applicants);
    	
    	List<counselor_patient_DTO> allPatientList = this.cns_svc.allPatientList();
    	
    	System.out.println("allCounselorList : " + allPatientList);
		
		Integer studentTotal = allPatientList.size();
		
		//페이징 관련 
		Map<String, Integer> pageinfo = this.m_pg.page_ea(pageno, studentTotal);
		int bno = this.m_pg.serial_no(pageno, studentTotal); 
				
		
		model.addAttribute("pttList", allPatientList);
		model.addAttribute("pttTotal", studentTotal);
		
		model.addAttribute("pageinfo", pageinfo);
		model.addAttribute("bno", bno);
        
        return "/counselor/counselor_applicants";
    }
    
    //내담자 관리 
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
    
    //근무일정관리
    @GetMapping("/counseling_schedule")
    public String counseling_schedule(Model model) {
        
    	
    	
        return "/counselor/counselor_counseling_schedule";
    }
    
    
    @GetMapping("/schedule_registration")
    public String schedule_registration(Model model,@SessionAttribute(name = "UserNo", required = false) String UserNo) {
        model.addAttribute("UserNo",UserNo);
        return "/counselor/counselor_schedule_registration";
    }
    
    
}