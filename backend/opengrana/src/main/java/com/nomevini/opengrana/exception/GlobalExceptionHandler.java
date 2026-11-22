package com.nomevini.opengrana.exception;

import com.nomevini.opengrana.account.exception.AccountNotfoundException;
import com.nomevini.opengrana.account.exception.InvalidCurrencyException;
import com.nomevini.opengrana.account.exception.InvalidIfMatchException;
import com.nomevini.opengrana.account.exception.MissingIfMatchException;
import com.nomevini.opengrana.account.exception.StaleAccountException;
import com.nomevini.opengrana.user.exception.EmailAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String TYPE_BASE = "https://opengrana.dev/problems/";

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ProblemDetail handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return problem(HttpStatus.CONFLICT, "Email already in use", ex.getMessage(), "email-already-in-use");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        ProblemDetail problem = problem(HttpStatus.BAD_REQUEST, "Validation failed",
                "One or more fields are invalid", "validation-failed");

        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", error.getDefaultMessage()
                ))
                .toList();
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ProblemDetail handleInvalidInput(Exception ex) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid request",
                "Body or path parameter is invalid", "invalid-request");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        return problem(HttpStatus.UNAUTHORIZED, "Invalid credentials",
                "Email or password is incorrect", "invalid-credentials");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ProblemDetail handleUsernameNotFound(UsernameNotFoundException ex) {
        return problem(HttpStatus.UNAUTHORIZED, "Unauthorized",
                "The authenticated user no longer exists", "user-not-found");
    }

    @ExceptionHandler(AccountNotfoundException.class)
    public ProblemDetail handleAccountNotFound(AccountNotfoundException ex) {
        // "não existe" e "é de outro usuário" têm a mesma resposta: sem enumeração.
        return problem(HttpStatus.NOT_FOUND, "Account not found",
                "Account not found or does not belong to the authenticated user", "account-not-found");
    }

    @ExceptionHandler(InvalidCurrencyException.class)
    public ProblemDetail handleInvalidCurrency(InvalidCurrencyException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid currency", ex.getMessage(), "invalid-currency");
    }

    @ExceptionHandler(MissingIfMatchException.class)
    public ProblemDetail handleMissingIfMatch(MissingIfMatchException ex) {
        return problem(HttpStatus.PRECONDITION_REQUIRED, "Precondition required",
                ex.getMessage(), "if-match-required");
    }

    @ExceptionHandler(InvalidIfMatchException.class)
    public ProblemDetail handleInvalidIfMatch(InvalidIfMatchException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid If-Match", ex.getMessage(), "invalid-if-match");
    }

    @ExceptionHandler({StaleAccountException.class, OptimisticLockingFailureException.class})
    public ProblemDetail handleStale(Exception ex) {
        return problem(HttpStatus.PRECONDITION_FAILED, "Precondition failed",
                ex.getMessage(), "stale-version");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error",
                "An unexpected error occurred. Please try again later.", "internal-error");
    }

    private static ProblemDetail problem(HttpStatus status, String title, String detail, String type) {
        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setType(URI.create(TYPE_BASE + type));
        problem.setTitle(title);
        problem.setDetail(detail);
        String traceId = MDC.get("traceId");
        if (traceId != null) {
            problem.setProperty("traceId", traceId);
        }
        return problem;
    }
}
