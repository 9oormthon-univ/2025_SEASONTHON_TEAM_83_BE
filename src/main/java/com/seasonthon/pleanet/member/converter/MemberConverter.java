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

    public static Member toMember(MemberRequestDto.JoinDto request){

        return Member.builder()
                .nickname(request.getNickname())
                .birthday(request.getBirthday())
                .email(request.getEmail())
                .socialType(SocialType.LOCAL)
                .build();
    }
}
