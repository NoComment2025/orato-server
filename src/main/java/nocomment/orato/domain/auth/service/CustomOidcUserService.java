package nocomment.orato.domain.auth.service;

import nocomment.orato.domain.auth.dto.CustomOAuth2User;
import nocomment.orato.domain.auth.dto.CustomOidcUser;
import nocomment.orato.domain.auth.dto.UserDTO;
import nocomment.orato.domain.auth.entity.User;
import nocomment.orato.domain.auth.repository.UserRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;

    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            OidcUser oidcUser = super.loadUser(userRequest);
            System.out.println("OIDC User loaded: " + oidcUser);

            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            System.out.println("OIDC Registration ID: " + registrationId);

            // Google OIDC에서 정보 추출
            String email = oidcUser.getEmail();
            String name = oidcUser.getFullName();
            String providerId = oidcUser.getSubject(); // Google의 고유 ID

            String username = registrationId + "_" + providerId;
            System.out.println("Generated OIDC username: " + username);

            User existData = userRepository.findByUsername(username);
            System.out.println("Existing user found: " + (existData != null));

            if (existData == null) {
                System.out.println("Creating new OIDC user...");

                User userEntity = new User();
                userEntity.setUsername(username);
                userEntity.setEmail(email);
                userEntity.setName(name);
                userEntity.setRole("ROLE_USER");
                userEntity.setProvider(registrationId);

                User savedUser = userRepository.save(userEntity);
                System.out.println("OIDC User saved with ID: " + savedUser.getId());

                UserDTO userDTO = new UserDTO();
                userDTO.setUsername(username);
                userDTO.setName(name);
                userDTO.setRole("ROLE_USER");

                return new CustomOidcUser(userDTO, oidcUser);
            } else {
                System.out.println("Updating existing OIDC user...");

                existData.setEmail(email);
                existData.setName(name);

                userRepository.save(existData);

                UserDTO userDTO = new UserDTO();
                userDTO.setUsername(existData.getUsername());
                userDTO.setName(name);
                userDTO.setRole(existData.getRole());

                return new CustomOidcUser(userDTO, oidcUser);
            }
        } catch (Exception e) {
            System.err.println("Error in CustomOidcUserService: " + e.getMessage());
            e.printStackTrace();
            throw new OAuth2AuthenticationException("OIDC 인증 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
