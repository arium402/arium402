package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dgstfn_eval",
       uniqueConstraints = @UniqueConstraint(columnNames = {"prg_id", "std_id", "sur_id"}))
public class Dgstfn_Eval {
//만족도 조사 실시 
	
    @Id
    @Column(name = "sur_eval_id", length = 10)
    private String surEvalId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prg_id", nullable = false)
    private Ncs_PrgInfo ncsPrgInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "std_id", nullable = false)
    private Std_Info stdInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sur_id", nullable = false)
    private Dgstfn_Qst dgstfnQst;
    
    @Column(name = "ans_score", nullable = false)
    private Integer ansScore;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private String regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private String updDt;
}