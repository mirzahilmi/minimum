package id.my.mrz.minimum.domain.article.event;

import id.my.mrz.minimum.domain.article.entity.Article;
import org.springframework.context.ApplicationEvent;

public final class ArticleUpdatedEvent extends ApplicationEvent {
  private final Article article;

  public ArticleUpdatedEvent(Object source, Article article) {
    super(source);
    this.article = article;
  }

  public Article getArticle() {
    return article;
  }
}
