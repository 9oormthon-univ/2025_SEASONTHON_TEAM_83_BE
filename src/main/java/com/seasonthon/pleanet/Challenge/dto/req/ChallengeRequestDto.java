package com.seasonthon.pleanet.Challenge.dto.req;

import com.seasonthon.pleanet.member.domain.enums.Interest;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public class ChallengeRequestDto {

    @Getter
    public static class GpsDto {
        private Double latitude;
        private Double longitude;
        private LocalDateTime recordedAt;
    }

}
