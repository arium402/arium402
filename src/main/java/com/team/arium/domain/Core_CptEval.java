package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "core_cpt_eval")
public class Core_CptEval {
//핵심 역량 진단 실시    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eval_no")
    private Integer evalNo;
    
    @Column(name = "eval_id", length = 10, nullable = false)
    private String evalId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qst_id", nullable = false)
    private Core_CptQst coreCptQst;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "std_id", nullable = false)
    private Std_Info stdInfo;
    
    @Column(name = "ans_score", nullable = false)
    private Integer ansScore;
    
    @Column(name = "ans_dt", nullable = false)
    private String ansDt;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private String regDt;
}