package com.seasonthon.pleanet.member.service;

import com.seasonthon.pleanet.apiPayload.code.status.ErrorStatus;
import com.seasonthon.pleanet.apiPayload.exception.GeneralException;
import com.seasonthon.pleanet.common.config.jwt.TokenProvider;
import com.seasonthon.pleanet.member.domain.enums.Interest;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public Member joinMember(MemberRequestDto.JoinDto request) {

        validateNicknameForSignup(request.getEmail(),request.getNickname());

        Member newMember = MemberConverter.toMember(request);

        newMember.encodePassword(passwordEncoder.encode(request.getPassword()));

       return memberRepository.save(newMember);
    }

    @Transactional(readOnly = true)
    public MemberResponseDto.EmailCheckDto checkEmail(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        boolean exists = memberRepository.existsByEmail(normalizedEmail);
        boolean available = !exists;
        return MemberConverter.toEmailCheckDto(email, available);
    }

    // 카카오 로그인 시 신규 회원가입 또는 기존 회원 조회
    public Member kakaoSignup(KakaoUserInfoResponseDto userInfo) {
        return memberRepository.findBySocialId(userInfo.getId())
                .orElseGet(() -> {
                    Member newMem = Member.builder()
                            //.name(userInfo.getKakaoAccount().getProfile().getNickname())
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

    public MemberResponseDto.LoginDto login(String emailOrNickname, String password) {
        Member member;
        if (emailOrNickname.contains("@")) {
            // 이메일인 경우
            member = memberRepository.findByEmail(emailOrNickname)
                    .orElseThrow(() -> new GeneralException(ErrorStatus._EMAIL_NOT_FOUND));
        } else {
            // 닉네임인 경우
            member = memberRepository.findByNickname(emailOrNickname)
                    .orElseThrow(() -> new GeneralException(ErrorStatus._NICKNAME_NOT_FOUND));
        }

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new GeneralException(ErrorStatus._PASSWORD_NOT_MATCH);
        }
        String accessToken = tokenProvider.createAccessToken(member);

        return MemberConverter.toLoginDto(member, accessToken);
    }

    public MemberResponseDto.InterestsDto setInterests(Long id,Set<Interest> interests){
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        member.setInterests(interests);
        return MemberConverter.toInterestsDto(member);
    }

    public MemberResponseDto.AgreementsDto updateAgreements(Long memberId, MemberRequestDto.AgreementsDto request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        // 푸시 알림 설정 변경
        if (request.getAllowPush() != null) {
            member.setAllowPush(request.getAllowPush());
        }

        // 위치 정보 설정 변경
        if (request.getAllowLocation() != null) {
            member.setAllowLocation(request.getAllowLocation());
        }

        return  MemberConverter.toAgreementsDto(member);
    }

    @Transactional(readOnly = true)
    public MemberResponseDto.MemberInfoDto getUserInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        return  MemberConverter.toMemberInfoDto(member);
    }

    public MemberResponseDto.MemberUpdateInfoDto updateUserInfo(Long memberId, MemberRequestDto.MemberInfoDto request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        if (request.getNickname() != null) {
            validateNicknameForUpdate(request.getNickname(), member.getId());
        }
        member.update(request);
        return  MemberConverter.toMemberUpdateInfoDto(member);
    }

    //유저 정보 업데이트 시 닉네임 유효성 검사 로직
    private void validateNicknameForUpdate(String nickname, Long memberId) {
        if (memberRepository.existsByNicknameAndIdNot(nickname, memberId)) {
            throw new GeneralException(ErrorStatus._NICKNAME_ALREADY_EXISTS);
        }

        if (containsForbiddenWords(nickname)) {
            throw new GeneralException(ErrorStatus._NICKNAME_FORBIDDEN);
        }
    }

    //닉네임 유효성 검사 로직
    private void validateNicknameForSignup(String email, String nickname) {
        if(memberRepository.existsByEmail(email)) {
            throw new GeneralException(ErrorStatus._MEMBER_ALREADY_EXISTS);
        }

        if (memberRepository.existsByNickname(nickname)) {
            throw new GeneralException(ErrorStatus._NICKNAME_ALREADY_EXISTS);
        }

        if (containsForbiddenWords(nickname)) {
            throw new GeneralException(ErrorStatus._NICKNAME_FORBIDDEN);
        }
    }

    //닉네임 금칙어 제한 로직
    private boolean containsForbiddenWords(String nickname) {

        List<String> forbiddenWords = List.of( "fuck","shit","bastard","sex","시발","병신","개새끼","썅");
        return forbiddenWords.stream().anyMatch(nickname::contains);
    }






}
