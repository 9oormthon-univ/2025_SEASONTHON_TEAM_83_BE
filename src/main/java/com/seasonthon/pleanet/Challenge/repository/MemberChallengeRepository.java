package com.seasonthon.pleanet.Challenge.repository;

import com.seasonthon.pleanet.Challenge.domain.ChallengeStatus;
import com.seasonthon.pleanet.Challenge.domain.MemberChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MemberChallengeRepository extends JpaRepository<MemberChallenge, Long> {
    boolean existsByMemberIdAndChallengeIdAndStatus(Long memberId, Long challengeId, ChallengeStatus status);

    boolean existsByMemberIdAndChallengeIdAndCreatedAtBetween(
            Long memberId,
            Long challengeId,
            LocalDateTime start,
            LocalDateTime end
    );

    Optional<MemberChallenge> findByMemberIdAndChallengeIdAndCreatedAtBetween(
            Long memberId,
            Long challengeId,
            LocalDateTime start,
            LocalDateTime end
    );

    // 사용자의 가장 최근 참여 챌린지 조회 (생성일 내림차순 기준)
    Optional<MemberChallenge> findTopByMemberIdOrderByCreatedAtDesc(Long memberId);
}
