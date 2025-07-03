package com.team.arium.student.counsel.add;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentCounselAddDTO {

	// 상담사 기본 정보
	private Integer emplId;	// 상담사 ID
	private String emplName;	// 상담사 이름
	
	// 스케줄 정보
	private Integer schdId;	// 시간표 ID
	private String workYear;	// 근무 년도
	private String workMonth;	// 근무 월
	private Integer workDayCode;	// 근무 요일 코드 (111~115)
	private String code;	// 근무 요일명 (월, 화, 수, 목, 금)
	private String startTime;	// 근무 시작시간
	private String endTime;	// 근무 종료시간
	
	// 예약 상태 정보
	private String cnslTime;	// 예약된 상담 시간 (있으면 예약됨, null이면 예약 가능)
	private Integer stdId;	// 예약한 학생 ID (예약된 경우만)
	private String status;	// 예약 상태 (AVAILABLE, BOOKED)
	
	// 화면 표시용
	private String timeSlot;	// 시간대 (09:00~10:00)
	private String dayName;	// 요일명 (월, 화, 수, 목, 금)
}
