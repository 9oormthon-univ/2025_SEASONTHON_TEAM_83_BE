package com.seasonthon.pleanet.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class AttendanceResponseDto {
    private LocalDate date;   // 출석 날짜
    private boolean checked;  // 출석 여부
}

