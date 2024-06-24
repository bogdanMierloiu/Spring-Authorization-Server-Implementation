package ro.bogdan_mierloiu.authserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ro.bogdan_mierloiu.authserver.entity.ServerUser;
import ro.bogdan_mierloiu.authserver.service.MutualService;
import ro.bogdan_mierloiu.authserver.service.ServerUserService;
import ro.bogdan_mierloiu.authserver.util.constant.RegistrationMessages;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class WebUserController {

    private final ServerUserService serverUserService;
    private final MutualService mutualService;

    private static final String MESSAGE_PAGE = "redirect:/redirect-message";
    private static final String MESSAGE_ATTRIBUTE = "message";

    @GetMapping
    public String userConsole(Authentication authentication, Model model) {
        ServerUser user = (ServerUser) authentication.getPrincipal();
        model.addAttribute("user", user);
        return "user-console";
    }

    @GetMapping("/request-admin/{userId}")
    public String requestAdmin(@PathVariable("userId") Long userId,
                               RedirectAttributes redirectAttributes) {
        serverUserService.requestAdminRole(userId);
        Optional<ServerUser> userOptional = mutualService.findUserOptionalById(userId);
        redirectAttributes.addFlashAttribute("user", userOptional.orElse(null));
        redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, RegistrationMessages.REQUEST_ADMIN_SUCCESSFUL);
        return MESSAGE_PAGE;
    }
}