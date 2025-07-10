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
@Table(name = "cnsl_pre_eval")
@IdClass(Cnsl_PreEvalId.class)
public class Cnsl_PreEval {
// 상담 사전검사 실시 
	
    @Id
    @Column(name = "pre_eval_id", length = 10)
    private String preEvalId;
    
    @Id
    @Column(name = "pre_qst_id")
    private Integer preQstId;
    
    @Id
    @Column(name = "pre_opt_id")
    private Integer preOptId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_eval_id", insertable = false, updatable = false)
    private Cnsl_PreEvalMaster cnslPreEvalMaster;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_qst_id", insertable = false, updatable = false)
    private Cnsl_PreQst cnslPreQst;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_opt_id")
    private Cnsl_PreQstOpt cnslPreQstOpt;
    
    @Lob
    @Column(name = "ans_text")
    private String ansText;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private String regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private String updDt;
}
