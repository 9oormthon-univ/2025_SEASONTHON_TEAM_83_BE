package com.seasonthon.pleanet.badge.service;

import com.seasonthon.pleanet.Challenge.domain.ChallengeType;
import com.seasonthon.pleanet.Challenge.repository.MemberChallengeRepository;
import com.seasonthon.pleanet.badge.domain.Badge;
import com.seasonthon.pleanet.badge.domain.MemberBadge;
import com.seasonthon.pleanet.badge.repository.BadgeRepository;
import com.seasonthon.pleanet.badge.repository.MemberBadgeRepository;
import com.seasonthon.pleanet.member.domain.Member;
import com.seasonthon.pleanet.member.repository.MemberRepository;
import com.seasonthon.pleanet.point.repository.PointRepository;
import com.seasonthon.pleanet.reward.domain.RewardType;
import com.seasonthon.pleanet.reward.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final MemberBadgeRepository memberBadgeRepository;
    private final PointRepository pointRepository;
    private final MemberChallengeRepository memberChallengeRepository;
    private final MemberRepository memberRepository;
    private final RewardRepository rewardRepository;

    public List<MemberBadge> checkAndAssignBadges(Long memberId) {
        List<MemberBadge> newlyAssigned = new ArrayList<>();

        // 1. 활동 기반
        if (memberChallengeRepository.countByMemberIdAndChallenge_Type(memberId, ChallengeType.GPS) >= 10) {
            assignIfNotExists(memberId, "초록 발걸음").ifPresent(newlyAssigned::add);
        }
        if (memberChallengeRepository.countByMemberIdAndChallenge_Type(memberId, ChallengeType.PHOTO) >= 10) {
            assignIfNotExists(memberId, "텀블러 지킴이").ifPresent(newlyAssigned::add);
        }
        // 🌎 지구 지킴이 (리워드 기반)
        long treeRewardCount = rewardRepository.countByMemberIdAndRewardType(memberId, RewardType.TREE);
        if (treeRewardCount >= 3) {
            assignIfNotExists(memberId, "지구 지킴이").ifPresent(newlyAssigned::add);
        }


        // 2. 포인트 기반
        int totalPoint = pointRepository.sumByMemberId(memberId);
        if (totalPoint < 500) {
            assignIfNotExists(memberId, "새싹").ifPresent(newlyAssigned::add);
        } else if (totalPoint < 1500) {
            assignIfNotExists(memberId, "묘목").ifPresent(newlyAssigned::add);
        } else if (totalPoint < 3000) {
            assignIfNotExists(memberId, "나무").ifPresent(newlyAssigned::add);
        } else if (totalPoint < 5000) {
            assignIfNotExists(memberId, "숲").ifPresent(newlyAssigned::add);
        }

        // 3. 이벤트 기반
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        if (today.equals(LocalDate.of(today.getYear(), 4, 5))) {
            assignIfNotExists(memberId, "식목일 챌린지 뱃지").ifPresent(newlyAssigned::add);
        }
        if (today.equals(LocalDate.of(today.getYear(), 4, 22))) {
            assignIfNotExists(memberId, "지구의 날 챌린지 뱃지").ifPresent(newlyAssigned::add);
        }

        Member member = memberRepository.findById(memberId).orElseThrow();

        // 오늘 날짜와 생일 월/일 비교
                if (today.getMonth() == member.getBirthday().getMonth() &&
                        today.getDayOfMonth() == member.getBirthday().getDayOfMonth()) {
                    assignIfNotExists(memberId, "생일 뱃지").ifPresent(newlyAssigned::add);
                }

        // 전체 보유 뱃지 반환
        return memberBadgeRepository.findAllByMemberId(memberId);
    }

    private Optional<MemberBadge> assignIfNotExists(Long memberId, String badgeName) {
        Badge badge = badgeRepository.findByName(badgeName).orElse(null);
        if (badge == null) return Optional.empty();

        boolean exists = memberBadgeRepository.existsByMemberIdAndBadgeId(memberId, badge.getId());
        if (exists) return Optional.empty();

        Member member = memberRepository.findById(memberId).orElseThrow();
        MemberBadge memberBadge = MemberBadge.builder()
                .member(member)
                .badge(badge)
                .build();

        return Optional.of(memberBadgeRepository.save(memberBadge));
    }
}
