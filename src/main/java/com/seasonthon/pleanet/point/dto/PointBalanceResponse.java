package com.seasonthon.pleanet.point.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PointBalanceResponse {
    private Integer currentPoints;       // 현재 보유 포인트
    private Integer totalEarnedPoints;   // 누적 적립 포인트
    private String currentLevel;         // 현재 단계
    private String nextLevel;            // 다음 단계
    private Double progressToNextLevel;  // 진행률 % (소수점 둘째 자리까지 반올림)
}
