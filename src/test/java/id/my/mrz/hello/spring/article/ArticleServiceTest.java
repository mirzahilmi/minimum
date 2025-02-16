package id.my.mrz.hello.spring.article;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import id.my.mrz.hello.spring.exception.ResourceViolationException;
import id.my.mrz.hello.spring.minio.IStorageRepository;
import id.my.mrz.hello.spring.tag.Tag;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

  @Mock private IArticleRepository repository;

  @Mock private IStorageRepository storageRepository;

  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks private ArticleService service;

  @Test
  void fetchArticles_ShouldReturnAllArticles() {
    Article article1 = new Article("Title 1", "slug-1", "Content 1", List.of(new Tag("tag1")));
    Article article2 = new Article("Title 2", "slug-2", "Content 2", List.of(new Tag("tag2")));
    article1.setId(1);
    article2.setId(2);
    when(repository.findAll()).thenReturn(List.of(article1, article2));

    List<ArticleResourceResponse> result = service.fetchArticles();

    assertThat(result)
        .hasSize(2)
        .extracting(ArticleResourceResponse::title)
        .containsExactly("Title 1", "Title 2");

    verify(repository).findAll();
  }

  @Test
  void getArticle_WhenExists_ShouldReturnArticle() {
    long id = 1L;
    Article article =
        new Article("Test Title", "test-slug", "Test Content", List.of(new Tag("test")));
    when(repository.findById(id)).thenReturn(Optional.of(article));
    article.setId(1);
    ArticleResourceResponse result = service.getArticle(id);

    assertThat(result)
        .isNotNull()
        .satisfies(
            response -> {
              assertThat(response.title()).isEqualTo("Test Title");
              assertThat(response.slug()).isEqualTo("test-slug");
              assertThat(response.content()).isEqualTo("Test Content");
            });

    verify(repository).findById(id);
  }

  @Test
  void getArticle_WhenNotExists_ShouldThrowException() {
    long id = 1L;
    when(repository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getArticle(id))
        .isInstanceOf(ResourceViolationException.class)
        .hasMessageContaining("not found");

    verify(repository).findById(id);
  }

  @Test
  void createArticle_WithValidData_ShouldCreateArticle() {
    ArticleCreateRequest request =
        new ArticleCreateRequest(
            "Test Title", "test-slug", "Test Content", List.of(new Tag("test")));
    Article article =
        new Article(request.title(), request.slug(), request.content(), request.tags());
    article.setId(1);
    when(repository.save(any(Article.class))).thenReturn(article);

    ArticleResourceResponse result = service.createArticle(request);

    assertThat(result)
        .isNotNull()
        .satisfies(
            response -> {
              assertThat(response.title()).isEqualTo(request.title());
              assertThat(response.slug()).isEqualTo(request.slug());
              assertThat(response.content()).isEqualTo(request.content());
              assertThat(response.tags()).containsExactlyElementsOf(request.tags());
            });

    verify(repository).save(any(Article.class));
  }

  @Test
  void createArticle_WithDuplicateSlug_ShouldThrowException() {
    ArticleCreateRequest request =
        new ArticleCreateRequest(
            "Test Title", "test-slug", "Test Content", List.of(new Tag("test")));
    when(repository.save(any(Article.class)))
        .thenThrow(new DataIntegrityViolationException("Duplicate slug"));

    assertThatThrownBy(() -> service.createArticle(request))
        .isInstanceOf(ResourceViolationException.class)
        .hasMessageContaining("already exist");

    verify(repository).save(any(Article.class));
  }

  @Test
  void updateArticle_WhenExists_ShouldUpdateArticle() {
    long id = 1L;
    ArticleCreateRequest request =
        new ArticleCreateRequest(
            "Updated Title", "updated-slug", "Updated Content", List.of(new Tag("updated")));
    Article existingArticle =
        new Article("Old Title", "old-slug", "Old Content", List.of(new Tag("old")));
    Article updatedArticle =
        new Article(request.title(), request.slug(), request.content(), request.tags());

    updatedArticle.setId(id);

    when(repository.findById(id)).thenReturn(Optional.of(existingArticle));
    when(repository.save(any(Article.class))).thenReturn(updatedArticle);

    ArticleResourceResponse result = service.updateArticle(id, request);

    assertThat(result)
        .isNotNull()
        .satisfies(
            response -> {
              assertThat(response.title()).isEqualTo(request.title());
              assertThat(response.slug()).isEqualTo(request.slug());
              assertThat(response.content()).isEqualTo(request.content());
              assertThat(response.tags()).containsExactlyElementsOf(request.tags());
            });

    verify(repository).findById(id);
    verify(repository).save(any(Article.class));
  }

  @Test
  void updateArticle_WhenNotExists_ShouldThrowException() {
    long id = 1L;
    ArticleCreateRequest request =
        new ArticleCreateRequest("Title", "slug", "Content", List.of(new Tag("tag")));
    when(repository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.updateArticle(id, request))
        .isInstanceOf(ResourceViolationException.class)
        .hasMessageContaining("not found");

    verify(repository).findById(id);
    verify(repository, never()).save(any(Article.class));
  }

  @Test
  void delete_ShouldCallRepository() {
    long id = 1L;

    service.delete(id);

    verify(repository).deleteById(id);
  }

  @Test
  void uploadThumbnail_WhenArticleExists_ShouldUploadAndUpdate() throws Exception {
    long id = 1L;
    Article article = new Article("Title", "slug", "Content", List.of(new Tag("tag")));
    MultipartFile file =
        new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test data".getBytes());
    article.setId(id);

    when(repository.findById(id)).thenReturn(Optional.of(article));
    when(storageRepository.uploadFile(any(), any(), anyLong(), any()))
        .thenReturn("uploaded-file.jpg");
    when(repository.save(any(Article.class))).thenReturn(article);

    ArticleResourceResponse result = service.uploadThumbnail(id, file);

    assertThat(result).isNotNull();

    verify(repository).findById(id);
    verify(storageRepository).uploadFile(any(), any(), anyLong(), any());
    verify(repository).save(any(Article.class));
  }

  @Test
  void uploadThumbnail_WhenArticleNotExists_ShouldThrowException() throws Exception {
    long id = 1L;
    MultipartFile file =
        new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test data".getBytes());
    when(repository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.uploadThumbnail(id, file))
        .isInstanceOf(ResourceViolationException.class)
        .hasMessageContaining("not found");

    verify(repository).findById(id);
    verify(storageRepository, never()).uploadFile(any(), any(), anyLong(), any());
    verify(repository, never()).save(any(Article.class));
  }
}
