package com.seasonthon.pleanet.reward.repository;

import com.seasonthon.pleanet.reward.domain.Reward;
import com.seasonthon.pleanet.reward.domain.RewardType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    // ENUM 그대로 검색할 수 있음
    List<Reward> findByRewardType(RewardType rewardType);
    long countByMemberIdAndRewardType(Long memberId, RewardType rewardType);
}
