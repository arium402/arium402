package com.team.arium.student.mileage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * 마일리지 알림 DTO
 * -지급 내역 + 전환 완료 내역
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StdMileNotiDTO {
	private String title;      // 프로그램명 또는 마일리지 장학금
    private Integer score;     // 마일리지 점수 (지급: +60, 전환: 2000원)
    private String dt;         // 날짜시간 (mlg_dt 또는 pay_dt)
    private String type;       // earned(지급) / converted(전환완료) / pending(전환신청)
    private String icon;       // 아이콘 클래스
    private String scoreText;  // "+60P" 또는 "2,000원"
}
