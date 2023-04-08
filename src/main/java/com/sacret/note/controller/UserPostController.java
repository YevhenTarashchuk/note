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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "User post API (authorization is required)")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/user/posts")
@SecurityRequirement(name = "note")
public class UserPostController {

    private final PostService postService;

    @GetMapping
    @Operation(description = "Get user posts")
    @Parameter(
            in = ParameterIn.QUERY, name = "page",
            schema = @Schema(type = "integer", defaultValue = "0")
    )
    @Parameter(
            in = ParameterIn.QUERY, name = "size",
            schema = @Schema(type = "integer", defaultValue = "10")
    )
    @Parameter(in = ParameterIn.QUERY, name = "sort", schema = @Schema(defaultValue = "created_at,desc"))
    public Page<PostResponse> getPosts(
            @Parameter(hidden = true) @RequestAttribute String userId,
            @Parameter(hidden = true) Pageable pageable
    ) {
        return postService.getPosts(userId, pageable);
    }

    @PutMapping("/{postId}")
    @Operation(description = "Update user post")
    public PostResponse updatePost(
            @Parameter(hidden = true) @RequestAttribute String userId,
            @PathVariable String postId,
            @RequestBody @Valid PostRequest request
    ) {
        return postService.updatePost(request, postId, userId);
    }

    @GetMapping("/{postId}")
    @Operation(description = "Get user post")
    public PostResponse getPost(
            @Parameter(hidden = true) @RequestAttribute String userId,
            @PathVariable String postId
    ) {
        return postService.getPost(postId, userId);
    }

    @DeleteMapping("/{postId}")
    @Operation(description = "Delete user post")
    public ResponseEntity<Void> removePost(
            @Parameter(hidden = true) @RequestAttribute String userId,
            @PathVariable String postId
    ) {
        postService.removePost(postId, userId);
        return ResponseEntity.noContent()
                .build();
    }

    @PutMapping("/{postId}/likes")
    @Operation(description = "Add or remove like")
    public ResponseEntity<Void> updateLike(
            @Parameter(hidden = true) @RequestAttribute String userId,
            @PathVariable String postId
    ) {
        postService.updateLike(userId, postId);
        return ResponseEntity.noContent()
                .build();
    }
}
