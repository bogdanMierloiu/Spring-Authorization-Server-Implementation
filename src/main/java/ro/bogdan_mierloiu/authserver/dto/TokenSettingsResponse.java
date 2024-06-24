package ro.bogdan_mierloiu.authserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "Request object for token settings")
@Builder
public record TokenSettingsResponse(
        @Schema(description = "The time to live for access tokens in minutes")
        String accessTokenTimeToLive,
        @Schema(description = "The time to live for refresh tokens in minutes")
        String refreshTokenTimeToLive) {
}