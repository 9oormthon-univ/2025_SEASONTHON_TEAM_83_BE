package com.seasonthon.pleanet.search.repository;

import com.seasonthon.pleanet.member.domain.Member;
import com.seasonthon.pleanet.search.domain.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    // 유저별 최근 검색어
    List<SearchHistory> findByMemberOrderByCreatedAtDesc(Member member);
}

