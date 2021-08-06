package study.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import study.example.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    public User findByEmail(String email);

    //User findByEmail(String email);
    //User findByUserName(String userName);
}
