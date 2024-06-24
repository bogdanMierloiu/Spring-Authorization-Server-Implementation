package ro.bogdan_mierloiu.authserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.bogdan_mierloiu.authserver.entity.RequestAdmin;

@Repository
public interface RequestAdminRepository extends JpaRepository<RequestAdmin, Long> {
}
