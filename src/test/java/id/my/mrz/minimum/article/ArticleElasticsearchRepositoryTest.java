package id.my.mrz.minimum.article;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import id.my.mrz.minimum.domain.article.entity.ArticleDocument;
import id.my.mrz.minimum.domain.article.repository.ArticleElasticsearchRepository;
import id.my.mrz.minimum.domain.tag.entity.TagDocument;
import java.util.List;
import java.util.Optional;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataElasticsearchTest
@Testcontainers
class ArticleElasticsearchRepositoryTest {
  @SuppressWarnings("resource")
  @Container
  @ServiceConnection
  static ElasticsearchContainer es =
      new ElasticsearchContainer("elasticsearch:8.16.2").withEnv("xpack.security.enabled", "false");

  @Autowired private ArticleElasticsearchRepository repository;

  @BeforeEach
  void setup() {
    repository.deleteAll();
  }

  @After
  void close() {
    es.close();
  }

  @Test
  void Index_single_article_data_should_not_throw_exception() {
    ArticleDocument article =
        new ArticleDocument(1L, "title", "slug", "content", List.of(new TagDocument(1L, "tag")));

    assertThatNoException()
        .isThrownBy(
            () -> {
              ArticleDocument saved = repository.save(article);
              assertThat(saved).isNotNull().isEqualTo(article);
            });
  }

  @Test
  void Index_single_article_data_should_exist_in_the_store() {
    ArticleDocument article =
        new ArticleDocument(1L, "title", "slug", "content", List.of(new TagDocument(1L, "tag")));

    ArticleDocument saved = repository.save(article);
    assertThat(saved).isNotNull().isEqualTo(article);

    Optional<ArticleDocument> stored = repository.findById(article.getId());
    assertThat(stored)
        .isPresent()
        .containsInstanceOf(ArticleDocument.class)
        .hasValueSatisfying(
            val -> {
              assertThat(val).usingRecursiveComparison().isEqualTo(article);
            });
  }

  @Test
  void Update_indexed_article_data_should_be_updated_but_not_create_new_document() {
    ArticleDocument article =
        new ArticleDocument(1L, "title", "slug", "content", List.of(new TagDocument(1L, "tag")));

    ArticleDocument saved = repository.save(article);
    assertThat(saved).isNotNull().isEqualTo(article);

    ArticleDocument stored = repository.findById(article.getId()).orElseThrow();
    assertThat(stored).usingRecursiveComparison().isEqualTo(article);

    stored.setContent("new content!");
    repository.save(stored);

    Optional<ArticleDocument> updated = repository.findById(article.getId());
    assertThat(updated)
        .isPresent()
        .containsInstanceOf(ArticleDocument.class)
        .hasValueSatisfying(
            val -> {
              assertThat(val).usingRecursiveComparison().isEqualTo(stored);
            });

    Iterable<ArticleDocument> articles = repository.findAll();
    assertThat(articles).hasSize(1).singleElement().usingRecursiveComparison().isEqualTo(stored);
  }

  @Test
  void Delete_indexed_data_should_be_successful_and_actually_delete_the_data() {
    ArticleDocument article =
        new ArticleDocument(1L, "title", "slug", "content", List.of(new TagDocument(1L, "tag")));

    ArticleDocument saved = repository.save(article);
    assertThat(saved).isNotNull().isEqualTo(article);

    repository.deleteById(article.getId());

    Optional<ArticleDocument> stored = repository.findById(article.getId());
    assertThat(stored).isEmpty();

    Iterable<ArticleDocument> articles = repository.findAll();
    assertThat(articles).hasSize(0).isEmpty();
  }
}
