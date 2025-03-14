package id.my.mrz.minimum.article;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
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

import id.my.mrz.minimum.domain.article.entity.Article;
import id.my.mrz.minimum.domain.article.repository.IArticleRepository;
import id.my.mrz.minimum.domain.tag.entity.Tag;
import id.my.mrz.minimum.domain.user.entity.User;
import id.my.mrz.minimum.domain.user.repository.IUserRepository;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(
    replace = Replace.NONE)
class ArticleRepositoryTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresql =
        new PostgreSQLContainer<>("postgres:17.3-alpine3.21");

    @Autowired
    private IArticleRepository articleRepository;

    @Autowired
    private IUserRepository userRepository;

    @BeforeEach
    void cleanup() {
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void Save_article_and_find_all_returns_saved_article() {
        User user = userRepository.save(new User("testuser", "password"));
        Article article = new Article("title", "slug", "content", user, List.of(new Tag("tag")));

        articleRepository.save(article);
        Iterable<Article> articles = articleRepository.findAll();

        assertThat(articles)
            .singleElement()
            .usingRecursiveAssertion()
            .ignoringFields("id", "tags.id", "user.id")
            .isEqualTo(article);
    }

    @Test
    void Save_article_and_find_by_id_returns_saved_article() {
        User user = userRepository.save(new User("testuser", "password"));
        Article article = new Article("title", "slug", "content", user, List.of(new Tag("tag")));

        Article saved = articleRepository.save(article);
        Article found = articleRepository.findById(saved.getId()).orElseThrow();

        assertThat(found)
            .usingRecursiveAssertion()
            .ignoringFields("id", "tags.id", "user.id")
            .isEqualTo(article);
    }

    @Test
    void Update_article_with_new_tags_persists_changes() {
        User user = userRepository.save(new User("testuser", "password"));
        Article article =
            new Article("title", "slug", "content", user, new ArrayList<>(List.of(new Tag("tag"))));
        Article saved = articleRepository.save(article);

        Article toUpdate = articleRepository.findById(saved.getId()).orElseThrow();
        toUpdate.setTitle("title2");
        toUpdate.setSlug("slug2");
        toUpdate.setContent("content2");
        toUpdate.getTags().add(new Tag("tag2"));
        Article updated = articleRepository.save(toUpdate);

        assertThat(updated)
            .usingRecursiveAssertion()
            .ignoringFields("id", "tags.id", "user.id")
            .isEqualTo(toUpdate);
    }

    @Test
    void Delete_article_removes_it_from_database() {
        User user = userRepository.save(new User("testuser", "password"));
        Article article = new Article("title", "slug", "content", user, List.of(new Tag("tag")));
        Article saved = articleRepository.save(article);

        articleRepository.deleteById(saved.getId());
        Optional<Article> found = articleRepository.findById(saved.getId());

        assertThat(found).isEmpty();
    }
}
