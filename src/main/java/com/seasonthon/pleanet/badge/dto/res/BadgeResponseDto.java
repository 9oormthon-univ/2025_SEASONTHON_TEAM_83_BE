package com.seasonthon.pleanet.badge.dto.res;

import com.seasonthon.pleanet.badge.domain.MemberBadge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class BadgeResponseDto {
    private Long badgeId;
    private String name;
    private String iconUrl;
    private String category;
    private LocalDateTime earnedAt;

    public static BadgeResponseDto fromEntity(MemberBadge memberBadge) {
        return BadgeResponseDto.builder()
                .badgeId(memberBadge.getBadge().getId())
                .name(memberBadge.getBadge().getName())
                .iconUrl(memberBadge.getBadge().getIconUrl())
                .category(memberBadge.getBadge().getCategory().name())
                .earnedAt(memberBadge.getCreatedAt())
                .build();
    }
}
