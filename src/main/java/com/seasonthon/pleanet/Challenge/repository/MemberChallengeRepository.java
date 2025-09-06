package com.seasonthon.pleanet.Challenge.repository;

import com.seasonthon.pleanet.Challenge.domain.ChallengeStatus;
import com.seasonthon.pleanet.Challenge.domain.ChallengeType;
import com.seasonthon.pleanet.Challenge.domain.MemberChallenge;
import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
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


    Optional<MemberChallenge> findTopByMemberIdOrderByCreatedAtDesc(Long memberId);

    Page<MemberChallenge> findAllByMember_Id(Long memberId, Pageable pageable);

    long countByMemberIdAndChallenge_Type(Long memberId, ChallengeType type);
    long countByMemberIdAndChallenge_TypeAndStatus(Long memberId, ChallengeType type, ChallengeStatus status);
}
