package com.seasonthon.pleanet.member.dto.res;

import com.seasonthon.pleanet.member.domain.enums.Interest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public class MemberResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginDto{

        private Long memberId;
        private String nickname;
        private String accessToken;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterestsDto{
        Set<Interest> interests;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgreementsDto{
        Boolean allowLocation;
        Boolean allowPush;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailCheckDto {
        private String email;     // 요청한 이메일
        private boolean available; // 사용 가능 여부 (true = 사용 가능)
    }

}
