package com.sacret.note.controller;

import com.sacret.note.model.request.PostRequest;
import com.sacret.note.model.response.PostResponse;
import com.sacret.note.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Common post API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/posts")
@SecurityRequirement(name = "note")
public class CommonPostController {

    private final PostService postService;

    @PostMapping
    @Operation(description = "Create a new post")
    public PostResponse createPost(
            @Parameter(hidden = true) @RequestAttribute(required = false) String userId,
            @RequestBody @Valid PostRequest request
    ) {
        return postService.createPost(request, userId);
    }

    @GetMapping
    @Operation(description = "Get all posts")
    @Parameter(
            in = ParameterIn.QUERY, name = "page",
            schema = @Schema(type = "integer", defaultValue = "0")
    )
    @Parameter(
            in = ParameterIn.QUERY, name = "size",
            schema = @Schema(type = "integer", defaultValue = "10")
    )
    @Parameter(in = ParameterIn.QUERY, name = "sort", schema = @Schema(defaultValue = "created_at,desc"))
    public Page<PostResponse> getPosts(@Parameter(hidden = true) Pageable pageable) {
        return postService.getPosts(pageable);
    }
}
