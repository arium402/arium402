package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pst_ntc")
public class Pst_Ntc {
//게시판 
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ntc_id")
    private Integer ntcId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ntc_type", nullable = false)
    private Common_Code ntcType;
}