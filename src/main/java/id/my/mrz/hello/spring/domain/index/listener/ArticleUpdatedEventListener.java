package id.my.mrz.hello.spring.domain.index.listener;

import id.my.mrz.hello.spring.domain.article.event.ArticleUpdatedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ArticleUpdatedEventListener implements ApplicationListener<ArticleUpdatedEvent> {

  @Override
  public void onApplicationEvent(ArticleUpdatedEvent event) {
    System.out.println("EVENT: " + event.getArticle().getTitle() + " updated");
  }
}
