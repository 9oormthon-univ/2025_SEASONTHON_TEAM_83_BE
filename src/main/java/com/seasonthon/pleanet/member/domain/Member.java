package com.seasonthon.pleanet.member.domain;

import com.seasonthon.pleanet.member.domain.enums.Interest;
import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.seasonthon.pleanet.member.domain.enums.SocialType;
import com.seasonthon.pleanet.global.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
@DynamicUpdate
@DynamicInsert //이 두 개는 insert와 update 시 null 인 경우는 그냥 쿼리를 보내지 않도록 해줍니다.
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private Long socialId;

    private String email;

    private String password;

    private LocalDate birthday;

    private String profileUrl;

    @ElementCollection(targetClass = Interest.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "member_interests", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "interest")
    @Builder.Default
    private Set<Interest> interests = new HashSet<>();

    @Builder.Default
    private Boolean allowPush = false;

    @Builder.Default
    private Boolean allowLocation = false;

    public void encodePassword(String password) {
        this.password = password;
    }

    public void setInterests(Set<Interest> newInterests) {
        if (newInterests == null) {
            this.interests.clear();
        } else {
            this.interests.clear();
            this.interests.addAll(newInterests);
        }
    }


}
