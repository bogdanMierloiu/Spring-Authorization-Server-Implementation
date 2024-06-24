package ro.bogdan_mierloiu.authserver.util.constant;

public final class RegistrationMessages {
    public static final String CONFIRMATION_SIGN_UP_MESSAGE = "A confirmation message has been sent to your e-mail." +
            " The code will expire in 15 minutes.";

    public static final String CLIENT_REGISTERED_MESSAGE = "Client registered successfully";
    public static final String CLIENT_UPDATED_MESSAGE = "Client updated successfully";
    public static final String SUCCESSFULLY_EMAIL_SEND = "Mails have been sent successfully!";
    public static final String RESET_PASSWORD_MESSAGE = "If the account with the provided email exists, " +
            "you will receive instructions to reset your password.";

    public static final String CHANGE_PASS_MESSAGE = "Password has been changed successfully.";

    public static final String REQUEST_ADMIN_SUCCESSFUL = "Your request has been sent to the admins team." +
            "<br> " +
            "Thank you for your patience.";

    public static final String HELP_MESSAGE_SEND = "Thank you for your message." +
            " Our support team will investigate your issue and will contact you in maximum 24 hours." +
            " You can close this page.";

    private RegistrationMessages() {
    }
}
