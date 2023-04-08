package com.sacret.note.model.request;

import jakarta.validation.constraints.NotBlank;

public record PostRequest(@NotBlank String content) { }
