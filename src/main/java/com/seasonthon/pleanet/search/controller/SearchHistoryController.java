package com.seasonthon.pleanet.search.controller;

import com.seasonthon.pleanet.apiPayload.ApiResponse;
import com.seasonthon.pleanet.apiPayload.code.status.ErrorStatus;
import com.seasonthon.pleanet.apiPayload.exception.GeneralException;
import com.seasonthon.pleanet.common.config.security.CustomUserDetails;
import com.seasonthon.pleanet.search.dto.SearchHistoryResponse;
import com.seasonthon.pleanet.search.service.SearchHistoryService;
import com.seasonthon.pleanet.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    @GetMapping("/history")
    public ApiResponse<List<SearchHistoryResponse>> getSearchHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = userDetails.getMember();
        List<SearchHistoryResponse> result = searchHistoryService.getRecentSearches(member);
        return ApiResponse.onSuccess(result);
    }

    // 개별 검색어 삭제
    @DeleteMapping("/history/{id}")
    public ApiResponse<Map<String, Object>> deleteHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        Member member = userDetails.getMember();
        List<SearchHistoryResponse> remaining = searchHistoryService.deleteSearchHistory(member, id);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "검색 기록이 삭제되었습니다.");
        result.put("remainingHistories", remaining);

        return ApiResponse.onSuccess(result);
    }
}

