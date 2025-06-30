package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "std_mileage_hist",
       uniqueConstraints = @UniqueConstraint(columnNames = {"std_id", "cmp_id", "sur_eval_id"}))
public class Std_MileageHist {
//학생 마일리지 점수 
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mlg_id")
    private Integer mlgId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "std_id", nullable = false)
    private Std_Info stdInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cmp_id", nullable = false)
    private Ncs_CmpInfo ncsCmpInfo;
    
    @Column(name = "sur_eval_id", length = 10, nullable = false)
    private String surEvalId;
    
    @Column(name = "mlg_score", nullable = false)
    private Integer mlgScore;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mlg_add_cd", nullable = false)
    private Common_Code mlgAddCd;
    
    @Column(name = "mlg_dt", nullable = false)
    private String mlgDt;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private String regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private String updDt;
}