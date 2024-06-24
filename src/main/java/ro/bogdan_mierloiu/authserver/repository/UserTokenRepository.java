package ro.bogdan_mierloiu.authserver.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ro.bogdan_mierloiu.authserver.entity.UserToken;

import java.util.List;
import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    List<UserToken> findAllByUserEmail(String userEmail);

    Optional<UserToken> findByToken(String token);
}
