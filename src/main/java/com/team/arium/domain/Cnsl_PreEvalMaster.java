package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CNSL_PRE_EVAL_MASTER")
public class Cnsl_PreEvalMaster {
    
    @Id
    @Column(name = "PRE_EVAL_ID", length = 10)
    private String preEvalId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STD_ID", nullable = false)
    private Std_Info stdInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRE_SURVEY_ID", nullable = false)
    private Cnsl_PreInfo cnslPreInfo;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @Builder.Default
    @OneToMany(mappedBy = "cnslPreEvalMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cnsl_PreEval> evaluations = new ArrayList<>();
}