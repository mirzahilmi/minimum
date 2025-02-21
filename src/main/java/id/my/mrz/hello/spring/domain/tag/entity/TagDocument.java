package id.my.mrz.hello.spring.domain.tag.entity;

import org.springframework.data.annotation.Id;

public final class TagDocument {
  @Id private Long id;
  private String name;

  public TagDocument(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
