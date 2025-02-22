package id.my.mrz.minimum.domain.article.listener;

import id.my.mrz.minimum.domain.article.event.ArticleDeletedEvent;
import id.my.mrz.minimum.domain.article.repository.IArticleIndexRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public final class ArticleDeletedEventListener implements ApplicationListener<ArticleDeletedEvent> {
  private final IArticleIndexRepository repository;

  public ArticleDeletedEventListener(IArticleIndexRepository repository) {
    this.repository = repository;
  }

  @Override
  public void onApplicationEvent(ArticleDeletedEvent event) {
    long articleId = event.getArticleId();
    repository.deleteById(articleId);
  }
}
