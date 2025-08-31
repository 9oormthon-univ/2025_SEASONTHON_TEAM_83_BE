package com.seasonthon.pleanet.Challenge.domain;

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

    private Long memberId;

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


