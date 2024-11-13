package ru.manannikov.testspringsecurityfilterjwt.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String PROBLEM_TYPE_FORMAT = "tag:%s,%s";

    private URI createProblemType(String exceptionClassName) {
        URI problemType = null;
        try {
            problemType = new URI(String.format(PROBLEM_TYPE_FORMAT, exceptionClassName, Instant
                .now().toString()));
        } catch (URISyntaxException ex) {
            log.error("Cannot create problem type", ex);
        }
        return problemType;
    }

    @ExceptionHandler(value = {
        UnsupportedJwtException.class,
        ExpiredJwtException.class,
        MalformedJwtException.class,
        BadCredentialsException.class,
        AuthenticationException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleBadCredentialsException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problemDetail.setType(createProblemType(ex.getClass().getName()));
        problemDetail.setTitle("You are passed bad credentials");

        log.error("Bad credentials: {}", ex.toString());
        return problemDetail;
    }

    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleSignatureException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setType(createProblemType(ex.getClass().getName()));
        problemDetail.setTitle("Cannot verify JWT signature");
        problemDetail.setProperty("description", "he JWT signature is invalid");

        log.error("Token signature is invalid: {}", ex.toString());
        return problemDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleAccessDeniedException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setType(createProblemType(ex.getClass().getName()));
        problemDetail.setTitle("You are not authorized");
        problemDetail.setProperty("description", "You are not authorized to access this resource");

        log.error("User cannot access this resource: {}", ex.toString());
       return problemDetail;
    }

    @ExceptionHandler(value = {
        ResourceNotFoundException.class,
        UsernameNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleResourceNotFoundExceptions(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setType(createProblemType(ex.getClass().getName()));
        problemDetail.setTitle("Resource not found");

        log.error("Some resource not found: {}", ex.toString());
        return problemDetail;
    }

    @ExceptionHandler(value = {
        IllegalStateException.class,
        RuntimeException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleRuntimeExceptions(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setType(createProblemType(ex.getClass().getName()));
        problemDetail.setTitle("Your request contains invalid data");

        log.error("Bad request: {}", ex.toString());
        return problemDetail;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setType(createProblemType(ex.getClass().getName()));

        problemDetail.setProperty("description", "Unknown internal server error.");

        log.error("Unhandled exception: {}", ex.toString());
        return problemDetail;
    }
}
