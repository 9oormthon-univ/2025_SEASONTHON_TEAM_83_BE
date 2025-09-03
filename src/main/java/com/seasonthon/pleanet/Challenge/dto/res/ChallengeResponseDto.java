package com.seasonthon.pleanet.Challenge.dto.res;

import com.seasonthon.pleanet.Challenge.domain.ChallengeStatus;
import lombok.*;

import java.time.LocalDateTime;

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




}
