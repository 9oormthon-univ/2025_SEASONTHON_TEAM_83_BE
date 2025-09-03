package com.seasonthon.pleanet.search.service;

import com.seasonthon.pleanet.search.dto.SearchHistoryResponse;
import com.seasonthon.pleanet.search.repository.SearchHistoryRepository;
import com.seasonthon.pleanet.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;

    // 유저의 최근 검색어 5개 조회
    public List<SearchHistoryResponse> getRecentSearches(Member member) {
        return searchHistoryRepository.findTop5ByMemberOrderByCreatedAtDesc(member)
                .stream()
                .map(h -> new SearchHistoryResponse(h.getKeyword()))
                .toList();
    }
}

