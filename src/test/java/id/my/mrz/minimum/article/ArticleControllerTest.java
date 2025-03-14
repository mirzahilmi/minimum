package id.my.mrz.minimum.article;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import id.my.mrz.minimum.domain.article.controller.ArticleController;
import id.my.mrz.minimum.domain.article.dto.ArticleCreateRequest;
import id.my.mrz.minimum.domain.article.dto.ArticleResourceResponse;
import id.my.mrz.minimum.domain.article.service.IArticleService;
import id.my.mrz.minimum.domain.tag.dto.TagCreateRequest;
import id.my.mrz.minimum.domain.tag.dto.TagResourceResponse;
import id.my.mrz.minimum.domain.user.entity.Principal;

@ExtendWith(MockitoExtension.class)
final class ArticleControllerTest {
    @Mock
    IArticleService service;
    @Mock
    Principal principal;
    @InjectMocks
    ArticleController controller;

    @Test
    void Get_articles_when_empty() {
        when(service.fetchArticles()).thenReturn(List.of());
        ResponseEntity<List<ArticleResourceResponse>> response = controller.getArticles(null);

        assertThat(response.getStatusCode())
            .as("check http status code is 200")
            .isEqualTo(HttpStatus.OK);

        assertThat(response.getBody())
            .as("check body content of list is not null")
            .isNotNull()
            .as("check list size is 0")
            .hasSize(0)
            .isEmpty();
    }

    @Test
    void Get_articles_when_service_return_something() {
        List<TagResourceResponse> tags = List.of(new TagResourceResponse(1L, "makanan"));
        List<ArticleResourceResponse> articles =
            List.of(
                new ArticleResourceResponse(
                    1L,
                    "Cah Kangkung",
                    "cah-kangkung",
                    "Cah Kangkung straight up fire",
                    "kangkung.png",
                    tags),
                new ArticleResourceResponse(2L, "Mie Ayam", "mie-ayam", "Mie Ayam", "mie.png",
                    tags));

        when(service.fetchArticles()).thenReturn(articles);
        ResponseEntity<List<ArticleResourceResponse>> response = controller.getArticles(null);

        assertThat(response.getStatusCode())
            .as("check http status code is 200")
            .isEqualTo(HttpStatus.OK);

        assertThat(response.getBody())
            .as("check body content of list is not null")
            .isNotNull()
            .as("check list size is 2")
            .hasSize(2)
            .containsExactly(articles.toArray(ArticleResourceResponse[]::new));
    }

    @Test
    void Create_article_returns_created_status() throws Exception {
        ArticleCreateRequest request =
            new ArticleCreateRequest(
                "Test Article", "test-article", "Test Content",
                List.of(new TagCreateRequest("test")));
        ArticleResourceResponse createdArticle =
            new ArticleResourceResponse(
                1L,
                "Test Article",
                "test-article",
                "Test Content",
                null,
                List.of(new TagResourceResponse(1L, "test")));

        when(principal.getId()).thenReturn(1L);
        when(service.createArticle(1L, request)).thenReturn(createdArticle);

        ResponseEntity<ArticleResourceResponse> response =
            controller.postArticle(principal, request);

        assertThat(response.getStatusCode())
            .as("check http status code is 201")
            .isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody())
            .as("check response body matches created article")
            .isEqualTo(createdArticle);
    }

    @Test
    void Update_article_returns_ok_status() throws Exception {
        ArticleCreateRequest request =
            new ArticleCreateRequest(
                "Updated Article",
                "updated-article",
                "Updated Content",
                List.of(new TagCreateRequest("test")));
        ArticleResourceResponse updatedArticle =
            new ArticleResourceResponse(
                1L,
                "Updated Article",
                "updated-article",
                "Updated Content",
                null,
                List.of(new TagResourceResponse(1L, "test")));

        when(principal.getId()).thenReturn(1L);
        when(service.updateArticle(1L, 1L, request)).thenReturn(updatedArticle);

        ResponseEntity<ArticleResourceResponse> response =
            controller.putArticle(1L, principal, request);

        assertThat(response.getStatusCode())
            .as("check http status code is 200")
            .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
            .as("check response body matches updated article")
            .isEqualTo(updatedArticle);
    }

    @Test
    void Delete_article_returns_no_content_status() throws Exception {
        when(principal.getId()).thenReturn(1L);

        ResponseEntity<Void> response = controller.deleteArticle(1L, principal);

        assertThat(response.getStatusCode())
            .as("check http status code is 204")
            .isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).as("check body is null").isNull();
    }
}
