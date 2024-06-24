package ro.bogdan_mierloiu.authserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ro.bogdan_mierloiu.authserver.dto.HelpMessage;
import ro.bogdan_mierloiu.authserver.dto.ServerUserRequest;
import ro.bogdan_mierloiu.authserver.entity.ServerUser;
import ro.bogdan_mierloiu.authserver.entity.UserCode;
import ro.bogdan_mierloiu.authserver.service.CodeVerifierService;
import ro.bogdan_mierloiu.authserver.service.HelpService;
import ro.bogdan_mierloiu.authserver.service.ServerUserService;
import ro.bogdan_mierloiu.authserver.util.constant.RegistrationMessages;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final CodeVerifierService codeVerifierService;
    private final ServerUserService serverUserService;
    private final HelpService helpService;
    private static final String MESSAGE_PAGE = "redirect:/redirect-message";
    private static final String MESSAGE_ATTRIBUTE = "message";
    private static final String ERROR_ATTRIBUTE = "errorMessage";

    @GetMapping("/login")
    public String login() {
        return "login-page";
    }

    @GetMapping("/error")
    public String handleError(@ModelAttribute(ERROR_ATTRIBUTE) String errorMessage, Model model) {
        model.addAttribute(ERROR_ATTRIBUTE, errorMessage);
        return "error-page";
    }

    @GetMapping("/redirect-message")
    public String redirectToMessagePage(
            @ModelAttribute(MESSAGE_ATTRIBUTE) String message,
            @ModelAttribute("user") ServerUser userSaved,
            Model model) {
        model.addAttribute(MESSAGE_ATTRIBUTE, message);
        model.addAttribute("user", userSaved);
        return "message-page";
    }

    // ----- SIGN UP ROUTES  ------//

    @GetMapping("/signup")
    public String signUp(Model model) {
        ServerUserRequest serverUserRequest = new ServerUserRequest();
        model.addAttribute("serverUserRequest", serverUserRequest);
        return "register";
    }

    @PostMapping("/signup/user")
    public String signUpUser(@ModelAttribute ServerUserRequest serverUserRequest,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             HttpServletRequest request) {
        try {
            serverUserService.saveUser(serverUserRequest, request);
            Optional<ServerUser> userSaved = serverUserService.findByEmailOptional(serverUserRequest.getEmail());
            redirectAttributes.addFlashAttribute("user", userSaved.orElse(null));
            redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, RegistrationMessages.CONFIRMATION_SIGN_UP_MESSAGE);
            return MESSAGE_PAGE;
        } catch (Exception e) {
            model.addAttribute(ERROR_ATTRIBUTE, e.getMessage());
            model.addAttribute("serverUserRequest", serverUserRequest);
            return "register";
        }
    }

    @GetMapping("/verify")
    public String verifyCodeForm() {
        return "verify-form";
    }

    @PostMapping("/verify")
    public String verifyCodeBeforeCreatingAccount(@RequestParam("code") String code, Model model) {
        try {
            codeVerifierService.activateAccount(code);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute(ERROR_ATTRIBUTE, e.getMessage());
            return "verify-form";
        }
    }

    // ----- RESET PASSWORD ROUTES  ------//

    @GetMapping("/reset/password")
    public String resetPassForm() {
        return "reset-pass-form-email";
    }

    @PostMapping("/reset/email")
    public String emailForm(@RequestParam("email") String email,
                            Model model,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {
        try {
            serverUserService.sendEmailBeforeResetPassword(email, request);
            redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, RegistrationMessages.RESET_PASSWORD_MESSAGE);
            return MESSAGE_PAGE;
        } catch (Exception e) {
            model.addAttribute(ERROR_ATTRIBUTE, e.getMessage());
            return "error-page";
        }
    }

    @GetMapping("/reset")
    public String newPasswordForm(@RequestParam("code") String code,
                                  Model model) throws IllegalAccessException {
        UserCode userCode = codeVerifierService.verifyResetPasswordCode(code);
        model.addAttribute("userCode", userCode);
        return "reset-pass-form-passwords";

    }

    @PostMapping("/reset/process")
    public String resetPass(@ModelAttribute("userCode") UserCode userCode,
                            @RequestParam("password") String password,
                            @RequestParam("passwordConfirmed") String passwordConfirmed,
                            HttpServletRequest request,
                            Model model) {
        try {
            serverUserService.resetPassword(userCode.getUserEmail(), password, passwordConfirmed, request);
            codeVerifierService.useCode(userCode.getCode());
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("userCode", userCode);
            model.addAttribute(ERROR_ATTRIBUTE, e.getMessage());
            return "reset-pass-form-passwords";
        }
    }

    // ----- CHANGE PASSWORD ROUTES  ------//

    @GetMapping("/change/password")
    public String changePassForm(Model model) {
        model.addAttribute("email", "");
        return "change-pass-form";
    }

    @PostMapping("/change/process")
    public String changePassword(@RequestParam("email") String email,
                                 @RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("newPasswordConfirmed") String newPasswordConfirmed,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request,
                                 Model model) {
        try {
            serverUserService.changePassword(email, oldPassword, newPassword, newPasswordConfirmed, request);
            Optional<ServerUser> userSaved = serverUserService.findByEmailOptional(email);
            redirectAttributes.addFlashAttribute("user", userSaved.orElse(null));
            redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, RegistrationMessages.CHANGE_PASS_MESSAGE);
            return MESSAGE_PAGE;
        } catch (Exception e) {
            model.addAttribute(ERROR_ATTRIBUTE, e.getMessage());
            model.addAttribute("email", email);
            return "change-pass-form";
        }
    }

    // ----- HELP ROUTES  ------//

    @GetMapping("/help")
    public String helpForm(Model model) {
        model.addAttribute("helpMessage", new HelpMessage());
        return "help";
    }

    @PostMapping("/help/submit")
    public String sendHelpMessage(@ModelAttribute HelpMessage helpMessage,
                                  Model model,
                                  RedirectAttributes redirectAttributes,
                                  HttpServletRequest request) {
        try {
            helpService.help(helpMessage, request);
            redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, RegistrationMessages.HELP_MESSAGE_SEND);
            return MESSAGE_PAGE;
        } catch (Exception e) {
            model.addAttribute(ERROR_ATTRIBUTE, e.getMessage());
            model.addAttribute("helpMessage", helpMessage);
            return "help";
        }
    }
}