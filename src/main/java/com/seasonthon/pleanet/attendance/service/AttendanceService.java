package com.seasonthon.pleanet.attendance.service;

import com.seasonthon.pleanet.attendance.domain.Attendance;
import com.seasonthon.pleanet.attendance.dto.AttendanceCheckResponseDto;
import com.seasonthon.pleanet.attendance.dto.AttendanceResponseDto;
import com.seasonthon.pleanet.attendance.repository.AttendanceRepository;
import com.seasonthon.pleanet.common.config.security.CustomUserDetails;
import com.seasonthon.pleanet.member.domain.Member;
import com.seasonthon.pleanet.member.repository.MemberRepository;
import com.seasonthon.pleanet.point.domain.Point;
import com.seasonthon.pleanet.point.domain.PointType;
import com.seasonthon.pleanet.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final PointRepository pointRepository;
    private final MemberRepository memberRepository;

    public List<AttendanceResponseDto> getMonthlyAttendance(Long memberId, int year, int month) {
        // DB에서 이번 달 출석한 날짜들 가져오기
        List<Attendance> attendances = attendanceRepository.findByMemberAndYearMonth(memberId, year, month);

        // 출석한 날짜만 set으로 저장 (빠른 조회용)
        Set<LocalDate> checkedDates = attendances.stream()
                .map(Attendance::getAttendanceDate)
                .collect(Collectors.toSet());

        // 이번 달의 전체 날짜
        YearMonth yearMonth = YearMonth.of(year, month);
        List<AttendanceResponseDto> response = new ArrayList<>();

        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = LocalDate.of(year, month, day);
            boolean checked = checkedDates.contains(date);
            response.add(new AttendanceResponseDto(date, checked));
        }

        return response;
    }

    public AttendanceCheckResponseDto checkToday(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        LocalDate today = LocalDate.now();

        // 이미 출석했는지 체크
        if (attendanceRepository.findByMemberIdAndAttendanceDate(memberId, today).isPresent()) {
            return new AttendanceCheckResponseDto("오늘은 이미 출석체크 하셨습니다.", today.toString(), 0);
        }

        // 출석 저장
        Attendance attendance = Attendance.builder()
                .member(member)
                .attendanceDate(today)
                .build();
        attendanceRepository.save(attendance);

        // 포인트 적립
        Point point = Point.builder()
                .member(member)
                .attendance(attendance)
                .amount(3)
                .type(PointType.earn)
                .description("출석 체크")
                .build();
        pointRepository.save(point);

        return new AttendanceCheckResponseDto("출석되었습니다", today.toString(), 3);
    }

    public int getCurrentMonthPoints(Long memberId) {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        return pointRepository.getMonthlyEarnedPoints(memberId, year, month);
    }
}

