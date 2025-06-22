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
@Table(name = "DGSTFN_EVAL",
       uniqueConstraints = @UniqueConstraint(columnNames = {"STD_ID", "SUR_ID"}))
public class Dgstfn_Eval {
    
    @Id
    @Column(name = "SUR_EVAL_ID", length = 10)
    private String surEvalId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STD_ID", nullable = false)
    private Std_Info stdInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUR_ID", nullable = false)
    private Dgstfn_Qst dgstfnQst;
    
    @Column(name = "ANS_SCORE", nullable = false)
    private Integer ansScore;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
}