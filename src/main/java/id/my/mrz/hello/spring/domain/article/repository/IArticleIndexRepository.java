package id.my.mrz.hello.spring.domain.article.repository;

import id.my.mrz.hello.spring.domain.article.entity.ArticleDocument;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface IArticleIndexRepository
    extends CrudRepository<ArticleDocument, Long>, QueryByExampleExecutor<ArticleDocument> {}
