package com.seasonthon.pleanet.member.service;

import com.seasonthon.pleanet.member.converter.MemberConverter;
import com.seasonthon.pleanet.member.domain.Member;

import com.seasonthon.pleanet.member.dto.res.MemberResponseDto;
import com.seasonthon.pleanet.member.repository.MemberRepository;
import com.seasonthon.pleanet.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;
    //private final BadgeRepository badgeRepository;

    public Page<MemberResponseDto.MemberRankingDto> getRanking(Pageable pageable) {
        // 1. 멤버 가져오기 (페이징)
        Page<Member> memberPage = memberRepository.findAll(pageable);

        // 2. DTO 변환 + 포인트 + 배지 조회
        List<MemberResponseDto.MemberRankingDto> rankingList = memberPage.getContent().stream()
                .map(member -> {
                    Integer totalPoint = pointRepository.getUserTotalEarnedPoints(member.getId());
                    //Integer badgeCount = badgeRepository.countByMember(member.getId());
                    Integer badgeCount =0;
                    return MemberConverter.toMemberRankingDto(member, totalPoint,badgeCount);
                })
                // 3. 포인트 많은 순 정렬
                .sorted(Comparator.comparing(MemberResponseDto.MemberRankingDto::getTotalPoint).reversed())
                .collect(Collectors.toList());

        // 4. 순위(rank) 부여
        long startRank = pageable.getOffset() + 1;
        AtomicLong rankCounter = new AtomicLong(startRank);
        rankingList.forEach(dto -> dto.setRank(rankCounter.getAndIncrement()));

        // 5. PageImpl으로 반환
        return new PageImpl<>(rankingList, pageable, memberPage.getTotalElements());
    }
}
