package nocomment.orato.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String name;

    private String email;

    private String password; // 일반 회원가입용 비밀번호 (OAuth2 사용자는 null)

    private String provider; // OAuth2 제공자 (google, naver) 또는 "local"

    private String role;
}
