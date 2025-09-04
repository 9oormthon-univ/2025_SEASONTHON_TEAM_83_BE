package com.seasonthon.pleanet.point.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PointHistoryResponse {
    private String date;        // YYYY-MM-DD
    private String type;        // WALK, TUMBLER, ATTENDANCE ...
    private String description; // 상세 설명
    private int pointChange;    // 증감 포인트 (ex: +20 → 20, -10 → -10)
}