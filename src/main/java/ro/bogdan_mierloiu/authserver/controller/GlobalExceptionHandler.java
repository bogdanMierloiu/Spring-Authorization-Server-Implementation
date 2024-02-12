package ro.bogdan_mierloiu.authserver.controller;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ro.bogdan_mierloiu.authserver.dto.ResponseDto;
import ro.bogdan_mierloiu.authserver.exception.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_ATTRIBUTE = "errorMessage";
    private static final String ERROR_PAGE = "redirect:/error";
    private static final String PROBLEM_MESSAGE = "There was a problem: ";

    @ExceptionHandler(OAuth2AuthenticationException.class)
    public ResponseEntity<ResponseDto> handleOAuth2AuthenticationException(OAuth2AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseDto(PROBLEM_MESSAGE + ex.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseDto> handleConstraintViolationException(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDto(PROBLEM_MESSAGE + ex.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseDto> handleMissingServletRequestParamException(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDto(PROBLEM_MESSAGE + ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDto(PROBLEM_MESSAGE + ex.getMessage()));
    }

    @ExceptionHandler(IpBlockedException.class)
    public ResponseEntity<ResponseDto> handleIpBlockedException(IpBlockedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDto(PROBLEM_MESSAGE + ex.getMessage()));
    }

    @ExceptionHandler(BadClientNameException.class)
    public ResponseEntity<ResponseDto> handleBadClientNameException(BadClientNameException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDto(PROBLEM_MESSAGE + ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto(PROBLEM_MESSAGE + ex.getMessage()));
    }

    @ExceptionHandler(PasswordNotMatchException.class)
    public ResponseEntity<ResponseDto> handlePasswordNotMatchExceptionException(PasswordNotMatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDto(PROBLEM_MESSAGE + ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseDto> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDto(PROBLEM_MESSAGE + ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseDto> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        int startIndex = ex.toString().indexOf("Detail");
        int endIndex = ex.toString().indexOf("]");

        startIndex = Math.max(0, startIndex); // Ensure non-negative start index
        if (endIndex == -1) {
            endIndex = ex.toString().length() - 1; // Ensure non-negative end index
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDto(PROBLEM_MESSAGE + ex.toString().substring(startIndex, endIndex)));
    }


    @ExceptionHandler(IllegalAccessException.class)
    public String handleIllegalOperationException(IllegalAccessException ex,
                                                  RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, ex.getMessage());
        return ERROR_PAGE;
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public String handleIllegalOperationException(InternalAuthenticationServiceException ex,
                                                  RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, ex.getMessage());
        return ERROR_PAGE;
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public String handleUserNotFoundException(UsernameNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, ex.getMessage());
        return ERROR_PAGE;
    }

    @ExceptionHandler(CodeExpiredException.class)
    public String handleCodeExpiredException(CodeExpiredException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, ex.getMessage());
        return ERROR_PAGE;
    }

    @ExceptionHandler(CodeUsedException.class)
    public String handleCodeExpiredException(CodeUsedException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(ERROR_ATTRIBUTE, ex.getMessage());
        return ERROR_PAGE;
    }


}
