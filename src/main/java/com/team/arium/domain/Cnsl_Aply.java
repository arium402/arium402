package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CNSL_APLY")
public class Cnsl_Aply {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CNSL_APLY_ID")
    private Long cnslAplyId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STD_ID", nullable = false)
    private Std_Info stdInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPL_ID", nullable = false)
    private Empl_Info emplInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRE_EVAL_ID", nullable = false)
    private Cnsl_PreEvalMaster cnslPreEvalMaster;
    
    @Column(name = "CNCL_DT", nullable = false)
    private LocalDate cnclDt;
    
    @Column(name = "CNCL_TIME", nullable = false)
    private LocalTime cnclTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CNSL_STAT_CD", nullable = false)
    private Common_Code cnslStatCd;
    
    @Lob
    @Column(name = "CNSL_NOTE")
    private String cnslNote;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
}