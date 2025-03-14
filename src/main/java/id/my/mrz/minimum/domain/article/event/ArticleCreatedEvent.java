package id.my.mrz.minimum.domain.article.event;

import org.springframework.context.ApplicationEvent;

import id.my.mrz.minimum.domain.article.entity.Article;

public final class ArticleCreatedEvent extends ApplicationEvent {
    private final Article article;

    public ArticleCreatedEvent(Object source, Article article) {
        super(source);
        this.article = article;
    }

    public Article getArticle() {
        return article;
    }
}
