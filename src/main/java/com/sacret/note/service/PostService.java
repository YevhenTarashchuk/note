package com.sacret.note.service;

import com.sacret.note.constant.ExceptionConstant;
import com.sacret.note.exception.NotFoundException;
import com.sacret.note.model.LikeCountResult;
import com.sacret.note.model.entity.PostEntity;
import com.sacret.note.model.entity.PostLikeEntity;
import com.sacret.note.model.request.PostRequest;
import com.sacret.note.model.response.PostResponse;
import com.sacret.note.repository.PostLikeRepository;
import com.sacret.note.repository.PostRepository;
import com.sacret.note.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sacret.note.constant.QueryParamConstant.ID;
import static com.sacret.note.constant.QueryParamConstant.LIKE_COUNT_ALIAS;
import static com.sacret.note.constant.QueryParamConstant.POST_ID;
import static com.sacret.note.constant.QueryParamConstant.POST_ID_ALIAS;
import static com.sacret.note.constant.QueryParamConstant.POST_LIKE_COLLECTION;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public PostResponse createPost(PostRequest request, String userId) {
        PostEntity post = postRepository.save(modelMapper.map(request, PostEntity.class)
                .setUserId(userId));
        return modelMapper.map(post, PostResponse.class);
    }

    public PostResponse getPost(String postId, String userId) {
        PostEntity post = postRepository.findByIdAndUserId(postId, userId)
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionConstant.POST_NOT_FOUND, postId)));

        Map<String, Long> likeCountMap = getLikeCounts(Collections.singletonList(postId));

        return modelMapper.map(post,PostResponse.class)
                .setLikeCount(likeCountMap.containsKey(postId) ? likeCountMap.get(postId) : 0);
    }

    public PostResponse updatePost(PostRequest request, String postId, String userId) {
        PostEntity post = postRepository.findByIdAndUserId(postId, userId)
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionConstant.POST_NOT_FOUND, postId)));

        post.setContent(request.content());
        postRepository.save(post);

        Map<String, Long> likeCountMap = getLikeCounts(Collections.singletonList(postId));

        return modelMapper.map(post,PostResponse.class)
                .setLikeCount(likeCountMap.containsKey(postId) ? likeCountMap.get(postId) : 0);
    }

    public void removePost(String postId, String userId) {
        postLikeRepository.deleteAllByPostId(postId);
        postRepository.deleteByIdAndUserId(postId, userId);
    }

    public void updateLike(String userId, String postId) {
        postLikeRepository.findByPostIdAndUserId(postId, userId)
                .ifPresentOrElse(
                        postLikeRepository::delete,
                        () -> {
                            validatePostExistence(postId);
                            userService.validateUserExistence(userId);
                            postLikeRepository.save(new PostLikeEntity().setPostId(postId).setUserId(userId));
                        }
                );
    }

    public Page<PostResponse> getPosts(String userId, Pageable pageable) {
        Page<PostEntity> posts = postRepository.findAllByUserId(userId, pageable);
        List<PostResponse> responses = getPosts(posts);

        return new PageImpl<>(responses, pageable, posts.getTotalElements());
    }

    public Page<PostResponse> getPosts(Pageable pageable) {
        Page<PostEntity> posts = postRepository.findAll(pageable);
        List<PostResponse> responses = getPosts(posts);

        return new PageImpl<>(responses, pageable, posts.getTotalElements());
    }

    private List<PostResponse> getPosts(Page<PostEntity> posts) {
        List<String> postIds = posts.stream()
                .map(PostEntity::getId)
                .toList();

        Map<String, Long> likeCountMap = getLikeCounts(postIds);

        return posts.stream()
                .map(post -> {
                            long likeCount = likeCountMap.containsKey(post.getId())
                                    ? likeCountMap.get(post.getId()) : 0;
                            return modelMapper.map(post, PostResponse.class)
                                    .setLikeCount(likeCount);
                        }
                ).toList();
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

    private void validatePostExistence(String postId) {
        ValidationUtil.validateOrNotFound(
                postRepository.existsById(postId),
                String.format(ExceptionConstant.POST_NOT_FOUND, postId));
    }
}
