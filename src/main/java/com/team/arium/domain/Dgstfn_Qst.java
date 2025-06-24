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
@Table(name = "dgstfn_qst",
       uniqueConstraints = @UniqueConstraint(columnNames = {"survey_id", "sur_drd"}))
public class Dgstfn_Qst {
//만족도 조사 문항 
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sur_id")
    private Integer surId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Dgstfn_Info dgstfnInfo;
    
    @Column(name = "sur_content", length = 500, nullable = false)
    private String surContent;
    
    @Column(name = "sur_ord", nullable = false)
    private Integer surOrd;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private String regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private String updDt;
}