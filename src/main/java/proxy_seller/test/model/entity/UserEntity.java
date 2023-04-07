package proxy_seller.test.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "user")
public class UserEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String login;
    private String password;
}
