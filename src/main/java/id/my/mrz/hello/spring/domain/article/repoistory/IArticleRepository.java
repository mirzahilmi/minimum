package id.my.mrz.hello.spring.domain.article.repoistory;

import id.my.mrz.hello.spring.domain.article.entity.Article;
import org.springframework.data.repository.CrudRepository;

public interface IArticleRepository extends CrudRepository<Article, Long> {}
