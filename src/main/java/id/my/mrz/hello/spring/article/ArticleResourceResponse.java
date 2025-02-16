package id.my.mrz.hello.spring.article;

import id.my.mrz.hello.spring.tag.Tag;
import java.util.List;

final record ArticleResourceResponse(
    long id, String title, String slug, String content, String thumbnail, List<Tag> tags) {}
