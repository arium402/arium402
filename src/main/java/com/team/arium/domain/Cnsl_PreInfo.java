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
@Table(name = "CNSL_PRE_INFO")
public class Cnsl_PreInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRE_SURVEY_ID")
    private Long preSurveyId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRE_TYPE_CD", nullable = false, unique = true)
    private Common_Code preTypeCd;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
    
    @Builder.Default
    @OneToMany(mappedBy = "cnslPreInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cnsl_PreQst> questions = new ArrayList<>();
}