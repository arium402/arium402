package com.team.arium.admin.mileage;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MileageStatisticsDTO {
    
    // 전체 통계
    private Integer totalPrograms;              // 전체 프로그램 수
    private Integer completedPrograms;          // 완료된 프로그램 수
    private Integer pendingPrograms;            // 대기 프로그램 수
    
    // 마일리지 통계
    private Integer totalMileagePaid;           // 총 지급된 마일리지
    private Integer totalParticipants;          // 총 참가자 수
    private Integer totalMileageRecipients;     // 총 마일리지 수령자 수
    
    // 월별/기간별 통계
    private List<MonthlyStatDTO> monthlyStats;

    // ✅ 명시적 Getter/Setter 추가
    public Integer getTotalPrograms() {
        return totalPrograms;
    }

    public void setTotalPrograms(Integer totalPrograms) {
        this.totalPrograms = totalPrograms;
    }

    public Integer getCompletedPrograms() {
        return completedPrograms;
    }

    public void setCompletedPrograms(Integer completedPrograms) {
        this.completedPrograms = completedPrograms;
    }

    public Integer getPendingPrograms() {
        return pendingPrograms;
    }

    public void setPendingPrograms(Integer pendingPrograms) {
        this.pendingPrograms = pendingPrograms;
    }

    public Integer getTotalMileagePaid() {
        return totalMileagePaid;
    }

    public void setTotalMileagePaid(Integer totalMileagePaid) {
        this.totalMileagePaid = totalMileagePaid;
    }

    public Integer getTotalParticipants() {
        return totalParticipants;
    }

    public void setTotalParticipants(Integer totalParticipants) {
        this.totalParticipants = totalParticipants;
    }

    public Integer getTotalMileageRecipients() {
        return totalMileageRecipients;
    }

    public void setTotalMileageRecipients(Integer totalMileageRecipients) {
        this.totalMileageRecipients = totalMileageRecipients;
    }

    public List<MonthlyStatDTO> getMonthlyStats() {
        return monthlyStats;
    }

    public void setMonthlyStats(List<MonthlyStatDTO> monthlyStats) {
        this.monthlyStats = monthlyStats;
    }

    // 내부 클래스도 Getter/Setter 추가
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyStatDTO {
        private String month;                   // 월 (YYYY-MM)
        private Integer programCount;           // 프로그램 수
        private Integer participantCount;       // 참가자 수
        private Integer mileageAmount;          // 마일리지 총액

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public Integer getProgramCount() {
            return programCount;
        }

        public void setProgramCount(Integer programCount) {
            this.programCount = programCount;
        }

        public Integer getParticipantCount() {
            return participantCount;
        }

        public void setParticipantCount(Integer participantCount) {
            this.participantCount = participantCount;
        }

        public Integer getMileageAmount() {
            return mileageAmount;
        }

        public void setMileageAmount(Integer mileageAmount) {
            this.mileageAmount = mileageAmount;
        }
    }
}