package proxy_seller.test.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import proxy_seller.test.model.entity.PostEntity;

public interface PostRepository extends MongoRepository<PostEntity, String> {
}
