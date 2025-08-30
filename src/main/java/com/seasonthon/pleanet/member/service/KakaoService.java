package com.seasonthon.pleanet.member.service;

import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.extern.slf4j.Slf4j;
import com.seasonthon.pleanet.member.dto.res.KakaoTokenResponseDto;
import com.seasonthon.pleanet.member.dto.res.KakaoUserInfoResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient; //의존서
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class KakaoService {
    private String clientId;
    private final String KAUTH_TOKEN_URL_HOST;
    private final String KAUTH_USER_URL_HOST;
    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    @Autowired
    public KakaoService(@Value("${kakao.client_id}") String clientId) {
        this.clientId = clientId;
        KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
        KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
    }

    //인가 토큰을 보내야함
    //https://developers.kakao.com/docs/latest/ko/pleanet/rest-api#request-token
    public String getAccessTokenFromKakao(String code) {

        KakaoTokenResponseDto kakaoTokenResponseDto = WebClient.create(KAUTH_TOKEN_URL_HOST).post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("redirect_uri", redirectUri)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("[KakaoService] 4xx 오류 발생. 응답 본문: {}", errorBody);
                                    return Mono.error(new RuntimeException("Invalid Parameter: " + errorBody));
                                })
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("[KakaoService] 5xx 오류 발생. 응답 본문: {}", errorBody);
                                    return Mono.error(new RuntimeException("Internal Server Error: " + errorBody));
                                })
                )
                .bodyToMono(KakaoTokenResponseDto.class)
                .block(); // 주의: block()은 동기 블로킹이므로 상황에 따라 다른 방식 고려


        return kakaoTokenResponseDto.getAccessToken();
    }


    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {

        KakaoUserInfoResponseDto userInfo = WebClient.create(KAUTH_USER_URL_HOST)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // access token 인가
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                //TODO : Custom Exception
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();

        log.info("[ Kakao Service ] Auth ID ---> {} ", userInfo.getId());
        log.info("[ Kakao Service ] Nickname ---> {} ", userInfo.getKakaoAccount().getProfile().getNickname());
        log.info("[ Kakao Service ] ProfileImageUrl ---> {} ", userInfo.getKakaoAccount().getProfile().getProfileImageUrl());
        log.info("[ Kakao Service ] Email ---> {} ", userInfo.getKakaoAccount().getEmail());

        return userInfo;
    }

}
