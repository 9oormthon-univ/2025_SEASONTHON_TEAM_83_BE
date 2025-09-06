package com.seasonthon.pleanet.badge.dto.res;

import com.seasonthon.pleanet.badge.domain.MemberBadge;
import lombok.Getter;

@Getter
public class MemberBadgeResponse {
    private final Long badgeId;
    private final String badgeName;

    public MemberBadgeResponse(MemberBadge memberBadge) {
        this.badgeId = memberBadge.getBadge().getId();
        this.badgeName = memberBadge.getBadge().getName();
    }
}
