package ro.bogdan_mierloiu.authserver.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import ro.bogdan_mierloiu.authserver.config.client_management.CustomLogoutSuccessHandler;
import ro.bogdan_mierloiu.authserver.entity.ServerUser;
import ro.bogdan_mierloiu.authserver.repository.UserTokenRepository;
import ro.bogdan_mierloiu.authserver.service.SessionService;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private static final String LOGIN_URL = "/login";
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String EMAIL = "email";

    @Bean
    @Order(1)
    SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http,
            UserTokenRepository userTokenRepository,
            CustomEncoder customEncoder,
            SessionService sessionService,
            CustomAuthenticationFailureHandler errorResponseHandler) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults())
                .tokenEndpoint(
                        oAuth2TokenEndpointConfigurer -> oAuth2TokenEndpointConfigurer
                                .accessTokenResponseHandler(authenticationSuccessHandler(userTokenRepository, customEncoder))
                                .errorResponseHandler(errorResponseHandler));
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOriginPatterns(Collections.singletonList("*"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setExposedHeaders(List.of("Authorization"));
            config.setMaxAge(3600L);
            return config;
        }));
        return http
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(
                                new LoginUrlAuthenticationEntryPoint(LOGIN_URL))
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http,
            CustomLogoutSuccessHandler customLogoutSuccessHandler) throws Exception {
        HeaderWriterLogoutHandler clearSiteData = new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.ALL));
        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(Collections.singletonList("*"));
                    config.setAllowedMethods(Collections.singletonList("*"));
                    config.setAllowCredentials(true);
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setExposedHeaders(List.of("Authorization"));
                    config.setMaxAge(3600L);
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(patternPermitAll()).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/users/**")).hasRole(ADMIN_ROLE)
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/admin/**")).hasRole(ADMIN_ROLE)
                        .requestMatchers(new AntPathRequestMatcher("/api/v1/client/**")).hasRole(ADMIN_ROLE)
                        .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole(ADMIN_ROLE)
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(sessionManagement -> sessionManagement
                        .maximumSessions(10)
                        .sessionRegistry(sessionRegistry()))
                .formLogin(formLogin -> formLogin
                        .loginPage(LOGIN_URL).permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout").permitAll()
                        .addLogoutHandler(clearSiteData)
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(
                                new LoginUrlAuthenticationEntryPoint(LOGIN_URL)));
        return http.build();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer(HttpServletRequest request) {
        return context -> {
            if (context.getTokenType() == OAuth2TokenType.ACCESS_TOKEN) {
                Authentication principal = context.getPrincipal();
                RegisteredClient registeredClient = context.getRegisteredClient();
                Set<String> scopes = registeredClient.getScopes();
                Set<String> authorities = principal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet());
                if (scopes.contains("profile")) {
                    context.getClaims().claim(EMAIL, ((ServerUser) principal.getPrincipal()).getUsername());
                    context.getClaims().claim("name", ((ServerUser) principal.getPrincipal()).getName());
                    context.getClaims().claim("surname", ((ServerUser) principal.getPrincipal()).getSurname());
                    context.getClaims().claim("roles", authorities);
                    context.getClaims().claim("account-enabled", ((ServerUser) principal.getPrincipal()).isEnabled());
                } else if (scopes.contains(EMAIL)) {
                    context.getClaims().claim(EMAIL, ((ServerUser) principal.getPrincipal()).getUsername());
                }
            }
        };
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomEncoder tokenEncoder(@Value("${key}") String key) {
        return new CustomEncoder(key);
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(UserTokenRepository userTokenRepository, CustomEncoder customEncoder) {
        return new CustomAuthenticationSuccessHandler(userTokenRepository, customEncoder);
    }

    AntPathRequestMatcher[] patternPermitAll() {
        AntPathRequestMatcher apiDocs = new AntPathRequestMatcher("/v1/api-docs/**");
        AntPathRequestMatcher swagger = new AntPathRequestMatcher("/swagger-ui/**");
        AntPathRequestMatcher actuator = new AntPathRequestMatcher("/actuator/**");
        AntPathRequestMatcher register = new AntPathRequestMatcher("/api/v1/register-user/**");
        AntPathRequestMatcher signup = new AntPathRequestMatcher("/signup/**");
        AntPathRequestMatcher loginPage = new AntPathRequestMatcher("/login/**");
        AntPathRequestMatcher verify = new AntPathRequestMatcher("/verify/**");
        AntPathRequestMatcher reset = new AntPathRequestMatcher("/reset/**");
        AntPathRequestMatcher change = new AntPathRequestMatcher("/change/**");
        AntPathRequestMatcher account = new AntPathRequestMatcher("/api/v1/account/**");
        AntPathRequestMatcher help = new AntPathRequestMatcher("/help/**");
        AntPathRequestMatcher img = new AntPathRequestMatcher("/img/**");
        AntPathRequestMatcher script = new AntPathRequestMatcher("/js/**");
        AntPathRequestMatcher style = new AntPathRequestMatcher("/style/**");
        AntPathRequestMatcher logout = new AntPathRequestMatcher("/logout/**");
        AntPathRequestMatcher messagePage = new AntPathRequestMatcher("/redirect-message");
        AntPathRequestMatcher deleteLoginAttempts = new AntPathRequestMatcher("/ip/**");
        return new AntPathRequestMatcher[]{swagger, apiDocs, actuator, register, signup, loginPage,
                verify, reset, change, account, help, img, style, logout, script, deleteLoginAttempts, messagePage};
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}