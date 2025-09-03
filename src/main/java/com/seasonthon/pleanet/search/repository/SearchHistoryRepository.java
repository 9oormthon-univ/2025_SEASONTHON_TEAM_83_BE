package com.seasonthon.pleanet.search.repository;

import com.seasonthon.pleanet.member.domain.Member;
import com.seasonthon.pleanet.search.domain.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    // 유저별 최근 검색어
    List<SearchHistory> findByMemberOrderByCreatedAtDesc(Member member);

    // 유저의 최근 검색어 5개 (최신순)
    List<SearchHistory> findTop5ByMemberOrderByCreatedAtDesc(Member member);
}

