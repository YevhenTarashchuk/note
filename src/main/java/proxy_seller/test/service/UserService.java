package proxy_seller.test.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import proxy_seller.test.model.entity.UserEntity;
import proxy_seller.test.model.request.UserRequest;
import proxy_seller.test.model.response.UserResponse;
import proxy_seller.test.repository.UserRepository;
import proxy_seller.test.util.ValidationUtil;

import static proxy_seller.test.constant.ExceptionConstant.USER_EXISTS;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserResponse register(UserRequest request) {
        ValidationUtil.validateOrBadRequest(
                !userRepository.existsByLogin(request.login()),
                String.format(USER_EXISTS, request.login())
        );
        UserEntity user = userRepository.save(modelMapper.map(request, UserEntity.class));
        return modelMapper.map(user, UserResponse.class);
    }
}
