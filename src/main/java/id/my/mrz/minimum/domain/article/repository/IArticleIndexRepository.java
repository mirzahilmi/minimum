package id.my.mrz.minimum.domain.article.repository;

import id.my.mrz.minimum.domain.article.entity.ArticleDocument;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface IArticleIndexRepository
    extends CrudRepository<ArticleDocument, Long>, QueryByExampleExecutor<ArticleDocument> {}
