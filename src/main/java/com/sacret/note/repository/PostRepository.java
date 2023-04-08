package com.sacret.note.repository;

import com.sacret.note.model.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PostRepository extends MongoRepository<PostEntity, String> {

    Optional<PostEntity> findByIdAndUserId(String postId, String userId);

    void deleteByIdAndUserId(String postId, String userId);

    Page<PostEntity> findAllByUserId(String userId, Pageable pageable);
}
