package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cnlr_slot_type")
public class Cnlr_SlotType {
//상담시간 유형 
	
    @Id
    @Column(name = "slot_type_id")
    private Integer slotTypeId;
    
    @Column(name = "slot_name", length = 20, nullable = false)
    private String slotName;
    
    @Column(name = "start_time", nullable = false)
    private String startTime;
    
    @Column(name = "end_time", nullable = false)
    private String endTime;
}