package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "std_ccl_score")
public class Std_CclScore {
//학생 핵심 역량 점수    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ccl_score_id")
    private Integer cclScoreId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "std_id", nullable = false)
    private Std_Info stdInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ccl_id", nullable = false)
    private Core_CptInfo coreCptInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "score_type", nullable = false)
    private Common_Code scoreType;
    
    @Column(name = "eval_id", length = 10)
    private String evalId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cmp_id")
    private Ncs_CmpInfo ncsCmpInfo;
    
    @Column(name = "score", nullable = false)
    @Builder.Default
    private Integer score = 0;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private String regDt;
}