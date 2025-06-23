package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cnlr_schd",
       uniqueConstraints = @UniqueConstraint(columnNames = {"empl_id", "work_year", "work_month", "work_day"}))
public class Cnlr_Schd {
//상담사 근무 시간표 테이블    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schd_id")
    private Integer schdId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empl_id", nullable = false)
    private Empl_Info emplInfo;
    
    @Column(name = "work_year", length = 4, nullable = false)
    private String workYear;
    
    @Column(name = "work_month", length = 2, nullable = false)
    private String workMonth;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_day", nullable = false)
    private Common_Code workDay;
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private LocalDateTime updDt;
    
    @Builder.Default
    @OneToMany(mappedBy = "cnlrschd", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cnlr_SchdSlot> slots = new ArrayList<>();
}