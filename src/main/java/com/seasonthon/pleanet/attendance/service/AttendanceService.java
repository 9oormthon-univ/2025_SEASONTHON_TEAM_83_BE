package com.seasonthon.pleanet.attendance.service;

import com.seasonthon.pleanet.attendance.domain.Attendance;
import com.seasonthon.pleanet.attendance.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public List<Attendance> getMonthlyAttendance(Long memberId, int year, int month) {
        return attendanceRepository.findByMemberAndYearMonth(memberId, year, month);
    }
}

