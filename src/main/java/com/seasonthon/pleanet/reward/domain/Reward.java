package com.seasonthon.pleanet.reward.domain;

import com.seasonthon.pleanet.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reward")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reward_id")
    private Long id;

    /** 교환한 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    /** 리워드 종류 (zeropay, tree) */
    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false)
    private RewardType rewardType;

    /** 사용 포인트 */
    @Column(name = "point_used", nullable = false)
    private Integer pointUsed;

    /** 전환 수량 (예: 나무 3그루) */
    private Integer quantity;

    /** 전환 일시 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}


