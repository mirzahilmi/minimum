package id.my.mrz.hello.spring.domain.article.listener;

import id.my.mrz.hello.spring.domain.article.event.ArticleCreatedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ArticleCreatedEventListener implements ApplicationListener<ArticleCreatedEvent> {

  @Override
  public void onApplicationEvent(ArticleCreatedEvent event) {
    System.out.println("EVENT: " + event.getArticle().getTitle() + " created");
  }
}
