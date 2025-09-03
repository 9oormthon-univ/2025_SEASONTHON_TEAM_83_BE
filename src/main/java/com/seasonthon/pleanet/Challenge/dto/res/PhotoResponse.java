package com.seasonthon.pleanet.Challenge.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PhotoResponse {
    private String photoUrl;  // 업로드된 S3 URL
    private boolean success;  // 업로드 성공 여부
}

