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
@Table(name = "NCS_PRG_APLY",
       uniqueConstraints = @UniqueConstraint(columnNames = {"STD_ID", "PRG_ID"}))
public class Ncs_PrgAply {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APLY_ID")
    private Long aplyId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRG_ID", nullable = false)
    private Ncs_PrgInfo ncsPrgInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STD_ID", nullable = false)
    private Std_Info stdInfo;
    
    @Column(name = "APLY_DT", nullable = false)
    private LocalDateTime aplyDt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APLY_STAT_CD", nullable = false)
    private Common_Code aplyStatCd;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
}