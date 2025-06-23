package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "core_cpt_eval")
public class Core_CptEval {
//핵심 역량 진단 실시    
    @Id
    @Column(name = "eval_id", length = 10)
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
    private LocalDateTime ansDt;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private LocalDateTime regDt;
}
