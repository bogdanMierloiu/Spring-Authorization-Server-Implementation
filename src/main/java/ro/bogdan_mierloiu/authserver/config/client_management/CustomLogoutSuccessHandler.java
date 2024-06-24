package ro.bogdan_mierloiu.authserver.config.client_management;


import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ro.bogdan_mierloiu.authserver.config.CustomEncoder;
import ro.bogdan_mierloiu.authserver.entity.ClientInternal;
import ro.bogdan_mierloiu.authserver.entity.ServerUser;
import ro.bogdan_mierloiu.authserver.entity.UserToken;
import ro.bogdan_mierloiu.authserver.repository.UserTokenRepository;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final UserTokenRepository userTokenRepository;

    private final CustomEncoder customEncoder;

    private final ClientServiceInternal clientService;

    @Value("${issueruri}")
    private String issuerUri;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        ServerUser userPrincipal = (ServerUser) authentication.getPrincipal();
        List<UserToken> userTokens = userTokenRepository.findAllByUserEmail(userPrincipal.getEmail());

        List<ClientTokenMap> clientTokenMapsToRevoke = getClientTokenMapsToRevoke(userTokens);

        revokeTokensForAllClients(clientTokenMapsToRevoke);
        userTokenRepository.deleteAll(userTokens);

        if (validateRequestParameter(request.getParameter("redirect_uri"))){
            String redirectBackInApp = request.getParameter("redirect_uri");
            response.sendRedirect(redirectBackInApp);
        } else {
            response.sendRedirect("/login?logout");
        }
    }

    private List<ClientTokenMap> getClientTokenMapsToRevoke(List<UserToken> userTokens) {
        List<ClientTokenMap> clientTokenMaps = new ArrayList<>();

        List<ClientInternal> clients = clientService.getAll();
        clients.forEach(client -> userTokens.forEach(userToken -> {
            String clientFromToken = getClientFromToken(userToken.getToken());
            if (client.getClientId().equals(clientFromToken)) {
                ClientTokenMap clientTokenMap = ClientTokenMap.builder()
                        .clientId(client.getClientId())
                        .clientSecret(client.getClientSecret())
                        .token(userToken.getToken())
                        .build();
                clientTokenMaps.add(clientTokenMap);
            }
        }));
        return clientTokenMaps;
    }

    private void revokeTokensForAllClients(List<ClientTokenMap> clientTokenMaps) {
        clientTokenMaps.forEach(this::revokeTokenFromClient);
    }

    private void revokeTokenFromClient(ClientTokenMap clientTokenMap) {
        RestTemplate restTemplate = new RestTemplateBuilder().build();

        String clientId = clientTokenMap.clientId();
        String clientSecret = clientTokenMap.clientSecret();
        String credentials = clientId + ":" + clientSecret;
        String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Basic " + base64Credentials);

        MultiValueMap<String, String> parametersMap = new LinkedMultiValueMap<>();
        parametersMap.add("token", customEncoder.decrypt(clientTokenMap.token()));

        HttpEntity<MultiValueMap<String, String>> postRequest = new HttpEntity<>(parametersMap, headers);

        ResponseEntity<String> exchange = restTemplate.exchange(issuerUri + "/oauth2/revoke",
                HttpMethod.POST, postRequest, String.class);

        if (exchange.getStatusCode() != HttpStatus.OK) {
            log.warn("Status code: " + exchange.getStatusCode() + " - " + exchange.getBody());
        }
    }

    private static class ParseOnlyJWTProcessor extends DefaultJWTProcessor<SecurityContext> {
        @Override
        public JWTClaimsSet process(SignedJWT jwt, SecurityContext context) {
            try {
                return jwt.getJWTClaimsSet();
            } catch (ParseException e) {
                throw new SecurityException("There was an error parsing the JWT");
            }
        }
    }

    private String getClientFromToken(String token) {
        try {
            JwtDecoder jwtDecoder = new NimbusJwtDecoder(new ParseOnlyJWTProcessor());
            String decryptedToken = customEncoder.decrypt(token);
            Jwt jwt = jwtDecoder.decode(decryptedToken);
            return jwt.getClaimAsString("aud").replace("[", "").replace("]", "");
        } catch (Exception e) {
            log.error("Error getting client from token: " + e.getMessage());
            return "";
        }
    }

    private boolean validateRequestParameter(String parameter) {
        return parameter != null && !parameter.isEmpty();
    }

}