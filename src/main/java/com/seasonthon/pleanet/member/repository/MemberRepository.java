package com.seasonthon.pleanet.member.repository;

import com.seasonthon.pleanet.member.domain.Member;
import com.seasonthon.pleanet.member.domain.enums.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findBySocialId(Long socialId);

}
