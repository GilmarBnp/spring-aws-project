package br.group.gil.exceptions;



import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AuthenticationJwtException extends AuthenticationException{
	
	private static final long serialVersionUID = 1L;

	public AuthenticationJwtException(String message) {
		super(message);
	};
};	