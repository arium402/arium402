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
@Table(name = "cnsl_pre_eval_master")
public class Cnsl_PreEvalMaster {
//상담 사전 검사 실시 정보 
	
    @Id
    @Column(name = "pre_eval_id", length = 10)
    private String preEvalId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "std_id", nullable = false)
    private Std_Info stdInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_survey_id", nullable = false)
    private Cnsl_PreInfo cnslPreInfo;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private String regDt;
    
    @Builder.Default
    @OneToMany(mappedBy = "cnslPreEvalMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cnsl_PreEval> evaluations = new ArrayList<>();
}