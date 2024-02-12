package ro.bogdan_mierloiu.authserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.bogdan_mierloiu.authserver.entity.UserCode;
import ro.bogdan_mierloiu.authserver.repository.UserCodeRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserCodeService {

    private final UserCodeRepository userCodeRepository;


    public UserCode save(String email) {
        UserCode userCode = UserCode.builder()
                .userEmail(email)
                .code(CodeGenerator.generateRandomCode())
                .generatedAt(LocalDateTime.now())
                .isUsed(false)
                .build();
        return userCodeRepository.save(userCode);
    }


}
