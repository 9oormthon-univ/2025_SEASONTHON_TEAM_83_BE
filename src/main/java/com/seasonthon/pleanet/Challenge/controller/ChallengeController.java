package com.seasonthon.pleanet.Challenge.controller;

import com.seasonthon.pleanet.Challenge.dto.req.ChallengeRequestDto;
import com.seasonthon.pleanet.Challenge.dto.res.ChallengeResponseDto;
import com.seasonthon.pleanet.Challenge.service.ChallengeCommandService;
import com.seasonthon.pleanet.Challenge.service.ChallengeQueryService;
import com.seasonthon.pleanet.apiPayload.ApiResponse;
import com.seasonthon.pleanet.common.config.security.CustomUserDetails;
import com.seasonthon.pleanet.member.dto.res.MemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/challenges")
public class ChallengeController {

    private final ChallengeCommandService  challengeCommandService;
    private final ChallengeQueryService challengeQueryService;

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

    //미션 리스트 조회
    @GetMapping("")
    public ApiResponse<Page<ChallengeResponseDto.ChallengeListDto>> getMisiions(@PageableDefault(size = 2) Pageable pageable) {
        return ApiResponse.onSuccess(challengeQueryService.getMissions(pageable));
    }

    //세부 미션 조회
    @GetMapping("{challengeId}")
    public ApiResponse<ChallengeResponseDto.ChallengeDetailDto> getMissionDetail(@PathVariable Long challengeId) {
        return ApiResponse.onSuccess(challengeQueryService.getMissionDetail(challengeId));
    }
}
