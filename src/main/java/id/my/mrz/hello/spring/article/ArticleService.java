package id.my.mrz.hello.spring.article;

import id.my.mrz.hello.spring.exception.ResourceViolationException;
import id.my.mrz.hello.spring.minio.IStorageRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
class ArticleService implements IArticleService {
  private final IArticleRepository repository;
  private final IStorageRepository storageRepository;

  ArticleService(IArticleRepository repository, IStorageRepository storageRepository) {
    this.repository = repository;
    this.storageRepository = storageRepository;
  }

  @Override
  public List<ArticleResourceResponse> fetchArticles() {
    Iterable<Article> articles = repository.findAll();
    return StreamSupport.stream(articles.spliterator(), false)
        .map(article -> article.toArticleResourceResponse())
        .collect(Collectors.toList());
  }

  @Override
  public ArticleResourceResponse getArticle(long id) {
    Article article =
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResourceViolationException(
                        String.format("article of id %d not found", id)));
    return article.toArticleResourceResponse();
  }

  @Override
  @Transactional
  public ArticleResourceResponse createArticle(ArticleCreateRequest payload) {
    Article article =
        new Article(payload.title(), payload.slug(), payload.content(), payload.tags());

    try {
      article = repository.save(article);
    } catch (DataIntegrityViolationException e) {
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
    Article article =
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResourceViolationException(
                        String.format("article of id %d not found", id)));

    article.setTitle(payload.title());
    article.setSlug(payload.slug());
    article.setContent(payload.content());
    article.setTags(payload.tags());

    try {
      article = repository.save(article);
    } catch (DataIntegrityViolationException e) {
      throw new ResourceViolationException(
          String.format("article slug of %s already exist", payload.slug()), e);
    }

    return article.toArticleResourceResponse();
  }

  @Override
  public void delete(long id) {
    repository.deleteById(id);
  }

  @Override
  public ArticleResourceResponse uploadThumbnail(long id, MultipartFile file) throws Exception {
    Article article =
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResourceViolationException(
                        String.format("article of id %d not found", id)));

    String filename =
        storageRepository.uploadFile(
            file.getInputStream(), file.getName(), file.getSize(), file.getContentType());

    article.setThumbnail(filename);
    repository.save(article);

    return article.toArticleResourceResponse();
  }
}
