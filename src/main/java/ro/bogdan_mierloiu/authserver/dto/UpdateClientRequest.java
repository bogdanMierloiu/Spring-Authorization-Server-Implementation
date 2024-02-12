package ro.bogdan_mierloiu.authserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request object for updating a client")
public class UpdateClientRequest {

    @Size(min = 5, max = 50, message = "Client name must be between 5-50 characters")
    @Schema(description = "The name of the application or client")
    String clientName;


//    @NotBlank(message = "Client secret cannot be empty")
//    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters")
//    @Schema(
//            description = "The secret password for the client",
//            minLength = 8,
//            maxLength = 32
//    )
//    String clientSecret;
//
//    @NotBlank(message = "Confirmed client secret cannot be empty")
//    @Schema(
//            description = "Confirmed client secret (should match the clientSecret field)"
//    )
//    String clientSecretConfirmed;

    @Schema(description = "The scope defines the level of access requested by the client. " +
            "This field can include 'profile' to request all available user information " +
            "or 'email' to request only the user's email address.")
    String scope;

    @Schema(description = "The token settings for the client")
    TokenSettingsRequest tokenSettings;

    @Schema(description = "Set of allowed redirect URIs for the client")
    Set<String> redirectURIs;
}
