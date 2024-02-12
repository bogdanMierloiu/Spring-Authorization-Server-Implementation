package ro.bogdan_mierloiu.authserver.service;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.bogdan_mierloiu.authserver.dto.ServerUserRequest;
import ro.bogdan_mierloiu.authserver.dto.ServerUserResponse;
import ro.bogdan_mierloiu.authserver.entity.Role;
import ro.bogdan_mierloiu.authserver.entity.ServerUser;
import ro.bogdan_mierloiu.authserver.entity.UserCode;
import ro.bogdan_mierloiu.authserver.exception.*;
import ro.bogdan_mierloiu.authserver.repository.RoleRepository;
import ro.bogdan_mierloiu.authserver.repository.ServerUserRepository;
import ro.bogdan_mierloiu.authserver.util.constant.RegistrationErrorMessages;
import ro.bogdan_mierloiu.authserver.util.constant.RegistrationMessages;
import ro.bogdan_mierloiu.authserver.util.mapper.ServerUserMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ServerUserService {

    private final ServerUserRepository serverUserRepository;
    private final UserCodeService userCodeService;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;
    private final EmailSender emailSender;
    private final AppRequestService appRequestService;
    private final RequestAdminService requestAdminService;
    private final MutualService mutualService;


    public ServerUserResponse findByEmail(String email) {
        ServerUser serverUser = serverUserRepository.findByEmail(email).orElseThrow();
        return ServerUserMapper.userToDto(serverUser);
    }

    public Optional<ServerUser> findByEmailOptional(String email) {
        return serverUserRepository.findByEmail(email);
    }


    public List<ServerUserResponse> findByEmailCharacters(String emailCharacters) {
        return serverUserRepository.findByEmailCharacters(emailCharacters)
                .stream()
                .map(ServerUserMapper::userToDto)
                .toList();
    }

    @Transactional
    public void saveUser(ServerUserRequest serverUserRequest, HttpServletRequest request) {
        //CHECK NR. OF REQUESTS
        appRequestService.checkRequest(request);
        //FIELD VALIDATION
        validateRegistration(serverUserRequest);

        saveUserAfterValidation(serverUserRequest);
    }


    public void saveUserAfterValidation(ServerUserRequest serverUserRequest) {

        // OBJECT CREATION
        ServerUser serverUserToSave = buildServerUser(serverUserRequest);
        serverUserToSave.getRoles().add(userRole());
        // DATABASE SAVE
        ServerUser userSaved = serverUserRepository.save(serverUserToSave);
        UserCode userCode = userCodeService.save(userSaved.getEmail());
        // SEND EMAIL
        emailSender.sendEmailForRegistration(userCode);
    }


    @Transactional
    public void makeAdmin(String email) {
        Role adminRole = roleRepository.findById(1L).orElseThrow();
        ServerUser user = serverUserRepository.findByEmail(email).orElseThrow();
        user.getRoles().add(adminRole);
        serverUserRepository.save(user);
    }

    @Transactional
    public void saveAdmin(ServerUserRequest request) {
        validateRegistration(request);
        ServerUser serverUserToSave = buildServerUser(request);
        serverUserToSave.getRoles().add(adminRole());
        serverUserRepository.save(serverUserToSave);

    }

    @Transactional
    public void changePassword(String userEmail, String oldPassword, String newPassword, String newPasswordConfirmed, HttpServletRequest request) {
        appRequestService.checkRequest(request);
        checkSamePassword(oldPassword, newPassword);
        validatePassword(newPassword, newPasswordConfirmed);
        changePasswordAfterValidation(userEmail, oldPassword, newPassword);
    }


    private void changePasswordAfterValidation(String userEmail, String oldPassword, String newPassword) {
        ServerUser serverUser = getUserByEmail(userEmail);

        if (encoder.matches(oldPassword.trim(), serverUser.getPassword())) {
            serverUser.setPassword(encoder.encode(newPassword));
            serverUserRepository.save(serverUser);
        } else {
            throw new PasswordNotMatchException(RegistrationErrorMessages.WRONG_PASSWORD);
        }
        emailSender.sendSuccessEmailForChangePassword(serverUser.getEmail());
    }

    @Transactional
    public void resetPassword(String userEmail, String password, String confirmedPassword, HttpServletRequest request) {
        appRequestService.checkRequest(request);
        validatePassword(password, confirmedPassword);
        resetPasswordAfterValidation(userEmail, password);
    }


    public void resetPasswordAfterValidation(String userEmail, String password) {
        ServerUser serverUser = getUserByEmail(userEmail);
        serverUser.setPassword(encoder.encode(password));
        emailSender.sendSuccessEmailForResetPassword(serverUser.getEmail());
        serverUserRepository.save(serverUser);
    }

    @Transactional
    public void sendEmailBeforeResetPassword(String userEmail, HttpServletRequest request) {
        appRequestService.checkRequest(request);
        Optional<ServerUser> serverUserOptional = getUserByEmailOptional(userEmail);
        if (serverUserOptional.isPresent()) {
            sendEmailAfterUserValidation(serverUserOptional.get());
        } else throw new UserNotFoundException(RegistrationMessages.RESET_PASSWORD_MESSAGE);
    }


    private void sendEmailAfterUserValidation(ServerUser serverUser) {
        UserCode userCode = userCodeService.save(serverUser.getEmail());
        emailSender.sendEmailForResetPassword(userCode);
    }


    @Transactional
    public void disableAccount(String email) {
        ServerUser user = serverUserRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(String.format("User with email %s was not found", email))
        );
        if (email.equals("bogdan.mierloiu01@gmail.com")) {
            throw new IllegalArgumentException("This account can't be disabled");
        }
        user.setEnabled(false);
        serverUserRepository.save(user);
    }

    @Transactional
    public void enableAccount(String email) {
        ServerUser user = serverUserRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(String.format("User with email %s was not found", email))
        );
        user.setEnabled(true);
        serverUserRepository.save(user);
    }

    @Transactional
    public void requestAdminRole(Long userId) {
        ServerUser user = mutualService.findUserById(userId);
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            throw new IllegalArgumentException("User already has admin role");
        }
        requestAdminService.save(user);
        emailSender.sendAdminRequestNotification(user.getEmail());
    }

    private ServerUser buildServerUser(ServerUserRequest request) {
        return ServerUser.builder()
                .email(request.getEmail().trim())
                .password(encoder.encode(request.getPassword().trim()))
                .name(request.getName().trim())
                .surname(request.getSurname().trim())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(false)
                .roles(new HashSet<>())
                .build();
    }

    private void validateRegistration(ServerUserRequest serverUserRequest) {
        validateFieldsLength(serverUserRequest.getName(), serverUserRequest.getSurname());
        validatePassword(serverUserRequest.getPassword(), serverUserRequest.getPasswordConfirmed());
        validateEmail(serverUserRequest.getEmail());
    }

    private void validateFieldsLength(String name, String surname) {
        if (name == null || name.isEmpty() || surname == null || surname.isEmpty()) {
            throw new IllegalArgumentException("Name and surname must not be null or empty");
        }

        if (name.length() < 3 || name.length() > 64 || surname.length() < 3 || surname.length() > 64) {
            throw new IllegalArgumentException("Name and surname must be between 3 and 64 characters");
        }
    }


    private void validateEmail(String email) {
        Optional<ServerUser> userFound = serverUserRepository.findByEmail(email);
        if (userFound.isPresent()) {
            throw new EmailExistsException(RegistrationErrorMessages.USED_EMAIL);
        }
    }

    private ServerUser getUserByEmail(String userEmail) {
        return serverUserRepository.findByEmail(userEmail.trim()).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with email: %s was not found!", userEmail))
        );
    }

    private Optional<ServerUser> getUserByEmailOptional(String userEmail) {
        return serverUserRepository.findByEmail(userEmail.trim());
    }

    private void checkSamePassword(String oldPassword, String newPassword) {
        if (oldPassword.equals(newPassword))
            throw new SamePasswordException(RegistrationErrorMessages.SAME_WITH_THE_CURRENT_ONE);
    }

    private void validatePassword(String password, String passwordConfirmed) {
        assert password != null;
        assert passwordConfirmed != null;
        if (!password.equals(passwordConfirmed)) {
            throw new PasswordNotMatchException(RegistrationErrorMessages.PASSWORDS_DO_NOT_MATCH);
        }
        String passwordPattern = "^\\S{16,32}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(passwordConfirmed);
        if (!matcher.matches()) {
            throw new PasswordFormatException(RegistrationErrorMessages.WEAK_USER_PASSWORD);
        }
    }

    private Role userRole() {
        return roleRepository.findById(2L).orElseThrow();
    }

    private Role adminRole() {
        return roleRepository.findById(1L).orElseThrow();
    }
}