package com.seoultech.codemos.service;

import com.seoultech.codemos.dto.TokenDto;
import com.seoultech.codemos.dto.UserRequestDTO;
import com.seoultech.codemos.dto.UserResponseDTO;
import com.seoultech.codemos.jwt.TokenProvider;
import com.seoultech.codemos.model.UserEntity;
import com.seoultech.codemos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final AuthenticationManagerBuilder managerBuilder;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public UserResponseDTO signup(UserRequestDTO requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다");
        }
        UserEntity user = requestDto.toUser(passwordEncoder);
        return UserResponseDTO.of(userRepository.save(user));
    }

    public TokenDto login(UserRequestDTO requestDto) {
        UsernamePasswordAuthenticationToken authenticationToken = requestDto.toAuthentication();
        Authentication authentication = managerBuilder.getObject().authenticate(authenticationToken);
        //궁극적으로 만들어야 할 SpringSecurity 출입증.
        return tokenProvider.generateTokenDto(authentication);
    }
    public Authentication getAuthentication(String email) {
        return new UsernamePasswordAuthenticationToken(email, null);
    }

}