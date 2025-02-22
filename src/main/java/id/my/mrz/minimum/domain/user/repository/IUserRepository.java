package id.my.mrz.minimum.domain.user.repository;

import id.my.mrz.minimum.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface IUserRepository extends CrudRepository<User, Long> {
  Optional<User> findByUsername(String username);
}
