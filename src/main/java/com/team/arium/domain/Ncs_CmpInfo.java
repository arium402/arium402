package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "NCS_CMP_INFO")
public class Ncs_CmpInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CMP_ID")
    private Long cmpId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APLY_ID", nullable = false, unique = true)
    private Ncs_PrgAply ncsPrgAply;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRG_ID", nullable = false)
    private Ncs_PrgInfo ncsPrgInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STD_ID", nullable = false)
    private Std_Info stdInfo;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "CMP_YN", nullable = false, length = 1)
    private YN cmpYn;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "SURVEY_YN", nullable = false, length = 1)
    private YN surveyYn;
}