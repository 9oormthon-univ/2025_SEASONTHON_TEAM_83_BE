package com.seasonthon.pleanet.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class SearchResultResponse {
    private List<ActivityResult> activities;
    private List<RewardResult> rewards;
    private List<UserResult> users;

    @Data @Builder @AllArgsConstructor
    public static class ActivityResult {
        private Long id;
        private String title;
        private String date;
    }

    @Data @Builder @AllArgsConstructor
    public static class RewardResult {
        private Long id;
        private String name;
        private Integer cost;
    }

    @Data @Builder @AllArgsConstructor
    public static class UserResult {
        private Long id;
        private String nickname;
    }
}

