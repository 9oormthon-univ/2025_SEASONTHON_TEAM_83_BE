package com.seasonthon.pleanet.point.repository;

import com.seasonthon.pleanet.point.domain.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Point p " +
            "WHERE p.member.id = :memberId " +
            "AND p.type = 'earn' " +
            "AND FUNCTION('YEAR', p.createdAt) = :year " +
            "AND FUNCTION('MONTH', p.createdAt) = :month")
    int getMonthlyEarnedPoints(Long memberId, int year, int month);
}

