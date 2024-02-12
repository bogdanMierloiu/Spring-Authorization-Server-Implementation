package ro.bogdan_mierloiu.authserver.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.bogdan_mierloiu.authserver.entity.UserIp;
import ro.bogdan_mierloiu.authserver.exception.IpBlockedException;
import ro.bogdan_mierloiu.authserver.repository.UserIpRepository;
import ro.bogdan_mierloiu.authserver.util.constant.IpAddressUtils;
import ro.bogdan_mierloiu.authserver.util.extractor.IpAddressInterceptor;

import java.time.LocalDateTime;
import java.util.List;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class AppRequestService {

    private final UserIpRepository userIpRepository;

    private final IpAddressInterceptor ipAddressInterceptor;
    @Transactional(noRollbackFor = {IpBlockedException.class})
    public void checkRequest(HttpServletRequest request) {
        String ipFromHeader = ipAddressInterceptor.getIpFromHeader(request);
        requestObservation(ipFromHeader);    }


    private void requestObservation(String ipAddress) {
        UserIp userIp = userIpRepository.findByIpAddress(ipAddress).orElseGet(
                () -> UserIp.builder()
                        .loginAttempts(0)
                        .wasBlocked(false)
                        .ipAddress(ipAddress)
                        .build());

        checkIfIpIsBlocked(userIp);
        userIp.setLastAction(LocalDateTime.now());

        int attempts = (checkIfActionIsRecent(userIp)) ? 0 : userIp.getLoginAttempts();
        userIp.setLoginAttempts(++attempts);

        if (attempts >= IpAddressUtils.MAX_ATTEMPT) {
            LocalDateTime currentTime = LocalDateTime.now();
            if (userIp.getFirstBlockEndTime() == null) {
                userIp.setFirstBlockEndTime(currentTime.plusMinutes(IpAddressUtils.FIRST_BLOCKED_PERIOD));
            } else {
                userIp.setSecondBlockEndTime(currentTime.plusMinutes(IpAddressUtils.SECOND_BLOCKED_PERIOD));
            }

            userIp.setWasBlocked(true);
            userIp.setBlockedAt(LocalDateTime.now());
            userIp.setLoginAttempts(0);
        }

        userIpRepository.save(userIp);
    }

    public void checkIfIpIsBlocked(UserIp userIp) {
        if (Boolean.TRUE.equals(userIp.getWasBlocked())) {
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime firstBlockEndTime = userIp.getFirstBlockEndTime();
            LocalDateTime secondBlockEndTime = userIp.getSecondBlockEndTime();
            if (secondBlockEndTime != null && currentTime.isBefore(secondBlockEndTime)) {
                throw new IpBlockedException(IpAddressUtils.IP_ADDRESS_BLOCKED_60);
            }
            if (firstBlockEndTime != null && currentTime.isBefore(firstBlockEndTime)) {
                throw new IpBlockedException(IpAddressUtils.IP_ADDRESS_BLOCKED_15);
            }
        }
    }

    public void checkIfIpIsBlocked(String ipAddress) {
        userIpRepository.findByIpAddress(ipAddress).ifPresent(this::checkIfIpIsBlocked);
    }

    private boolean checkIfActionIsRecent(UserIp userIp) {
        return userIp.getLastAction() != null
                && userIp.getLastAction().plusMinutes(IpAddressUtils.RECENT_ACTIONS_PERIOD).isBefore(LocalDateTime.now());
    }

    public List<UserIp> getAllLoginAttempts() {
        return userIpRepository.findAll();
    }

    public void deleteAllLoginAttempts() {
        userIpRepository.deleteAll();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteLoginAttempts() {
        userIpRepository.deleteAll();
    }
}