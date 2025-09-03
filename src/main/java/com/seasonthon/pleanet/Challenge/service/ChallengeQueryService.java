package com.seasonthon.pleanet.Challenge.service;

import com.seasonthon.pleanet.Challenge.converter.ChallengeConverter;
import com.seasonthon.pleanet.Challenge.domain.Challenge;
import com.seasonthon.pleanet.Challenge.dto.res.ChallengeResponseDto;
import com.seasonthon.pleanet.Challenge.repository.ChallengeRepository;
import com.seasonthon.pleanet.apiPayload.code.status.ErrorStatus;
import com.seasonthon.pleanet.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeQueryService {

    private final ChallengeRepository challengeRepository;

    public Page<ChallengeResponseDto.ChallengeListDto> getMissions(Pageable pageable){

        Page<Challenge> challenges = challengeRepository.findAll(pageable);

        return challenges.map(ChallengeConverter::toChallengeListDto);
    }

    public ChallengeResponseDto.ChallengeDetailDto getMissionDetail(Long challengeId){

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(()-> new GeneralException(ErrorStatus._MISSION_NOT_FOUND));


        return ChallengeConverter.toChallengeDetailDto(challenge);
    }

}
