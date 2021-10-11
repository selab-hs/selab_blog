package kr.ac.hs.selab.member.domain;

import kr.ac.hs.selab.auth.dto.CustomOAuth2User;
import kr.ac.hs.selab.auth.dto.CustomUserDetails;
import kr.ac.hs.selab.auth.dto.SocialAttributes;
import kr.ac.hs.selab.common.domain.Date;
import kr.ac.hs.selab.follow.domain.Follow;
import kr.ac.hs.selab.follow.domain.Follows;
import kr.ac.hs.selab.member.domain.vo.*;
import kr.ac.hs.selab.member.dto.MemberSocialSignUpDto;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Member extends Date {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "member_email", nullable = false)
    private String email;

    @Column(name = "member_password")
    private String password;

    @Column(name = "member_name")
    private String name;

    @Column(name = "member_nickname")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_gender")
    private Gender gender;

    @Column(name = "member_birth")
    private String birth;

    @Column(name = "member_phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_social_type", nullable = false)
    private SocialType socialType;

    @Column(name = "member_student_id")
    private String studentId;

    private String socialUserKey;

    @Column(name = "member_term_service", nullable = false)
    private boolean termService;

    @Column(name = "member_term_privacy", nullable = false)
    private boolean termPrivacy;

    @Column(name = "member_term_location", nullable = false)
    private boolean termLocation;

    @Embedded
    private Follows follows;

    public CustomUserDetails toCustomUserDetails() {
        return CustomUserDetails.builder()
                .id(id)
                .email(email)
                .password(password)
                .socialType(socialType)
                .role(role)
                .build();
    }

    public CustomOAuth2User toCustomOAuth2User() {
        return CustomOAuth2User.builder()
                .id(id)
                .email(email)
                .password(password)
                .role(role)
                .socialType(socialType)
                .build();
    }

    public static Member ofSocial(SocialAttributes socialAttributes) {
        return Member.builder()
                .name(socialAttributes.name())
                .email(socialAttributes.email())
                .socialUserKey(socialAttributes.userKey())
                .socialType(socialAttributes.socialType())
                .role(Role.USER)
                .build();
    }

    public boolean isSocial() {
        return socialType != SocialType.BASIC;
    }

    public boolean isNotCompletedSingUp() {
        return Objects.isNull(nickname) ||
                Objects.isNull(birth) ||
                Objects.isNull(phoneNumber);
    }

    public void updateSocialMember(MemberSocialSignUpDto memberSocialSignUpDto) {
        this.gender = memberSocialSignUpDto.getGender();
        this.nickname = memberSocialSignUpDto.getNickname();
        this.phoneNumber = memberSocialSignUpDto.getPhoneNumber();
        this.birth = memberSocialSignUpDto.getBirth();
        this.studentId = memberSocialSignUpDto.getStudentId();
        this.termLocation = memberSocialSignUpDto.isTermLocation();
        this.termPrivacy = memberSocialSignUpDto.isTermPrivacy();
        this.termService = memberSocialSignUpDto.isTermService();
    }

    /**
     * 팔로우
     */
    public boolean follow(Member toMember) {
        Follow follow = new Follow(this, toMember);
        if (isFollowing(follow)) {
            return removeFollow(follow, toMember);
        }
        return addFollow(follow, toMember);
    }

    private boolean isFollowing(Follow follow) {
        return follows.existsFromFollows(follow) && follows.existsToFollows(follow);
    }

    private boolean addFollow(Follow follow, Member toMember) {
        follows.addFromFollows(follow);
        toMember.follows.addToFollows(follow);
        return true;
    }

    private boolean removeFollow(Follow follow, Member toMember) {
        follows.removeFromFollows(follow);
        toMember.follows.removeToFollows(follow);
        return false;
    }
}
