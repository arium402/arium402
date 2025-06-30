package com.team.arium.admin;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;

import org.springframework.stereotype.Repository;

@Repository("admin_module")
public class admin_module {
	
	public String todays_module() {
		LocalDate parisNow = LocalDate.now(ZoneId.of("Asia/Seoul"));
		return String.valueOf(parisNow);
	}
	
	public String code_random() {
		String results = "";
		Random random = new Random();
		for(int a = 1; a<=3; a++) {
			results += random.nextInt(10);
		}
		return results;
	}
}
