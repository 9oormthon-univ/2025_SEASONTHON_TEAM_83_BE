package com.seasonthon.pleanet.member.controller;

import com.seasonthon.pleanet.apiPayload.ApiResponse;
import com.seasonthon.pleanet.apiPayload.code.status.SuccessStatus;
import com.seasonthon.pleanet.common.config.security.CustomUserDetails;
import com.seasonthon.pleanet.member.converter.MemberConverter;
import com.seasonthon.pleanet.member.domain.Member;
import com.seasonthon.pleanet.member.dto.req.MemberRequestDto;
import com.seasonthon.pleanet.member.dto.res.MemberResponseDto;
import com.seasonthon.pleanet.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/members")
public class MemberController {

    private final MemberService memberService;

    //회원가입
    @PostMapping("/signup")
    public ApiResponse<String> join(@RequestBody @Valid MemberRequestDto.JoinDto request){
        Member savedMember = memberService.joinMember(request);
        return ApiResponse.onSuccess("회원가입이 완료되었습니다.");
    }

    //이메일 중복 체크
    @GetMapping("/signup/email-check")
    public ApiResponse<MemberResponseDto.EmailCheckDto> join(@RequestParam("email") String email){
        MemberResponseDto.EmailCheckDto emailCheckDto = memberService.checkEmail(email);
        return ApiResponse.onSuccess(emailCheckDto);
    }

    //로그인
    @PostMapping("/login")
    public ApiResponse<MemberResponseDto.LoginDto> login(@RequestBody @Valid MemberRequestDto.LoginDto request ){
        MemberResponseDto.LoginDto loginDto = memberService.login(request.getEmailOrNickname(), request.getPassword());
        return ApiResponse.onSuccess(loginDto);
    }

    //관심 활동 설정
    @PostMapping("/interests")
    public ApiResponse<MemberResponseDto.InterestsDto> setInterests(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody MemberRequestDto.InterestsDto request){
        MemberResponseDto.InterestsDto interestsDto = memberService.setInterests(userDetails.getId(),request.getInterests());
        return ApiResponse.onSuccess(interestsDto);
    }

    //동의 항목 수정
    @PatchMapping("/agreements")
    public ApiResponse<MemberResponseDto.AgreementsDto> updateAgreements(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody MemberRequestDto.AgreementsDto request
    ) {
        MemberResponseDto.AgreementsDto agreementsDto = memberService.updateAgreements(userDetails.getId(), request);
        return ApiResponse.onSuccess(agreementsDto);
    }





}
