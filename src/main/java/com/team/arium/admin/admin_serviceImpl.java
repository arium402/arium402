package com.team.arium.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;

import com.team.arium.DTO.admin_counselor_DTO;
import com.team.arium.cmncode.commonCode_service;
import com.team.arium.domain.Empl_Info;
import com.team.arium.model.generateNo;

import jakarta.annotation.Resource;
@Service
public class admin_serviceImpl implements admin_service{
	
	@Autowired
	public admin_counselor_repo admin_cnsl_repo;
	
	@Autowired
	public commonCode_service cmn_svc;
	
	@Resource(name="generateNo")
	public generateNo gen_no;
	
	@Override
	public List<admin_counselor_DTO> my_counselor_info2(String sno) {
		List<Empl_Info> entityList = this.admin_cnsl_repo.dtoinfo(sno);
		List<admin_counselor_DTO> dtoList = new ArrayList<>();
		 for (Empl_Info e : entityList) {
		    	admin_counselor_DTO dto = new admin_counselor_DTO();
		    	dto.setEmplNo(e.getEmplNo());
		        dto.setEmplName(e.getEmplName());
		        dto.setCnslCdDesc(this.cmn_svc.getCodeDesc("cnsl_cd", e.getCnslCd().getCodeId()));
		        dto.setEmplStatCdDesc(this.cmn_svc.getCodeDesc("empl_stat_cd", e.getEmplStatCd().getCodeId()));
		        dto.setEmplTellno(e.getEmplTellno());
		        dto.setEmplEmlAddr(e.getEmplEmlAddr());
		        dto.setRegDt(e.getRegDt());
		        dtoList.add(dto); // 리스트에 추가
		   }
		  return dtoList;

	}
	
	@Override
	public List<admin_counselor_DTO> my_counselor_info(int statcode) {
		
		List<Empl_Info> entityList = this.admin_cnsl_repo.dtoList(statcode);
		List<admin_counselor_DTO> dtoList = new ArrayList<>();
		 for (Empl_Info e : entityList) {
		    	admin_counselor_DTO dto = new admin_counselor_DTO();
		    	dto.setEmplId(e.getEmplId());
		        dto.setEmplNo(e.getEmplNo());
		        dto.setEmplName(e.getEmplName());
		        dto.setCnslCdDesc(this.cmn_svc.getCodeDesc("cnsl_cd", e.getCnslCd().getCodeId()));
		        dto.setEmplStatCdDesc(this.cmn_svc.getCodeDesc("empl_stat_cd", e.getEmplStatCd().getCodeId()));
		        dto.setEmplTellno(e.getEmplTellno());
		        dto.setEmplEmlAddr(e.getEmplEmlAddr());
		        dto.setRegDt(e.getRegDt());
		        
		        dtoList.add(dto); // 리스트에 추가
		   }
		  return dtoList;
	}
	@Override
	public List<admin_counselor_DTO> conunselorlist_data() {
		List<Empl_Info> entityList = this.admin_cnsl_repo.conunselorlist_data();
	    List<admin_counselor_DTO> dtoList = new ArrayList<>();

	    for (Empl_Info e : entityList) {
	    	admin_counselor_DTO dto = new admin_counselor_DTO();
	    	dto.setEmplId(e.getEmplId());
	        dto.setEmplNo(e.getEmplNo());
	        dto.setEmplName(e.getEmplName());
	        dto.setCnslCdDesc(this.cmn_svc.getCodeDesc("cnsl_cd", e.getCnslCd().getCodeId()));
	        dto.setEmplStatCdDesc(this.cmn_svc.getCodeDesc("empl_stat_cd", e.getEmplStatCd().getCodeId()));
	        dto.setEmplTellno(e.getEmplTellno());
	        dto.setEmplEmlAddr(e.getEmplEmlAddr());
	        dto.setRegDt(e.getRegDt());
	        
	        dtoList.add(dto); // 리스트에 추가
	    }
	    return dtoList;
	}
	
	@Override
	public List<admin_counselor_DTO> getCounselorDtoList(int statcode) {
		//List<Empl_Info> entityList = this.admin_cnsl_repo.findAllByOrderByEmplId();
		List<Empl_Info> entityList = this.admin_cnsl_repo.findAllByemplStatCd(statcode);
	    List<admin_counselor_DTO> dtoList = new ArrayList<>();

	    for (Empl_Info e : entityList) {
	    	admin_counselor_DTO dto = new admin_counselor_DTO();
	    	dto.setEmplId(e.getEmplId());
	        dto.setEmplNo(e.getEmplNo());
	        dto.setEmplName(e.getEmplName());
	        dto.setCnslCdDesc(this.cmn_svc.getCodeDesc("cnsl_cd", e.getCnslCd().getCodeId()));
	        dto.setEmplStatCdDesc(this.cmn_svc.getCodeDesc("empl_stat_cd", e.getEmplStatCd().getCodeId()));
	        dto.setEmplTellno(e.getEmplTellno());
	        dto.setEmplEmlAddr(e.getEmplEmlAddr());
	        dto.setRegDt(e.getRegDt());
	        
	        dtoList.add(dto); // 리스트에 추가
	    }
	    return dtoList;
	}
	
	@Override
	public List<admin_counselor_DTO> getCounselorDtoList() {
	    List<Empl_Info> entityList = this.admin_cnsl_repo.findAllByOrderByEmplId();
	    List<admin_counselor_DTO> dtoList = new ArrayList<>();

	    for (Empl_Info e : entityList) {
	    	admin_counselor_DTO dto = new admin_counselor_DTO();
	    	dto.setEmplId(e.getEmplId());
	        dto.setEmplNo(e.getEmplNo());
	        dto.setEmplName(e.getEmplName());
	        dto.setCnslCdDesc(this.cmn_svc.getCodeDesc("cnsl_cd", e.getCnslCd().getCodeId()));
	        dto.setEmplStatCdDesc(this.cmn_svc.getCodeDesc("empl_stat_cd", e.getEmplStatCd().getCodeId()));
	        dto.setEmplTellno(e.getEmplTellno());
	        dto.setEmplEmlAddr(e.getEmplEmlAddr());
	        dto.setRegDt(e.getRegDt());
	        
	        dtoList.add(dto); // 리스트에 추가
	    }
	    return dtoList;
	}

//	@Override
//	public Empl_Info insert_counselor(String emp_data) {
//		Empl_Info entity = new Empl_Info();
//		
//		JSONArray ja = new JSONArray(emp_data);
//		JSONObject jo = ja.getJSONObject(0);
//		
//		String newEmpNo = this.gen_no.generateEmpNo(); 
//		entity.setEmplNo("C"+newEmpNo);
//		
//		entity.setEmplName(jo.getString("emplName"));
//	    entity.setEmplTellno(jo.getString("emplTellno"));
//	    entity.setEmplEmlAddr(jo.getString("emplEmlAddr"));
//	   
//	    entity.setCnslCd(new Common_Code(jo.getInt("cnslCd"),null,null,null));
//	    entity.setEmplStatCd(new Common_Code(jo.getInt("emplStatCd"),null,null,null));
//	    
//	    String today = this.admin_cnsl_repo.mysql_today();
//	    
//	    entity.setEmplStatCd(new Common_Code(jo.getInt("emplStatCd"),null,null,null));
//	    
//	 
//	    
//		Empl_Info save_counselor = this.admin_cnsl_repo.save(entity);
//	    
//		return save_counselor;
//	}

	

}
