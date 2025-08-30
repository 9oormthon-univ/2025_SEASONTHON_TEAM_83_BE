package com.seasonthon.pleanet.member.dto.req;

import com.seasonthon.pleanet.member.domain.enums.Interest;
import com.seasonthon.pleanet.member.domain.enums.SocialType;
import lombok.Getter;
import java.time.LocalDate;
import java.util.Set;

public class MemberRequestDto {

    @Getter
    public static class JoinDto{
        String nickname;
        LocalDate birthday;
        String email;
        String password;
    }

    @Getter
    public static class loginDto{
        String emailOrNickname;
        String password;
    }

    @Getter
    public static class InterestsDto{
        Set<Interest> interests;
    }
}
