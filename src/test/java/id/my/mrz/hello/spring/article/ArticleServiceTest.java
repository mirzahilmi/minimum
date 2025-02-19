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
import id.my.mrz.hello.spring.tag.Tag;
import id.my.mrz.hello.spring.user.IUserRepository;
import id.my.mrz.hello.spring.user.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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

  private User testUser;
  private Article testArticle;
  private static final long USER_ID = 1L;
  private static final long ARTICLE_ID = 1L;

  @BeforeEach
  void setUp() {
    testUser = new User(USER_ID, "username", "password");
    testUser.setId(USER_ID);

    testArticle = new Article("Title", "slug", "Content", testUser, List.of(new Tag("tag")));
    testArticle.setId(ARTICLE_ID);
  }

  @Test
  void fetchArticles_ShouldReturnAllArticles() {
    Article article1 =
        new Article("Title 1", "slug-1", "Content 1", testUser, List.of(new Tag("tag1")));
    Article article2 =
        new Article("Title 2", "slug-2", "Content 2", testUser, List.of(new Tag("tag2")));
    article1.setId(1);
    article2.setId(2);
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
    when(repository.findById(ARTICLE_ID)).thenReturn(Optional.of(testArticle));

    ArticleResourceResponse result = service.getArticle(ARTICLE_ID);

    assertThat(result)
        .isNotNull()
        .satisfies(
            response -> {
              assertThat(response.getTitle()).isEqualTo("Title");
              assertThat(response.getSlug()).isEqualTo("slug");
              assertThat(response.getContent()).isEqualTo("Content");
            });

    verify(repository).findById(ARTICLE_ID);
  }

  @Test
  void createArticle_WithValidData_ShouldCreateArticle() {
    ArticleCreateRequest request =
        new ArticleCreateRequest(
            "Test Title", "test-slug", "Test Content", List.of(new Tag("test")));

    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
    when(repository.save(any(Article.class))).thenReturn(testArticle);

    ArticleResourceResponse result = service.createArticle(USER_ID, request);

    assertThat(result).isNotNull();
    verify(repository).save(any(Article.class));
    verify(userRepository).findById(USER_ID);
    verify(eventPublisher).publishEvent(any(ArticleCreatedEvent.class));
  }

  @Test
  void updateArticle_WhenAuthorized_ShouldUpdateArticle() {
    ArticleCreateRequest request =
        new ArticleCreateRequest(
            "Updated Title", "updated-slug", "Updated Content", List.of(new Tag("updated")));

    when(repository.findById(ARTICLE_ID)).thenReturn(Optional.of(testArticle));
    when(repository.save(any(Article.class))).thenReturn(testArticle);

    ArticleResourceResponse result = service.updateArticle(ARTICLE_ID, USER_ID, request);

    assertThat(result).isNotNull();
    verify(repository).findById(ARTICLE_ID);
    verify(repository).save(any(Article.class));
    verify(eventPublisher).publishEvent(any(ArticleUpdatedEvent.class));
  }

  @Test
  void updateArticle_WhenUnauthorized_ShouldThrowException() {
    ArticleCreateRequest request =
        new ArticleCreateRequest("Title", "slug", "Content", List.of(new Tag("tag")));
    long unauthorizedUserId = 999L;

    when(repository.findById(ARTICLE_ID)).thenReturn(Optional.of(testArticle));

    assertThatThrownBy(() -> service.updateArticle(ARTICLE_ID, unauthorizedUserId, request))
        .isInstanceOf(ResourceViolationException.class)
        .hasMessageContaining("not authorized");

    verify(repository).findById(ARTICLE_ID);
    verify(repository, never()).save(any(Article.class));
  }

  @Test
  void delete_WhenAuthorized_ShouldDeleteArticle() {
    when(repository.findById(ARTICLE_ID)).thenReturn(Optional.of(testArticle));

    service.delete(ARTICLE_ID, USER_ID);

    verify(repository).findById(ARTICLE_ID);
    verify(repository).deleteById(ARTICLE_ID);
    verify(eventPublisher).publishEvent(any(ArticleDeletedEvent.class));
  }

  @Test
  void delete_WhenUnauthorized_ShouldThrowException() {
    long unauthorizedUserId = 999L;
    when(repository.findById(ARTICLE_ID)).thenReturn(Optional.of(testArticle));

    assertThatThrownBy(() -> service.delete(ARTICLE_ID, unauthorizedUserId))
        .isInstanceOf(ResourceViolationException.class)
        .hasMessageContaining("not authorized");

    verify(repository).findById(ARTICLE_ID);
    verify(repository, never()).deleteById(anyLong());
  }

  @Test
  void uploadThumbnail_WhenAuthorized_ShouldUploadAndUpdate() throws Exception {
    MultipartFile file =
        new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test data".getBytes());

    when(repository.findById(ARTICLE_ID)).thenReturn(Optional.of(testArticle));
    when(storageRepository.uploadFile(any(), any(), anyLong(), any()))
        .thenReturn("uploaded-file.jpg");
    when(repository.save(any(Article.class))).thenReturn(testArticle);

    ArticleResourceResponse result = service.uploadThumbnail(ARTICLE_ID, USER_ID, file);

    assertThat(result).isNotNull();
    verify(repository).findById(ARTICLE_ID);
    verify(storageRepository).uploadFile(any(), any(), anyLong(), any());
    verify(repository).save(any(Article.class));
  }

  @Test
  void uploadThumbnail_WhenUnauthorized_ShouldThrowException() throws Exception {
    long unauthorizedUserId = 999L;
    MultipartFile file =
        new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test data".getBytes());

    when(repository.findById(ARTICLE_ID)).thenReturn(Optional.of(testArticle));

    assertThatThrownBy(() -> service.uploadThumbnail(ARTICLE_ID, unauthorizedUserId, file))
        .isInstanceOf(ResourceViolationException.class)
        .hasMessageContaining("not authorized");

    verify(repository).findById(ARTICLE_ID);
    verify(storageRepository, never()).uploadFile(any(), any(), anyLong(), any());
    verify(repository, never()).save(any(Article.class));
  }
}
