package ro.bogdan_mierloiu.authserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.bogdan_mierloiu.authserver.entity.UserCode;
import ro.bogdan_mierloiu.authserver.exception.CodeExpiredException;
import ro.bogdan_mierloiu.authserver.exception.CodeUsedException;
import ro.bogdan_mierloiu.authserver.repository.UserCodeRepository;
import ro.bogdan_mierloiu.authserver.util.constant.RegistrationErrorMessages;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CodeVerifierService {

    private final UserCodeRepository userCodeRepository;

    private final ServerUserService serverUserService;

    private final EmailSender emailSender;


    public void activateAccount(String code) throws IllegalAccessException {
        boolean isResetPasswordFlow = false;
        UserCode userCode = verifyAndUseCode(code, isResetPasswordFlow);
        serverUserService.enableAccount(userCode.getUserEmail());
        emailSender.sendSuccessEmailForRegistration(userCode);
    }

    @Transactional
    public UserCode verifyResetPasswordCode(String code) throws IllegalAccessException {
        boolean isResetPasswordFlow = true;
        return verifyCode(code, isResetPasswordFlow);
    }

    public void useCode(String code) throws IllegalAccessException {
        UserCode userCode = userCodeRepository.findByCode(code).orElseThrow(() -> new IllegalAccessException(RegistrationErrorMessages.ILLEGAL_OPERATION));
        userCode.setIsUsed(true);
        userCodeRepository.save(userCode);
    }

    private UserCode verifyCode(String code, boolean isResetPasswordFlow) throws IllegalAccessException {
        Optional<UserCode> userCodeOptional = userCodeRepository.findByCode(code);
        if (userCodeOptional.isPresent()) {
            UserCode userCode = userCodeOptional.get();
            isCodeUsed(userCode);
            isCodeAvailable(userCode, isResetPasswordFlow);
            userCode.setIsUsed(true);
            return userCode;
        }
        throw new IllegalAccessException(RegistrationErrorMessages.ILLEGAL_OPERATION);
    }

    private UserCode verifyAndUseCode(String code, boolean isResetPasswordFlow) throws IllegalAccessException {
        UserCode userCode = verifyCode(code, isResetPasswordFlow);
        return userCodeRepository.save(userCode);
    }

    private void isCodeUsed(UserCode userCode) {
        if (Boolean.TRUE.equals(userCode.getIsUsed())) {
            throw new CodeUsedException(RegistrationErrorMessages.CODE_USED);
        }
    }


    public void isCodeAvailable(UserCode userCode, boolean isResetPasswordFlow) {
        if (userCode.getGeneratedAt().plusMinutes(15).isBefore(LocalDateTime.now())) {
            UserCode userCodeNew = UserCode.builder()
                    .userEmail(userCode.getUserEmail())
                    .code(CodeGenerator.generateRandomCode())
                    .generatedAt(LocalDateTime.now())
                    .isUsed(false)
                    .build();
            userCodeRepository.save(userCodeNew);
            if (isResetPasswordFlow) {
                emailSender.sendEmailForResetPassword(userCodeNew);
            } else {
                emailSender.sendEmailForRegistration(userCodeNew);
            }
            throw new CodeExpiredException(RegistrationErrorMessages.CODE_EXPIRED);
        }
    }
}
