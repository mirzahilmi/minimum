package id.my.mrz.hello.spring.article;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import id.my.mrz.hello.spring.article.dto.ArticleCreateRequest;
import id.my.mrz.hello.spring.article.dto.ArticleResourceResponse;
import id.my.mrz.hello.spring.article.entity.Article;
import id.my.mrz.hello.spring.article.event.ArticleCreatedEvent;
import id.my.mrz.hello.spring.article.event.ArticleDeletedEvent;
import id.my.mrz.hello.spring.article.event.ArticleUpdatedEvent;
import id.my.mrz.hello.spring.article.repository.IArticleRepository;
import id.my.mrz.hello.spring.article.service.ArticleService;
import id.my.mrz.hello.spring.exception.ResourceViolationException;
import id.my.mrz.hello.spring.filestorage.IFileStorageRepository;
import id.my.mrz.hello.spring.tag.dto.TagCreateRequest;
import id.my.mrz.hello.spring.tag.entity.Tag;
import id.my.mrz.hello.spring.user.IUserRepository;
import id.my.mrz.hello.spring.user.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

  @Mock private IArticleRepository repository;
  @Mock private IUserRepository userRepository;
  @Mock private IFileStorageRepository storageRepository;
  @Mock private ApplicationEventPublisher eventPublisher;
  @InjectMocks private ArticleService service;

  @Test
  void fetchArticles_ShouldReturnAllArticles() {
    long articleId = 1L;
    long tagId = 1L;

    User principal = new User("username", "password");

    Tag tag = new Tag("tag");
    tag.setId(tagId);

    Article article1 = new Article("Title 1", "slug", "content", principal, List.of(tag));
    article1.setId(articleId);
    Article article2 = new Article("Title 2", "slug", "content", principal, List.of(tag));
    article2.setId(articleId + 1);

    when(repository.findAll()).thenReturn(List.of(article1, article2));
    List<ArticleResourceResponse> result = service.fetchArticles();

    assertThat(result)
        .hasSize(2)
        .extracting(ArticleResourceResponse::getTitle)
        .containsExactly("Title 1", "Title 2");

    verify(repository).findAll();
  }

  @Test
  void getArticle_WhenExists_ShouldReturnArticle() {
    long articleId = 1L;
    long tagId = 1L;

    User principal = new User("username", "password");

    Tag tag = new Tag("tag");
    tag.setId(tagId);

    Article article = new Article("title", "slug", "content", principal, List.of(tag));
    article.setId(articleId);

    when(repository.findById(1L)).thenReturn(Optional.of(article));

    ArticleResourceResponse result = service.getArticle(1L);

    assertThat(result)
        .isNotNull()
        .satisfies(
            response -> {
              assertThat(response.getTitle()).isEqualTo("title");
              assertThat(response.getSlug()).isEqualTo("slug");
              assertThat(response.getContent()).isEqualTo("content");
            });

    verify(repository).findById(1L);
  }

  @Test
  void createArticle_WithValidData_ShouldCreateArticle() {
    long userId = 1L;
    long articleId = 1L;
    long tagId = 1L;

    User principal = new User("username", "password");
    Tag tag = new Tag("tag");
    tag.setId(tagId);
    Article article = new Article("title", "slug", "content", principal, List.of(tag));
    article.setId(articleId);

    ArticleCreateRequest request =
        new ArticleCreateRequest(
            article.getTitle(),
            article.getSlug(),
            article.getContent(),
            List.of(new TagCreateRequest(tag.getName())));

    when(userRepository.findById(userId)).thenReturn(Optional.of(principal));
    when(repository.save(any(Article.class))).thenReturn(article);

    ArticleResourceResponse result = service.createArticle(articleId, request);
    assertThat(result).isNotNull();

    verify(repository).save(any(Article.class));
    verify(userRepository).findById(userId);
    verify(eventPublisher).publishEvent(any(ArticleCreatedEvent.class));
  }

  @Test
  void updateArticle_WhenAuthorized_ShouldUpdateArticle() {
    long userId = 1L;
    long articleId = 1L;
    long tagId = 1L;

    User principal = new User("username", "password");
    principal.setId(userId);

    Tag tag = new Tag("tag");
    tag.setId(tagId);

    Article article = new Article("title", "slug", "content", principal, List.of(tag));
    article.setId(articleId);

    ArticleCreateRequest request =
        new ArticleCreateRequest(
            "Updated Title",
            "updated-slug",
            "Updated Content",
            List.of(new TagCreateRequest(tag.getName())));

    when(repository.findById(1L)).thenReturn(Optional.of(article));
    when(repository.save(any(Article.class)))
        .thenAnswer(
            invocation -> {
              Article updated = invocation.getArgument(0);
              updated.setTags(List.of(tag));
              return updated;
            });

    ArticleResourceResponse result = service.updateArticle(1L, 1L, request);

    assertThat(result)
        .isNotNull()
        .satisfies(
            response -> {
              assertThat(response.getTitle()).isEqualTo("Updated Title");
              assertThat(response.getSlug()).isEqualTo("updated-slug");
              assertThat(response.getContent()).isEqualTo("Updated Content");
              assertThat(response.getTags()).hasSize(1);
              assertThat(response.getTags().get(0).name()).isEqualTo("tag");
            });

    verify(repository).findById(1L);
    verify(repository).save(any(Article.class));
    verify(eventPublisher).publishEvent(any(ArticleUpdatedEvent.class));
  }

  @Test
  void updateArticle_WhenUnauthorized_ShouldThrowException() {
    User principal = new User("username", "password");
    principal.setId(1L);

    Article article =
        new Article(
            "Test Title", "test-slug", "Test Content", principal, List.of(new Tag("test-tag")));
    article.setId(1L);

    ArticleCreateRequest request =
        new ArticleCreateRequest("Title", "slug", "Content", List.of(new TagCreateRequest("tag")));

    when(repository.findById(1L)).thenReturn(Optional.of(article));

    assertThatThrownBy(() -> service.updateArticle(1L, 999L, request))
        .isInstanceOf(ResourceViolationException.class)
        .hasMessageContaining("not authorized");

    verify(repository).findById(1L);
    verify(repository, never()).save(any(Article.class));
    verify(eventPublisher, never()).publishEvent(any(ArticleUpdatedEvent.class));
  }

  @Test
  void delete_WhenAuthorized_ShouldDeleteArticle() {
    User principal = new User("username", "password");
    principal.setId(1L);

    Article article =
        new Article(
            "Test Title", "test-slug", "Test Content", principal, List.of(new Tag("test-tag")));
    article.setId(1L);

    when(repository.findById(1L)).thenReturn(Optional.of(article));

    service.delete(1L, 1L);

    verify(repository).findById(1L);
    verify(repository).deleteById(1L);
    verify(eventPublisher).publishEvent(any(ArticleDeletedEvent.class));
  }

  @Test
  void delete_WhenUnauthorized_ShouldThrowException() {
    User principal = new User("username", "password");
    principal.setId(1L);

    Article article =
        new Article(
            "Test Title", "test-slug", "Test Content", principal, List.of(new Tag("test-tag")));
    article.setId(1L);

    when(repository.findById(1L)).thenReturn(Optional.of(article));

    assertThatThrownBy(() -> service.delete(1L, 999L))
        .isInstanceOf(ResourceViolationException.class)
        .hasMessageContaining("not authorized");

    verify(repository).findById(1L);
    verify(repository, never()).deleteById(anyLong());
    verify(eventPublisher, never()).publishEvent(any(ArticleDeletedEvent.class));
  }

  @Test
  void uploadThumbnail_WhenAuthorized_ShouldUploadAndUpdate() throws Exception {
    long userId = 1L;
    long articleId = 1L;
    long tagId = 1L;

    User principal = new User("username", "password");
    principal.setId(userId);

    Tag tag = new Tag("tag");
    tag.setId(tagId);

    Article article = new Article("title", "slug", "content", principal, List.of(tag));
    article.setId(articleId);

    MultipartFile file =
        new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test data".getBytes());
    String expectedFileName = "uploaded-file.jpg";

    when(repository.findById(1L)).thenReturn(Optional.of(article));
    when(storageRepository.uploadFile(any(), any(), anyLong(), any())).thenReturn(expectedFileName);
    when(repository.save(any(Article.class)))
        .thenAnswer(
            invocation -> {
              Article updated = invocation.getArgument(0);
              updated.setTags(List.of(tag));
              updated.setThumbnail(expectedFileName);
              return updated;
            });

    ArticleResourceResponse result = service.uploadThumbnail(1L, 1L, file);

    assertThat(result)
        .isNotNull()
        .satisfies(
            response -> {
              assertThat(response.getThumbnail()).isEqualTo(expectedFileName);
            });

    verify(repository).findById(1L);
    verify(storageRepository).uploadFile(any(), any(), anyLong(), any());
    verify(repository).save(any(Article.class));
  }

  @Test
  void uploadThumbnail_WhenUnauthorized_ShouldThrowException() throws Exception {
    User principal = new User("username", "password");
    principal.setId(1L);

    Article article =
        new Article(
            "Test Title", "test-slug", "Test Content", principal, List.of(new Tag("test-tag")));
    article.setId(1L);

    MultipartFile file =
        new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test data".getBytes());

    when(repository.findById(1L)).thenReturn(Optional.of(article));

    assertThatThrownBy(() -> service.uploadThumbnail(1L, 999L, file))
        .isInstanceOf(ResourceViolationException.class)
        .hasMessageContaining("not authorized");

    verify(repository).findById(1L);
    verify(storageRepository, never()).uploadFile(any(), any(), anyLong(), any());
    verify(repository, never()).save(any(Article.class));
  }
}
