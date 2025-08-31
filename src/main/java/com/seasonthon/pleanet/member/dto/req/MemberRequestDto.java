package com.seasonthon.pleanet.member.dto.req;

import com.seasonthon.pleanet.member.domain.enums.Interest;
import com.seasonthon.pleanet.member.domain.enums.SocialType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import java.time.LocalDate;
import java.util.Set;

public class MemberRequestDto {

    @Getter
    public static class JoinDto{
        @NotNull(message = "닉네임은 필수 입력입니다.")
        String nickname;
        @NotNull(message = "생년월일은 필수 입력입니다.")
        LocalDate birthday;
        @NotNull(message = "이메일은 필수 입력입니다.")
        String email;
        @NotNull(message = "비밀번호는 필수 입력입니다.")
        String password;
    }

    @Getter
    public static class LoginDto{
        @NotNull(message = "이메일 혹은 닉네임은 필수 입력입니다.")
        String emailOrNickname;
        @NotNull(message = "비밀번호는 필수 입력입니다.")
        String password;
    }

    @Getter
    public static class InterestsDto{
        Set<Interest> interests;
    }

    @Getter
    public static class AgreementsDto{
        Boolean allowLocation;
        Boolean allowPush;
    }

}
