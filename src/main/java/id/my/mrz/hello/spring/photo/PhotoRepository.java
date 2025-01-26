package id.my.mrz.hello.spring.photo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface PhotoRepository extends CrudRepository<Photo, Long> {
	@Override
	List<Photo> findAll();
}
