package proxy_seller.test.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import proxy_seller.test.model.entity.UserEntity;

public interface UserRepository extends MongoRepository<UserEntity, String> {

    boolean existsByLogin(String login);
}
