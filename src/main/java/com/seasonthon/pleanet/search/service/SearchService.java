package com.seasonthon.pleanet.search.service;

import com.seasonthon.pleanet.Challenge.repository.MemberChallengeRepository;
import com.seasonthon.pleanet.reward.repository.RewardRepository;
import com.seasonthon.pleanet.reward.domain.RewardType;
import com.seasonthon.pleanet.member.repository.MemberRepository;
import com.seasonthon.pleanet.search.dto.SearchResultResponse;
import com.seasonthon.pleanet.search.domain.SearchHistory;
import com.seasonthon.pleanet.search.repository.SearchHistoryRepository;
import com.seasonthon.pleanet.member.domain.Member;
import com.seasonthon.pleanet.Challenge.repository.ChallengeRepository;
import com.seasonthon.pleanet.apiPayload.exception.GeneralException;
import com.seasonthon.pleanet.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ChallengeRepository challengeRepository;
    private final RewardRepository rewardRepository;
    private final MemberRepository memberRepository;
    private final SearchHistoryRepository searchHistoryRepository;

    /**
     * 통합 검색
     * - 활동 / 리워드 / 유저 검색
     * - 검색 실행 시 SearchHistory에 자동 저장 (최대 5개 유지, 중복 허용)
     */
    @Transactional
    public SearchResultResponse search(Member member, String keyword) {
        // 1. 검색 기록 저장
        saveSearchHistory(member, keyword);

        // 2. 활동(챌린지) 검색 - 제목(title) 기준
        var activities =   challengeRepository.findByTitleContaining(keyword)
                .stream()
                .map(c -> new SearchResultResponse.ActivityResult(
                        c.getId(),
                        c.getTitle(),
                        c.getDescription()
                ))
                .toList();

        // 3. 리워드 검색 (type 기준)
        List<SearchResultResponse.RewardResult> rewards = Collections.emptyList();
        try {
            RewardType rewardType = RewardType.valueOf(keyword.toUpperCase());
            rewards = rewardRepository.findByType(rewardType)
                    .stream()
                    .map(r -> new SearchResultResponse.RewardResult(
                            r.getId(),
                            r.getType().name(),   // ENUM → 문자열
                            r.getPointUsed()
                    ))
                    .toList();
        } catch (IllegalArgumentException e) {
            // keyword가 RewardType에 해당하지 않으면 → 빈 리스트 반환
        }

        // 4. 유저 검색
        var users = memberRepository.findByNicknameContaining(keyword)
                .stream()
                .map(m -> new SearchResultResponse.UserResult(
                        m.getId(),
                        m.getNickname()
                ))
                .toList();

        return SearchResultResponse.builder()
                .activities(activities) // 못 찾으면 []
                .rewards(rewards)       // 못 찾으면 []
                .users(users)           // 못 찾으면 []
                .build();
    }

    /**
     * 검색 기록 저장 (중복 허용, 최대 5개 유지)
     */
    private void saveSearchHistory(Member member, String keyword) {
        // 1. 새 기록 저장
        SearchHistory history = SearchHistory.builder()
                .member(member)
                .keyword(keyword)
                .createdAt(LocalDateTime.now())
                .build();
        searchHistoryRepository.save(history);

        // 2. 유저의 전체 검색 기록 개수 확인
        List<SearchHistory> histories = searchHistoryRepository.findByMemberOrderByCreatedAtDesc(member);

        // 3. 5개 초과 시 → 오래된 것부터 삭제
        if (histories.size() > 5) {
            List<SearchHistory> toDelete = histories.subList(5, histories.size()); // 6번째 이후
            searchHistoryRepository.deleteAll(toDelete);
        }
    }
}
