package com.sacret.note.controller;

import com.sacret.note.model.response.PostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sacret.note.service.PostService;

@Tag(name = "Common post API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/posts")
@SecurityRequirement(name = "note")
public class PostController {

    private final PostService postService;

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

    @PutMapping("/{postId}/likes")
    @Operation(description = "Add or remove like - user authorization is required")
    public ResponseEntity<Void> updateLike(
            @Parameter(hidden = true) @RequestAttribute String userId,
            @PathVariable String postId
    ) {
        postService.updateLike(userId, postId);
        return ResponseEntity.noContent()
                .build();
    }
}
