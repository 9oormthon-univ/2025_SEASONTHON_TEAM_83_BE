package com.seasonthon.pleanet.Challenge.domain;

import com.seasonthon.pleanet.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import com.seasonthon.pleanet.global.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberChallenge extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Member와 N:1 연관관계 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false) // DB 컬럼 이름에 맞춤
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @Enumerated(EnumType.STRING)
    private ChallengeStatus status;

    private LocalDateTime endedAt;

    private Boolean rewardGranted;

    @Column(columnDefinition = "json")
    private String extraData;
}


