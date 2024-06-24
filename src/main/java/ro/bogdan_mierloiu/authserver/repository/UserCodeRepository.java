package ro.bogdan_mierloiu.authserver.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ro.bogdan_mierloiu.authserver.entity.UserCode;

import java.util.Optional;

public interface UserCodeRepository extends JpaRepository<UserCode, Long> {
    Optional<UserCode> findByCode(String code);
}
