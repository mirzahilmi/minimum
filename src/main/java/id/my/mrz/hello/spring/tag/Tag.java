package id.my.mrz.hello.spring.tag;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tags")
public final class Tag {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long Id;

  private String name;

  public Tag(String name) {
    this.name = name;
  }

  public long getId() {
    return Id;
  }

  public void setId(long id) {
    Id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
