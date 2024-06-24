package ro.bogdan_mierloiu.authserver.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ro.bogdan_mierloiu.authserver.entity.UserIp;

import java.util.Optional;

public interface UserIpRepository extends JpaRepository<UserIp, Long> {
    Optional<UserIp> findByIpAddress(String ipAddress);
}
