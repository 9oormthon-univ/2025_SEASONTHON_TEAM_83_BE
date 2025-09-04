package com.seasonthon.pleanet.Challenge.controller;

import com.seasonthon.pleanet.Challenge.dto.req.ChallengeRequestDto;
import com.seasonthon.pleanet.Challenge.dto.res.ChallengeResponseDto;
import com.seasonthon.pleanet.Challenge.dto.res.PhotoResponse;
import com.seasonthon.pleanet.Challenge.dto.res.VerifyResponse;
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
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping("/{challengeId}/gps")
    public ApiResponse<ChallengeResponseDto.GpsDto> updateProgress(@PathVariable Long challengeId, @AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ChallengeRequestDto.GpsDto request) {
        ChallengeResponseDto.GpsDto gpsDto = challengeCommandService.updateProgress(challengeId, userDetails.getId(),request);
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

    // 인증 사진 업로드
    @PostMapping("/{challengeId}/photo")
    public ApiResponse<PhotoResponse> uploadPhoto(
            @PathVariable Long challengeId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.onSuccess(challengeCommandService.uploadPhoto(challengeId, file,userDetails.getId()));
    }

    // 사진 인증 검증 API
    @PostMapping("/{challengeId}/verify")
    public ApiResponse<VerifyResponse> verifyPhoto(
            @PathVariable Long challengeId ,@AuthenticationPrincipal CustomUserDetails userDetails) {
        VerifyResponse response = challengeCommandService.verifyPhoto(challengeId, userDetails.getId());
        return ApiResponse.onSuccess(response);
    }

    //미션 완료
    @PostMapping("/{challengeId}/complete")
    public ApiResponse<ChallengeResponseDto.ChallengeCompleteDto> missionComplete(@PathVariable Long challengeId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.onSuccess(challengeCommandService.missionComplete(challengeId, userDetails.getId()));
    }

    // 오늘의 챌린지 추천 API
    @GetMapping("/recommendation")
    public ApiResponse<ChallengeResponseDto.ChallengeRecommendationDto> getRecommendation(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 로그인한 사용자의 ID를 가져와서 서비스 호출
        Long memberId = userDetails.getId();
        return ApiResponse.onSuccess(challengeQueryService.getChallengeRecommendation(memberId));
    }
}
