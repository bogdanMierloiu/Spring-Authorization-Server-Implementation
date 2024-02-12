package ro.bogdan_mierloiu.authserver.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ro.bogdan_mierloiu.authserver.dto.RegisterClientRequest;
import ro.bogdan_mierloiu.authserver.dto.UpdateClientRequest;
import ro.bogdan_mierloiu.authserver.dto.UsersMessage;
import ro.bogdan_mierloiu.authserver.service.ClientService;
import ro.bogdan_mierloiu.authserver.util.constant.RegistrationMessages;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class WebAdminController {

    private final ClientService clientService;
    private static final String MESSAGE_PAGE = "redirect:/redirect-message";
    private static final String MESSAGE_ATTRIBUTE = "message";

    @GetMapping
    public String clientConsole() {
        return "admin-console";
    }

    // save client
    @GetMapping("/register-client-form")
    public String registerClientForm(Model model) {
        model.addAttribute("clientRequest", new RegisterClientRequest());
        return "register-client";
    }

    @PostMapping("/register-client")
    public String registerClient(@ModelAttribute RegisterClientRequest request,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        try {
            request.setRedirectURIs(request.getRedirectURIs().contains("") ? removeEmptyRedirectURIs(request.getRedirectURIs()) : request.getRedirectURIs());
            clientService.save(request);
            redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, RegistrationMessages.CLIENT_REGISTERED_MESSAGE);
            return MESSAGE_PAGE;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("clientRequest", request);
            return "register-client";
        }
    }

    public Set<String> removeEmptyRedirectURIs(Set<String> redirectURIs) {
        return redirectURIs.stream()
                .filter(uri -> !uri.isEmpty())
                .collect(Collectors.toSet());
    }


    @GetMapping("/clients")
    public String viewClients(Model model) {
        model.addAttribute("clients", clientService.getAll());
        return "clients";
    }

    // update client
    @GetMapping("/update-client-form/{clientId}")
    public String updateClient(@PathVariable("clientId") String clientId, Model model) {
        model.addAttribute("clientToUpdate", clientService.getByClientId(clientId));
        model.addAttribute("updateClientRequest", new UpdateClientRequest());
        return "update-client";
    }

    @PostMapping("/update-client/{clientId}")
    public String updateClient(
            @PathVariable("clientId") String clientId,
            @ModelAttribute UpdateClientRequest updateClientRequest,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            clientService.update(clientId, updateClientRequest);
            redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, RegistrationMessages.CLIENT_UPDATED_MESSAGE);
            return MESSAGE_PAGE;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("updateClientRequest", updateClientRequest);
            model.addAttribute("clientToUpdate", clientService.getByClientId(clientId));
            return "update-client";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") String id) {
        clientService.deleteByDatabaseID(id);
        return "redirect:/admin/clients";
    }

    //     Emails to All Users:
    @GetMapping("/mails-form")
    public String goToMailForm(Model model) {
        model.addAttribute("usersMessage", new UsersMessage());
        return "mails-form";
    }

    @PostMapping("/send-emails")
    public String sendEmails(@ModelAttribute UsersMessage usersMessage,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        try {
            clientService.sendEmailToAllUsers(usersMessage);
            redirectAttributes.addFlashAttribute(MESSAGE_ATTRIBUTE, RegistrationMessages.SUCCESSFULLY_EMAIL_SEND);
            return MESSAGE_PAGE;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getLocalizedMessage());
            model.addAttribute("usersMessage", usersMessage);
            return "mails-form";
        }
    }
}
