package com.seasonthon.pleanet.common.config.security;

import lombok.RequiredArgsConstructor;
import com.seasonthon.pleanet.apiPayload.code.status.ErrorStatus;
import com.seasonthon.pleanet.apiPayload.exception.GeneralException;
import com.seasonthon.pleanet.member.domain.Member;
import com.seasonthon.pleanet.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        Member member = memberRepository.findById(Long.parseLong(memberId))
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        return new CustomUserDetails(member);
    }
}