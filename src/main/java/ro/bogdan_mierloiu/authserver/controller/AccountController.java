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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.bogdan_mierloiu.authserver.dto.ResponseDto;
import ro.bogdan_mierloiu.authserver.service.ServerUserService;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@OpenAPIDefinition(info = @Info(title = "Authorization Server API", version = "v1"))
public class AccountController {

    private final ServerUserService serverUserService;

    @Operation(summary = "Change user's password", description = "This endpoint is used to change user's password. " +
            "The user's email, oldPassword, newPassword and newPasswordConfirmed must be passed in request params.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully changed",
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
    @PatchMapping("/change-password")
    public ResponseEntity<ResponseDto> changePassword(@RequestParam("email") @Parameter(description = "The email of the user for whom you want to change the password.")
                                                      String email,
                                                      @RequestParam("oldPassword") String oldPassword,
                                                      @RequestParam("newPassword") String newPassword,
                                                      @RequestParam("newPasswordConfirmed") String newPasswordConfirmed,
                                                      HttpServletRequest request) {
        serverUserService.changePassword(email, oldPassword, newPassword, newPasswordConfirmed, request);
        return ResponseEntity.ok(new ResponseDto("The password was successfully changed"));
    }
}
