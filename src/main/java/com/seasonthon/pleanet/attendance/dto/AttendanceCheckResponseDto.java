package com.seasonthon.pleanet.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttendanceCheckResponseDto {
    private String message;
    private String date;
    private int earnedPoint;
    private int totalMonthPoints; // 👈 이번 달 누적 포인트를 담을 필드 추가
}

