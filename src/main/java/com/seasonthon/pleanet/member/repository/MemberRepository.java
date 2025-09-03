package com.seasonthon.pleanet.member.repository;

import com.seasonthon.pleanet.member.domain.Member;
import com.seasonthon.pleanet.member.domain.enums.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);
    List<Member> findByNicknameContaining(String keyword);  // 부분 일치 검색
    Optional<Member> findBySocialId(Long socialId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
