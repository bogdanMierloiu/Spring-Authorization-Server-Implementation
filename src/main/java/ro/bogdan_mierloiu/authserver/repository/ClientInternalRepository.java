package ro.bogdan_mierloiu.authserver.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ro.bogdan_mierloiu.authserver.entity.ClientInternal;

public interface ClientInternalRepository extends JpaRepository<ClientInternal, Long> {
}
