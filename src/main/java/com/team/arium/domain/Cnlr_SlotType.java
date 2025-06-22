package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CNLR_SLOT_TYPE")
public class Cnlr_SlotType {
    
    @Id
    @Column(name = "SLOT_TYPE_ID")
    private Integer slotTypeId;
    
    @Column(name = "SLOT_NAME", length = 20, nullable = false)
    private String slotName;
    
    @Column(name = "START_TIME", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "END_TIME", nullable = false)
    private LocalTime endTime;
}