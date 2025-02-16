package id.my.mrz.hello.spring.article;

import id.my.mrz.hello.spring.exception.ResourceViolationException;
import id.my.mrz.hello.spring.minio.IStorageRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
class ArticleService implements IArticleService {
  private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

  private final IArticleRepository repository;
  private final IStorageRepository storageRepository;

  ArticleService(IArticleRepository repository, IStorageRepository storageRepository) {
    this.repository = repository;
    this.storageRepository = storageRepository;
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
  public ArticleResourceResponse getArticle(long id) {
    logger.info("Fetching article with id: {}", id);
    Article article =
        repository
            .findById(id)
            .orElseThrow(
                () -> {
                  logger.error("Article not found with id: {}", id);
                  return new ResourceViolationException(
                      String.format("article of id %d not found", id));
                });
    logger.debug("Found article: {}", article);
    return article.toArticleResourceResponse();
  }

  @Override
  @Transactional
  public ArticleResourceResponse createArticle(ArticleCreateRequest payload) {
    logger.info("Creating article with title: {}", payload.title());
    Article article =
        new Article(payload.title(), payload.slug(), payload.content(), payload.tags());
    try {
      article = repository.save(article);
      logger.info("Article created successfully with id: {}", article.getId());
    } catch (DataIntegrityViolationException e) {
      logger.error(
          "Data integrity violation while creating article with slug: {}", payload.slug(), e);
      throw new ResourceViolationException(
          String.format("article slug of %s already exist", payload.slug()),
          List.of("duplicate"),
          e);
    }
    return article.toArticleResourceResponse();
  }

  @Override
  @Transactional
  public ArticleResourceResponse updateArticle(long id, ArticleCreateRequest payload) {
    logger.info("Updating article with id: {}", id);
    Article article =
        repository
            .findById(id)
            .orElseThrow(
                () -> {
                  logger.error("Article not found with id: {}", id);
                  return new ResourceViolationException(
                      String.format("article of id %d not found", id));
                });
    article.setTitle(payload.title());
    article.setSlug(payload.slug());
    article.setContent(payload.content());
    article.setTags(payload.tags());
    try {
      article = repository.save(article);
      logger.info("Article updated successfully with id: {}", article.getId());
    } catch (DataIntegrityViolationException e) {
      logger.error(
          "Data integrity violation while updating article with slug: {}", payload.slug(), e);
      throw new ResourceViolationException(
          String.format("article slug of %s already exist", payload.slug()), e);
    }
    return article.toArticleResourceResponse();
  }

  @Override
  public void delete(long id) {
    logger.info("Deleting article with id: {}", id);
    repository.deleteById(id);
    logger.info("Article deleted successfully with id: {}", id);
  }

  @Override
  public ArticleResourceResponse uploadThumbnail(long id, MultipartFile file) throws Exception {
    logger.info("Uploading thumbnail for article with id: {}", id);
    Article article =
        repository
            .findById(id)
            .orElseThrow(
                () -> {
                  logger.error("Article not found with id: {}", id);
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
