-- 걷기 인증 미션 1km
INSERT INTO challenge
(title, image_url, description, type, required_distance, reward_point,created_at, updated_at)
VALUES
    ('걷기 1km 미션',
     'https://example.com/walk1km.png',
     '사용자가 최소 1km 이상 걸으면 완료되는 걷기 인증 미션입니다.',
     'GPS',
     1.0,
     20,
     NOW(),
     NOW());

-- 사진 인증 미션 (사진 2장 필요)
INSERT INTO challenge
(title, image_url, description, type,required_photo_count, reward_point, created_at,updated_at)
VALUES
    ('사진 인증 미션',
     'https://example.com/photo.png',
     '사진 인증 미션입니다. 최소 3장 사진 필요',
     'PHOTO',
     2,
     50,
     NOW(),
     NOW());


