package com.seasonthon.pleanet.member.service;

import com.seasonthon.pleanet.common.config.jwt.TokenProvider;
import com.seasonthon.pleanet.member.domain.enums.SocialType;
import com.seasonthon.pleanet.member.dto.res.MemberResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import com.seasonthon.pleanet.member.converter.MemberConverter;
import com.seasonthon.pleanet.member.domain.Member;
import com.seasonthon.pleanet.member.dto.req.MemberRequestDto;
import com.seasonthon.pleanet.member.dto.res.KakaoUserInfoResponseDto;
import com.seasonthon.pleanet.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public Member joinMember(MemberRequestDto.JoinDto request) {

        Member newMember = MemberConverter.toMember(request); //컨버터 위치

        newMember.encodePassword(passwordEncoder.encode(request.getPassword()));

       return memberRepository.save(newMember);
    }

    public boolean existsById(Long memberId) {
        return memberRepository.existsById(memberId);
    }

    // 카카오 로그인 시 신규 회원가입 또는 기존 회원 조회
    public Member kakaoSignup(KakaoUserInfoResponseDto userInfo) {
        return memberRepository.findBySocialId(userInfo.getId())
                .orElseGet(() -> {
                    Member newMem = Member.builder()
                           // .name(userInfo.getKakaoAccount().getProfile().getNickname())
                            .socialId(userInfo.getId())
                            .socialType(SocialType.KAKAO)
                            .email(userInfo.getKakaoAccount().getEmail())
                            .build();
                    memberRepository.save(newMem);
                    return newMem;
                });
    }

    // 카카오 로그인 처리 후 토큰 발급
    public MemberResponseDto.LoginDto kakaoLogin(HttpServletRequest request, HttpServletResponse response, Member member) {
        String accessToken = tokenProvider.createAccessToken(member);


        return MemberConverter.toLoginDto(member, accessToken);
    }


}
