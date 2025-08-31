package com.seasonthon.pleanet.Challenge.repository;

import com.seasonthon.pleanet.Challenge.domain.Challenge;
import com.seasonthon.pleanet.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    Optional<Challenge> findByTitle(String title);

}
