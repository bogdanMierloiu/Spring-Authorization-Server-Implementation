package ro.bogdan_mierloiu.authserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.bogdan_mierloiu.authserver.entity.RequestAdmin;
import ro.bogdan_mierloiu.authserver.entity.ServerUser;
import ro.bogdan_mierloiu.authserver.entity.Status;
import ro.bogdan_mierloiu.authserver.repository.RequestAdminRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class RequestAdminService {

    private final RequestAdminRepository requestAdminRepository;

    @Transactional
    public void save(ServerUser user) {
        RequestAdmin requestAdmin = RequestAdmin.builder()
                .user(user)
                .requestedAt(ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/Bucharest")))
                .status(Status.PENDING)
                .build();
        requestAdminRepository.save(requestAdmin);
    }

}
