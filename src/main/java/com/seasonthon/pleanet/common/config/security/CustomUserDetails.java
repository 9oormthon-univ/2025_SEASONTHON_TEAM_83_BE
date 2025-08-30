package com.seasonthon.pleanet.common.config.security;


import com.seasonthon.pleanet.member.domain.enums.SocialType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.seasonthon.pleanet.member.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails {

    private final Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        );
    }

    public Long getId() {
        return member.getId();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }
    @Override
    public String getPassword() {
        if (member.getSocialType() == SocialType.LOCAL) {
            return member.getPassword(); // 자체 가입
        }
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}