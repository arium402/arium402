package com.team.arium.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.arium.domain.Common_Code;
import com.team.arium.domain.Empl_Info;
import com.team.arium.model.generateNo;

import jakarta.annotation.Resource;

@Service
public class admin_serviceImpl implements admin_service{
	
	@Autowired
	public admin_counselor_repo admin_cnsl_repo;
	
	@Resource(name="generateNo")
	public generateNo gen_no;
	
	
	

	@Override
	public Empl_Info insert_counselor(admin_counselor_DTO emp_data) {
		Empl_Info entity = new Empl_Info();
		
		String newEmpNo = this.gen_no.generateEmpNo(); 
		entity.setEmplNo("C"+newEmpNo);
		
		entity.setEmplName(emp_data.getEmplName());
	    entity.setEmplTellno(emp_data.getEmplTellno());
	    entity.setEmplEmlAddr(emp_data.getEmplEmlAddr());
	    
	   
//	    entity.setCnslCd(new Common_Code(Integer.parseInt(emp_data.getCnslCd()),null,null,null));
//	    entity.setEmplStatCd(new Common_Code(Integer.parseInt(emp_data.getEmplStatCd()),null,null,null));
	    
//		Empl_Info save_counselor = this.admin_cnsl_repo.save(entity);
	    
		return null;
	}

	

}
