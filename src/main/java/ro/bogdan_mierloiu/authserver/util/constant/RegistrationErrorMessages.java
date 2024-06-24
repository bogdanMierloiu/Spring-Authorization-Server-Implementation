package ro.bogdan_mierloiu.authserver.util.constant;

public final class RegistrationErrorMessages {
    public static final String ILLEGAL_OPERATION = "The code is invalid. Please try again.";

    public static final String CODE_EXPIRED = "The activation code is expired! A new code was sent to your email address!";

    public static final String CODE_USED = "The verifier code is already used";

    public static final String SAME_WITH_THE_CURRENT_ONE = "The new password must not be the same with the current one";

    public static final String INVALID_EMAIL = "Invalid email format.";

    public static final String USED_EMAIL = "Email already used. Try to reset your password!";

    public static final String WEAK_USER_PASSWORD = "The password must consist of 16 to 32 characters. " +
            "It should not begin or end with spaces.";

    public static final String WEAK_CLIENT_PASSWORD = "The password must consist of 8 to 32 characters.<br/>" +
            "It should not begin or end with spaces.";

    public static final String WRONG_PASSWORD = "The old password is not correct";

    public static final String PASSWORDS_DO_NOT_MATCH = "The passwords do not match";
    private RegistrationErrorMessages() {
    }
}
