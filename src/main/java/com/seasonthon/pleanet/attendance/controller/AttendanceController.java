package com.seasonthon.pleanet.attendance.controller;

import com.seasonthon.pleanet.attendance.domain.Attendance;
import com.seasonthon.pleanet.attendance.dto.AttendanceResponseDto;
import com.seasonthon.pleanet.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/monthly")
    public ResponseEntity<List<AttendanceResponseDto>> getMonthlyAttendance(
            @RequestParam Long memberId,
            @RequestParam int year,
            @RequestParam int month) {

        List<Attendance> attendances = attendanceService.getMonthlyAttendance(memberId, year, month);

        List<AttendanceResponseDto> response = attendances.stream()
                .map(a -> new AttendanceResponseDto(a.getAttendanceDate(), true))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
