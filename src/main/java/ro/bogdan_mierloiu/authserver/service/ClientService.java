package ro.bogdan_mierloiu.authserver.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.bogdan_mierloiu.authserver.config.CustomEncoder;
import ro.bogdan_mierloiu.authserver.dto.*;
import ro.bogdan_mierloiu.authserver.entity.ClientInternal;
import ro.bogdan_mierloiu.authserver.exception.*;
import ro.bogdan_mierloiu.authserver.repository.ClientInternalRepository;
import ro.bogdan_mierloiu.authserver.util.constant.RegistrationErrorMessages;
import ro.bogdan_mierloiu.authserver.util.mapper.RegisterClientMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final CustomEncoder customEncoder;

    private final DataSource dataSource;

    private final JdbcTemplate jdbcTemplate;

    private final EmailSender emailSender;

    private final BCryptPasswordEncoder encoder;

    private final ClientInternalRepository clientInternalRepository;

    @Transactional
    public RegisterClientResponse save(RegisterClientRequest request) {
        validateRegistration(request);

        RegisteredClient registeredClientToSave = RegisterClientMapper.requestToObject(request, encoder);

        JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);

        registeredClientRepository.save(registeredClientToSave);
        clientInternalRepository.save(ClientInternal.builder()
                .clientId(request.getClientId())
                .clientSecret(customEncoder.encrypt(request.getClientSecret()))
                .build());

        return RegisterClientMapper.objectToResponse(
                Objects.requireNonNull(getByClientId(registeredClientToSave.getClientId()))
        );
    }

    public RegisterClientResponse getResponseByClientId(String clientId) {
        return RegisterClientMapper.objectToResponse(getByClientId(clientId));
    }

    public RegisterClientResponse update(String clientId, UpdateClientRequest request) {
        RegisteredClient registeredClient = getByClientId(clientId);
        if (request.getClientName() != null && !request.getClientName().equals(registeredClient.getClientName())) {
            validateClientNameAndID(request.getClientName(), clientId);
        }
        RegisteredClient registeredClientUpdated = RegisteredClient.withId(registeredClient.getId())
                .clientName(isNull(request.getClientName())
                        ? registeredClient.getClientName()
                        : request.getClientName())
                .clientId(registeredClient.getClientId())
                .clientSecret(registeredClient.getClientSecret())
                .scopes(scopes -> scopes.addAll(registeredClient.getScopes())) //validate new scopes provided
                .redirectUris(redirectUris -> redirectUris.addAll(registeredClient.getRedirectUris())) //validate new redirect URIs provided
                .tokenSettings(buildTokenSettingsToUpdate(request, registeredClient.getTokenSettings()))
                .clientAuthenticationMethods(methods -> methods.addAll(
                        Set.of(ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                                ClientAuthenticationMethod.CLIENT_SECRET_POST)))
                .authorizationGrantTypes(authGrantTypes -> authGrantTypes.addAll(
                        Set.of(AuthorizationGrantType.AUTHORIZATION_CODE,
                                AuthorizationGrantType.REFRESH_TOKEN)
                ))
                .build();

        JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
        registeredClientRepository.save(registeredClientUpdated);

        return RegisterClientMapper.objectToResponse(
                Objects.requireNonNull(getByClientId(registeredClientUpdated.getClientId()))
        );
    }

    private TokenSettings buildTokenSettingsToUpdate(
            UpdateClientRequest request,
            TokenSettings curentTokenSettings) {
        return Optional.ofNullable(request.getTokenSettings()).map(tokenSettingsRequest -> {
            validateTokenSettings(tokenSettingsRequest);
            return TokenSettings.builder()
                    .accessTokenTimeToLive(isNull(request.getTokenSettings().getAccessTokenTimeToLive())
                            ? Duration.ofMinutes(60)
                            : Duration.ofMinutes(request.getTokenSettings().getAccessTokenTimeToLive()))
                    .refreshTokenTimeToLive(isNull(request.getTokenSettings().getRefreshTokenTimeToLive())
                            ? Duration.ofMinutes(480)
                            : Duration.ofMinutes(request.getTokenSettings().getRefreshTokenTimeToLive()))
                    .authorizationCodeTimeToLive(Duration.ofMinutes(2))
                    .deviceCodeTimeToLive(Duration.ofMinutes(2))
                    .build();
        }).orElse(curentTokenSettings);
    }


    public List<RegisterClientResponse> getAll() {
        List<RegisterClientResponse> registeredClientsFromDb = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM oauth2_registered_client");
            while (resultSet.next()) {
                RegisterClientResponse registerClientResponse = RegisterClientResponse.builder()
                        .id(resultSet.getString("id"))
                        .clientName(resultSet.getString("client_name"))
                        .clientId(resultSet.getString("client_id"))
                        .redirectUris(Collections.singleton(resultSet.getString("redirect_uris")))
                        .clientAuthenticationMethods(Collections.singleton(resultSet.getString("client_authentication_methods")))
                        .authorizationGrantTypes(Collections.singleton(resultSet.getString("authorization_grant_types")))
                        .scopes(Collections.singleton(resultSet.getString("scopes")))
                        .build();
                registeredClientsFromDb.add(registerClientResponse);
            }
            return registeredClientsFromDb;
        } catch (SQLException e) {
            throw new DbConnectionException("There was a problem with database connection: " + e.getMessage());
        }
    }


    public void deleteByDatabaseID(String id) {
        try (Connection connection = dataSource.getConnection()) {
            String checkQuery = "SELECT * FROM oauth2_registered_client WHERE id=?";
            String deleteQuery = "DELETE FROM oauth2_registered_client WHERE id=?";

            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setString(1, id);
                ResultSet resultSet = checkStatement.executeQuery();

                if (!resultSet.next()) {
                    throw new NotFoundException("ID not found: " + id);
                }

                try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                    deleteStatement.setString(1, id);
                    deleteStatement.executeUpdate();
                } catch (SQLException e) {
                    throw new DbConnectionException("Failed to execute delete operation: " + e.getMessage());
                }
            } catch (SQLException e) {
                throw new DbConnectionException("Failed to check for ID: " + e.getMessage());
            }
        } catch (SQLException e) {
            throw new DbConnectionException("There was a problem with the database connection: " + e.getMessage());
        }
    }

    public void sendEmailToAllUsers(@Valid UsersMessage usersMessage) {
        long startTime = System.currentTimeMillis();
        log.info("Thread current: " + Thread.currentThread().getName() + " start sending emails !");
        assert usersMessage != null;
        emailSender.sendEmailToAllUsers(usersMessage);
        long endTime = System.currentTimeMillis();
        log.info("Thread current: " + Thread.currentThread().getName() + " finished in: " + (endTime - startTime));
    }

    public RegisteredClient getByClientId(String clientId) {
        JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
        return Objects.requireNonNull(registeredClientRepository.findByClientId(clientId));
    }

    private void validateRegistration(RegisterClientRequest clientRequest) {
        validateClientNameAndID(clientRequest.getClientName(), clientRequest.getClientId());
        validatePassword(clientRequest.getClientSecret(), clientRequest.getClientSecretConfirmed());
        validateUniqueClient(clientRequest.getClientName(), clientRequest.getClientId());
        if (nonNull(clientRequest.getTokenSettings())) {
            validateTokenSettings(clientRequest.getTokenSettings());
        }
    }

    private void validateTokenSettings(TokenSettingsRequest tokenSettings) {
        if (tokenSettings.getAccessTokenTimeToLive() != null
                && tokenSettings.getRefreshTokenTimeToLive() != null
                && (tokenSettings.getAccessTokenTimeToLive() > tokenSettings.getRefreshTokenTimeToLive())) {
            throw new IllegalArgumentException(
                    "Token settings: We recommend the access token time to live to be less than the refresh token time to live");
        }
    }

    private void validateClientNameAndID(String clientName, String clientId) {
        Objects.requireNonNull(clientName, "Client name cannot be null");
        Objects.requireNonNull(clientId, "Client ID cannot be null");

        String clientDetailsPattern = "^[a-zA-Z0-9_-]{5,50}$";
        Pattern pattern = Pattern.compile(clientDetailsPattern);
        Matcher matcherForName = pattern.matcher(clientName);
        if (!matcherForName.matches()) {
            throw new BadClientNameException("Client name must be between 5 and 50 characters and can only contain letters and digits");
        }

        Matcher matcherForId = pattern.matcher(clientId);
        if (!matcherForId.matches()) {
            throw new BadClientIdException("Client ID must be between 5 and 50 characters and can only contain letters and digits");
        }
    }

    private void validateUniqueClient(String clientName, String clientId) {
        boolean clientNameExists = getAll().stream().anyMatch(c -> c.clientName().equals(clientName));
        if (clientNameExists) {
            throw new ClientAlreadyExistException("This client name is already in use. Please set a different one");
        }
        boolean clientIdExists = getAll().stream().anyMatch(c -> c.clientId().equals(clientId));
        if (clientIdExists) {
            throw new ClientAlreadyExistException("This client ID is already in use. Please set a different one");
        }
    }

    private void validatePassword(String password, String passwordConfirmed) {
        assert password != null;
        assert passwordConfirmed != null;
        if (!password.equals(passwordConfirmed)) {
            throw new PasswordNotMatchException(RegistrationErrorMessages.PASSWORDS_DO_NOT_MATCH);
        }
        String passwordPattern = "^\\S{8,32}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(passwordConfirmed);
        if (!matcher.matches()) {
            throw new PasswordFormatException(RegistrationErrorMessages.WEAK_CLIENT_PASSWORD);
        }
    }
}
