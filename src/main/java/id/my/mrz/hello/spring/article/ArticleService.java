package id.my.mrz.hello.spring.article;

import id.my.mrz.hello.spring.exception.ResourceViolationException;
import id.my.mrz.hello.spring.filestorage.IFileStorageRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
class ArticleService implements IArticleService {
  private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

  private final IArticleRepository repository;
  private final IFileStorageRepository storageRepository;
  private final ApplicationEventPublisher eventPublisher;

  ArticleService(
      IArticleRepository repository,
      IFileStorageRepository storageRepository,
      ApplicationEventPublisher eventPublisher) {
    this.repository = repository;
    this.storageRepository = storageRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public List<ArticleResourceResponse> fetchArticles() {
    logger.info("Fetching all articles");
    Iterable<Article> articles = repository.findAll();
    List<ArticleResourceResponse> response =
        StreamSupport.stream(articles.spliterator(), false)
            .map(Article::toArticleResourceResponse)
            .collect(Collectors.toList());
    logger.debug("Fetched {} articles", response.size());
    return response;
  }

  @Override
  @Cacheable(cacheNames = "articles", key = "#id")
  public ArticleResourceResponse getArticle(long id) {
    logger.info("Fetching article with id: {}", id);
    Article article =
        repository
            .findById(id)
            .orElseThrow(
                () -> {
                  return new ResourceViolationException(
                      String.format("article of id %d not found", id));
                });
    logger.debug("Found article: {}", article);
    return article.toArticleResourceResponse();
  }

  @Override
  @Transactional
  public ArticleResourceResponse createArticle(ArticleCreateRequest payload) {
    logger.info("Creating article with title: {}", payload.getTitle());
    Article article =
        new Article(payload.getTitle(), payload.getSlug(), payload.getContent(), payload.getTags());
    try {
      article = repository.save(article);
      logger.info("Article created successfully with id: {}", article.getId());

      eventPublisher.publishEvent(new ArticleCreatedEvent(this, article));
    } catch (DataIntegrityViolationException e) {
      throw new ResourceViolationException(
          String.format("article slug of %s already exist", payload.getSlug()),
          List.of("duplicate"),
          e);
    }
    return article.toArticleResourceResponse();
  }

  @Override
  @Transactional
  @CachePut(cacheNames = "articles", key = "#id")
  public ArticleResourceResponse updateArticle(long id, ArticleCreateRequest payload) {
    logger.info("Updating article with id: {}", id);
    Article article =
        repository
            .findById(id)
            .orElseThrow(
                () -> {
                  return new ResourceViolationException(
                      String.format("article of id %d not found", id));
                });
    article.setTitle(payload.getTitle());
    article.setSlug(payload.getSlug());
    article.setContent(payload.getContent());
    article.setTags(payload.getTags());
    try {
      article = repository.save(article);
      logger.info("Article updated successfully with id: {}", article.getId());

      eventPublisher.publishEvent(new ArticleUpdatedEvent(this, article));
    } catch (DataIntegrityViolationException e) {
      throw new ResourceViolationException(
          String.format("article slug of %s already exist", payload.getSlug()), e);
    }
    return article.toArticleResourceResponse();
  }

  @Override
  @CacheEvict(cacheNames = "articles", key = "#id")
  public void delete(long id) {
    logger.info("Deleting article with id: {}", id);
    repository.deleteById(id);
    logger.info("Article deleted successfully with id: {}", id);

    eventPublisher.publishEvent(new ArticleDeletedEvent(this, id));
  }

  @Override
  @CachePut(cacheNames = "articles", key = "#id")
  public ArticleResourceResponse uploadThumbnail(long id, MultipartFile file) throws Exception {
    logger.info("Uploading thumbnail for article with id: {}", id);
    Article article =
        repository
            .findById(id)
            .orElseThrow(
                () -> {
                  return new ResourceViolationException(
                      String.format("article of id %d not found", id));
                });
    String filename =
        storageRepository.uploadFile(
            file.getInputStream(), file.getName(), file.getSize(), file.getContentType());
    article.setThumbnail(filename);
    repository.save(article);
    logger.info("Thumbnail uploaded successfully for article with id: {}", id);
    return article.toArticleResourceResponse();
  }
}
