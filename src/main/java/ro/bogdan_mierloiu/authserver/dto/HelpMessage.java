package ro.bogdan_mierloiu.authserver.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HelpMessage {

    @NotBlank
    private String email;

    @NotBlank
    private String subject;

    @NotBlank
    private String description;
}
