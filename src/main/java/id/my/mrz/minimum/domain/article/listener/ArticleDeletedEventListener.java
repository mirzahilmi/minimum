package id.my.mrz.minimum.domain.article.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import id.my.mrz.minimum.domain.article.event.ArticleDeletedEvent;
import id.my.mrz.minimum.domain.article.repository.ArticleElasticsearchRepository;

@Component
public final class ArticleDeletedEventListener implements ApplicationListener<ArticleDeletedEvent> {
    private final ArticleElasticsearchRepository repository;

    public ArticleDeletedEventListener(ArticleElasticsearchRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onApplicationEvent(ArticleDeletedEvent event) {
        long articleId = event.getArticleId();
        repository.deleteById(articleId);
    }
}
