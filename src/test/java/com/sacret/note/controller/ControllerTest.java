package com.sacret.note.controller;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sacret.note.AbstractBaseTest;
import com.sacret.note.JsonAssertUtil;
import com.sacret.note.model.entity.UserEntity;
import com.sacret.note.model.enumeration.Role;
import com.sacret.note.model.enumeration.TokenType;
import com.sacret.note.model.request.PostRequest;
import com.sacret.note.repository.PostLikeRepository;
import com.sacret.note.repository.PostRepository;
import com.sacret.note.repository.UserRepository;
import com.sacret.note.security.util.JwtUtilService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.bson.Document;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.sacret.note.constant.AuthConstants.HEADER_PARAM_JWT_TOKEN;
import static com.sacret.note.constant.AuthConstants.ROLE_ATTRIBUTE;
import static com.sacret.note.constant.AuthConstants.TOKEN_TYPE_CLAIM;
import static com.sacret.note.constant.ExceptionConstant.USER_EXISTS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ControllerTest extends AbstractBaseTest {

    private final static String REFRESH_TOKEN_URL = "/v1/users/auth/refresh-tokens";
    private final static String REGISTRATION_URL = "/v1/users/registrations";
    private final static String COMMON_POST_URL_URL = "/v1/posts";
    private final static String AUTH_URL = "/v1/users/auth";
    private final static String SORT_PARAM = "sort";
    private final static String USER_LOGIN = "test";
    private final static String INVALID_LOGIN = "Slayer";
    private final static String DATABASE_NAME = "note_db";
    private final static String USER_COLLECTION = "user";
    private final static String POST_COLLECTION = "post";
    private final static String POST_LIKE_COLLECTION = "post_like";
    protected final static String USER_ID = "6431303b4f3f471b0652a9eb";

    @Autowired
    private PostLikeRepository postLikeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    @SpyBean
    private JwtUtilService jwtUtilService;

    @BeforeEach
    public void beforeEach() {
        postLikeRepository.deleteAll();
        userRepository.deleteAll();
        postRepository.deleteAll();

        MongoClient mongoClient = MongoClients.create(
                MongoClientSettings.builder()
                        .applyToClusterSettings(builder ->
                                builder.hosts(List.of(new ServerAddress(container.getHost(), container.getFirstMappedPort()))))
                        .build()
        );

        MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
        MongoCollection<Document> userCollection = database.getCollection(USER_COLLECTION);
        MongoCollection<Document> postCollection = database.getCollection(POST_COLLECTION);
        MongoCollection<Document> postLikeCollection = database.getCollection(POST_LIKE_COLLECTION);

        List<Document> users = List.of(
                new Document("login", "JohnDoe")
                        .append("_id", "6431303b4f3f471b0652a9eb")
                        .append("password", "$2a$10$Qid3F5X6fubJw3v8jNi6S.P.jTUXRt2dREyWjF0QvLCgcnnMtVd76")
                        .append("role", "ROLE_USER"),
                new Document("login", "Slayer")
                        .append("_id", "643133aa52a5bd57de8414e9")
                        .append("password", "password")
                        .append("role", "ROLE_USER")
        );
        userCollection.insertMany(users);

        List<Document> posts = List.of(
                new Document("_id", "643130e04f3f471b0652a9ed")
                        .append("content", "version 1")
                        .append("created_at", LocalDateTime.parse("2023-04-08T09:53:11"))
                        .append("updated_at", LocalDateTime.parse("2023-04-08T09:53:11"))
                        .append("user_id", "643133aa52a5bd57de8414e9"),
                new Document("_id", "643139861a2cfc7f3b3d6c2f")
                        .append("content", "version 2")
                        .append("created_at", LocalDateTime.parse("2023-04-08T10:53:11"))
                        .append("updated_at", LocalDateTime.parse("2023-04-08T10:53:11"))
                        .append("user_id", "6431303b4f3f471b0652a9eb"),
                new Document("_id", "643139871a2cfc7f3b3d6c30")
                        .append("content", "version 3")
                        .append("created_at", LocalDateTime.parse("2023-04-08T09:10:11"))
                        .append("updated_at", LocalDateTime.parse("2023-04-08T09:10:11"))
                        .append("user_id", "643133aa52a5bd57de8414e9"),
                new Document("_id", "643139871a2cfc7f3b3d6c31")
                        .append("content", "version 4")
                        .append("created_at", LocalDateTime.parse("2023-04-08T11:10:11"))
                        .append("updated_at", LocalDateTime.parse("2023-04-08T11:10:11")),
                new Document("_id", "64313a591a2cfc7f3b3d6c37")
                        .append("content", "version 5")
                        .append("created_at", LocalDateTime.parse("2023-04-08T12:10:11"))
                        .append("updated_at", LocalDateTime.parse("2023-04-08T12:10:11"))
                        .append("user_id", "643133aa52a5bd57de8414e9")
        );
        postCollection.insertMany(posts);

        List<Document> postLikes = List.of(
                new Document("post_id", "643130e04f3f471b0652a9ed")
                        .append("user_id", "643133aa52a5bd57de8414e9"),
                new Document("post_id", "643130e04f3f471b0652a9ed")
                        .append("user_id", "6431303b4f3f471b0652a9eb"),
                new Document("post_id", "643139861a2cfc7f3b3d6c2f")
                        .append("user_id", "643133aa52a5bd57de8414e9"),
                new Document("post_id", "643139871a2cfc7f3b3d6c31")
                        .append("user_id", "643133aa52a5bd57de8414e9"),
                new Document("post_id", "643139871a2cfc7f3b3d6c31")
                        .append("user_id", "6431303b4f3f471b0652a9eb")
        );
        postLikeCollection.insertMany(postLikes);

        mongoClient.close();
    }

    @Test
    void shouldRegisterUser() throws Exception {
        String requestAsString = JsonAssertUtil.readJsonFromClassPath("data/json/auth/registrationValidRequest.json");

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTRATION_URL)
                        .content(requestAsString)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.accessToken", Matchers.notNullValue()))
                .andExpect(jsonPath("$.refreshToken", Matchers.notNullValue()));

        Optional<UserEntity> user = userRepository.findByLogin(USER_LOGIN);

        Assertions.assertTrue(user.isPresent());

        JsonAssertUtil.assertJsons(
                JsonAssertUtil.readJsonFromClassPath("data/json/auth/userEntity.json"),
                objectMapper.writeValueAsString(user.get()),
                "id", "password"
        );
    }

    @Test
    void shouldNotRegisterUserThenBadRequest() throws Exception {
        String requestAsString = JsonAssertUtil.readJsonFromClassPath("data/json/auth/registrationInvalidRequest.json");

        mockMvc.perform(MockMvcRequestBuilders.post(REGISTRATION_URL)
                        .content(requestAsString)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", Matchers.containsString(String.format(USER_EXISTS, INVALID_LOGIN))));
    }

    @ParameterizedTest
    @MethodSource("getAuthValidParams")
    void shouldAuthThenRefreshToken(String url, String requestPath) throws Exception {
        doReturn(getRefreshTokenClaims()).when(jwtUtilService).parseToken(anyString());

        String requestAsString = JsonAssertUtil.readJsonFromClassPath(requestPath);

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content(requestAsString)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$.accessToken", Matchers.notNullValue()))
                .andExpect(jsonPath("$.refreshToken", Matchers.notNullValue()));
    }

    @ParameterizedTest
    @MethodSource("getAuthInvalidParams")
    void shouldNotAuthThenNotRefreshToken(String url, String requestPath) throws Exception {
        String requestAsString = JsonAssertUtil.readJsonFromClassPath(requestPath);

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content(requestAsString)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(401));
    }

    @ParameterizedTest
    @MethodSource("getPostParams")
    void shouldCreatePost(
            String content,
            MockHttpServletRequestBuilder requestBuilder,
            String responsePath
    ) throws Exception {
        doReturn(getAccessTokenClaims()).when(jwtUtilService).parseToken(anyString());

        ResultActions result = mockMvc.perform(requestBuilder
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PostRequest(content))))
                .andExpect(status().isOk());

        JsonAssertUtil.assertResultActionsAndJsonFile(
                result,
                responsePath,
                "id", "createdAt", "updatedAt"
        );
    }

    @Test
    void shouldGetPosts() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get(COMMON_POST_URL_URL)
                        .param(SORT_PARAM, "created_at,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonAssertUtil.assertResultActionsAndJsonPageable(result, "data/json/post/postsResponse.json");
    }

    protected static Stream<Arguments> getPostParams() {
        return Stream.of(

                Arguments.of(
                        "auth post",
                        MockMvcRequestBuilders.post(COMMON_POST_URL_URL)
                                .header(HEADER_PARAM_JWT_TOKEN, "Bearer eyJhbGciOiJIfewfNiJ9.eyJqdGkiOiI2NDMxMzAzY" +
                                        "jRmM2Y0NzFiMDY1MmE5ZWIiLCJ0b2tlblR5cGUiOiJBQ0NFU1NfVE9LRU4iLCJyb2xlIjoiUk9MRV9VU" +
                                        "0VSIiwiaWF0IjoxNjgwOTYyOTkxLCJleHAiOjE2ODEwNDkzOTF9.chLaijysgIs96TeyEDHckD" +
                                        "gresrgsrt4t43353543"),
                        "data/json/post/authPostResponse.json"
                ),
                Arguments.of(
                        "not auth post",
                        MockMvcRequestBuilders.post(COMMON_POST_URL_URL),
                        "data/json/post/notAuthPostResponse.json"
                )
        );
    }

    protected static Stream<Arguments> getAuthValidParams() {
        return Stream.of(
                Arguments.of(
                        AUTH_URL,
                        "data/json/auth/authValidRequest.json"
                ),
                Arguments.of(
                        REFRESH_TOKEN_URL,
                        "data/json/auth/refreshTokenValidRequest.json"
                )
        );
    }

    protected static Stream<Arguments> getAuthInvalidParams() {
        return Stream.of(
                Arguments.of(
                        AUTH_URL,
                        "data/json/auth/authInvalidRequest.json"
                ),
                Arguments.of(
                        REFRESH_TOKEN_URL,
                        "data/json/auth/refreshTokenInvalidRequest.json"
                )
        );
    }

    protected Claims getAccessTokenClaims() {
        Claims claims = Jwts.claims()
                .setId(USER_ID);
        claims.put(TOKEN_TYPE_CLAIM, TokenType.ACCESS_TOKEN.name());
        claims.put(ROLE_ATTRIBUTE, Role.ROLE_USER.name());
        return claims;
    }

    protected Claims getRefreshTokenClaims() {
        Claims claims = Jwts.claims()
                .setId(USER_ID);
        claims.put(TOKEN_TYPE_CLAIM, TokenType.REFRESH_TOKEN.name());
        claims.put(ROLE_ATTRIBUTE, Role.ROLE_USER.name());
        return claims;
    }
}
