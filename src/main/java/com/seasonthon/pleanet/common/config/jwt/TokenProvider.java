package com.seasonthon.pleanet.common.config.jwt;

import com.seasonthon.pleanet.common.config.security.CustomUserDetails;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.seasonthon.pleanet.common.config.security.CustomUserDetailsService;
import com.seasonthon.pleanet.member.domain.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenProvider {

    private final JwtProperties jwtProperties;

    private Key secretKey;

    private final CustomUserDetailsService userDetailService;

    @PostConstruct
    protected void init() {
        log.info("📦 JWT_SECRET(raw): '{}'", jwtProperties.getSecretKey());
        byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
        secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    // 액세스 토큰 생성
    public String createAccessToken(Member member) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(String.valueOf(member.getId()))
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpiration()))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT 토큰의 유효성을 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    // JWT 토큰에서 사용자 ID를 직접 추출
    public String getUserEmailInToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    // 토큰에서 인증 정보 추출하여 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        String memberId = getUserEmailInToken(token);
        CustomUserDetails userDetails = userDetailService.loadUserByUsername(memberId);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

}