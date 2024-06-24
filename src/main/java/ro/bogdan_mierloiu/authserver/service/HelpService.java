package ro.bogdan_mierloiu.authserver.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.bogdan_mierloiu.authserver.dto.HelpMessage;
import ro.bogdan_mierloiu.authserver.util.constant.RegistrationErrorMessages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class HelpService {


    private final EmailSender emailSender;
    private final AppRequestService appRequestService;

    public void help(HelpMessage message, HttpServletRequest request) {
        appRequestService.checkRequest(request);
        validateEmail(message.getEmail());
        emailSender.sendHelpMessage(message);

    }

    private void validateEmail(String email) {
        Pattern emailPattern =
                Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$");
        Matcher matcher = emailPattern.matcher(email);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(RegistrationErrorMessages.INVALID_EMAIL);
        }
    }
}
