package com.seasonthon.pleanet.member.controller;

import com.seasonthon.pleanet.member.dto.res.MemberResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.seasonthon.pleanet.apiPayload.ApiResponse;
import com.seasonthon.pleanet.member.dto.res.KakaoUserInfoResponseDto;
import com.seasonthon.pleanet.member.service.KakaoService;
import com.seasonthon.pleanet.member.service.MemberService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class KaokaoLoginController {

    private final KakaoService kakaoService;
    private final MemberService memberService;

    @GetMapping("/api/callback")
    public ApiResponse<MemberResponseDto.LoginDto> callback(@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accessToken = kakaoService.getAccessTokenFromKakao(code);

        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);

        //회원가입, 로그인 동시진행
        return ApiResponse.onSuccess(memberService.kakaoLogin(request,response, memberService.kakaoSignup(userInfo)));
    }



}

