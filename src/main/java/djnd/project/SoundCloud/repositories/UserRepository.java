package djnd.project.SoundCloud.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import djnd.project.SoundCloud.domain.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String Email);
}
