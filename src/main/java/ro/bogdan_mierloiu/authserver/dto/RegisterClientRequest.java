package ro.bogdan_mierloiu.authserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterClientRequest {

    @NotBlank(message = "Client name can't be null or blank")
    @Size(min = 5, max = 50, message = "Client name must be between 5-50 characters")
    @Schema(description = "The name of the application or client")
    String clientName;

    @NotBlank(message = "Client id can't be null or blank")
    @Size(min = 5, max = 50, message = "Client ID must be between 5-50 characters")
    @Schema(description = "The unique identifier for the client used to obtain an access token")
    String clientId;

    @NotBlank(message = "Client secret cannot be empty")
    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
    @Schema(
            description = "The secret password for the client",
            minLength = 8,
            maxLength = 32
    )
    String clientSecret;

    @NotBlank(message = "Confirmed client secret cannot be empty")
    @Schema(
            description = "Confirmed client secret (should match the clientSecret field)"
    )
    String clientSecretConfirmed;

    @NotBlank(message = "Scope can't be empty")
    @Schema(description = "The scope defines the level of access requested by the client. " +
            "This field can include 'profile' to request all available user information " +
            "or 'email' to request only the user's email address.")
    String scope;

    TokenSettingsRequest tokenSettings;

    @NotEmpty
    @Schema(description = "Set of allowed redirect URIs for the client")
    Set<String> redirectURIs;
}
