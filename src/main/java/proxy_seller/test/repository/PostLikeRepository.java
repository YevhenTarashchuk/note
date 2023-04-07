package proxy_seller.test.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import proxy_seller.test.model.entity.PostLikeEntity;

import java.util.Optional;

public interface PostLikeRepository extends MongoRepository<PostLikeEntity, String> {

    Optional<PostLikeEntity> findByPostIdAndUserId(String postId, String userId);
}
