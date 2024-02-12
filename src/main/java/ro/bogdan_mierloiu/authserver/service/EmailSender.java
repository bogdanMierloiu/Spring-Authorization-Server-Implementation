package ro.bogdan_mierloiu.authserver.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ro.bogdan_mierloiu.authserver.dto.HelpMessage;
import ro.bogdan_mierloiu.authserver.dto.UsersMessage;
import ro.bogdan_mierloiu.authserver.entity.UserCode;

@Component
@RequiredArgsConstructor
@Async
public class EmailSender {


    private final JavaMailSender javaMailSender;

    private final MutualService mutualService;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${issueruri}")
    private String serverUri;


    public void sendEmailForRegistration(UserCode userCode) {
        try {
            MimeMessage message = composeRegistrationMail(userCode);
            javaMailSender.send(message);
        } catch (MessagingException ex) {
            throw new InternalError("Error while sending email: " + ex.getMessage());
        }
    }

    public void sendSuccessEmailForRegistration(UserCode userCode) {
        SimpleMailMessage message = composeRegistrationSuccessMail(userCode);
        javaMailSender.send(message);
    }

    public void sendEmailForResetPassword(UserCode userCode) {
        SimpleMailMessage message = composePasswordResetMail(userCode);
        javaMailSender.send(message);
    }

    public void sendSuccessEmailForResetPassword(String email) {
        SimpleMailMessage message = composePasswordResetSuccessMail(email);
        javaMailSender.send(message);
    }

    public void sendSuccessEmailForChangePassword(String email) {
        SimpleMailMessage message = composePasswordChangedSuccessMail(email);
        javaMailSender.send(message);
    }

    public void sendEmailToAllUsers(UsersMessage usersMessage) {
        SimpleMailMessage message = composeEmailToAllUsers(usersMessage);
        javaMailSender.send(message);
    }

    public void sendAdminRequestNotification(String email) {
        SimpleMailMessage message = composeEmailForAdminRequest(email);
        javaMailSender.send(message);
    }

    private SimpleMailMessage composeEmailForAdminRequest(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(mutualService.getOnlyAdminEmails());
        message.setSubject("ADMIN ROLE REQUEST");
        message.setText(String.format("User with email %s requested ADMIN role in Authentication Server", email));
        return message;
    }


    private SimpleMailMessage composeEmailToAllUsers(UsersMessage usersMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo("bogdan.mierloiu01@gmail.com");
        message.setBcc(mutualService.getUsersEmails());
        message.setSubject(usersMessage.getSubject());
        message.setText(usersMessage.getText());
        return message;
    }

    private MimeMessage composeRegistrationMail(UserCode userCode) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(senderEmail);
        helper.setTo(userCode.getUserEmail());
        helper.setSubject("ACTIVATE YOUR ACCOUNT");
        helper.setText(composeRegistrationMessage(userCode.getCode()), true);
        return message;
    }

    private SimpleMailMessage composeRegistrationSuccessMail(UserCode userCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(userCode.getUserEmail());
        message.setSubject("ACCOUNT CREATED");
        message.setText(composeRegistrationSuccessMessage());
        return message;
    }

    private String composeRegistrationMessage(String code) {
        return "Your activation code is: " + code + "<br/>" +
                "Click <a href='" + serverUri + "/verify' target='_blank'>here</a> and enter the code to activate your account.";
    }

    private String composeRegistrationSuccessMessage() {
        return "Your account was successfully created";
    }

    private SimpleMailMessage composePasswordResetMail(UserCode userCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(userCode.getUserEmail());
        message.setSubject("RESET YOUR PASSWORD!");
        message.setText(composeResetPasswordMessage(userCode.getCode()));
        return message;
    }

    private SimpleMailMessage composePasswordResetSuccessMail(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(email);
        message.setSubject("PASSWORD WAS RESET!");
        message.setText(composeResetPasswordSuccessMessage());
        return message;
    }

    private String composeResetPasswordMessage(String code) {
        return "Click here to reset your password: " +
                serverUri + "/reset?code=" + code;
    }

    private SimpleMailMessage composePasswordChangedSuccessMail(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(email);
        message.setSubject("PASSWORD WAS CHANGED!");
        message.setText(composeChangedPasswordSuccessMessage());
        return message;
    }

    private String composeResetPasswordSuccessMessage() {
        return "Your account password was successfully reset";
    }

    private String composeChangedPasswordSuccessMessage() {
        return "Your account password was successfully changed";
    }


    public void sendHelpMessage(HelpMessage helpMessage) {
        SimpleMailMessage message = composeHelpMessage(helpMessage);
        javaMailSender.send(message);
    }

    private SimpleMailMessage composeHelpMessage(HelpMessage helpMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(mutualService.getOnlyAdminEmails());
        message.setSubject(helpMessage.getSubject());
        message.setText("Help email from: " + helpMessage.getEmail() + "\n" + helpMessage.getDescription());
        return message;
    }
}
