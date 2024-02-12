package ro.bogdan_mierloiu.authserver.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import ro.bogdan_mierloiu.authserver.service.AppRequestService;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomEventApplicationListener implements
        ApplicationListener<AuthenticationFailureBadCredentialsEvent> {


    private final HttpServletRequest request;
    private final AppRequestService appRequestService;

    @Override
    public void onApplicationEvent(@NonNull AuthenticationFailureBadCredentialsEvent event) {
        appRequestService.checkRequest(request);
    }
}