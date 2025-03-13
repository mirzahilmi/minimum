package id.my.mrz.minimum.article;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import id.my.mrz.minimum.domain.article.dto.ArticleCreateRequest;
import id.my.mrz.minimum.domain.article.dto.ArticleDocumentSearchQuery;
import id.my.mrz.minimum.domain.article.dto.ArticleResourceResponse;
import id.my.mrz.minimum.domain.article.entity.ArticleDocument;
import id.my.mrz.minimum.domain.article.repository.ArticleElasticsearchRepository;
import id.my.mrz.minimum.domain.article.repository.IArticleRepository;
import id.my.mrz.minimum.domain.session.dto.SessionCreateRequest;
import id.my.mrz.minimum.domain.session.dto.SessionCreatedResponse;
import id.my.mrz.minimum.domain.tag.dto.TagCreateRequest;
import id.my.mrz.minimum.domain.tag.entity.TagDocument;
import id.my.mrz.minimum.domain.user.dto.UserSignupRequest;
import id.my.mrz.minimum.domain.user.repository.IUserRepository;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(
    replace = Replace.NONE)
final class ArticleIntegrationTest {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgresql =
        new PostgreSQLContainer<>("postgres:17.3-alpine3.21");

    @Container
    @ServiceConnection
    static final RedisContainer redis = new RedisContainer("redis:7.4.2-alpine3.21");

    @Container
    static final MinIOContainer minio =
        new MinIOContainer("minio/minio:RELEASE.2025-02-07T23-21-09Z");

    @SuppressWarnings("resource")
    @Container
    @ServiceConnection
    static final ElasticsearchContainer es = new ElasticsearchContainer("elasticsearch:8.16.2")
        .withEnv("xpack.security.enabled", "false");

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ArticleElasticsearchRepository articleIndexRepository;

    @DynamicPropertySource
    static void minioProperties(DynamicPropertyRegistry registry) {
        registry.add("minio.key.access", minio::getUserName);
        registry.add("minio.key.secret", minio::getPassword);
        registry.add("minio.endpoint", minio::getS3URL);
        registry.add("minio.bucket", () -> "bucket");
    }

    @BeforeEach
    void setup(
        @Autowired IArticleRepository articleRepository,
        @Autowired IUserRepository userRepository) {
        articleRepository.deleteAll();
        articleIndexRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterAll
    static void close() {
        redis.stop();
    }

    String authenticate()
        throws JsonMappingException, JsonProcessingException, UnsupportedEncodingException {
        MockMvcTester client = MockMvcTester.create(mockMvc);

        UserSignupRequest credential =
            new UserSignupRequest(
                "username", "admin123aksdhjfkajhsdfkahdjsf", "admin123aksdhjfkajhsdfkahdjsf");
        String payload = objectMapper.writeValueAsString(credential);

        client
            .post()
            .uri("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload)
            .exchange();

        SessionCreateRequest sessionRequest =
            new SessionCreateRequest(credential.getUsername(), credential.getPassword());
        payload = objectMapper.writeValueAsString(sessionRequest);

        MvcTestResult result =
            client
                .post()
                .uri("/api/v1/users/self/sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
                .exchange();

        SessionCreatedResponse response = objectMapper
            .readValue(result.getResponse().getContentAsString(), SessionCreatedResponse.class);

        return response.accessToken();
    }

    @Test
    void getGreeting() throws Exception {
        MockMvcTester client = MockMvcTester.create(mockMvc);
        MvcTestResult result = client.get().uri("/greeting").exchange();
        assertThat(result.getResponse().getContentAsString()).contains("Hello");
    }

    @Test
    void Get_articles_data_with_empty_datastore() throws Exception {
        MockMvcTester client = MockMvcTester.create(mockMvc);

        String accessToken = authenticate();
        assertThat(accessToken).isNotNull().isNotBlank();

        MvcTestResult result =
            client
                .get()
                .uri("/api/v1/articles")
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken))
                .exchange();

        List<ArticleResourceResponse> articles =
            objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<ArticleResourceResponse>>() {
                });

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(articles).isNotNull().hasSize(0).isEmpty();
    }

    @Test
    void Get_single_article_data_when_datastore_empty() throws Exception {
        MockMvcTester client = MockMvcTester.create(mockMvc);

        String accessToken = authenticate();
        assertThat(accessToken).isNotNull().isNotBlank();

        MvcTestResult result =
            client
                .get()
                .uri("/api/v1/articles/{id}", 1)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();
        assertThat(result)
            .hasStatus(HttpStatus.NOT_FOUND.value())
            .hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    void Create_new_article() throws Exception {
        MockMvcTester client = MockMvcTester.create(mockMvc);

        String accessToken = authenticate();
        assertThat(accessToken).isNotNull().isNotBlank();

        ArticleCreateRequest request =
            new ArticleCreateRequest("title", "slug", "content",
                List.of(new TagCreateRequest("tag")));
        String json = objectMapper.writeValueAsString(request);

        MvcTestResult result =
            client
                .post()
                .uri("/api/v1/articles")
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .exchange();

        assertThat(result)
            .hasStatus(HttpStatus.CREATED.value())
            .hasContentType(MediaType.APPLICATION_JSON)
            .redirectedUrl()
            .containsPattern("\\/articles\\/\\d");

        assertThat(result)
            .bodyJson()
            .hasPathSatisfying("$.title", value -> value.assertThat().isEqualTo(request.getTitle()))
            .hasPathSatisfying("$.slug", value -> value.assertThat().isEqualTo(request.getSlug()))
            .hasPathSatisfying("$.content",
                value -> value.assertThat().isEqualTo(request.getContent()))
            .hasPathSatisfying(
                "$.tags[*].name",
                value -> value
                    .assertThat()
                    .isEqualTo(request.getTags().stream().map(tag -> tag.name()).toList()));
    }

    @Test
    void Updating_existing_article() throws Exception {
        MockMvcTester client = MockMvcTester.create(mockMvc);
        String accessToken = authenticate();

        ArticleCreateRequest createRequest =
            new ArticleCreateRequest(
                "initial title",
                "initial-slug",
                "initial content",
                List.of(new TagCreateRequest("initial-tag")));
        String createJson = objectMapper.writeValueAsString(createRequest);

        MvcTestResult createResult =
            client
                .post()
                .uri("/api/v1/articles")
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson)
                .exchange();

        String location = createResult.getResponse().getHeader("Location");
        String articleId = location.substring(location.lastIndexOf('/') + 1);

        ArticleCreateRequest updateRequest =
            new ArticleCreateRequest(
                "updated title",
                "updated-slug",
                "updated content",
                List.of(new TagCreateRequest("updated-tag")));
        String updateJson = objectMapper.writeValueAsString(updateRequest);

        MvcTestResult updateResult =
            client
                .put()
                .uri("/api/v1/articles/{id}", articleId)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson)
                .exchange();

        assertThat(updateResult)
            .hasStatus(HttpStatus.OK.value())
            .hasContentType(MediaType.APPLICATION_JSON)
            .bodyJson()
            .hasPathSatisfying(
                "$.title", value -> value.assertThat().isEqualTo(updateRequest.getTitle()))
            .hasPathSatisfying("$.slug",
                value -> value.assertThat().isEqualTo(updateRequest.getSlug()))
            .hasPathSatisfying(
                "$.content", value -> value.assertThat().isEqualTo(updateRequest.getContent()))
            .hasPathSatisfying(
                "$.tags[*].name",
                value -> value
                    .assertThat()
                    .isEqualTo(updateRequest.getTags().stream().map(tag -> tag.name()).toList()));

        MvcTestResult getResult =
            client
                .get()
                .uri("/api/v1/articles/{id}", articleId)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken))
                .exchange();

        assertThat(getResult)
            .hasStatus(HttpStatus.OK.value())
            .hasContentType(MediaType.APPLICATION_JSON)
            .bodyJson()
            .hasPathSatisfying(
                "$.title", value -> value.assertThat().isEqualTo(updateRequest.getTitle()));
    }

    @Test
    void Update_non_existent_article() throws Exception {
        MockMvcTester client = MockMvcTester.create(mockMvc);
        String accessToken = authenticate();

        ArticleCreateRequest updateRequest =
            new ArticleCreateRequest("title", "slug", "content",
                List.of(new TagCreateRequest("tag")));
        String json = objectMapper.writeValueAsString(updateRequest);

        MvcTestResult result =
            client
                .put()
                .uri("/api/v1/articles/{id}", 999999)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .exchange();

        assertThat(result)
            .hasStatus(HttpStatus.NOT_FOUND.value())
            .hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    void Delete_existing_article() throws Exception {
        MockMvcTester client = MockMvcTester.create(mockMvc);
        String accessToken = authenticate();

        ArticleCreateRequest createRequest =
            new ArticleCreateRequest("title", "slug", "content",
                List.of(new TagCreateRequest("tag")));
        String json = objectMapper.writeValueAsString(createRequest);

        MvcTestResult createResult =
            client
                .post()
                .uri("/api/v1/articles")
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .exchange();

        String location = createResult.getResponse().getHeader("Location");
        String articleId = location.substring(location.lastIndexOf('/') + 1);

        MvcTestResult deleteResult =
            client
                .delete()
                .uri("/api/v1/articles/{id}", articleId)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken))
                .exchange();

        assertThat(deleteResult).hasStatus(HttpStatus.NO_CONTENT.value());

        MvcTestResult getResult =
            client
                .get()
                .uri("/api/v1/articles/{id}", articleId)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken))
                .exchange();

        assertThat(getResult)
            .hasStatus(HttpStatus.NOT_FOUND.value())
            .hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    void Search_articles_by_keyword() throws Exception {
        MockMvcTester client = MockMvcTester.create(mockMvc);

        String accessToken = authenticate();
        assertThat(accessToken).isNotNull().isNotBlank();

        ArticleDocument article1 =
            new ArticleDocument(1L, "title", "slug", "content",
                List.of(new TagDocument(1L, "name")));
        ArticleDocument article2 =
            new ArticleDocument(
                2L,
                "Cah Kangkung",
                "cah-kangkung",
                "Cah Kangkung straight up fire",
                List.of(new TagDocument(2L, "makanan")));
        articleIndexRepository.saveAll(List.of(article1, article2));

        MvcTestResult result =
            client
                .get()
                .uri("/api/v1/articles")
                .param(ArticleDocumentSearchQuery.QUERY_STRING_KEY, "kangkung")
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        assertThat(result)
            .hasStatusOk()
            .hasContentType(MediaType.APPLICATION_JSON)
            .bodyJson()
            .extractingPath("$")
            .asInstanceOf(InstanceOfAssertFactories.list(ArticleResourceResponse.class))
            .hasSize(1)
            .singleElement()
            .extracting("title", "slug", "content")
            .containsExactly(article2.getTitle(), article2.getSlug(), article2.getContent());
    }
}
