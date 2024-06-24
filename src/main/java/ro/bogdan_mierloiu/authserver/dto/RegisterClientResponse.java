package ro.bogdan_mierloiu.authserver.dto;

import lombok.Builder;

import java.util.Set;

@Builder
public record RegisterClientResponse(

        String id,
        String clientName,
        String clientId,
        Set<String> redirectUris,
        TokenSettingsResponse tokenSettings,
        Set<String> clientAuthenticationMethods,
        Set<String> authorizationGrantTypes,
        Set<String> scopes


) {
}
