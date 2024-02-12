package ro.bogdan_mierloiu.authserver.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.bogdan_mierloiu.authserver.service.AppRequestService;

@RestController
@RequestMapping("/ip")
@RequiredArgsConstructor
public class UserIpController {

    private final AppRequestService appRequestService;

    //TODO -> DELETE THIS IN PRODUCTION
    @Hidden
    @GetMapping("/login-attempts")
    public ResponseEntity<String> deleteLoginAttempts() {
        appRequestService.deleteAllLoginAttempts();
        return ResponseEntity.ok().body("All Login Attempts deleted");
    }
}
