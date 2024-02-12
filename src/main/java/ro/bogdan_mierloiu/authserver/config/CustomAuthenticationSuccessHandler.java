package ro.bogdan_mierloiu.authserver.config;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ro.bogdan_mierloiu.authserver.entity.UserToken;
import ro.bogdan_mierloiu.authserver.repository.UserTokenRepository;

import java.io.IOException;
import java.text.ParseException;
import java.time.temporal.ChronoUnit;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final HttpMessageConverter<OAuth2AccessTokenResponse> accessTokenHttpResponseConverter;
    private final UserTokenRepository userTokenRepository;
    private final CustomEncoder customEncoder;

    public CustomAuthenticationSuccessHandler(UserTokenRepository userTokenRepository, CustomEncoder customEncoder) {
        this.userTokenRepository = userTokenRepository;
        this.customEncoder = customEncoder;
        this.accessTokenHttpResponseConverter = new OAuth2AccessTokenResponseHttpMessageConverter();

    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        // get access and refresh tokens

        final OAuth2AccessTokenAuthenticationToken accessTokenAuthentication = (OAuth2AccessTokenAuthenticationToken) authentication;
        OAuth2AccessToken accessToken = accessTokenAuthentication.getAccessToken();
        OAuth2RefreshToken refreshToken = accessTokenAuthentication.getRefreshToken();
        OAuth2AccessTokenResponse.Builder tokenResponseBuilder = OAuth2AccessTokenResponse
                .withToken(accessToken.getTokenValue())
                .tokenType(accessToken.getTokenType())
                .scopes((accessToken.getScopes()));

        if (accessToken.getIssuedAt() != null && accessToken.getExpiresAt() != null) {
            tokenResponseBuilder.expiresIn(ChronoUnit.SECONDS.between(accessToken.getIssuedAt(), accessToken.getExpiresAt()));
        }
        if (refreshToken != null) {
            tokenResponseBuilder.refreshToken(refreshToken.getTokenValue());
        }

        // create the response

        OAuth2AccessTokenResponse tokenResponse = tokenResponseBuilder.build();

        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);

        // decode the token to get the user email

        JwtDecoder decoder = new NimbusJwtDecoder(new ParseOnlyJWTProcessor());

        Jwt jwt = decoder.decode(accessToken.getTokenValue());

        // encrypt and save the token

        UserToken userToken = UserToken.builder()
                .userEmail(jwt.getClaimAsString("email"))
                .token(customEncoder.encrypt(accessToken.getTokenValue()))
                .build();

        userTokenRepository.save(userToken);

        // send the response

        this.accessTokenHttpResponseConverter.write(tokenResponse, MediaType.APPLICATION_JSON, httpResponse);
    }

    // used to decode the token
    private static class ParseOnlyJWTProcessor extends DefaultJWTProcessor<SecurityContext> {
        @Override
        public JWTClaimsSet process(SignedJWT jwt, SecurityContext context) {
            try {
                return jwt.getJWTClaimsSet();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

