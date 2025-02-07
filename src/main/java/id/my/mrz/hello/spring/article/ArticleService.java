package id.my.mrz.hello.spring.article;

import id.my.mrz.hello.spring.exception.ResourceViolationException;
import java.util.LinkedList;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
final class ArticleService implements IArticleService {
  private final IArticleRepository repository;

  ArticleService(IArticleRepository repository) {
    this.repository = repository;
  }

  @Override
  public List<ArticleResourceResponse> fetchArticles() {
    var articles = repository.findAll();
    List<ArticleResourceResponse> resourceArticles = new LinkedList<>();
    articles.forEach((article) -> resourceArticles.add(article.toArticleResourceResponse()));
    return resourceArticles;
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
  public ArticleResourceResponse createArticle(ArticleCreateRequest payload) {
    Article article =
        new Article(0, payload.title(), payload.slug(), payload.content(), payload.tags());

    try {
      article = repository.save(article);
    } catch (DataIntegrityViolationException e) {
      throw new ResourceViolationException(
          String.format("article slug of %s already exist", payload.slug()), e);
    }

    return article.toArticleResourceResponse();
  }

  @Override
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
}
