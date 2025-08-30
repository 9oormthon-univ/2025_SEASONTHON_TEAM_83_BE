package com.seasonthon.pleanet.attendance.controller;

import com.seasonthon.pleanet.attendance.domain.Attendance;
import com.seasonthon.pleanet.attendance.dto.AttendanceCheckResponseDto;
import com.seasonthon.pleanet.attendance.dto.AttendanceResponseDto;
import com.seasonthon.pleanet.attendance.service.AttendanceService;
import com.seasonthon.pleanet.common.config.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
            @RequestParam int year,
            @RequestParam int month,
            Authentication authentication) { // 로그인 정보 주입받음

        // 토큰/세션에서 로그인된 사용자 꺼내오기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getId();

        List<Attendance> attendances = attendanceService.getMonthlyAttendance(memberId, year, month);

        List<AttendanceResponseDto> response = attendances.stream()
                .map(a -> new AttendanceResponseDto(a.getAttendanceDate(), true))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/check")
    public ResponseEntity<AttendanceCheckResponseDto> checkToday(Authentication authentication) {
        AttendanceCheckResponseDto response = attendanceService.checkToday(authentication);
        return ResponseEntity.ok(response);
    }
}
