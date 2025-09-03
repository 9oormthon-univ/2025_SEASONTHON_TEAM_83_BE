package com.seasonthon.pleanet.search.controller;

import com.seasonthon.pleanet.apiPayload.ApiResponse;
import com.seasonthon.pleanet.common.config.security.CustomUserDetails;
import com.seasonthon.pleanet.search.dto.SearchHistoryResponse;
import com.seasonthon.pleanet.search.service.SearchHistoryService;
import com.seasonthon.pleanet.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}

