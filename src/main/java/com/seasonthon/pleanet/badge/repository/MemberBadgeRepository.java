package com.seasonthon.pleanet.badge.repository;

import com.seasonthon.pleanet.badge.domain.MemberBadge;
import org.springdoc.core.providers.JavadocProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberBadgeRepository extends JpaRepository<MemberBadge,Long> {
    int countByMember_Id(Long memberId);
}
