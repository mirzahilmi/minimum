package id.my.mrz.hello.spring.article;

import id.my.mrz.hello.spring.tag.Tag;
import java.util.List;

final record ArticleCreateRequest(String title, String slug, String content, List<Tag> tags) {}
