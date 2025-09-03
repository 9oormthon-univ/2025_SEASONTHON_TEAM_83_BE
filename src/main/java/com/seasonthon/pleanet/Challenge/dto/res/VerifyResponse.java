package com.seasonthon.pleanet.Challenge.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VerifyResponse {
    private boolean success;      // 인증 성공 여부
    private int reward;           // 지급 포인트 (성공 시 +50, 실패 시 0)
    private String message;       // 결과 메시지
}

