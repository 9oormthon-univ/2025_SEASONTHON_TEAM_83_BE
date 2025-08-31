package com.seasonthon.pleanet.attendance.repository;

import com.seasonthon.pleanet.attendance.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query("SELECT a FROM Attendance a " +
            "WHERE a.member.id = :memberId " +
            "AND FUNCTION('YEAR', a.attendanceDate) = :year " +
            "AND FUNCTION('MONTH', a.attendanceDate) = :month")
    List<Attendance> findByMemberAndYearMonth(Long memberId, int year, int month);

    // 출석 여부 체크용 메소드
    Optional<Attendance> findByMemberIdAndAttendanceDate(Long memberId, LocalDate date);
}
