package com.seoultech.codemos.service.oauth;

import com.seoultech.codemos.dto.TokenDto;
import com.seoultech.codemos.jwt.TokenProvider;
import com.seoultech.codemos.model.UserEntity;
import com.seoultech.codemos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private GoogleUserService googleUserService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User;

        try {
            oAuth2User = super.loadUser(userRequest);
            if (oAuth2User == null) {
                throw new OAuth2AuthenticationException("OAuth2User is null after loadUser call.");
            }
            return processOAuth2User(userRequest, oAuth2User);
        } catch (OAuth2AuthenticationException e) {
            System.out.println("OAuth2AutheticationException");
            System.out.println(e);
            throw e;
        } catch (Exception ex) {
            System.out.println("Exception");
            System.out.println(ex);
            throw new OAuth2AuthenticationException(String.valueOf(ex));
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        System.out.println("UserEmail: " + email);

        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        UserEntity user;
        if (!userOptional.isPresent()) {
            // 사용자 등록 로직
            user = googleUserService.registerOrUpdateGoogleUser(email, name);
            System.out.println("새 유저 생성 로직으로");
        } else {
            user = userOptional.get();
            System.out.println("이미 있는 유저, JWT 토큰 발급 로직 여기서");
        }

        System.out.println("!1");
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));


        System.out.println("!2");
        OAuth2AuthenticationToken authToken = new OAuth2AuthenticationToken(oAuth2User, authorities, userRequest.getClientRegistration().getRegistrationId());

        System.out.println("!3");
        TokenDto tokenDto = tokenProvider.generateTokenDto(authToken);
        System.out.println("Generated JWT: " + tokenDto.getAccessToken());

        return oAuth2User;
    }

}
