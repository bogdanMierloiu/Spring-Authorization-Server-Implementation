package ro.bogdan_mierloiu.authserver.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.bogdan_mierloiu.authserver.dto.ResponseDto;
import ro.bogdan_mierloiu.authserver.dto.ServerUserRequest;
import ro.bogdan_mierloiu.authserver.service.ServerUserService;

@RestController
@RequestMapping("/api/v1/register-user")
@Validated
@OpenAPIDefinition(info = @Info(title = "Authorization Server API", version = "v1"))
@RequiredArgsConstructor
public class RegisterController {

    private final ServerUserService serverUserService;

    @Operation(summary = "Add user", description = "This endpoint is used to create a new user. " +
            "The details of the user to be created must be passed in the request body.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request due to validation error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))}),
            @ApiResponse(responseCode = "500", description = "Server error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class))})})
    @PostMapping("/user")
    public ResponseEntity<ResponseDto> registerUser(@RequestBody @Valid @Parameter(description = "User's details")
                                                    ServerUserRequest serverUserRequest, HttpServletRequest request) {
        serverUserService.saveUser(serverUserRequest, request);
        return ResponseEntity.ok().body(new ResponseDto("User successfully created."));
    }


}
