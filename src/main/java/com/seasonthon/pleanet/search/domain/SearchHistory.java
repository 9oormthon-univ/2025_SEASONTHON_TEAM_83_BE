package com.seasonthon.pleanet.search.domain;

import com.seasonthon.pleanet.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_history")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 검색한 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    /** 검색 키워드 */
    @Column(nullable = false)
    private String keyword;

    /** 검색 시각 */
    private LocalDateTime createdAt;
}

