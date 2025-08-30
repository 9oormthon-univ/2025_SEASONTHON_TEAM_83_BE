package com.seasonthon.pleanet.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlyPointResponseDto {
    private int totalPoints;   // 이번 달 출석 포인트 합계
}

