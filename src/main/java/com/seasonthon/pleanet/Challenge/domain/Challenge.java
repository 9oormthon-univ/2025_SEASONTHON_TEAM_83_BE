package com.seasonthon.pleanet.Challenge.domain;

import com.seasonthon.pleanet.member.domain.enums.Interest;
import jakarta.persistence.*;
import lombok.*;
import com.seasonthon.pleanet.global.BaseEntity;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Challenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String imageUrl;

    private String description;

    @Enumerated(EnumType.STRING)
    private ChallengeType type;

    private Double requiredDistance;

    private Interest requiredPhotoCount;

    private Integer rewardPoint;

}

