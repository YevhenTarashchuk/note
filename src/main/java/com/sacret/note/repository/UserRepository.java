package com.sacret.note.repository;

import com.sacret.note.model.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserEntity, String> {

    boolean existsByLogin(String login);

    Optional<UserEntity> findByLogin(String login);
}
