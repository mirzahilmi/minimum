package id.my.mrz.hello.spring.article;

import org.springframework.data.repository.CrudRepository;

interface IArticleRepository extends CrudRepository<Article, Long> {}
