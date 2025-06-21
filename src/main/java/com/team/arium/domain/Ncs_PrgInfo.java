package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "NCS_PRG_INFO")
public class Ncs_PrgInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRG_ID")
    private Long prgId;
    
    @Column(name = "PRG_CD", length = 10, nullable = false, unique = true)
    private String prgCd;
    
    @Column(name = "PRG_NM", length = 100, nullable = false)
    private String prgNm;
    
    @Column(name = "PRG_DESC", length = 500, nullable = false)
    private String prgDesc;
    
    @Column(name = "PRG_ST_DT", nullable = false)
    private LocalDate prgStDt;
    
    @Column(name = "PRG_END_DT", nullable = false)
    private LocalDate prgEndDt;
    
    @Column(name = "MAX_CNT", nullable = false)
    private Integer maxCnt;
    
    @Column(name = "MLG_DEF_SCORE", nullable = false)
    private Integer mlgDefScore;
    
    @Column(name = "SURVEY_DT", nullable = false)
    private LocalDate surveyDt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private Common_File comFile;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRG_STAT_CD", nullable = false)
    private Common_Code prgStatCd;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
    
    @Builder.Default
    @OneToMany(mappedBy = "ncsPrgInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ncs_PrgAply> applications = new ArrayList<>();
}