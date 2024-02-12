package ro.bogdan_mierloiu.authserver.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ro.bogdan_mierloiu.authserver.dto.RegisterClientRequest;
import ro.bogdan_mierloiu.authserver.dto.RegisterClientResponse;
import ro.bogdan_mierloiu.authserver.dto.ResponseDto;
import ro.bogdan_mierloiu.authserver.dto.UpdateClientRequest;
import ro.bogdan_mierloiu.authserver.service.ClientService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/client")
@Validated
@OpenAPIDefinition(info = @Info(title = "Authorization Server API", version = "v1"))
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @Operation(summary = "Add client", description = "This endpoint is used to create a new client. " +
            "The details of the client to be created must be passed in the request body.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegisterClientResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request due to validation error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))})})
    @PostMapping
    public ResponseEntity<RegisterClientResponse> registerClient(
            @RequestBody
            @Valid
            @Parameter(description = "Client details")
            RegisterClientRequest clientRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.save(clientRequest));
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<RegisterClientResponse> update(
            @PathVariable("clientId") String clientId,
            @RequestBody UpdateClientRequest updateClientRequest) {
        return ResponseEntity.ok().body(clientService.update(clientId, updateClientRequest));
    }

    @GetMapping
    public ResponseEntity<List<RegisterClientResponse>> getAll() {
        return ResponseEntity.ok().body(clientService.getAll());
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<RegisterClientResponse> getByClientId(@PathVariable("clientId") String clientId) {
        return ResponseEntity.ok().body(clientService.getResponseByClientId(clientId));
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<String> delete(@PathVariable("clientId") String clientId) {
        clientService.deleteByDatabaseID(clientId);
        return ResponseEntity.ok().body("Client deleted successfully!");
    }

}
