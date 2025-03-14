package id.my.mrz.minimum.domain.article.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import id.my.mrz.minimum.domain.article.entity.Article;
import id.my.mrz.minimum.domain.article.entity.ArticleDocument;
import id.my.mrz.minimum.domain.article.event.ArticleUpdatedEvent;
import id.my.mrz.minimum.domain.article.repository.ArticleElasticsearchRepository;

@Component
public final class ArticleUpdatedEventListener implements ApplicationListener<ArticleUpdatedEvent> {
    private final ArticleElasticsearchRepository repository;

    public ArticleUpdatedEventListener(ArticleElasticsearchRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onApplicationEvent(ArticleUpdatedEvent event) {
        Article article = event.getArticle();

        // FIXME: find way to handle this, retryable?
        ArticleDocument indexed = repository.findById(article.getId()).orElseThrow();
        ArticleDocument document = event.getArticle().toArticleDocument();

        indexed.setTitle(document.getTitle());
        indexed.setSlug(document.getSlug());
        indexed.setContent(document.getContent());
        indexed.setTags(document.getTags());

        repository.save(indexed);
    }
}
