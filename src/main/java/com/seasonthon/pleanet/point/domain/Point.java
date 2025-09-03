package com.seasonthon.pleanet.point.domain;

import com.seasonthon.pleanet.Challenge.domain.MemberChallenge;
import com.seasonthon.pleanet.attendance.domain.Attendance;
import com.seasonthon.pleanet.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "point")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;

    /** 대상 사용자 (Member와 다대일 관계) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    /** 출석 ID (Attendance와 다대일 관계, null 허용) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", nullable = true)
    private Attendance attendance;

    /** 챌린지 ID (추후 MemberChallenge 테이블과 연결, 지금은 null 허용) */
//    private Long memberChallengeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberChallenge_id", nullable = true)
    private MemberChallenge memberChallenge;

    /** 포인트 양 (+적립, -사용) */
    private int amount;

    /** 적립/사용 구분 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointType type;

    /** 설명 (예: 출석 체크, 걷기 인증 성공 등) */
    private String description;

    /** 생성 시각 */
    private LocalDateTime createdAt = LocalDateTime.now();
}
