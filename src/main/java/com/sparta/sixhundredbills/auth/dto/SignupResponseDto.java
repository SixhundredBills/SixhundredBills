package com.sparta.sixhundredbills.auth.dto;

import com.sparta.sixhundredbills.auth.entity.Role;
import com.sparta.sixhundredbills.auth.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 회원가입 응답을 위한 데이터 전송 객체 (DTO).
 * 회원가입 후 사용자 정보를 클라이언트에게 전달하는 역할.
 */

@Getter
public class SignupResponseDto {

    private String email;          // 사용자 아이디
    private String name;           // 사용자 이름
    private LocalDateTime createdAt;  // 계정 생성 시간
    private LocalDateTime modifiedAt; // 계정 수정 시간
    private String userStatusEnum;    // 사용자 상태
    private Role role;              // 사용자 권한

    /**
     * User 엔티티 객체로부터 데이터를 가져와서 SignupResponseDto 객체를 생성.
     *
     * @param user User 엔티티 객체
     */
    public SignupResponseDto(User user) {
        this.email = user.getEmail();        // User 엔티티로부터 사용자 아이디를 가져옴
        this.name = user.getName();          // User 엔티티로부터 사용자 이름을 가져옴
        this.createdAt = user.getCreatedAt();      // User 엔티티로부터 계정 생성 시간을 가져옴
        this.modifiedAt = user.getModifiedAt();    // User 엔티티로부터 계정 수정 시간을 가져옴
        this.userStatusEnum = user.getUserStatus().getStatus(); // User 엔티티로부터 사용자 상태를 가져옴
        this.role = Role.valueOf(user.getRole());         // User 엔티티로부터 사용자 권한을 가져옴
    }
}
