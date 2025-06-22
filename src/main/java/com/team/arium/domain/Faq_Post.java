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
@Table(name = "FAQ_POST")
public class Faq_Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FAQ_ID")
    private Long faqId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NTC_ID", nullable = false)
    private Pst_Ntc pstNtc;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_TYPE", nullable = false)
    private Common_Code postType;
    
    @Column(name = "SUB_CAT_NM", length = 100, nullable = false)
    private String subCatNm;
    
    @Lob
    @Column(name = "QUESTION", nullable = false)
    private String question;
    
    @Lob
    @Column(name = "ANSWER", nullable = false)
    private String answer;
    
    @CreationTimestamp
    @Column(name = "REG_DT", nullable = false, updatable = false)
    private LocalDateTime regDt;
    
    @UpdateTimestamp
    @Column(name = "UPD_DT", insertable = false)
    private LocalDateTime updDt;
}