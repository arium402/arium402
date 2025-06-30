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
@Table(name = "core_cpt_qst",
       uniqueConstraints = @UniqueConstraint(columnNames = {"ccl_id", "qst_ord"}))
public class Core_CptQst {
//핵심 역량 진단 문항   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qst_id")
    private Integer qstId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ccl_id", nullable = false)
    private Core_CptInfo coreCptInfo;
    
    @Column(name = "qst_content", length = 500, nullable = false)
    private String qstContent;
    
    @Column(name = "qst_ord", nullable = false)
    private Integer qstOrd;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private String regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private String updDt;
}