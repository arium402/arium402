package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cnsl_pre_info")
public class Cnsl_PreInfo {
//상담 사전 검사 정보  
  
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pre_servey_id")
    private Long preSurveyId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_type_cd", nullable = false, unique = true)
    private Common_Code preTypeCd;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_Dt", insertable = false)
    private LocalDateTime updDt;
    
    @Builder.Default
    @OneToMany(mappedBy = "cnslPreInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cnsl_PreQst> questions = new ArrayList<>();
}