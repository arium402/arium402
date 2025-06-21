package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CNLR_SCHD_SLOT",
       uniqueConstraints = @UniqueConstraint(columnNames = {"SCHD_ID", "CNSL_TIME"}))
public class Cnlr_SchdSlot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SLOT_ID")
    private Long slotId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHD_ID", nullable = false)
    private Cnlr_Schd cnlrSchd;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SLOT_TYPE_ID", nullable = false)
    private Cnlr_SlotType cnlrSlotType;
    
    @Column(name = "CNSL_TIME")
    private LocalTime cnslTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STD_ID")
    private Std_Info stdInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CNSL_APLY_ID")
    private Cnsl_Aply cnslAply;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
}