package ro.bogdan_mierloiu.authserver.config.client_management;

import lombok.Builder;

@Builder
public record ClientTokenMap(
        String clientId,
        String clientSecret,
        String token
) {
}
