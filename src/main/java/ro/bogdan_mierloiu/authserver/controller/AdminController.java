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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ro.bogdan_mierloiu.authserver.dto.ResponseDto;
import ro.bogdan_mierloiu.authserver.dto.ServerUserRequest;
import ro.bogdan_mierloiu.authserver.entity.UserIp;
import ro.bogdan_mierloiu.authserver.service.AppRequestService;
import ro.bogdan_mierloiu.authserver.service.ServerUserService;
import ro.bogdan_mierloiu.authserver.service.SessionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@OpenAPIDefinition(info = @Info(title = "Authorization Server API", version = "v1"))
@Validated
@RequiredArgsConstructor
public class AdminController {

    private final ServerUserService serverUserService;
    private final AppRequestService appRequestService;
    private final SessionService sessionService;


    @Operation(summary = "Add admin user", description = "This endpoint is used to create a new admin user. " +
            "The details of the user to be created must be passed in the request body.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Admin user successfully created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request due to validation error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))})})
    @PostMapping
    public ResponseEntity<ResponseDto> register(@RequestBody @Valid ServerUserRequest serverUserRequest) {
        serverUserService.saveAdmin(serverUserRequest);
        return ResponseEntity.ok().body(new ResponseDto("Admin account successfully created"));
    }

    @Operation(summary = "Give admin rights to user", description = "This endpoint is used to give admin rights to user. " +
            "The user's email must be passed in request param.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User promoted to admin",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized for this action",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Resource does not exist",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))})})
    @PatchMapping()
    public ResponseEntity<ResponseDto> makeAdmin(@RequestParam @Parameter(description = "The user's email to be promoted to admin.")
                                                 String email) {
        serverUserService.makeAdmin(email);
        return ResponseEntity.ok().body(new ResponseDto("User promoted to admin"));
    }

    @Operation(summary = "Disable account", description = "This endpoint is used to disable account for user. " +
            "The user's email must be passed in request param.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User account disabled",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized for this action",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Resource does not exist",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))})})
    @PatchMapping("/disable-account")
    public ResponseEntity<ResponseDto> disableAccount(@RequestParam @Parameter(description = "The user's email to disable account")
                                                      String email) {
        serverUserService.disableAccount(email);
        return ResponseEntity.ok().body(new ResponseDto("Account disabled!"));
    }

    @Operation(summary = "Enable account", description = "This endpoint is used to enable account for user. " +
            "The user's email must be passed in request param.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User account enabled",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized for this action",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Resource does not exist",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))})})
    @PatchMapping("/enable-account")
    public ResponseEntity<ResponseDto> enableAccount(@RequestParam @Parameter(description = "The user's email to enable account")
                                                     String email) {
        serverUserService.enableAccount(email);
        return ResponseEntity.ok().body(new ResponseDto("Account enabled!"));
    }

    @Operation(summary = "Get all logging attempts",
            description = "This endpoint is used to retrieve all logging attempts details, registered in db.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of logging attempts",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserIp.class)))}),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))})})
    @GetMapping("/login-attempts")
    public ResponseEntity<List<UserIp>> getLoginAttempts() {
        return ResponseEntity.ok().body(appRequestService.getAllLoginAttempts());
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<String>> getActiveUsers() {
        return ResponseEntity.ok().body(sessionService.getActiveUsers());
    }
}
