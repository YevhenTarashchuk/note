package proxy_seller.test.model;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LikeCountResult {
    private String postId;
    private Long likeCount;
}
