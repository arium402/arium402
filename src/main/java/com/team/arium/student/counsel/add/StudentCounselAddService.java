package com.team.arium.student.counsel.add;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.arium.domain.Cnlr_Schd;
import com.team.arium.domain.Cnsl_Aply;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentCounselAddService {

	@Autowired
	private StudentCounselAddRepository ar;
	
	// 기본 데이터만 조회
	public Map<String, Object> getScheduleData(String startDate) {
		
		// 날짜가 없으면 이번 주 월요일로 설정
		if (startDate == null || startDate.isEmpty()) {
			startDate = getCurrentMondayDate();
		}
		
		// 해당 월의 모든 상담사 스케줄 가져오기
		String[] dateParts = startDate.split("-");
		String year = dateParts[0];
		String month = dateParts[1];
		
		List<Cnlr_Schd> schedules = this.ar.findByYearAndMonth(year, month);
		
		// DTO로 변환
		List<StudentCounselAddDTO> counselors = new ArrayList<>();
		
		for (Cnlr_Schd schedule : schedules) {
			StudentCounselAddDTO dto = new StudentCounselAddDTO();
			dto.setEmplId(schedule.getEmplInfo().getEmplId());
			dto.setEmplName(schedule.getEmplInfo().getEmplName());
			dto.setCode(schedule.getWorkDay().getCode());	// 요일명 (월,화,수,목,금)
			dto.setStartTime(schedule.getStartTime());
			dto.setEndTime(schedule.getEndTime());
			
			// 실제 예약 여부 확인해서 status 설정
			String status = checkBookingStatus(schedule.getEmplInfo().getEmplId(), startDate);
			dto.setStatus(status);
			
			counselors.add(dto);
		}
		
		// 결과 반환
		Map<String, Object> result = new HashMap<>();
		result.put("counselors", counselors);
		result.put("startDate", startDate);
		result.put("endDate", calculateEndDate(startDate));
		result.put("timeSlots", Arrays.asList("09:00 ~ 10:00", "10:00 ~ 11:00", "11:00 ~ 12:00", "12:00 ~ 13:00", "13:00 ~ 14:00", "14:00 ~ 15:00", "15:00 ~ 16:00"));
		result.put("days", Arrays.asList("월", "화", "수", "목", "금"));
		
		// 화면 표시용 날짜 (yyyy.MM.dd)
		result.put("displayStartDate", startDate.replace("-", "."));
		result.put("displayEndDate", calculateEndDate(startDate).replace("-", "."));
		
		return result;
	}
	
	// 주 종료일 계산 (금요일)
	private String calculateEndDate(String startDate) {
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(startDate));
			cal.add(Calendar.DAY_OF_MONTH, 4);	// 월요일 + 4일 = 금요일
			
			return sdf.format(cal.getTime());
		}
		catch(Exception e) {
			return startDate;
		}
	}
	
	// 예약 상태 확인
	private String checkBookingStatus(Integer emplId, String date) {
		
		// 해당 상담사의 해당 날짜 예약 조회
		List<Cnsl_Aply> bookings = this.ar.findBookingsByEmplAndDate(emplId, date);
		
		// 예약이 있으면 BOOKED, 없으면 AVAILABLE
		return bookings.isEmpty() ? "AVAILABLE" : "BOOKED";
	}
	
	// 이번주 월요일 날짜 구하기
	private String getCurrentMondayDate() {
		
		Calendar cal = Calendar.getInstance();
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int daysToSubtract = (dayOfWeek == Calendar.SUNDAY) ? 6 : (dayOfWeek - Calendar.MONDAY);
		cal.add(Calendar.DAY_OF_MONTH, -daysToSubtract);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		return sdf.format(cal.getTime());
	}
}
