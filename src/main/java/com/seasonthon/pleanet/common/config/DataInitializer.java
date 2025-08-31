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

        insertOrUpdate("걷기 1km 미션",
                "https://example.com/walk1km.png",
                "사용자가 최소 1km 이상 걸으면 완료되는 걷기 인증 미션입니다.",
                ChallengeType.GPS, 1.0, null, 20);

        insertOrUpdate("텀블러 미션",
                "https://example.com/photo.png",
                "사진 인증 미션입니다. 최소 2장 사진 필요",
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

