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
            logger.error("Cannot create problem type", ex);
        }
        return problemType;
    }

    private ProblemDetail forTypeStatusTitleAndDetail(String exceptionClassName, HttpStatus status, String title, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);

        URI problemType = createProblemType(exceptionClassName);
        if (problemType != null) {
            problemDetail.setType(problemType);
        }
        problemDetail.setTitle(title);

        return problemDetail;
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
        logger.error("Authentication failed because user submitted bad credentials: {}", ex.toString());
        return forTypeStatusTitleAndDetail(ex.getClass().getName(), HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "You are submitted bad credentials");
    }

    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleSignatureException(Exception ex) {
        logger.error("JWT signature is invalid: {}", ex.toString());

        return forTypeStatusTitleAndDetail(ex.getClass().getName(), HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Cannot verify JWT signature");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ProblemDetail handleAccessDeniedException(Exception ex) {
        logger.error("Client cannot access this resource: {}", ex.toString());

        return forTypeStatusTitleAndDetail(ex.getClass().getName(), HttpStatus.FORBIDDEN, "FORBIDDEN", "You do not have authorities to access this resource");
    }

    @ExceptionHandler(value = {
        ResourceNotFoundException.class,
        UsernameNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleResourceNotFoundExceptions(Exception ex) {
        logger.error("Some resource not found: {}", ex.toString());

        return forTypeStatusTitleAndDetail(ex.getClass().getName(), HttpStatus.NOT_FOUND, "RESOURCE NOT FOUND", "Resource not found");
    }

    @ExceptionHandler(value = {
        IllegalStateException.class,
        RuntimeException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleRuntimeExceptions(Exception ex) {
        logger.error("Client sent bad request: {}", ex.toString());

        return forTypeStatusTitleAndDetail(ex.getClass().getName(), HttpStatus.BAD_REQUEST, "BAD REQUEST", "Your request contains invalid data. Please change data and try again");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleException(Exception ex) {
        logger.error("Unhandled exception: {}", ex.toString());

        return forTypeStatusTitleAndDetail(ex.getClass().getName(), HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR", "Unknown internal server error. The server is not in a state to carry out the client’s request");
    }
}
