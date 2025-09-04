package com.seasonthon.pleanet.member.service;

import com.seasonthon.pleanet.badge.dto.res.BadgeResponseDto;
import com.seasonthon.pleanet.badge.repository.MemberBadgeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberBadgeService {

    private final MemberBadgeRepository memberBadgeRepository;

    public List<BadgeResponseDto> getBadgesByMemberId(Long memberId) {
        return memberBadgeRepository.findAllByMemberId(memberId)
                .stream()
                .map(BadgeResponseDto::fromEntity)
                .toList();
    }
}