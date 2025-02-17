package id.my.mrz.hello.spring.article;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import id.my.mrz.hello.spring.tag.Tag;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ArticleControllerTest {

  @Mock private IArticleService articleService;

  @InjectMocks private ArticleController articleController;

  private ArticleResourceResponse sampleArticle;
  private ArticleCreateRequest createRequest;
  private List<ArticleResourceResponse> articlesList;

  @BeforeEach
  void setUp() {
    sampleArticle =
        new ArticleResourceResponse(
            1L,
            "Test Article",
            "test-article",
            "Test Content",
            "thumbnail.jpg",
            List.of(new Tag("test")));

    createRequest =
        new ArticleCreateRequest(
            "Test Article", "test-article", "Test Content", List.of(new Tag("test")));

    articlesList = List.of(sampleArticle);
  }

  @Test
  void getArticles_ShouldReturnListOfArticles() {
    when(articleService.fetchArticles()).thenReturn(articlesList);

    ResponseEntity<List<ArticleResourceResponse>> response = articleController.getArticles();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull().hasSize(1).containsExactly(sampleArticle);
    verify(articleService).fetchArticles();
  }

  @Test
  void getArticle_ShouldReturnSingleArticle() {
    when(articleService.getArticle(1L)).thenReturn(sampleArticle);

    ResponseEntity<ArticleResourceResponse> response = articleController.getArticle(1L);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody())
        .isNotNull()
        .satisfies(
            article -> {
              assertThat(article.id()).isEqualTo(1L);
              assertThat(article.title()).isEqualTo("Test Article");
              assertThat(article.slug()).isEqualTo("test-article");
            });
    verify(articleService).getArticle(1L);
  }

  @Test
  void postArticle_ShouldCreateAndReturnArticle() {
    when(articleService.createArticle(any(ArticleCreateRequest.class))).thenReturn(sampleArticle);

    ResponseEntity<ArticleResourceResponse> response = articleController.postArticle(createRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getHeaders().getLocation()).isNotNull().hasToString("/articles/1");
    assertThat(response.getBody()).isEqualTo(sampleArticle);
    verify(articleService).createArticle(createRequest);
  }

  @Test
  void putArticle_ShouldUpdateAndReturnArticle() {
    when(articleService.updateArticle(anyLong(), any(ArticleCreateRequest.class)))
        .thenReturn(sampleArticle);

    ResponseEntity<ArticleResourceResponse> response =
        articleController.putArticle(1L, createRequest);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(sampleArticle);
    verify(articleService).updateArticle(1L, createRequest);
  }

  @Test
  void patchThumbnail_ShouldUpdateThumbnailAndReturnArticle() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile(
            "thumbnail", "test.jpg", "image/jpeg", "test image content".getBytes());
    when(articleService.uploadThumbnail(anyLong(), any(MultipartFile.class)))
        .thenReturn(sampleArticle);

    ResponseEntity<ArticleResourceResponse> response = articleController.patchThumbnail(1L, file);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(sampleArticle);
    verify(articleService).uploadThumbnail(1L, file);
  }

  @Test
  void deleteArticle_ShouldDeleteAndReturnNoContent() {
    doNothing().when(articleService).delete(1L);

    ResponseEntity<Void> response = articleController.deleteArticle(1L);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response.getBody()).isNull();
    verify(articleService).delete(1L);
  }
}
