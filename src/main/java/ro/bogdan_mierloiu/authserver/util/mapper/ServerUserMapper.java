package ro.bogdan_mierloiu.authserver.util.mapper;


import ro.bogdan_mierloiu.authserver.dto.ServerUserResponse;
import ro.bogdan_mierloiu.authserver.entity.Role;
import ro.bogdan_mierloiu.authserver.entity.ServerUser;

import java.util.Set;
import java.util.stream.Collectors;

public class ServerUserMapper {

    private ServerUserMapper() {
    }

    public static ServerUserResponse userToDto(ServerUser serverUser) {
        return ServerUserResponse.builder()
                .email(serverUser.getEmail())
                .name(serverUser.getName())
                .surname(serverUser.getSurname())
                .roles(convertRolesToSetOfStrings(serverUser.getRoles()))
                .accountEnabled(serverUser.isEnabled())
                .build();
    }

    private static Set<String> convertRolesToSetOfStrings(Set<Role> roles) {
        return roles.stream()
                .map(Role::getAuthority)
                .collect(Collectors.toSet());
    }
}
