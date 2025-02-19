package id.my.mrz.hello.spring.article.repository;

import id.my.mrz.hello.spring.article.entity.Article;
import org.springframework.data.repository.CrudRepository;

public interface IArticleRepository extends CrudRepository<Article, Long> {}
