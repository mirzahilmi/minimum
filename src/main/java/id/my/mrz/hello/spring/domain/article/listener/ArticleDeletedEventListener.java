package id.my.mrz.hello.spring.domain.article.listener;

import id.my.mrz.hello.spring.domain.article.event.ArticleDeletedEvent;
import id.my.mrz.hello.spring.domain.article.repository.IArticleIndexRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ArticleDeletedEventListener implements ApplicationListener<ArticleDeletedEvent> {
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
