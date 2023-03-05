package az.nadir.springsecurity.exception;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
public class ErrorHandler extends DefaultErrorAttributes {

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handle(UsernameNotFoundException exception, WebRequest request) {
        String message = String.format("Unexpected Exception exception: %s, file: %s, line: %s",exception.getMessage(), exception.getStackTrace()[0].getFileName(), exception.getStackTrace()[0].getLineNumber());
        return this.ofType(request, HttpStatus.UNAUTHORIZED, message);
    }

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<Map<String, Object>> handle(BadCredentialsException exception, WebRequest request) {
        String message = String.format("Unexpected Exception exception: %s, file: %s, line: %s",exception.getMessage(), exception.getStackTrace()[0].getFileName(), exception.getStackTrace()[0].getLineNumber());
        return this.ofType(request, HttpStatus.UNAUTHORIZED, message);
    }

    protected ResponseEntity<Map<String, Object>> ofType(WebRequest request, HttpStatus status, String message) {
        Map<String, Object> attributes = this.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        attributes.put("status", status.value());
        attributes.put("error", status.getReasonPhrase());
        attributes.put("message", message);
        attributes.put("path", ((ServletWebRequest)request).getRequest().getRequestURI());
        log.error(attributes.toString());
        return new ResponseEntity<>(attributes, status);
    }
}
