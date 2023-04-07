package proxy_seller.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proxy_seller.test.model.request.PostRequest;
import proxy_seller.test.model.response.PostResponse;
import proxy_seller.test.service.PostService;

@Tag(name = "Post API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(description = "Create new post")
    public PostResponse createPost(@RequestBody @Valid PostRequest request) {
        return postService.createPost(request);
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
    public Page<PostResponse> getPosts(
            @Parameter(hidden = true) Pageable pageable) {
        return postService.getPosts(pageable);
    }

    @PutMapping("/{postId}/likes")
    @Operation(description = "Add or remove favorites")
    public ResponseEntity<Void> updateFavorite(@RequestParam String userId, @PathVariable String postId) {
        postService.updateFavorite(userId, postId);
        return ResponseEntity.noContent()
                .build();
    }
}
