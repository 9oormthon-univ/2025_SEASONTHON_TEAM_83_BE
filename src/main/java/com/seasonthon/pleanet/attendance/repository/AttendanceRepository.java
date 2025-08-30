package com.seasonthon.pleanet.attendance.repository;

import com.seasonthon.pleanet.attendance.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query("SELECT a FROM Attendance a " +
            "WHERE a.userId = :userId " +
            "AND FUNCTION('YEAR', a.attendanceDate) = :year " +
            "AND FUNCTION('MONTH', a.attendanceDate) = :month")
    List<Attendance> findByUserIdAndYearMonth(Long userId, int year, int month);
}

