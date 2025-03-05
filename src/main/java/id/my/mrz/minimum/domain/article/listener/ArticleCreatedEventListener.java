package id.my.mrz.minimum.domain.article.listener;

import id.my.mrz.minimum.domain.article.entity.ArticleDocument;
import id.my.mrz.minimum.domain.article.event.ArticleCreatedEvent;
import id.my.mrz.minimum.domain.article.repository.ArticleElasticsearchRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public final class ArticleCreatedEventListener implements ApplicationListener<ArticleCreatedEvent> {
  private final ArticleElasticsearchRepository repository;

  public ArticleCreatedEventListener(ArticleElasticsearchRepository repository) {
    this.repository = repository;
  }

  @Override
  public void onApplicationEvent(ArticleCreatedEvent event) {
    ArticleDocument article = event.getArticle().toArticleDocument();
    repository.save(article);
  }
}
