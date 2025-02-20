package id.my.mrz.hello.spring.domain.article.service;

import id.my.mrz.hello.spring.domain.article.dto.ArticleCreateRequest;
import id.my.mrz.hello.spring.domain.article.dto.ArticleResourceResponse;
import id.my.mrz.hello.spring.domain.article.entity.Article;
import id.my.mrz.hello.spring.domain.article.event.ArticleCreatedEvent;
import id.my.mrz.hello.spring.domain.article.event.ArticleDeletedEvent;
import id.my.mrz.hello.spring.domain.article.event.ArticleUpdatedEvent;
import id.my.mrz.hello.spring.domain.article.repoistory.IArticleRepository;
import id.my.mrz.hello.spring.domain.filestorage.repository.IFileStorageRepository;
import id.my.mrz.hello.spring.domain.tag.entity.Tag;
import id.my.mrz.hello.spring.domain.user.entity.User;
import id.my.mrz.hello.spring.domain.user.repository.IUserRepository;
import id.my.mrz.hello.spring.exception.ResourceViolationException;
import jakarta.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArticleService implements IArticleService {
  private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

  private final IArticleRepository repository;
  private final IUserRepository userRepository;
  private final IFileStorageRepository storageRepository;
  private final ApplicationEventPublisher eventPublisher;

  ArticleService(
      IArticleRepository repository,
      IUserRepository userRepository,
      @Qualifier("miniorepository") IFileStorageRepository storageRepository,
      ApplicationEventPublisher eventPublisher) {
    this.repository = repository;
    this.userRepository = userRepository;
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
            .toList();
    logger.debug("Fetched {} articles", response.size());
    return response;
  }

  @Override
  @Cacheable(cacheNames = "articles", key = "#id")
  public ArticleResourceResponse getArticle(long id) {
    logger.info("Fetching article with id: {}", id);
    Article article = findArticleById(id);
    logger.debug("Found article: {}", article);
    return article.toArticleResourceResponse();
  }

  @Override
  @Transactional
  public ArticleResourceResponse createArticle(long userId, ArticleCreateRequest payload) {
    User user = findUserById(userId);

    logger.info("Creating article with title: {}", payload.getTitle());
    List<Tag> tags = payload.getTags().stream().map(tag -> new Tag(tag.name())).toList();
    Article article =
        new Article(payload.getTitle(), payload.getSlug(), payload.getContent(), user, tags);
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
  public ArticleResourceResponse updateArticle(long id, long userId, ArticleCreateRequest payload) {
    logger.info("Updating article with id: {}", id);
    Article article = findArticleById(id);

    if (article.getUser().getId() != userId) {
      throw new ResourceViolationException(
          String.format("User %d is not authorized to update article %d", userId, id));
    }

    article.setTitle(payload.getTitle());
    article.setSlug(payload.getSlug());
    article.setContent(payload.getContent());
    List<Tag> tags =
        payload.getTags().stream()
            .map(tag -> new Tag(tag.name()))
            .collect(Collectors.toCollection(LinkedList::new));
    article.setTags(tags);

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
  public void delete(long id, long userId) {
    logger.info("Deleting article with id: {}", id);
    Article article = findArticleById(id);

    if (article.getUser().getId() != userId) {
      throw new ResourceViolationException(
          String.format("User %d is not authorized to delete article %d", userId, id));
    }

    repository.deleteById(id);
    logger.info("Article deleted successfully with id: {}", id);

    eventPublisher.publishEvent(new ArticleDeletedEvent(this, id));
  }

  @Override
  @CachePut(cacheNames = "articles", key = "#id")
  public ArticleResourceResponse uploadThumbnail(long id, long userId, MultipartFile file)
      throws Exception {
    logger.info("Uploading thumbnail for article with id: {}", id);
    Article article = findArticleById(id);

    if (article.getUser().getId() != userId) {
      throw new ResourceViolationException(
          String.format(
              "User %d is not authorized to upload thumbnail for article %d", userId, id));
    }

    String filename =
        storageRepository.uploadFile(
            file.getInputStream(), file.getName(), file.getSize(), file.getContentType());
    article.setThumbnail(filename);
    repository.save(article);
    logger.info("Thumbnail uploaded successfully for article with id: {}", id);
    return article.toArticleResourceResponse();
  }

  private User findUserById(long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(
            () -> new ResourceViolationException(String.format("user of id %d not found", userId)));
  }

  private Article findArticleById(long id) {
    return repository
        .findById(id)
        .orElseThrow(
            () ->
                new ResourceViolationException(
                    String.format("article of id %d not found", id), List.of("not_exist")));
  }
}
