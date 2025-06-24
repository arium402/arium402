package com.team.arium.counselor.counsel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.team.arium.DTO.counselor_patient_DTO;
import com.team.arium.admin.admin_service;
import com.team.arium.domain.Empl_Info;
import com.team.arium.domain.Std_Info;
import com.team.arium.model.pageing;

import jakarta.annotation.Resource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/counselor")
public class counselor_controller {
    
    
	@Autowired
	public counselor_repo cns_repo;
	
	@Autowired
	public counselor_service cns_svc;
	
	@Resource(name="pageing")
	pageing m_pg;
	
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
    
    @GetMapping("/counseling_schedule")
    public String counseling_schedule(Model model) {
        
        return "/counselor/counselor_counseling_schedule";
    }
    
    
    @GetMapping("/schedule_registration")
    public String schedule_registration(Model model) {
        
        return "/counselor/counselor_schedule_registration";
    }
    
    
}