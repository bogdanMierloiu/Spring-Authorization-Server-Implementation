package ro.bogdan_mierloiu.authserver.util.mapper;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import ro.bogdan_mierloiu.authserver.config.AuthorizationServerConfig;
import ro.bogdan_mierloiu.authserver.dto.RegisterClientRequest;
import ro.bogdan_mierloiu.authserver.dto.RegisterClientResponse;
import ro.bogdan_mierloiu.authserver.dto.TokenSettingsResponse;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RegisterClientMapper {

    private RegisterClientMapper() {
    }

    public static RegisteredClient requestToObject(RegisterClientRequest request, BCryptPasswordEncoder encoder) {
        return RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientIdIssuedAt(Instant.now())
                .clientSecretExpiresAt(Instant.now().plusSeconds(60 * 60 * 24 * 365L)) // 1 year
                .clientName(request.getClientName().trim())
                .clientId(request.getClientId().trim())
                .clientSecret(encoder.encode(request.getClientSecret().trim()))
                .scopes(scopes -> scopes.addAll(Set.of(request.getScope(), OidcScopes.OPENID)))
                .redirectUris(uris -> uris.addAll(request.getRedirectURIs()))
                .tokenSettings(AuthorizationServerConfig.defaultTokenSettings())   // method to use settings from request if provided
                .clientAuthenticationMethods(methods -> methods.addAll(
                        Set.of(ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                                ClientAuthenticationMethod.CLIENT_SECRET_POST)))
                .authorizationGrantTypes(authGrantTypes -> authGrantTypes.addAll(
                        Set.of(AuthorizationGrantType.AUTHORIZATION_CODE,
                                AuthorizationGrantType.REFRESH_TOKEN)))
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(false)
                        .requireAuthorizationConsent(false)
                        .build())
                .build();
    }


    public static RegisterClientResponse objectToResponse(RegisteredClient client) {
        return RegisterClientResponse.builder()
                .id(client.getId())
                .clientName(client.getClientName())
                .clientId(client.getClientId())
                .redirectUris(client.getRedirectUris())
                .tokenSettings(mapTokenSettingsDto(client.getTokenSettings()))
                .clientAuthenticationMethods(client.getClientAuthenticationMethods().stream()
                        .map(ClientAuthenticationMethod::getValue)
                        .collect(Collectors.toSet()))
                .authorizationGrantTypes(client.getAuthorizationGrantTypes().stream()
                        .map(AuthorizationGrantType::getValue)
                        .collect(Collectors.toSet()))
                .scopes(client.getScopes())
                .build();
    }

    private static TokenSettingsResponse mapTokenSettingsDto(TokenSettings tokenSettings) {
        return TokenSettingsResponse.builder()
                .accessTokenTimeToLive(tokenSettings.getAccessTokenTimeToLive().toMinutes() + " minutes")
                .refreshTokenTimeToLive(tokenSettings.getRefreshTokenTimeToLive().toMinutes() + " minutes")
                .build();
    }
}
