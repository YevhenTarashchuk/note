package proxy_seller.test.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import proxy_seller.test.model.LikeCountResult;
import proxy_seller.test.model.entity.PostEntity;
import proxy_seller.test.model.entity.PostLikeEntity;
import proxy_seller.test.model.request.PostRequest;
import proxy_seller.test.model.response.PostResponse;
import proxy_seller.test.repository.PostLikeRepository;
import proxy_seller.test.repository.PostRepository;
import proxy_seller.test.util.ValidationUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static proxy_seller.test.constant.ExceptionConstant.POST_NOT_FOUND;
import static proxy_seller.test.constant.QueryParamConstant.ID;
import static proxy_seller.test.constant.QueryParamConstant.LIKE_COUNT_ALIAS;
import static proxy_seller.test.constant.QueryParamConstant.POST_ID;
import static proxy_seller.test.constant.QueryParamConstant.POST_ID_ALIAS;
import static proxy_seller.test.constant.QueryParamConstant.POST_LIKE_COLLECTION;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;
    private final ModelMapper modelMapper;

    public PostResponse createPost(PostRequest request) {
        PostEntity post = postRepository.save(modelMapper.map(request, PostEntity.class));
        return modelMapper.map(post, PostResponse.class);
    }

    public Page<PostResponse> getPosts(Pageable pageable) {
        Page<PostEntity> posts = postRepository.findAll(pageable);

        List<String> postIds = posts.stream()
                .map(PostEntity::getId)
                .toList();

        Map<String, Long> likeCountsByPostIdMap = getLikeCounts(postIds);

        List<PostResponse> responses = posts.stream()
                .map(post -> {
                            long likeCount = likeCountsByPostIdMap.containsKey(post.getId())
                                    ? likeCountsByPostIdMap.get(post.getId()) : 0;
                            return modelMapper.map(post, PostResponse.class)
                                    .setLikeCount(likeCount);
                        }
                ).toList();

        return new PageImpl<>(responses, pageable, posts.getTotalElements());
    }


    public void updateFavorite(String userId, String postId) {
        ValidationUtil.validateOrNotFound(
                !postRepository.existsById(postId),
                String.format(POST_NOT_FOUND, postId));

        postLikeRepository.findByPostIdAndUserId(postId, userId)
                .ifPresentOrElse(
                        postLikeRepository::delete,
                        () -> postLikeRepository.save(new PostLikeEntity().setPostId(postId).setUserId(userId))
                );
    }

    private Map<String, Long> getLikeCounts(List<String> postIds) {
        Aggregation query = Aggregation.newAggregation(
                Aggregation.match(Criteria.where(POST_ID).in(postIds)),
                Aggregation.group(POST_ID).count().as(LIKE_COUNT_ALIAS),
                Aggregation.project().and(LIKE_COUNT_ALIAS).as(LIKE_COUNT_ALIAS).and(ID).as(POST_ID_ALIAS)
        );

        return mongoTemplate.aggregate(query, POST_LIKE_COLLECTION, LikeCountResult.class).getMappedResults().stream()
                .collect(Collectors.toMap(LikeCountResult::getPostId, LikeCountResult::getLikeCount));
    }
}
