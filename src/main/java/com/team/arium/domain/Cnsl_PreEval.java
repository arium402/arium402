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
@Table(name = "CNSL_PRE_EVAL")
@IdClass(Cnsl_PreEvalId.class)
public class Cnsl_PreEval {
    
    @Id
    @Column(name = "PRE_EVAL_ID", length = 10)
    private String preEvalId;
    
    @Id
    @Column(name = "PRE_QST_ID")
    private Long preQstId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRE_EVAL_ID", insertable = false, updatable = false)
    private Cnsl_PreEvalMaster cnslPreEvalMaster;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRE_QST_ID", insertable = false, updatable = false)
    private Cnsl_PreQst cnslPreQst;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRE_OPT_ID")
    private Cnsl_PreQstOpt cnslPreQstOpt;
    
    @Lob
    @Column(name = "ANS_TEXT")
    private String ansText;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
}
