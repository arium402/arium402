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
@Table(name = "cnlr_schd_slot",
       uniqueConstraints = @UniqueConstraint(columnNames = {"schd_id", "cnsl_time"}))
public class Cnlr_SchdSlot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private Long slotId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schd_id", nullable = false)
    private Cnlr_Schd cnlrSchd;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_type_id", nullable = false)
    private Cnlr_SlotType cnlrSlotType;
    
    @Column(name = "cnsl_time")
    private LocalTime cnslTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "std_id")
    private Std_Info stdInfo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cnsl_aply_id")
    private Cnsl_Aply cnslAply;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private LocalDateTime updDt;
}