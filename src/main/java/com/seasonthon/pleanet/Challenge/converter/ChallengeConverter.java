package com.seasonthon.pleanet.Challenge.converter;

import com.seasonthon.pleanet.Challenge.domain.Challenge;
import com.seasonthon.pleanet.Challenge.domain.ChallengeStatus;
import com.seasonthon.pleanet.Challenge.domain.MemberChallenge;
import com.seasonthon.pleanet.Challenge.dto.res.ChallengeResponseDto;
import com.seasonthon.pleanet.member.domain.Member;
import com.seasonthon.pleanet.member.dto.res.MemberResponseDto;

import java.time.LocalDateTime;

public class ChallengeConverter {

    public static ChallengeResponseDto.ChallengeStartDto toChallengeStartDto(MemberChallenge memberChallenge) {
        return ChallengeResponseDto.ChallengeStartDto.builder()
                .memberChallengeId(memberChallenge.getId())
                .missionStatus(memberChallenge.getStatus())
                .startedAt(LocalDateTime.now())
                .build();
    }

    public static ChallengeResponseDto.GpsDto toGpsDto(Double totalDistance,
                                                       Double requiredDistance,
                                                       Double remainingDistance,
                                                       Integer pathCount,
                                                       ChallengeStatus status) {
        return ChallengeResponseDto.GpsDto.builder()
                .totalDistance(totalDistance)
                .requiredDistance(requiredDistance)
                .remainingDistance(remainingDistance)
                .pathCount(pathCount)
                .status(status)
                .build();
    }

    public static ChallengeResponseDto.ChallengeListDto toChallengeListDto(Challenge challenge) {
        return ChallengeResponseDto.ChallengeListDto.builder()
                .challengeId(challenge.getId())
                .imageUrl(challenge.getImageUrl())
                .point(challenge.getRewardPoint())
                .title(challenge.getTitle())
                .build();
    }

    public static ChallengeResponseDto.ChallengeDetailDto toChallengeDetailDto(Challenge challenge) {
        return ChallengeResponseDto.ChallengeDetailDto.builder()
                .challengeId(challenge.getId())
                .imageUrl(challenge.getImageUrl())
                .point(challenge.getRewardPoint())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .build();
    }
}
