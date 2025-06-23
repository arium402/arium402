package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ncs_cmp_info")
public class Ncs_CmpInfo {
//비교과 이수정보 
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cmp_id")
    private Long cmpId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aply_id", nullable = false, unique = true)
    private Ncs_PrgAply ncsPrgAply;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prg_id", nullable = false)
    private Ncs_PrgInfo ncsPrgInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "std_id", nullable = false)
    private Std_Info stdInfo;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "cmp_yn", nullable = false, length = 1)
    private yn cmpYn;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "survey_yn", nullable = false, length = 1)
    private yn surveyYn;
}