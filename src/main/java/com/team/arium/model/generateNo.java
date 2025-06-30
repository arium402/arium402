package com.team.arium.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.team.arium.admin.admin_counselor_repo;

@Repository("generateNo")
public class generateNo {
	
	@Autowired
	public admin_counselor_repo admin_cnsl_repo;
	
	
	public String generateEmpNo() {
		
		Date date = new Date();  
		SimpleDateFormat sf = new SimpleDateFormat("yyyy");
		String today = sf.format(date);
		
		// 2. 오늘 생성된 마지막 사번 조회
	    String lastEmpNo = this.admin_cnsl_repo.findLastEmpNoByToday(today);  //C2025003

	    int nextSeq = 1;
	    if (lastEmpNo != null && lastEmpNo.length() == 8) {
	        // 마지막 3자리만 추출해서 숫자로 변환
	        String seqStr = lastEmpNo.substring(5);  // "003"
	        nextSeq = Integer.parseInt(seqStr) + 1;
	    }

	    // 다음 사번 반환 (예: 2025004)
	    return String.format("%s%03d", today, nextSeq);
	}

}
