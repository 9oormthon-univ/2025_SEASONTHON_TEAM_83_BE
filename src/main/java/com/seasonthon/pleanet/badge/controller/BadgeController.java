package com.seasonthon.pleanet.badge.controller;

import com.seasonthon.pleanet.apiPayload.ApiResponse;
import com.seasonthon.pleanet.badge.domain.MemberBadge;
import com.seasonthon.pleanet.badge.dto.res.MemberBadgeResponse;
import com.seasonthon.pleanet.badge.service.BadgeService;
import com.seasonthon.pleanet.common.config.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    @PostMapping("/check-and-assign")
    public ApiResponse<List<MemberBadgeResponse>> checkAndAssign(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<MemberBadge> badges = badgeService.checkAndAssignBadges(userDetails.getId());

        List<MemberBadgeResponse> response = badges.stream()
                .map(MemberBadgeResponse::new)
                .toList();

        return ApiResponse.onSuccess(response);
    }
}
