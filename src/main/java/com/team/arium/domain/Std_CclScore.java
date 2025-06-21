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
@Table(name = "STD_CCL_SCORE")
public class Std_CclScore {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CCL_SCORE_ID")
    private Long cclScoreId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STD_ID", nullable = false)
    private Std_Info stdInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CCL_ID", nullable = false)
    private Core_CptInfo coreCptInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCORE_TYPE", nullable = false)
    private Common_Code scoreType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVAL_ID")
    private Core_CptEval coreCptEval;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CMP_ID")
    private Ncs_CmpInfo ncsCmpInfo;
    
    @Column(name = "SCORE", nullable = false)
    @Builder.Default
    private Integer score = 0;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
}