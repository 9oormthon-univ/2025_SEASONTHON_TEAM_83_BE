package com.seasonthon.pleanet.Challenge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seasonthon.pleanet.Challenge.converter.ChallengeConverter;
import com.seasonthon.pleanet.Challenge.domain.Challenge;
import com.seasonthon.pleanet.Challenge.domain.ChallengeStatus;
import com.seasonthon.pleanet.Challenge.domain.ChallengeType;
import com.seasonthon.pleanet.Challenge.domain.MemberChallenge;
import com.seasonthon.pleanet.Challenge.dto.req.ChallengeRequestDto;
import com.seasonthon.pleanet.Challenge.dto.res.ChallengeResponseDto;
import com.seasonthon.pleanet.Challenge.dto.res.PhotoResponse;
import com.seasonthon.pleanet.Challenge.dto.res.VerifyResponse;
import com.seasonthon.pleanet.Challenge.repository.ChallengeRepository;
import com.seasonthon.pleanet.Challenge.repository.MemberChallengeRepository;
import com.seasonthon.pleanet.Challenge.service.openai.ChatGptVisionService;
import com.seasonthon.pleanet.apiPayload.code.status.ErrorStatus;
import com.seasonthon.pleanet.apiPayload.exception.GeneralException;
import com.seasonthon.pleanet.common.service.ImageConverterService;
import com.seasonthon.pleanet.member.domain.Member;
import com.seasonthon.pleanet.member.repository.MemberRepository;
import com.seasonthon.pleanet.point.domain.Point;
import com.seasonthon.pleanet.point.domain.PointType;
import com.seasonthon.pleanet.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeCommandService {

    private final MemberChallengeRepository memberChallengeRepository;
    private final  ChallengeRepository challengeRepository;
    private final S3Client s3Client;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ChatGptVisionService chatGptVisionService;  // ChatGPT Vision 서비스 주입
    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;
    private final ImageConverterService imageConverterService; // ImageConverterService 주입


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public ChallengeResponseDto.ChallengeStartDto startMission(Long memberId, Long challengeId) {

        // memberId로 Member 엔티티 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

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
                .member(member)
                .challenge(c)
                .status(ChallengeStatus.IN_PROGRESS)
                .rewardGranted(false)
                .extraData("{}")
                .build();
       memberChallengeRepository.save(mc);
        return ChallengeConverter.toChallengeStartDto(mc);
    }

    public ChallengeResponseDto.GpsDto updateProgress(Long challengeId, Long memberId, ChallengeRequestDto.GpsDto request) {

        MemberChallenge mc = memberChallengeRepository
                .findByMemberIdAndChallengeIdAndCreatedAtBetween(
                        memberId,
                        challengeId,
                        LocalDate.now().atStartOfDay(),
                        LocalDate.now().plusDays(1).atStartOfDay()
                ).orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_MISSION_NOT_FOUND));
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

    @Transactional
    public PhotoResponse uploadPhoto(Long challengeId, MultipartFile file, Long memberId) {
        MemberChallenge mc = memberChallengeRepository
                .findByMemberIdAndChallengeIdAndCreatedAtBetween(
                        memberId,
                        challengeId,
                        LocalDate.now().atStartOfDay(),
                        LocalDate.now().plusDays(1).atStartOfDay()
                ).orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_MISSION_NOT_FOUND));

        // PHOTO 챌린지만 업로드 가능
        if (mc.getChallenge().getType() != ChallengeType.PHOTO) {
            throw new GeneralException(ErrorStatus._MISSION_NOT_PHOTO);
        }

        try {
            // HEIC만 변환
            ImageConverterService.ConvertedImage converted = imageConverterService.convertIfHeic(file);

            // 원래 키 구성 유지 + 확장자 교체 반영된 filename 사용
            String key = "challenges/" + mc.getChallenge().getId() + "/"
                    + mc.getMember().getId() + "_" + System.currentTimeMillis()
                    + "_" + converted.filename();

            // S3에 변환된 JPEG 파일 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(converted.contentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(converted.bytes()));

            String photoUrl = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + key;

            // extraData 업데이트
            Map<String, Object> extra = (mc.getExtraData() != null && !mc.getExtraData().isEmpty())
                    ? objectMapper.readValue(mc.getExtraData(), Map.class)
                    : new HashMap<>();
            extra.put("photoUrl", photoUrl);
            extra.put("uploadedAt", LocalDate.now().toString());
            mc.setExtraData(objectMapper.writeValueAsString(extra));

            return new PhotoResponse(photoUrl, true);

        } catch (Exception e) {
            log.error("Photo upload failed for memberChallengeId {}: {}", mc.getId(), e.getMessage(), e);
            throw new GeneralException(ErrorStatus._UPLOAD_FAIL);
        }
    }


    // 사진 인증 검증 (ChatGPT Vision API)
    @Transactional
    public VerifyResponse verifyPhoto(Long challengeId, Long memberId) {
        // 1. DB에서 오늘 날짜로 생성된 사용자 챌린지 조회
        MemberChallenge mc = memberChallengeRepository
                .findByMemberIdAndChallengeIdAndCreatedAtBetween(
                        memberId,
                        challengeId,
                        LocalDate.now().atStartOfDay(),
                        LocalDate.now().plusDays(1).atStartOfDay()
                ).orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_MISSION_NOT_FOUND));

        // 2. 사진 인증 타입의 챌린지인지 확인
        if (mc.getChallenge().getType() != ChallengeType.PHOTO) {
            throw new GeneralException(ErrorStatus._MISSION_NOT_PHOTO);
        }

        // 3. extraData에서 photoUrl 추출
        String photoUrl;
        try {
            Map<String, Object> extra = objectMapper.readValue(mc.getExtraData(), Map.class);
            photoUrl = (String) extra.get("photoUrl");

            if (photoUrl == null || photoUrl.trim().isEmpty()) {
                throw new GeneralException(ErrorStatus._PHOTO_URL_NOT_FOUND); // ErrorStatus에 _PHOTO_URL_NOT_FOUND 추가 필요
            }
        } catch (Exception e) {
            log.error("Failed to extract photoUrl from extraData: {}", e.getMessage());
            throw new GeneralException(ErrorStatus._UPLOAD_FAIL);
        }

        // 4. OpenAI Vision API 호출하여 키워드 받아오기
        String resultKeyword = chatGptVisionService.getVerificationKeyword(photoUrl.trim());
        log.info("AI Analysis Keyword for memberChallengeId {}: {}", mc.getId(), resultKeyword);

        boolean verificationSuccess = false;
        String message;

        // 5. 반환된 키워드에 따라 분기 처리
        switch (resultKeyword) {
            case "SUCCESS":
                verificationSuccess = true;
                mc.setStatus(ChallengeStatus.SUCCESS);
                message = "챌린지 성공!";
                break;
            case "NO_RECEIPT":
                mc.setStatus(ChallengeStatus.FAIL);
                message = "인증 실패, 영수증이 보이지 않아요.";
                break;
            case "NO_TUMBLER":
                mc.setStatus(ChallengeStatus.FAIL);
                message = "인증 실패, 텀블러가 보이지 않아요.";
                break;
            case "NOT_CAFE_RECEIPT":
                mc.setStatus(ChallengeStatus.FAIL);
                message = "인증 실패, 카페 영수증이 확인되지 않아요.";
                break;
            default: // FAIL 또는 예상치 못한 다른 응답
                mc.setStatus(ChallengeStatus.FAIL);
                message = "인증 실패, 텀블러와 오늘 날짜 카페 영수증이 모두 필요해요.";
                break;
        }

        mc.setRewardGranted(false);

        return new VerifyResponse(verificationSuccess, message);
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

        mc.setEndedAt(LocalDateTime.now());

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
