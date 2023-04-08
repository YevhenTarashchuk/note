package com.sacret.note.model.request;

import jakarta.validation.constraints.NotBlank;

public record RegistrationRequest(
        @NotBlank
        String login,

        @NotBlank
        String password
) { }
