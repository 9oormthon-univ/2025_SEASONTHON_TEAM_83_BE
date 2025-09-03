package com.seasonthon.pleanet.Challenge.service;

import com.seasonthon.pleanet.Challenge.converter.ChallengeConverter;
import com.seasonthon.pleanet.Challenge.domain.Challenge;
import com.seasonthon.pleanet.Challenge.domain.ChallengeStatus;
import com.seasonthon.pleanet.Challenge.domain.ChallengeType;
import com.seasonthon.pleanet.Challenge.domain.MemberChallenge;
import com.seasonthon.pleanet.Challenge.dto.req.ChallengeRequestDto;
import com.seasonthon.pleanet.Challenge.dto.res.ChallengeResponseDto;
import com.seasonthon.pleanet.Challenge.repository.ChallengeRepository;
import com.seasonthon.pleanet.Challenge.repository.MemberChallengeRepository;
import com.seasonthon.pleanet.apiPayload.code.status.ErrorStatus;
import com.seasonthon.pleanet.apiPayload.exception.GeneralException;
import com.seasonthon.pleanet.member.domain.Member;
import com.seasonthon.pleanet.member.repository.MemberRepository;
import com.seasonthon.pleanet.point.domain.Point;
import com.seasonthon.pleanet.point.domain.PointType;
import com.seasonthon.pleanet.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeCommandService {

    private final MemberChallengeRepository memberChallengeRepository;
    private final  ChallengeRepository challengeRepository;
    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;

    public ChallengeResponseDto.ChallengeStartDto startMission(Long memberId, Long challengeId) {

        Challenge c = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MISSION_NOT_FOUND));

        boolean existsInProgress = memberChallengeRepository.existsByMemberIdAndChallengeIdAndStatus(
                memberId, challengeId, ChallengeStatus.IN_PROGRESS
        );

        boolean existsToday = memberChallengeRepository.existsByMemberIdAndChallengeIdAndCreatedAtBetween(
                memberId,
                challengeId,
                LocalDate.now().atStartOfDay(),
                LocalDate.now().plusDays(1).atStartOfDay()
        );

        if (existsInProgress) {
            throw new GeneralException(ErrorStatus._MISSION_ALREADY_IN_PROGRESS);
        }

        if (existsToday) {
            throw new GeneralException(ErrorStatus._MISSION_ALREADY_TODAY);
        }

        MemberChallenge mc = MemberChallenge.builder()
                .memberId(memberId)
                .challenge(c)
                .status(ChallengeStatus.IN_PROGRESS)
                .rewardGranted(false)
                .extraData("{}")
                .build();
       memberChallengeRepository.save(mc);
        return ChallengeConverter.toChallengeStartDto(mc);
    }

    public ChallengeResponseDto.GpsDto updateProgress(Long memberChallengeId, ChallengeRequestDto.GpsDto request) {

        MemberChallenge mc = memberChallengeRepository.findById(memberChallengeId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_MISSION_NOT_FOUND));

        // 미션 타입 체크
        if (mc.getChallenge().getType() != ChallengeType.GPS) {
            throw new GeneralException(ErrorStatus._MISSION_NOT_GPS);
        }

        JSONObject extra = (JSONObject) JSONValue.parse(mc.getExtraData());
        JSONArray path = (JSONArray) extra.get("path");
        if(path == null) path = new JSONArray();
        JSONObject point = new JSONObject();
        point.put("lat", request.getLatitude());
        point.put("lng", request.getLongitude());
        point.put("recordedAt", request.getRecordedAt().toString());
        path.add(point);

        extra.put("path", path);
        double totalDistance = calculateDistance(path);
        extra.put("totalDistance", totalDistance);

        mc.setExtraData(extra.toString());

        double requiredDistance = mc.getChallenge().getRequiredDistance();
        double remainingDistance = Math.max(0, requiredDistance - totalDistance);
        if (totalDistance >= requiredDistance && mc.getStatus() != ChallengeStatus.SUCCESS) {
            mc.setStatus(ChallengeStatus.SUCCESS);
        }

        memberChallengeRepository.save(mc);

        return ChallengeConverter.toGpsDto(totalDistance,requiredDistance,remainingDistance,path.size(),mc.getStatus());

    }

    //path 배열을 순회하며 거리 합계 계산 (km)
    private double calculateDistance(net.minidev.json.JSONArray path) {
        double totalMeters = 0.0;
        for (int i = 1; i < path.size(); i++) {

            net.minidev.json.JSONObject a = (net.minidev.json.JSONObject) path.get(i - 1);
            net.minidev.json.JSONObject b = (net.minidev.json.JSONObject) path.get(i);

            double lat1 = ((Number) a.get("lat")).doubleValue();
            double lon1 = ((Number) a.get("lng")).doubleValue();
            double lat2 = ((Number) b.get("lat")).doubleValue();
            double lon2 = ((Number) b.get("lng")).doubleValue();

            totalMeters += haversineMeters(lat1, lon1, lat2, lon2);
        }
        return totalMeters / 1000.0; // km
    }

    // 두 좌표 사이 거리 계산
    private double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000; // meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public ChallengeResponseDto.ChallengeCompleteDto missionComplete(Long challengeId, Long memberId ) {

        MemberChallenge mc = memberChallengeRepository
                .findByMemberIdAndChallengeIdAndCreatedAtBetween(
                        memberId,
                        challengeId,
                        LocalDate.now().atStartOfDay(),
                        LocalDate.now().plusDays(1).atStartOfDay()
                ).orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_MISSION_NOT_FOUND));

        // 미션 상태 확인
        if (mc.getStatus() != ChallengeStatus.SUCCESS) {
            throw new GeneralException(ErrorStatus._MISSION_NOT_COMPLETED);
        }

        // 이미 리워드 지급받았는지 체크
        if (mc.getRewardGranted()) {
            throw new GeneralException(ErrorStatus._REWARD_ALREADY_GRANTED);
        }

        // 보상 포인트 가져오기
        Integer rewardPoint = mc.getChallenge().getRewardPoint();

        Member m = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        // 포인트 지급 기록 생성
        Point point = Point.builder()
                .member(m)
                .memberChallenge(mc)
                .amount(rewardPoint)
                .type(PointType.earn)
                .description(mc.getChallenge().getTitle())
                .createdAt(LocalDateTime.now())
                .build();

        pointRepository.save(point);

        // 리워드 지급 처리
        mc.setRewardGranted(true);

        return ChallengeConverter.toChallengeCompleteDto(rewardPoint);
    }



}
