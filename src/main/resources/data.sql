-- 걷기 인증 미션 1km
INSERT IGNORE INTO challenge
(id, title, image_url, description, type, required_distance, reward_point,created_at,updated_at)
VALUES
    (1,
     '걷기 1km 미션',
     'https://example.com/walk1km.png',
     '사용자가 최소 1km 이상 걸으면 완료되는 걷기 인증 미션입니다.',
     'GPS',
     1.0,
     20,
     NOW(),
     NOW()) ON DUPLICATE KEY UPDATE
                                        title = VALUES(title),
                                        image_url = VALUES(image_url),
                                        description = VALUES(description),
                                        type = VALUES(type),
                                        required_distance = VALUES(required_distance),
                                        reward_point = VALUES(reward_point),
                                        updated_at = NOW();

-- 사진 인증 미션 (사진 2장 필요)
INSERT IGNORE INTO challenge
(id, title, image_url, description, type, required_distance, reward_point,created_at,updated_at)
VALUES
    (2,
     '사진 인증 미션',
     'https://example.com/photo.png',
     '사진 인증 미션입니다. 최소 3장 사진 필요',
     'PHOTO',
     2,
     50,
     NOW(),
     NOW()) ON DUPLICATE KEY UPDATE
                                        title = VALUES(title),
                                        image_url = VALUES(image_url),
                                        description = VALUES(description),
                                        type = VALUES(type),
                                        required_distance = VALUES(required_distance),
                                        reward_point = VALUES(reward_point),
                                        updated_at = NOW();