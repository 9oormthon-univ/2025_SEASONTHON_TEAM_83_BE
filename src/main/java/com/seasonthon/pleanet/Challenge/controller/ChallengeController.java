package com.seasonthon.pleanet.Challenge.controller;

import com.seasonthon.pleanet.Challenge.dto.req.ChallengeRequestDto;
import com.seasonthon.pleanet.Challenge.dto.res.ChallengeResponseDto;
import com.seasonthon.pleanet.Challenge.service.ChallengeCommandService;
import com.seasonthon.pleanet.apiPayload.ApiResponse;
import com.seasonthon.pleanet.common.config.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/challenges")
public class ChallengeController {

    private final ChallengeCommandService  challengeCommandService;

    //챌린지 시작(멤버_챌린지 생성)
    @PostMapping("/{challengeId}/start")
    public ApiResponse<ChallengeResponseDto.ChallengeStartDto> startMission(@PathVariable Long challengeId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        ChallengeResponseDto.ChallengeStartDto challengeStartDto = challengeCommandService.startMission(userDetails.getId(), challengeId);
        return ApiResponse.onSuccess(challengeStartDto);
    }

    //gps 정보 받기
    @PostMapping("/{memberChallengeId}/gps")
    public ApiResponse<ChallengeResponseDto.GpsDto> updateProgress(@PathVariable Long memberChallengeId, @RequestBody ChallengeRequestDto.GpsDto request) {
        ChallengeResponseDto.GpsDto gpsDto = challengeCommandService.updateProgress(memberChallengeId, request);
        return ApiResponse.onSuccess(gpsDto);
    }
}
