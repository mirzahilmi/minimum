package id.my.mrz.hello.spring.article;

import static org.assertj.core.api.Assertions.assertThat;

import id.my.mrz.hello.spring.article.entity.Article;
import id.my.mrz.hello.spring.article.repository.IArticleRepository;
import id.my.mrz.hello.spring.tag.Tag;
import id.my.mrz.hello.spring.user.IUserRepository;
import id.my.mrz.hello.spring.user.User;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ArticleRepositoryTest {
  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>("postgres:17.3-alpine3.21");

  @Autowired private IArticleRepository repository;

  @Autowired private IUserRepository userRepository;

  private User testUser;

  @BeforeEach
  void setup() {
    repository.deleteAll();
    userRepository.deleteAll();

    testUser = new User("testuser", "password");
    testUser = userRepository.save(testUser);
  }

  @Test
  void fetchCreatedArticleContains() {
    Article article = new Article("title", "slug", "content", testUser, List.of(new Tag("tag")));
    create(article);

    Iterable<Article> articles = repository.findAll();

    assertThat(articles)
        .singleElement()
        .usingRecursiveAssertion()
        .ignoringFields("id", "tags.id", "user.id")
        .isEqualTo(article);
  }

  @Test
  void createAndGetArticle() {
    Article article = new Article("title", "slug", "content", testUser, List.of(new Tag("tag")));
    Article created = create(article);

    Optional<Article> _article = repository.findById(created.getId());

    assertThat(_article.orElseThrow())
        .usingRecursiveAssertion()
        .ignoringFields("id", "tags.id", "user.id")
        .isEqualTo(article);
  }

  @Test
  void updateAndAddNewTagsToArticle() {
    Article article =
        new Article(
            "title", "slug", "content", testUser, new LinkedList<>(List.of(new Tag("tag"))));
    Article created = create(article);

    article = repository.findById(created.getId()).orElseThrow();
    article.setTitle("title2");
    article.setSlug("slug2");
    article.setContent("content2");
    List<Tag> tags = article.getTags();
    tags.add(new Tag("tag2"));
    article.setTags(tags);

    repository.save(article);

    Article updated = repository.findById(created.getId()).orElseThrow();
    assertThat(updated)
        .usingRecursiveAssertion()
        .ignoringFields("id", "tags.id", "user.id")
        .isEqualTo(article);
  }

  @Test
  void deleteArticle() {
    Article article = new Article("title", "slug", "content", testUser, List.of(new Tag("tag")));
    article = create(article);

    repository.deleteById(article.getId());

    Optional<Article> _article = repository.findById(article.getId());
    assertThat(_article).isEmpty();
  }

  Article create(Article article) {
    return repository.save(article);
  }

  Iterable<Article> create(Article... articles) {
    return repository.saveAll(Arrays.asList(articles));
  }
}
