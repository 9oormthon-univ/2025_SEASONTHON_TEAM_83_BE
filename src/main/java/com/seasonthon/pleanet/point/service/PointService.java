package com.seasonthon.pleanet.point.service;

import com.seasonthon.pleanet.apiPayload.code.status.ErrorStatus;
import com.seasonthon.pleanet.apiPayload.exception.GeneralException;
import com.seasonthon.pleanet.point.domain.Point;
import com.seasonthon.pleanet.point.dto.PointBalanceResponse;
import com.seasonthon.pleanet.point.dto.PointHistoryResponse;
import com.seasonthon.pleanet.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 트랜잭션 추가
public class PointService {

    private final PointRepository pointRepository;

    public PointBalanceResponse getPointBalance(Long userId) {
        Integer currentPoints = pointRepository.getUserCurrentPoints(userId);
        Integer totalEarnedPoints = pointRepository.getUserTotalEarnedPoints(userId);

        if (currentPoints == null) currentPoints = 0;
        if (totalEarnedPoints == null) totalEarnedPoints = 0;

        // 단계 규칙 (누적 포인트 기준)
        String currentLevel;
        String nextLevel;
        double rawProgress;

        if (totalEarnedPoints < 500) {
            currentLevel = "새싹 전";
            nextLevel = "새싹";
            rawProgress = (totalEarnedPoints / 500.0) * 100.0;
        } else if (totalEarnedPoints < 1500) {
            currentLevel = "새싹";
            nextLevel = "묘목";
            rawProgress = ((totalEarnedPoints - 500) / 1000.0) * 100.0;
        } else if (totalEarnedPoints < 3000) {
            currentLevel = "묘목";
            nextLevel = "나무";
            rawProgress = ((totalEarnedPoints - 1500) / 1500.0) * 100.0;
        } else if (totalEarnedPoints < 5000) {
            currentLevel = "나무";
            nextLevel = "숲";
            rawProgress = ((totalEarnedPoints - 3000) / 2000.0) * 100.0;
        } else {
            currentLevel = "숲";
            nextLevel = "-";
            rawProgress = 100.0;
        }

        // 소수점 둘째 자리 반올림
        double progress = BigDecimal.valueOf(rawProgress)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        return PointBalanceResponse.builder()
                .currentPoints(currentPoints)
                .totalEarnedPoints(totalEarnedPoints)
                .currentLevel(currentLevel)
                .nextLevel(nextLevel)
                .progressToNextLevel(progress)
                .build();
    }

    // 특정 멤버의 포인트 히스토리 조회
    public List<PointHistoryResponse> getPointHistories(Long memberId) {
        List<Point> histories = pointRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId);

        return histories.stream()
                .map(h -> PointHistoryResponse.builder()
                        .date(h.getCreatedAt().toLocalDate().toString()) // LocalDateTime → YYYY-MM-DD
                        .type(h.getType().name())                        // enum → 문자열
                        .description(h.getDescription())                 // 설명
                        .pointChange(h.getAmount())                      // 증감 포인트
                        .build())
                .collect(Collectors.toList());
    }
}
