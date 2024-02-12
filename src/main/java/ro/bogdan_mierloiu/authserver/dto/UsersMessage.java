package ro.bogdan_mierloiu.authserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersMessage {

        @NotBlank(message = "Subject")
        @Size(min = 4, max = 48, message = "Mail subject must be between 4-48 characters")
        @Schema(description = "Email subject")
        String subject;

        @NotBlank(message = "Text")
        @Size(min = 4, max = 480, message = "Mail text must be between 4-480 characters")
        @Schema(description = "Email text")
        String text;


}
