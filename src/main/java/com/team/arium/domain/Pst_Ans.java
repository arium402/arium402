package com.team.arium.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pst_ans")
public class Pst_Ans {
//답변게시판 
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ans_id")
    private Integer ansId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Pst_Post pstPost;
    
    @Column(name = "writer", length = 50, nullable = false)
    private String writer;
    
    @Lob
    @Column(name = "ans_cont", nullable = false)
    private String ansCont;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private String regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private String updDt;
}