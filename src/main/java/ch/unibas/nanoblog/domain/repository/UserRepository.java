package ch.unibas.nanoblog.domain.repository;

import ch.unibas.nanoblog.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    User findByUserId(long id);
}
