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
@Table(name = "dgstfn_info")
public class Dgstfn_Info {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_id")
    private Long surveyId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prg_id", nullable = false, unique = true)
    private Ncs_PrgInfo ncsPrgInfo;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private LocalDateTime updDt;
    
    @Builder.Default
    @OneToMany(mappedBy = "dgstfnInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dgstfn_Qst> questions = new ArrayList<>();
}