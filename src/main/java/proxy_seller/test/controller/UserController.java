package proxy_seller.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proxy_seller.test.model.request.UserRequest;
import proxy_seller.test.model.response.UserResponse;
import proxy_seller.test.service.UserService;

@Tag(name = "User API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(description = "Register user")
    public UserResponse registerUser(@RequestBody @Valid UserRequest request) {
        return userService.register(request);
    }
}
