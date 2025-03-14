package id.my.mrz.minimum.domain.article.event;

import org.springframework.context.ApplicationEvent;

public final class ArticleDeletedEvent extends ApplicationEvent {
    private final long articleId;

    public ArticleDeletedEvent(Object source, long articleId) {
        super(source);
        this.articleId = articleId;
    }

    public long getArticleId() {
        return articleId;
    }
}
