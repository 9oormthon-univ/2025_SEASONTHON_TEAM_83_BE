package com.seasonthon.pleanet.common.config;

import com.seasonthon.pleanet.Challenge.domain.Challenge;
import com.seasonthon.pleanet.Challenge.domain.ChallengeType;
import com.seasonthon.pleanet.Challenge.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ChallengeRepository challengeRepository;

    @Override
    public void run(String... args) throws Exception {

        insertOrUpdate("1km 걷기",
                "/uploads/challenge1.png",
                "최소 1km 이상 보행 시 성공 처리||GPS 기반으로 사용자의 이동 경로 기록",
                ChallengeType.GPS, 1.0, null, 20);

        insertOrUpdate("텀블러 사용 챌린지",
                "/uploads/challenge2.png",
                "테이크아웃 또는 매장에서 음료를 받을 때||카페 영수증 + 텀블러 사진 제출 (1회 주문당 1회 인정)",
                ChallengeType.PHOTO, null, 2, 50);
    }

    private void insertOrUpdate(String title, String imageUrl, String description,
                                ChallengeType type, Double requiredDistance, Integer requiredPhotoCount,
                                Integer rewardPoint) {

        Optional<Challenge> existing = challengeRepository.findByTitle(title);
        if (existing.isPresent()) {
            Challenge c = existing.get();
            c.setTitle(title);
            c.setImageUrl(imageUrl);
            c.setDescription(description);
            c.setType(type);
            c.setRequiredDistance(requiredDistance);
            c.setRequiredPhotoCount(requiredPhotoCount);
            c.setRewardPoint(rewardPoint);
            challengeRepository.save(c);
        } else {
            Challenge c = Challenge.builder()
                    .title(title)
                    .imageUrl(imageUrl)
                    .description(description)
                    .type(type)
                    .requiredDistance(requiredDistance)
                    .requiredPhotoCount(requiredPhotoCount)
                    .rewardPoint(rewardPoint)
                    .build();
            challengeRepository.save(c);
        }
    }
}

