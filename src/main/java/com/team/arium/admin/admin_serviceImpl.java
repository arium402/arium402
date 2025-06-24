package com.team.arium.admin;

import org.json.JSONArray;
import org.json.JSONObject;
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
	public Empl_Info insert_counselor(String emp_data) {
		Empl_Info entity = new Empl_Info();
		
		JSONArray ja = new JSONArray(emp_data);
		JSONObject jo = ja.getJSONObject(0);
		
		String newEmpNo = this.gen_no.generateEmpNo(); 
		entity.setEmplNo("C"+newEmpNo);
		
		entity.setEmplName(jo.getString("emplName"));
	    entity.setEmplTellno(jo.getString("emplTellno"));
	    entity.setEmplEmlAddr(jo.getString("emplEmlAddr"));
	   
	    entity.setCnslCd(new Common_Code(jo.getInt("cnslCd"),null,null,null));
	    entity.setEmplStatCd(new Common_Code(jo.getInt("emplStatCd"),null,null,null));
	    
	    String today = this.admin_cnsl_repo.mysql_today();
	    
	    entity.setEmplStatCd(new Common_Code(jo.getInt("emplStatCd"),null,null,null));
	    
	 
	    
		Empl_Info save_counselor = this.admin_cnsl_repo.save(entity);
	    
		return save_counselor;
	}

	

}
