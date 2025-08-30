package com.seasonthon.pleanet.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttendanceCheckResponseDto {
    private String message;
    private String date;
    private int earnedPoint;
}

