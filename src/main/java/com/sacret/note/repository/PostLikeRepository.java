package com.sacret.note.repository;

import com.sacret.note.model.entity.PostLikeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PostLikeRepository extends MongoRepository<PostLikeEntity, String> {

    Optional<PostLikeEntity> findByPostIdAndUserId(String postId, String userId);

    void deleteAllByPostId(String postId);
}
