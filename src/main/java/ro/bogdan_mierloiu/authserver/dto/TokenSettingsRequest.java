package ro.bogdan_mierloiu.authserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request object for token settings")
public class TokenSettingsRequest {
    @Schema(description = "The time to live for access tokens in minutes")
    @Positive
    Integer accessTokenTimeToLive;

    @Schema(description = "The time to live for refresh tokens in minutes")
    @Positive
    Integer refreshTokenTimeToLive;
}