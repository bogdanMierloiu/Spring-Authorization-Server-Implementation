package ro.bogdan_mierloiu.authserver.dto;

import lombok.Builder;

import java.util.Set;

@Builder
public record ServerUserResponse(String email,
                                 String name,
                                 String surname,
                                 Set<String> roles,
                                 boolean accountEnabled) {
}
