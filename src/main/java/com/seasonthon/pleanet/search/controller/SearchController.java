package com.seasonthon.pleanet.search.controller;

import com.seasonthon.pleanet.apiPayload.ApiResponse;
import com.seasonthon.pleanet.common.config.security.CustomUserDetails;
import com.seasonthon.pleanet.search.dto.SearchResultResponse;
import com.seasonthon.pleanet.search.service.SearchService;
import com.seasonthon.pleanet.common.config.security.CustomUserDetails;
import com.seasonthon.pleanet.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ApiResponse<SearchResultResponse> search(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String keyword
    ) {
        Member member = userDetails.getMember();
        SearchResultResponse result = searchService.search(member, keyword);
        return ApiResponse.onSuccess(result);
    }
}
