package com.seasonthon.pleanet.search.repository;

import com.seasonthon.pleanet.member.domain.Member;
import com.seasonthon.pleanet.search.domain.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    // 유저별 최근 검색어
    List<SearchHistory> findByMemberOrderByCreatedAtDesc(Member member);

    // 유저의 최근 검색어 5개 (최신순)
    List<SearchHistory> findTop5ByMemberOrderByCreatedAtDesc(Member member);

    // 특정 유저의 검색 기록 단건 조회 (보안용: 다른 사람 기록 삭제 방지)
    Optional<SearchHistory> findByIdAndMember(Long id, Member member);
}

