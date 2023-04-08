package com.sacret.note.service;

import com.sacret.note.constant.ExceptionConstant;
import com.sacret.note.exception.NotFoundException;
import com.sacret.note.model.entity.UserEntity;
import com.sacret.note.model.request.RegistrationRequest;
import com.sacret.note.repository.UserRepository;
import com.sacret.note.security.CustomUserDetails;
import com.sacret.note.security.util.JwtUtilService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.sacret.note.model.enumeration.Role;
import com.sacret.note.model.response.AuthResponse;
import com.sacret.note.model.response.UserDetailsResponse;
import com.sacret.note.util.ValidationUtil;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtUtilService jwtUtilService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public AuthResponse register(RegistrationRequest request) {
        ValidationUtil.validateOrBadRequest(
                !userRepository.existsByLogin(request.login()),
                String.format(ExceptionConstant.USER_EXISTS, request.login())
        );

        UserEntity user = userRepository.save(new UserEntity()
                .setLogin(request.login())
                .setPassword(encoder.encode(request.password()))
                .setRole(Role.ROLE_USER));

        CustomUserDetails userDetails = new CustomUserDetails(user.getId(), user.getRole());

        return new AuthResponse()
                .setAccessToken(jwtUtilService.generateToken(userDetails).token())
                .setRefreshToken(jwtUtilService.generateRefreshToken(userDetails).token());
    }

    public UserDetailsResponse getUserDetails(String login) {
        UserEntity user = userRepository.findByLogin(login)
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionConstant.USER_NOT_FOUND, ExceptionConstant.LOGIN, login)));

        return modelMapper.map(user, UserDetailsResponse.class);
    }

    public void validateUserExistence(String userId) {
        ValidationUtil.validateOrNotFound(
                userRepository.existsById(userId),
                String.format(ExceptionConstant.USER_NOT_FOUND, ExceptionConstant.ID, userId)
        );
    }
}
