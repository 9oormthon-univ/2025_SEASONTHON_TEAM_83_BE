package com.seasonthon.pleanet.Challenge.dto.res;

import com.seasonthon.pleanet.Challenge.domain.Challenge;
import com.seasonthon.pleanet.Challenge.domain.ChallengeStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ChallengeResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengeStartDto {
        private ChallengeStatus missionStatus;
        private LocalDateTime startedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GpsDto {
        private double totalDistance;
        private double requiredDistance;
        private double remainingDistance;
        private int pathCount;
        private ChallengeStatus status;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengeListDto {
        private Long challengeId;
        private String title;
        private String imageUrl;
        private Integer point;

        // Challenge 엔티티를 DTO로 변환하는 생성자
        public ChallengeListDto(Challenge challenge) {
            this.challengeId = challenge.getId();
            this.title = challenge.getTitle();
            this.imageUrl = challenge.getImageUrl();
            this.point = challenge.getRewardPoint();
        }
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengeDetailDto {
        private Long challengeId;
        private String title;
        private String imageUrl;
        private Integer point;
        private String description;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengeCompleteDto {
        private Integer rewardPoint;
        private LocalDateTime endedAt;

    }

    // 오늘의 챌린지 추천 API 응답 DTO
    @Getter
    @AllArgsConstructor
    public static class ChallengeRecommendationDto {
        private ChallengeListDto lastChallenge;
        private ChallengeListDto recommendedChallenge;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengeLatestDto {
        private String title;
        private LocalDateTime endedAt;
    }




}
