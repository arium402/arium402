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
@Table(name = "pst_post")
public class Pst_Post {
//게시글 
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer postId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ntc_id", nullable = false)
    private Pst_Ntc pstNtc;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_type", nullable = false)
    private Common_Code postType;
    
    @Column(name = "title", length = 200, nullable = false)
    private String title;
    
    @Lob
    @Column(name = "post_cont", nullable = false)
    private String postCont;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private Common_File comFile;
    
    @Column(name = "writer", length = 50, nullable = false)
    private String writer;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "fixed_yn", nullable = false, length = 1)
    private yn fixedYn;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "public_yn", nullable = false, length = 1)
    private yn publicYn;
    
    @Column(name = "inq_cnt", nullable = false)
    @Builder.Default
    private Integer inqCnt = 0;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private String regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private String updDt;
    
    @Builder.Default
    @OneToMany(mappedBy = "pstPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pst_Ans> answers = new ArrayList<>();
}