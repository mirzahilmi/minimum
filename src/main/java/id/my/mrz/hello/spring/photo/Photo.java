package id.my.mrz.hello.spring.photo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import java.io.Serializable;

@Entity(name = "photos")
public class Photo implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String filename;

  @Lob
  @Column(nullable = false)
  private byte[] data;

  public Photo(String filename, byte[] data) {
    this.filename = filename;
    this.data = data;
  }

  protected Photo() {}

  public Photo(Long id, String filename, byte[] data) {
    this.id = id;
    this.filename = filename;
    this.data = data;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return "Photo [id=" + id + ", filename=" + filename + "]";
  }
}
