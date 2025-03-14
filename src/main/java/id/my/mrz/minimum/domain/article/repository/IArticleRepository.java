package id.my.mrz.minimum.domain.article.repository;

import org.springframework.data.repository.CrudRepository;

import id.my.mrz.minimum.domain.article.entity.Article;

public interface IArticleRepository extends CrudRepository<Article, Long> {
}
