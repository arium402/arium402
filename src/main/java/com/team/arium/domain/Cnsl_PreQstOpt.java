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
@Table(name = "cnsl_pre_qst_opt",
       uniqueConstraints = @UniqueConstraint(columnNames = {"ore_qst_id", "opt_ord"}))
public class Cnsl_PreQstOpt {
//상담 사전 검사 보기 
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pre_opt_id")
    private Integer preOptId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_qst_id", nullable = false)
    private Cnsl_PreQst cnslPreQst;
    
    @Column(name = "opt_content", length = 200, nullable = false)
    private String optContent;
    
    @Column(name = "opt_ord", nullable = false)
    private Integer optOrd;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private String regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private String updDt;
}