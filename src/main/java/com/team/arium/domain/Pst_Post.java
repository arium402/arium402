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
@Table(name = "PST_POST")
public class Pst_Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long postId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NTC_ID", nullable = false)
    private Pst_Ntc pstNtc;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_TYPE", nullable = false)
    private Common_Code postType;
    
    @Column(name = "TITLE", length = 200, nullable = false)
    private String title;
    
    @Lob
    @Column(name = "POST_CONT", nullable = false)
    private String postCont;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private Common_File comFile;
    
    @Column(name = "WRITER", length = 50, nullable = false)
    private String writer;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "FIXED_YN", nullable = false, length = 1)
    private YN fixedYn;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "PUBLIC_YN", nullable = false, length = 1)
    private YN publicYn;
    
    @Column(name = "INQ_CNT", nullable = false)
    @Builder.Default
    private Integer inqCnt = 0;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
    
    @Builder.Default
    @OneToMany(mappedBy = "pstPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pst_Ans> answers = new ArrayList<>();
}