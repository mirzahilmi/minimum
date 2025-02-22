package id.my.mrz.minimum.domain.article.repository;

import id.my.mrz.minimum.domain.article.entity.Article;
import org.springframework.data.repository.CrudRepository;

public interface IArticleRepository extends CrudRepository<Article, Long> {}
