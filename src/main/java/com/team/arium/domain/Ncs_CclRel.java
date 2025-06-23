package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ncs_ccl_rel")
@IdClass(Ncs_CclRelId.class)
public class Ncs_CclRel {
//비교과 프로그램 - 핵심역량 연결
	
	//'프로그램ID', '핵심 역량ID', '핵심역량 점수'
	
	
    @Id
    @Column(name = "prg_id")
    private Long prgId;
    
    @Id
    @Column(name = "ccl_id")
    private Long cclId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prg_id", insertable = false, updatable = false)
    private Ncs_PrgInfo ncsPrgInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ccl_id", insertable = false, updatable = false)
    private Core_CptInfo coreCptInfo;
    
    @Column(name = "ccl_score", nullable = false)
    private Integer cclScore;
}