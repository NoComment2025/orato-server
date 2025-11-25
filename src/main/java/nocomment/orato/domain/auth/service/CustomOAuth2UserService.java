package nocomment.orato.domain.auth.service;

import nocomment.orato.domain.auth.dto.*;
import nocomment.orato.domain.auth.entity.User;
import nocomment.orato.domain.auth.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        
        try {
            OAuth2User oAuth2User = super.loadUser(userRequest);
            System.out.println("OAuth2User loaded: " + oAuth2User);

            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            System.out.println("Registration ID: " + registrationId);
            
            OAuth2Response oAuth2Response = null;
            if (registrationId.equals("naver")) {

                oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
            }
            else if (registrationId.equals("google")) {

                oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
            }
            else {
                System.out.println("Unknown provider: " + registrationId);
                return null;
            }
            
            String username = oAuth2Response.getProvider()+"_"+oAuth2Response.getProviderId();
            System.out.println("Generated username: " + username);
            
            User existData = userRepository.findByUsername(username);
            System.out.println("Existing user found: " + (existData != null));

            if (existData == null) {
                System.out.println("Creating new user...");
                
                User userEntity = new User();
                userEntity.setUsername(username);
                userEntity.setEmail(oAuth2Response.getEmail());
                userEntity.setName(oAuth2Response.getName());
                userEntity.setRole("ROLE_USER");
                userEntity.setProvider(oAuth2Response.getProvider()); // OAuth2 제공자 저장

                User savedUser = userRepository.save(userEntity);
                System.out.println("User saved with ID: " + savedUser.getId());

                UserDTO userDTO = new UserDTO();
                userDTO.setUsername(username);
                userDTO.setName(oAuth2Response.getName());
                userDTO.setRole("ROLE_USER");

                return new CustomOAuth2User(userDTO);
            }
            else {
                System.out.println("Updating existing user...");
                
                existData.setEmail(oAuth2Response.getEmail());
                existData.setName(oAuth2Response.getName());

                userRepository.save(existData);

                UserDTO userDTO = new UserDTO();
                userDTO.setUsername(existData.getUsername());
                userDTO.setName(oAuth2Response.getName());
                userDTO.setRole(existData.getRole());

                return new CustomOAuth2User(userDTO);
            }
        } catch (Exception e) {
            System.err.println("Error in CustomOAuth2UserService: " + e.getMessage());
            e.printStackTrace();
            throw new OAuth2AuthenticationException("OAuth2 인증 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
