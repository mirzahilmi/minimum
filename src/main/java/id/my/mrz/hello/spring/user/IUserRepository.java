package id.my.mrz.hello.spring.user;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface IUserRepository extends CrudRepository<User, Long> {
  Optional<User> findByUsername(String username);
}
