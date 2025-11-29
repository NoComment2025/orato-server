package nocomment.orato.domain.auth.service;

import lombok.RequiredArgsConstructor;
import nocomment.orato.domain.auth.dto.LoginRequest;
import nocomment.orato.domain.auth.dto.LoginResponse;
import nocomment.orato.domain.auth.dto.SignUpRequest;
import nocomment.orato.domain.auth.dto.SignUpResponse;
import nocomment.orato.domain.auth.entity.User;
import nocomment.orato.domain.auth.repository.UserRepository;
import nocomment.orato.global.jwt.JWTUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {
        // 사용자명 중복 체크
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 생성
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encodedPassword);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setProvider("local"); // 일반 회원가입
        user.setRole("ROLE_USER");

        User savedUser = userRepository.save(user);

        return new SignUpResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getName(),
                savedUser.getRole()
        );
    }

    public LoginResponse login(LoginRequest request) {
        // 사용자명으로 사용자 찾기
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("사용자명 또는 비밀번호가 올바르지 않습니다.");
        }

        // OAuth2 사용자 체크
        if (!"local".equals(user.getProvider())) {
            throw new IllegalArgumentException("소셜 로그인으로 가입된 계정입니다.");
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("사용자명 또는 비밀번호가 올바르지 않습니다.");
        }

        // JWT 토큰 생성 (3시간 유효)
        String token = jwtUtil.createJwt(user.getUsername(), user.getRole(), user.getName(), 3 * 60 * 60 * 1000L);

        return new LoginResponse(token, user.getName(), user.getUsername(),user.getEmail());
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
