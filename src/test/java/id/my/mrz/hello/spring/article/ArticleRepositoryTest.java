package id.my.mrz.hello.spring.article;

import static org.assertj.core.api.Assertions.assertThat;

import id.my.mrz.hello.spring.tag.Tag;
import java.util.Arrays;
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

  @Autowired IArticleRepository repository;

  @BeforeEach
  void setup() {
    repository.deleteAll();
  }

  @Test
  void fetchCreatedArticleContains() {
    Article article = new Article("title", "slug", "content", List.of(new Tag("tag")));
    create(article);

    Iterable<Article> articles = repository.findAll();
    assertThat(articles)
        .singleElement()
        .usingRecursiveAssertion()
        .ignoringFields("id", "tags.id")
        .isEqualTo(article);
  }

  @Test
  void getCreatedArticle() {
    Article article = new Article("title", "slug", "content", List.of(new Tag("tag")));
    Article created = create(article);

    Optional<Article> _article = repository.findById(created.getId());

    assertThat(_article.orElseThrow())
        .usingRecursiveAssertion()
        .ignoringFields("id", "tags.id")
        .isEqualTo(article);
  }

  Article create(Article article) {
    return repository.save(article);
  }

  Iterable<Article> create(Article... articles) {
    return repository.saveAll(Arrays.asList(articles));
  }
}
