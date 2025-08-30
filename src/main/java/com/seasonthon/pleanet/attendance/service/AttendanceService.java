package com.seasonthon.pleanet.attendance.service;

import com.seasonthon.pleanet.attendance.domain.Attendance;
import com.seasonthon.pleanet.attendance.dto.AttendanceCheckResponseDto;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final PointRepository pointRepository;
    private final MemberRepository memberRepository;

    public List<Attendance> getMonthlyAttendance(Long memberId, int year, int month) {
        return attendanceRepository.findByMemberAndYearMonth(memberId, year, month);
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
}

