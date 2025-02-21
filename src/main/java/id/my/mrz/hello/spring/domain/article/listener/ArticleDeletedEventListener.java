package id.my.mrz.hello.spring.domain.article.listener;

import id.my.mrz.hello.spring.domain.article.event.ArticleDeletedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ArticleDeletedEventListener implements ApplicationListener<ArticleDeletedEvent> {

  @Override
  public void onApplicationEvent(ArticleDeletedEvent event) {
    System.out.println("EVENT: " + event.getArticleId() + " deleted");
  }
}
