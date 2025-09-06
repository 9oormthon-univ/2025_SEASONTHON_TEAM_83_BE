package com.seasonthon.pleanet.badge.domain;

import com.seasonthon.pleanet.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private BadgeType category;  // ACTIVITY, POINT, SPECIAL


    private String iconUrl;

}
