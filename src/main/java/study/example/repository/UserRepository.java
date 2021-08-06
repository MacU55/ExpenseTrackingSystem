package study.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.example.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
