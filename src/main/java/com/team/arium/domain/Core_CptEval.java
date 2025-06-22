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
@Table(name = "CORE_CPT_EVAL")
public class Core_CptEval {
    
    @Id
    @Column(name = "EVAL_ID", length = 10)
    private String evalId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QST_ID", nullable = false)
    private Core_CptQst coreCptQst;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STD_ID", nullable = false)
    private Std_Info stdInfo;
    
    @Column(name = "ANS_SCORE", nullable = false)
    private Integer ansScore;
    
    @Column(name = "ANS_DT", nullable = false)
    private LocalDateTime ansDt;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
}
