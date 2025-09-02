package com.seasonthon.pleanet.Challenge.dto.res;

import com.seasonthon.pleanet.Challenge.domain.ChallengeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ChallengeResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengeStartDto {
        private Long memberChallengeId;
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




}
