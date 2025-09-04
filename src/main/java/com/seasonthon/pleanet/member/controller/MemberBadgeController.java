package com.seasonthon.pleanet.member.controller;

import com.seasonthon.pleanet.apiPayload.ApiResponse;
import com.seasonthon.pleanet.badge.dto.res.BadgeResponseDto;
import com.seasonthon.pleanet.common.config.security.CustomUserDetails;
import com.seasonthon.pleanet.member.service.MemberBadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberBadgeController {

    private final MemberBadgeService memberBadgeQueryService;

    // 로그인한 유저의 뱃지 목록 조회
    @GetMapping("/me/badges")
    public ApiResponse<List<BadgeResponseDto>> getMyBadges(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMember().getId();
        return ApiResponse.onSuccess(memberBadgeQueryService.getBadgesByMemberId(memberId));
    }
}


