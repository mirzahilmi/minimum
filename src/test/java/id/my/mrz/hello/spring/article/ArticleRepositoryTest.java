package id.my.mrz.hello.spring.article;

import static org.assertj.core.api.Assertions.assertThat;

import id.my.mrz.hello.spring.tag.Tag;
import java.util.List;
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

  @Autowired IArticleRepository articleRepository;

  @Test
  void shouldReturnIterableContainSingleArticleWhenCreated() {
    Article article = new Article("title", "slug", "content", List.of(new Tag("tag")));
    articleRepository.save(article);
    Iterable<Article> articles = articleRepository.findAll();

    assertThat(articles)
        .singleElement()
        .usingRecursiveAssertion()
        .ignoringFields("id", "tags.id")
        .isEqualTo(article);
  }
}
