package ro.bogdan_mierloiu.authserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ServerUserRequest {
    @Schema(description = "The user's email (required)")
    @Email(message = "Invalid email")
    private String email;

    @Schema(description = "The user's password (required)")
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 16, max = 32, message = "Password must be between 16 and 32 characters")
    private String password;

    @Schema(description = "The user's confirmed password (required)")
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 16, max = 32, message = "Password must be between 16 and 32 characters")
    private String passwordConfirmed;

    @Schema(description = "The user's name (required)")
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 3, max = 64, message = "Name must be between 3 and 64 characters")
    private String name;

    @Schema(description = "The user's surname (required)")
    @NotBlank(message = "Surname cannot be empty")
    @Size(min = 3, max = 64, message = "Surname must be between 3 and 64 characters")
    private String surname;
}

