package ro.bogdan_mierloiu.authserver.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ro.bogdan_mierloiu.authserver.entity.ServerUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerUserRepository extends JpaRepository<ServerUser, Long> {
    Optional<ServerUser> findByEmail(String email);

    @Query("SELECT j FROM ServerUser j WHERE SUBSTRING(j.email, 1, 1000) LIKE %:characters%")
    List<ServerUser> findByEmailCharacters(String characters);
}
