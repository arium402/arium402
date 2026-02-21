package com.team.arium.admin.mileage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 관리자 마일리지 전환 신청 목록 DTO
 * 학생의 마일리지 → 장학금 전환 신청 내역을 표시
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MileageConversionDTO {
    

    // 기본 식별 정보

    //마일리지 사용 ID (PK)
    private Integer mlgUseId;
    //학생 ID
    private Integer stdId;
    
    // 학생 정보

    //학번
    private String stdNo;
    //이름
    private String stdNm;
    //학과명
    private String deptNm;
    
    // 신청 정보

    //신청 마일리지 점수
    private Integer appliedMile;
    //신청일자 (yyyy-MM-dd)
    private String applyDate;
    //신청일자 포맷팅 (yyyy.MM.dd)
    private String applyDateFmt;
    
    // 상태 정보
    //상태 코드 (81=대기, 83=완료)
    private Integer statusCd;
    //상태명 (대기/완료)
    private String statusNm;
    //상태 배지 CSS 클래스 (status-waiting / status-completed)
    private String statusClass;
    
    // 계좌 정보

    //은행명
    private String bankNm;
    //계좌번호
    private String accountNo;
    //예금주명
    private String depositor;
    
    // 전환 정보 (계산된 값)
    /**
     * 전환 금액 (원)
     * 계산식: appliedMile * 10
     * 예: 100P → 1,000원, 1500P → 15,000원
     */
    private Integer convertMoney;
    //전환 금액 포맷팅
    private String convertMoneyFmt;
    
    // 지급 정보 (완료된 경우)
    
    //지급일자 (yyyy-MM-dd)
    private String payDate;
    //지급일자 포맷팅 (yyyy.MM.dd)
    private String payDateFmt;
    //실제 지급 금액 (DB의 pay_money)
    private Integer paidMoney;

    // 편의 메서드
    
    //대기 상태 여부
    public boolean isWaiting() {
        return Integer.valueOf(81).equals(statusCd);
    }
    
    //완료 상태 여부
    public boolean isCompleted() {
        return Integer.valueOf(83).equals(statusCd);
    }
    
    //승인 가능 여부 (대기 상태만 가능)
    public boolean canApprove() {
        return isWaiting();
    }
}