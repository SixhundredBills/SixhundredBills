package com.sparta.sixhundredbills.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 다양한 예외 상황을 정의하고 각 상황에 맞는 상태 코드 & 메시지 제공.

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum ErrorEnum {
    // user 관련 예외 상황 정의
    BAD_RESIGN(403, "이미 탈퇴한 회원입니다."),
    BAD_DUPLICATE(400, "중복되거나 탈퇴한 사용자가 존재합니다."),

    // Token 관련 예외 상황 정의
    NOT_TOKEN(401,"토큰이 존재하지 않습니다."),
    NOT_VALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    EXPIRED_REFRESH_TOKEN_VALUE(403, "리프레시 토큰이 만료되었습니다, 재로그인이 필요합니다"),
    EXPIRED_TOKEN_VALUE(403, "토큰이 만료되었습니다, 재발급이 필요합니다"),
    BESIDES_TOKEN(500, "토큰 유효성 검증 오류 입니다."),

    // authorization 관련 예외 상황 정의
    USER_NOT_FOUND(400, "등록되지 않은 사용자입니다."),
    BAD_PASSWORD(400, "현재 비밀번호와 일치하지 않습니다."),
    BAD_PASSWORD_LIST(400, "새로운 비밀번호는 현재 비밀번호 및 최근 사용한 비밀번호와 달라야 합니다."),
    NOT_LOGIN(401, "로그인이 필요한 서비스입니다"),
    BAD_LOGIN_VALUE(400, "정확한 이메일과 비밀번호를 입력해주세요."),

    // post 관련 예외 상황 정의
    NOT_ROLE(401, "작성자 또는 관리자만 수정할 수 있습니다."),
    NOT_POST(400, "해당 게시물은 존재하지 않습니다."),
    NOT_CATEGORY(400, "카테고리에 일상공유, 고민상담, 익명토론 중 하나를 입력해주세요."),

    // comment 관련 예외 상황 정의
    NOT_COMMENT(400, "해당 댓글은 존재하지 않습니다."),

    // post_like 관련 예외 상황 정의
    POST_NOT_FOUND(404, "게시물을 찾을 수 없습니다."),
    POST_ALREADY_LIKED(400, "이미 좋아요를 했습니다."),
    CANNOT_LIKE_OWN_POST(400, "자신의 게시물에는 좋아요를 할 수 없습니다."),
    LIKE_NOT_FOUND(400, "좋아요를 하지 않았습니다."),

    // comment_like 관련 예외 상황 정의
    COMMENT_NOT_FOUND(404, "댓글을 찾을 수 없습니다."),
    COMMENT_ALREADY_LIKED(400, "이미 좋아요를 했습니다."),
    CANNOT_LIKE_OWN_COMMENT(400, "자신의 댓글에는 좋아요를 할 수 없습니다.");

    int statusCode; // 예외 발생 시 반환할 HTTP 상태 코드
    String message; // 예외 메시지
}