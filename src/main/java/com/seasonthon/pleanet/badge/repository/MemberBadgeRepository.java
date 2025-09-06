package com.seasonthon.pleanet.badge.repository;

import com.seasonthon.pleanet.badge.domain.MemberBadge;
import org.springdoc.core.providers.JavadocProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberBadgeRepository extends JpaRepository<MemberBadge,Long> {
    int countByMember_Id(Long memberId);

    // 멤버가 획득한 뱃지 목록 조회
    List<MemberBadge> findAllByMemberId(Long memberId);

    boolean existsByMemberIdAndBadgeId(Long memberId, Long badgeId);
}
