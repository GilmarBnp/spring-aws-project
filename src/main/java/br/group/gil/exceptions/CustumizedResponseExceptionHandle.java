package br.group.gil.exceptions;

import java.util.Date;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class CustumizedResponseExceptionHandle extends ResponseEntityExceptionHandler{

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ExceptionResponse> handleAllException(
			Exception ex, WebRequest request) {
		
		ExceptionResponse exceptionResponse = new ExceptionResponse( 
				new Date(), 
				ex.getMessage(),
				request.getDescription(false)
				);
		
		return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);	
	};
	
	@ExceptionHandler(NotFoundException.class)
	public final ResponseEntity<ExceptionResponse> handleNotFoundException(
			Exception ex, WebRequest request) {
		
		ExceptionResponse exceptionResponse = new ExceptionResponse( 
				new Date(), 
				ex.getMessage(),
				request.getDescription(false)
				);
		
		return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);	
	};
	
	@ExceptionHandler(RequiredObjetNullException.class)
	public final ResponseEntity<ExceptionResponse> handleBadrequestException(
			Exception ex, WebRequest request) {
		
		ExceptionResponse exceptionResponse = new ExceptionResponse( 
				new Date(), 
				ex.getMessage(),
				request.getDescription(false)
				);
		
		return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);	
	};
	
	@ExceptionHandler(AuthenticationJwtException.class)
	public final ResponseEntity<ExceptionResponse> handleAuthenticationJwtException(
			Exception ex, WebRequest request) {
		
		ExceptionResponse exceptionResponse = new ExceptionResponse( 
				new Date(), 
				ex.getMessage(),
				request.getDescription(false)
				);
		
		return new ResponseEntity<>(exceptionResponse, HttpStatus.FORBIDDEN);	
	};
};
