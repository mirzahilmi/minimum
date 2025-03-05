package id.my.mrz.minimum.domain.article.repository;

import id.my.mrz.minimum.domain.article.entity.ArticleDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface ArticleElasticsearchRepository
    extends ElasticsearchRepository<ArticleDocument, Long>,
        QueryByExampleExecutor<ArticleDocument> {}
