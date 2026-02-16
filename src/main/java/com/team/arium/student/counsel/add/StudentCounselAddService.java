package com.team.arium.student.counsel.add;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
		
		// 고정된 운영시간 (9시-16시, 1시간 단위)
		List<String> timeSlots = Arrays.asList(
			"09:00 ~ 10:00", "10:00 ~ 11:00", "11:00 ~ 12:00",
			"12:00 ~ 13:00", "13:00 ~ 14:00", "14:00 ~ 15:00", "15:00 ~ 16:00"
		);
		
		// 동적으로 실제 스케줄이 있는 요일만 수집
		List<String> dynamicDays = new ArrayList<>();
		
		// DTO로 변환하면서 동시에 실제 스케줄 있는 요일 수집
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
			
			// 실제 스케줄이 있는 요일만 수집 (중복 체크)
			String day = schedule.getWorkDay().getCode();
			
			if (!dynamicDays.contains(day)) {
				dynamicDays.add(day);
			}
		}
		
		// 스케줄이 없으면 기본 요일 표시 (월~금)
		if (dynamicDays.isEmpty()) {
			dynamicDays = Arrays.asList("월", "화", "수", "목", "금");
		}
		
		// 결과 반환
		Map<String, Object> result = new HashMap<>();
		result.put("counselors", counselors);
		result.put("startDate", startDate);
		result.put("endDate", calculateEndDate(startDate));
		result.put("timeSlots", timeSlots);
		result.put("days", dynamicDays); 
		
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
	
	// 특정 학생의 상담 신청 내역 조회 (페이징)
	public Page<StudentCounselAddDTO> getCnslAplyList(Integer stdId, Pageable pageable) {
		stdId = 1;

		Page<Cnsl_Aply> entityPage = this.ar.findCnslAplyByStdId(stdId, pageable);
		
		// Entity List를 DTO List로 변환
		List<StudentCounselAddDTO> dtoList = new ArrayList<>();
		for (Cnsl_Aply entity : entityPage.getContent()) {
			StudentCounselAddDTO dto = convertToDto(entity);
			dtoList.add(dto);
		}
		
		// 새로운 Page 객체 생성
		return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
	}
	
	// Entity를 DTO로 변환
	private StudentCounselAddDTO convertToDto(Cnsl_Aply entity) {
		StudentCounselAddDTO dto = new StudentCounselAddDTO();
		
		dto.setCnslAplyId(entity.getCnslAplyId());
		dto.setEmplName(entity.getEmplInfo().getEmplName());
		
		// 상담 분류는 cnslPreEvalMaster → cnslPreInfo → preTypeCd(공통코드) → codeName
		String preEvalType = entity.getCnslPreEvalMaster().getCnslPreInfo().getPreTypeCd().getCode();
		dto.setPreEvalType(preEvalType);
		
		dto.setRegDt(entity.getRegDt());
		
		// 날짜 기준으로 상담 현황 자동 계산
		String cnslStatus = calculateCnslStatus(entity.getCnclDt());
		dto.setCnslStatus(cnslStatus);
		
		// CSS 클래스 설정 (공통코드 기준)
		if ("심리".equals(preEvalType)) {
			dto.setPreEvalTypeCode("category-psychology");
		}
		else if ("진로/취업".equals(preEvalType)) {
			dto.setPreEvalTypeCode("category-career");
		}
		else if ("학습 컨설팅".equals(preEvalType)) {
			dto.setPreEvalTypeCode("category-learning");
		}
		else if ("익명".equals(preEvalType)) {
			dto.setPreEvalTypeCode("category-anonymous");
		}
		else if ("위기".equals(preEvalType)) {
			dto.setPreEvalTypeCode("category-crisis");
		}
		
		// 상담 현황별 CSS 클래스
		if ("신청".equals(cnslStatus)) {
			dto.setCnslStatusCode("status-waiting");
		}
		else if ("진행".equals(cnslStatus)) {
			dto.setCnslStatusCode("status-ongoing");
		}
		else if ("종료".equals(cnslStatus)) {
			dto.setCnslStatusCode("status-completed");
		}
		
		return dto;
	}
	
	// 상담 날짜 기준으로 현황 계산
	private String calculateCnslStatus(String cnclDt) {
		try {
			// 현재 날짜
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			String today = sdf.format(cal.getTime());
			
			// 날짜 문자열 비교
			if (today.compareTo(cnclDt) < 0) {
				return "신청";	// 현재 날짜 < 상담 날짜
			}
			else if (today.equals(cnclDt)) {
				return "진행";	// 현재 날짜 = 상담 날짜
			}
			else {
				return "종료";	// 현재 날짜 > 상담 날짜
			}
		}
		catch (Exception e) {
			return "신청";	// 오류시 기본값
		}
	}
}
