package com.team.arium.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import org.springframework.stereotype.Repository;

@Repository("admin_module")
public class admin_module {
	//현재 날짜 (yyyy-MM-dd)
	public String todays_module() {
		LocalDate parisNow = LocalDate.now(ZoneId.of("Asia/Seoul"));
		return String.valueOf(parisNow);
	}
	//현재 날짜+시간 (yyyy-MM-dd HH:mm:ss)
	public String datetime_module() {
		LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
		return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
	//랜덤 3자리 숫자
	public String code_random() {
		String results = "";
		Random random = new Random();
		for(int a = 1; a<=3; a++) {
			results += random.nextInt(10);
		}
		return results;
	}
}
