package com.seasonthon.pleanet.search.service;

import com.seasonthon.pleanet.apiPayload.code.status.ErrorStatus;
import com.seasonthon.pleanet.apiPayload.exception.GeneralException;
import com.seasonthon.pleanet.search.dto.SearchHistoryResponse;
import com.seasonthon.pleanet.search.repository.SearchHistoryRepository;
import com.seasonthon.pleanet.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // 최근 검색어 단일 삭제
    public List<SearchHistoryResponse> deleteSearchHistory(Member member, Long historyId) {
        var history = searchHistoryRepository.findByIdAndMember(historyId, member)
                .orElseThrow(() -> new GeneralException(ErrorStatus._SEARCH_HISTORY_NOT_FOUND));

        searchHistoryRepository.delete(history);

        // 삭제 후 남아있는 최근 5개 기록 반환
        return searchHistoryRepository.findTop5ByMemberOrderByCreatedAtDesc(member)
                .stream()
                .map(h -> new SearchHistoryResponse(h.getKeyword()))
                .toList();
    }
}

