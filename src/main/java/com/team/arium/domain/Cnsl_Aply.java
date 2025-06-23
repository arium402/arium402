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
@Table(name = "cnsl_aply")
public class Cnsl_Aply {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cnsl_aply_id")
    private Long cnslAplyId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "std_id", nullable = false)
    private Std_Info stdInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empl_id", nullable = false)
    private Empl_Info emplInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_eval_id", nullable = false)
    private Cnsl_PreEvalMaster cnslPreEvalMaster;
    
    @Column(name = "cncl_dt", nullable = false)
    private LocalDate cnclDt;
    
    @Column(name = "cncl_time", nullable = false)
    private LocalTime cnclTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cnsl_stat_cd", nullable = false)
    private Common_Code cnslStatCd;
    
    @Lob
    @Column(name = "cnsl_note")
    private String cnslNote;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private LocalDateTime updDt;
}