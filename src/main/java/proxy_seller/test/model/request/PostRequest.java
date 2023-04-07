package proxy_seller.test.model.request;

import jakarta.validation.constraints.NotBlank;

public record PostRequest(@NotBlank String content) { }
