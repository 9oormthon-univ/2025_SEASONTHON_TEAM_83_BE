package com.seasonthon.pleanet.member.converter;

import com.seasonthon.pleanet.member.domain.Member;
import com.seasonthon.pleanet.member.domain.enums.SocialType;
import com.seasonthon.pleanet.member.dto.req.MemberRequestDto;
import com.seasonthon.pleanet.member.dto.res.MemberResponseDto;

public class MemberConverter {

    public static MemberResponseDto.LoginDto toLoginDto(Member member, String accessToken){
        return MemberResponseDto.LoginDto.builder()
                .memberId(member.getId())
                .nickname((member.getNickname()))
                .accessToken(accessToken)
                .build();
    }

    public static MemberResponseDto.InterestsDto toInterestsDto(Member member){
        return MemberResponseDto.InterestsDto.builder()
                .interests(member.getInterests())
                .build();
    }

    public static MemberResponseDto.AgreementsDto toAgreementsDto(Member member) {
        return MemberResponseDto.AgreementsDto.builder()
                .allowLocation(member.getAllowLocation())
                .allowPush(member.getAllowPush())
                .build();
    }


    public static MemberResponseDto.EmailCheckDto toEmailCheckDto(String email, Boolean available) {
        return MemberResponseDto.EmailCheckDto.builder()
                .email(email)
                .available(available)
                .build();
    }

    public static MemberResponseDto.MemberRankingDto toMemberRankingDto(Member member, Integer totalPoint, Integer badgeCount) {
        return MemberResponseDto.MemberRankingDto.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profileUrl(member.getProfileUrl())
                .totalPoint(totalPoint)
                .badgeCount(badgeCount)
                .build();
    }

    public static MemberResponseDto.MemberInfoDto toMemberInfoDto(Member member) {
        return MemberResponseDto.MemberInfoDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileUrl(member.getProfileUrl())
                .build();
    }

    public static Member toMember(MemberRequestDto.JoinDto request){

        return Member.builder()
                .nickname(request.getNickname())
                .birthday(request.getBirthday())
                .email(request.getEmail())
                .socialType(SocialType.LOCAL)
                .build();
    }
}
