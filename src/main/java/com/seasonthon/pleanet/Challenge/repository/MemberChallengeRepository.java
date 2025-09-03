package com.seasonthon.pleanet.Challenge.repository;

import com.seasonthon.pleanet.Challenge.domain.ChallengeStatus;
import com.seasonthon.pleanet.Challenge.domain.MemberChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MemberChallengeRepository extends JpaRepository<MemberChallenge, Long> {
    boolean existsByMemberIdAndChallengeIdAndStatus(Long memberId, Long challengeId, ChallengeStatus status);

    boolean existsByMemberIdAndChallengeIdAndCreatedAtBetween(
            Long memberId,
            Long challengeId,
            LocalDateTime start,
            LocalDateTime end
    );

}
