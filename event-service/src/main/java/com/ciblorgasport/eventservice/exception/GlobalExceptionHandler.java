package com.ciblorgasport.eventservice.exception;

import java.util.stream.Collectors;
import com.ciblorgasport.eventservice.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex, HttpServletRequest req) {
		final HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
		final ErrorResponse er = new ErrorResponse(status.value(), status.getReasonPhrase(),
				ex.getReason() != null ? ex.getReason() : ex.getMessage(), req.getRequestURI());
		return ResponseEntity.status(status).body(er);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
		String msg = ex.getBindingResult().getFieldErrors().stream()
			.map(f -> f.getField() + ": " + f.getDefaultMessage())
			.collect(Collectors.joining("; "));
		ErrorResponse er = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", msg, req.getRequestURI());
		return ResponseEntity.badRequest().body(er);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
		ErrorResponse er = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Forbidden",
			ex.getMessage() != null ? ex.getMessage() : "Access denied", req.getRequestURI());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(er);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest req) {
		log.error("Unhandled server error", ex);
		ErrorResponse er = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
				"Unexpected server error", req.getRequestURI());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(er);
	}
}
