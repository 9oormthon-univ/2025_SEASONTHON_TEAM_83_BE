package com.seasonthon.pleanet.Challenge.service;

import static com.seasonthon.pleanet.Challenge.dto.res.ChallengeResponseDto.*;
import com.seasonthon.pleanet.Challenge.converter.ChallengeConverter;
import com.seasonthon.pleanet.Challenge.domain.Challenge;
import com.seasonthon.pleanet.Challenge.domain.MemberChallenge;
import com.seasonthon.pleanet.Challenge.repository.ChallengeRepository;
import com.seasonthon.pleanet.Challenge.repository.MemberChallengeRepository;
import com.seasonthon.pleanet.apiPayload.code.status.ErrorStatus;
import com.seasonthon.pleanet.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeQueryService {

    private final ChallengeRepository challengeRepository;
    private final MemberChallengeRepository memberChallengeRepository;

    public Page<ChallengeListDto> getMissions(Pageable pageable){

        Page<Challenge> challenges = challengeRepository.findAll(pageable);

        return challenges.map(ChallengeConverter::toChallengeListDto);
    }

    public ChallengeDetailDto getMissionDetail(Long challengeId){

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(()-> new GeneralException(ErrorStatus._MISSION_NOT_FOUND));


        return ChallengeConverter.toChallengeDetailDto(challenge);

    }

    public ChallengeRecommendationDto getChallengeRecommendation(Long memberId) {
        // 1. 사용자의 가장 최근 참여 챌린지 조회
        Optional<MemberChallenge> lastMemberChallengeOpt = memberChallengeRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId);

        // 2. 전체 챌린지 목록 조회
        List<Challenge> allChallenges = challengeRepository.findAll();

        Challenge lastChallenge = lastMemberChallengeOpt.map(MemberChallenge::getChallenge).orElse(null);
        Challenge recommendedChallenge = null;

        // 3. 추천할 챌린지 후보 목록 생성
        List<Challenge> candidateChallenges;
        if (lastChallenge != null) {
            // 최근 챌린지가 있으면, 그 챌린지를 제외한 목록을 후보로 선정
            final Long lastChallengeId = lastChallenge.getId();
            candidateChallenges = allChallenges.stream()
                    .filter(c -> !c.getId().equals(lastChallengeId))
                    .collect(Collectors.toList());
        } else {
            // 최근 챌린지가 없으면 (신규 유저), 모든 챌린지가 후보
            candidateChallenges = allChallenges;
        }

        // 4. 후보 목록에서 랜덤으로 하나를 추천
        if (!candidateChallenges.isEmpty()) {
            recommendedChallenge = candidateChallenges.get(new Random().nextInt(candidateChallenges.size()));
        } else if (lastChallenge != null) {
            // 만약 후보가 없다면 (예: 챌린지가 총 1개), 그냥 마지막 챌린지를 다시 추천
            recommendedChallenge = lastChallenge;
        }

        // 5. DTO로 변환하여 반환
        ChallengeListDto lastChallengeDto = (lastChallenge != null) ? new ChallengeListDto(lastChallenge) : null;
        ChallengeListDto recommendedChallengeDto = (recommendedChallenge != null) ? new ChallengeListDto(recommendedChallenge) : null;

        return new ChallengeRecommendationDto(lastChallengeDto, recommendedChallengeDto);
    }
}

