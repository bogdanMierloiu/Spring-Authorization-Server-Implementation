package ro.bogdan_mierloiu.authserver.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.bogdan_mierloiu.authserver.entity.ServerUser;
import ro.bogdan_mierloiu.authserver.repository.ServerUserRepository;
import ro.bogdan_mierloiu.authserver.service.AppRequestService;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ServerUserRepository serverUserRepository;

    private final AppRequestService appRequestService;

    private final HttpServletRequest request;

    public UserDetailsServiceImpl(ServerUserRepository serverUserRepository, AppRequestService appRequestService, HttpServletRequest request) {
        this.serverUserRepository = serverUserRepository;
        this.appRequestService = appRequestService;
        this.request = request;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        appRequestService.checkIfIpIsBlocked(getClientIP());
        Optional<ServerUser> serverUserOptional = serverUserRepository.findByEmail(email);
        if (serverUserOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found for email " + email);
        }
        return serverUserOptional.get();
    }

    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
