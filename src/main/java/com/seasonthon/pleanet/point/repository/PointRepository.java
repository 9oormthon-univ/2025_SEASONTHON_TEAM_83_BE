package com.seasonthon.pleanet.point.repository;

import com.seasonthon.pleanet.point.domain.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Point p " +
            "WHERE p.member.id = :memberId " +
            "AND p.type = 'earn' " +
            "AND FUNCTION('YEAR', p.createdAt) = :year " +
            "AND FUNCTION('MONTH', p.createdAt) = :month")
    int getMonthlyEarnedPoints(Long memberId, int year, int month);

    // 누적 포인트 (EARN만 합산)
    @Query("SELECT COALESCE(SUM(p.amount), 0) " +
            "FROM Point p WHERE p.member.id = :userId AND p.type = 'earn'")
    Integer getUserTotalEarnedPoints(@Param("userId") Long userId);

    // 현재 보유 포인트 (EARN - USE)
    @Query("SELECT COALESCE(SUM(CASE WHEN p.type = 'earn' THEN p.amount ELSE -p.amount END), 0) " +
            "FROM Point p WHERE p.member.id = :userId")
    Integer getUserCurrentPoints(@Param("userId") Long userId);

    // 포인트 내역 조회
    List<Point> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Point p WHERE p.member.id = :memberId")
    int sumByMemberId(Long memberId);
}

