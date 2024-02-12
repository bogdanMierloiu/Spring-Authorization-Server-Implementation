package ro.bogdan_mierloiu.authserver.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.bogdan_mierloiu.authserver.dto.ResponseDto;
import ro.bogdan_mierloiu.authserver.dto.ServerUserResponse;
import ro.bogdan_mierloiu.authserver.service.ServerUserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@OpenAPIDefinition(info = @Info(title = "Authorization Server API", version = "v1"))
public class ServerUserController {

    private final ServerUserService serverUserService;

    public ServerUserController(ServerUserService serverUserService) {
        this.serverUserService = serverUserService;
    }

    /***
     * This endpoint is used to retrieve all users with ROLE USER
     * If the substring parameter is present, it will return all users with ROLE USER that contain the emailCharacters
     * in their email.
     */
    @Operation(summary = "Get all users with email containing chars",
            description = "This endpoint is used to retrieve all users with email that contains input chars.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ServerUserResponse.class)))}),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))})})
    @GetMapping
    public ResponseEntity<List<ServerUserResponse>> getAllByEmailCharacters(@RequestParam(name = "emailCharacters")
                                                                             @Parameter(description = "Chars from user's email") String emailCharacters) {
        return ResponseEntity.ok(serverUserService.findByEmailCharacters(emailCharacters));
    }

    @Operation(summary = "Get user with provided email", description = "This endpoint is used to retrieve a user with provided email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User with provided email",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ServerUserResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))})})
    @GetMapping("/{userEmail}")
    public ResponseEntity<ServerUserResponse> getUserByEmail(@PathVariable("userEmail") String userEmail) {
        return ResponseEntity.ok(serverUserService.findByEmail(userEmail));
    }
}
