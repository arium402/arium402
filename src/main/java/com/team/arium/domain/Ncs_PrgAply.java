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
@Table(name = "ncs_prg_aply",
       uniqueConstraints = @UniqueConstraint(columnNames = {"std_id", "prg_id"}))
public class Ncs_PrgAply {
//비교과 신청 
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aply_id")
    private Long aplyId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prg_id", nullable = false)
    private Ncs_PrgInfo ncsPrgInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "std_id", nullable = false)
    private Std_Info stdInfo;
    
    @Column(name = "aply_dt", nullable = false)
    private LocalDateTime aplyDt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aply_stat_cd", nullable = false)
    private Common_Code aplyStatCd;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private LocalDateTime updDt;
}