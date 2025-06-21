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
@Table(name = "DGSTFN_INFO")
public class Dgstfn_Info {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SURVEY_ID")
    private Long surveyId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRG_ID", nullable = false, unique = true)
    private Ncs_PrgInfo ncsPrgInfo;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
    
    @Builder.Default
    @OneToMany(mappedBy = "dgstfnInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dgstfn_Qst> questions = new ArrayList<>();
}