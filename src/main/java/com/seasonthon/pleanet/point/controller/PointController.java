package com.seasonthon.pleanet.point.controller;

import com.seasonthon.pleanet.apiPayload.ApiResponse;
import com.seasonthon.pleanet.common.config.security.CustomUserDetails;
import com.seasonthon.pleanet.point.dto.PointBalanceResponse;
import com.seasonthon.pleanet.point.dto.PointHistoryResponse;
import com.seasonthon.pleanet.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping("/balance")
    public ApiResponse<PointBalanceResponse> getBalance(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId(); // JWT 인증에서 꺼낸 로그인 유저 PK
        PointBalanceResponse response = pointService.getPointBalance(userId);
        return ApiResponse.onSuccess(response);
    }

    // 포인트 히스토리 조회
    @GetMapping("/history")
    public ApiResponse<List<PointHistoryResponse>> getPointHistory(
            // Long memberId 대신 CustomUserDetails를 받도록 수정
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getId();
        List<PointHistoryResponse> histories = pointService.getPointHistories(memberId);
        return ApiResponse.onSuccess(histories);
    }
}
