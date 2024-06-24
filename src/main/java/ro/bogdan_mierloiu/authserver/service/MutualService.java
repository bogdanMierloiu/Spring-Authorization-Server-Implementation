package ro.bogdan_mierloiu.authserver.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.bogdan_mierloiu.authserver.dto.ServerUserResponse;
import ro.bogdan_mierloiu.authserver.entity.ServerUser;
import ro.bogdan_mierloiu.authserver.exception.NotFoundException;
import ro.bogdan_mierloiu.authserver.repository.ServerUserRepository;
import ro.bogdan_mierloiu.authserver.util.mapper.ServerUserMapper;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MutualService {

    private final ServerUserRepository serverUserRepository;

    public List<ServerUserResponse> getAll() {
        return serverUserRepository.findAll().stream()
                .map(ServerUserMapper::userToDto)
                .toList();
    }


    public String[] getUsersEmails() {
        List<String> userEmails = serverUserRepository.findAll().stream()
                .map(ServerUser::getEmail)
                .toList();
        return userEmails.toArray(new String[0]);
    }

    public String[] getOnlyAdminEmails() {
        List<String> adminEmails = serverUserRepository.findAll().stream()
                .filter(user -> user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN")))
                .map(ServerUser::getEmail)
                .toList();
        return adminEmails.toArray(new String[0]);
    }

    public ServerUser findUserById(Long id) {
        return serverUserRepository.findById(id).orElseThrow(
                () -> new NotFoundException("User not found!"));
    }

    public Optional<ServerUser> findUserOptionalById(Long id) {
        return serverUserRepository.findById(id);
    }
}
