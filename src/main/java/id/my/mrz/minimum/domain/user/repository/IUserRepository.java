package id.my.mrz.minimum.domain.user.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import id.my.mrz.minimum.domain.user.entity.User;

public interface IUserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
