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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyAttendance(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getId();

        // year, month 없으면 오늘 날짜 기준
        LocalDate today = LocalDate.now();
        int targetYear = (year != null) ? year : today.getYear();
        int targetMonth = (month != null) ? month : today.getMonthValue();

        List<AttendanceResponseDto> attendances =
                attendanceService.getMonthlyAttendance(memberId, targetYear, targetMonth);

        long totalChecked = attendances.stream().filter(AttendanceResponseDto::isChecked).count();

        // 응답 포맷 맞춰주기
        Map<String, Object> result = new HashMap<>();
        result.put("month", String.format("%d-%02d", targetYear, targetMonth));
        result.put("attendances", attendances);
        result.put("totalChecked", totalChecked);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/check")
    public ResponseEntity<AttendanceCheckResponseDto> checkToday(Authentication authentication) {
        AttendanceCheckResponseDto response = attendanceService.checkToday(authentication);
        return ResponseEntity.ok(response);
    }
}
