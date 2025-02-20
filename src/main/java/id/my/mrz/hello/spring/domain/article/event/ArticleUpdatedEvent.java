package id.my.mrz.hello.spring.domain.article.event;

import id.my.mrz.hello.spring.domain.article.entity.Article;
import org.springframework.context.ApplicationEvent;

public class ArticleUpdatedEvent extends ApplicationEvent {
  private final Article article;

  public ArticleUpdatedEvent(Object source, Article article) {
    super(source);
    this.article = article;
  }

  public Article getArticle() {
    return article;
  }
}
