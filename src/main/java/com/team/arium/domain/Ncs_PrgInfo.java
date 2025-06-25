package com.team.arium.domain;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ncs_prg_info")
public class Ncs_PrgInfo {
//비교과 정보 
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prg_id")
    private Integer prgId;
    
    @Column(name = "prg_cd", length = 10, nullable = false, unique = true)
    private String prgCd;
    
    @Column(name = "prg_nm", length = 100, nullable = false)
    private String prgNm;
    
    @Column(name = "prg_desc", length = 500, nullable = false)
    private String prgDesc;
    
    @Column(name = "prg_st_dt", nullable = false)
    private String prgStDt;
    
    @Column(name = "prg_end_dt", nullable = false)
    private String prgEndDt;
    
    @Column(name = "max_cnt", nullable = false)
    private Integer maxCnt;
    
    @Column(name = "mlg_def_score", nullable = false)
    private Integer mlgDefScore;
    
    @Column(name = "survey_dt", nullable = false)
    private String surveyDt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private Common_File comFile;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prg_stat_cd", nullable = false)
    private Common_Code prgStatCd;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private String regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private String updDt;
    
    @Builder.Default
    @OneToMany(mappedBy = "ncsPrgInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ncs_PrgAply> applications = new ArrayList<>();
}