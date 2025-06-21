package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PST_NTC")
public class Pst_Ntc {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NTC_ID")
    private Long ntcId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NTC_TYPE", nullable = false)
    private Common_Code ntcType;
}