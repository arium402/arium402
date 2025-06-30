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
@Table(name = "faq_post")
public class Faq_Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faq_id")
    private Integer faqId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ntc_id", nullable = false)
    private Pst_Ntc pstNtc;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_type", nullable = false)
    private Common_Code postType;
    
    @Column(name = "sub_cat_nm", length = 100, nullable = false)
    private String subCatNm;
    
    @Lob
    @Column(name = "question", nullable = false)
    private String question;
    
    @Lob
    @Column(name = "answer", nullable = false)
    private String answer;
    
    @CreationTimestamp
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private String regDt;
    
    @UpdateTimestamp
    @Column(name = "upd_dt", insertable = false)
    private String updDt;
}