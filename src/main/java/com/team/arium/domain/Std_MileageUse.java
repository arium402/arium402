package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "std_mileage_use",
       uniqueConstraints = @UniqueConstraint(columnNames = {"std_id", "aply_dt"}))
public class Std_MileageUse {
// 학생 마일리지 사용 신청 
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mlg_use_id")
    private Integer mlgUseId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "std_id", nullable = false)
    private Std_Info stdInfo;
    
    @Column(name = "aply_dt", nullable = false)
    private String aplyDt;
    
    @Column(name = "aply_mlg_score", nullable = false)
    private Integer aplyMlgScore;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mlg_use_cd", nullable = false)
    private Common_Code mlgUseCd;
    
    @Column(name = "pay_dt")
    private String payDt;
    
    @Column(name = "pay_money")
    private Integer payMoney;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private String regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private String updDt;
}