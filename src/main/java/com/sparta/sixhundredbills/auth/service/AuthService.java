package com.sparta.sixhundredbills.auth.service;

import com.sparta.sixhundredbills.auth.dto.LoginRequestDto;
import com.sparta.sixhundredbills.auth.entity.User;
import com.sparta.sixhundredbills.auth.entity.UserStatusEnum;
import com.sparta.sixhundredbills.auth.jwt.JwtUtil;
import com.sparta.sixhundredbills.auth.repository.UserRepository;
import com.sparta.sixhundredbills.exception.CommonResponse;
import com.sparta.sixhundredbills.exception.ErrorEnum;
import com.sparta.sixhundredbills.exception.InvalidEnteredException;
import com.sparta.sixhundredbills.exception.NotRoleException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service // Spring에서 이 클래스를 서비스로 등록하여 관리.
@RequiredArgsConstructor // Lombok을 사용하여 필수 생성자를 자동으로 생성.
public class AuthService {

    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화에 사용될 PasswordEncoder
    private final JwtUtil jwtUtil; // JWT 관련 유틸리티 클래스
    private final UserRepository userRepository; // 사용자 정보를 관리하는 Repository

    // 사용자 로그인 처리 메서드
    @Transactional // 트랜잭션 처리를 위한 어노테이션
    public ResponseEntity<CommonResponse<Void>> login(LoginRequestDto loginRequestDto, HttpServletResponse response, HttpServletRequest request) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new InvalidEnteredException(ErrorEnum.BAD_LOGIN_VALUE));

        // 사용자명을 기반으로 사용자 정보를 데이터베이스에서 조회.
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new InvalidEnteredException(ErrorEnum.BAD_LOGIN_VALUE);
        }

        // 사용자 상태가 USER_RESIGN인 경우 로그인을 허용 X.
        if (user.getUserStatus().equals(UserStatusEnum.USER_RESIGN)){
//            request.setAttribute("test", ErrorEnum.BAD_RESIGN);
            throw new NotRoleException(ErrorEnum.BAD_RESIGN);
        }

        // Refresh Token 생성 및 쿠키에 저장
        String refreshToken = jwtUtil.createRefreshToken(loginRequestDto.getEmail());
        jwtUtil.addRefreshJwtToCookie(refreshToken, response);

        // Access Token 생성 및 쿠키에 저장
        String accessToken = jwtUtil.createAccessToken(loginRequestDto.getEmail());
        jwtUtil.addAccessJwtToCookie(accessToken, response);

        // 사용자 객체에 생성된 Refresh Token을 저장. 나중에 사용자가 Refresh Token을 사용하여 접근 토큰을 재발급받을 때 사용.
        user.setRefreshToken(refreshToken);

        CommonResponse<Void> commonResponse = CommonResponse.<Void>builder()
                .msg("로그인 성공")
                .statusCode(200)
                .data(null)  // data 필드를 null로 설정
                .build();

        return ResponseEntity.ok(commonResponse);
    }

    // Access Token 재발급 처리 메서드
    public String tokenReissuance(String refreshToken, HttpServletResponse res) {
        // Refresh Token으로부터 사용자명을 추출.
        String email = String.valueOf(jwtUtil.getUserInfoFromToken(refreshToken).getSubject());

        // 데이터베이스에서 해당 사용자를 조회합니다. 사용자가 존재하지 않으면 예외를 발생.
        User user = userRepository.findByEmail(email).orElseThrow(() -> new InvalidEnteredException(ErrorEnum.USER_NOT_FOUND));

        // 데이터베이스에 저장된 Refresh Token과 입력된 Refresh Token을 비교하여 일치 여부를 확인.
        String storedRefreshToken = jwtUtil.substringToken(user.getRefreshToken());
        if (storedRefreshToken.equals(refreshToken)) {
            // 새로운 Access Token을 생성합니다.
            String newToken = jwtUtil.createAccessToken(email);
            // 생성된 Access Token을 HTTP 응답의 쿠키에 추가하여 클라이언트에게 반환.
            jwtUtil.addAccessJwtToCookie(newToken, res);
            return newToken; // 생성된 Access Token 반환
        }
        return "";
    }

    // 사용자 로그아웃 처리 메서드
    public void invalidateTokens(HttpServletResponse response, HttpServletRequest request) {
        // HTTP 요청에서 Access Token을 추출.
        String accessToken = jwtUtil.getAccessTokenFromRequest(request);
        accessToken = jwtUtil.substringToken(accessToken);
        // 추출된 Access Token을 이용하여 사용자명을 얻음.
        String email = String.valueOf(jwtUtil.getUserInfoFromToken(accessToken).getSubject());

        // 데이터베이스에서 해당 사용자를 조회합니다. 사용자가 존재하지 않으면 예외를 발생.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidEnteredException(ErrorEnum.USER_NOT_FOUND));

        // 사용자의 Refresh Token을 삭제하여 로그아웃 처리.
        jwtUtil.initJwtCookie(response); // 쿠키 초기화
        user.setRefreshToken(null); // Refresh Token 삭제
        userRepository.save(user); // 변경된 사용자 정보 저장
    }
}
