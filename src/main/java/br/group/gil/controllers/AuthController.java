package br.group.gil.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.group.gil.data.vo.v1.security.AccountCredentialsVO;
import br.group.gil.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authentication Endpoint")
@RestController
@RequestMapping(value = "/api/auth")
public class AuthController {

	@Autowired
	private AuthService authService;
	

	@Operation(summary = "Authentications a user and returns a token")
	@PostMapping(value = "/signin")
	public ResponseEntity<String> signin(@RequestBody AccountCredentialsVO data) {
		if (checkIfParamsIsNotNull(data)) {	
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
		}			
		var token = authService.signin(data);
		if (token == null) ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");	
		return token;	
	}
	
	@Operation(summary = "Refresh token for authenticated user and returns a token")
	@PutMapping(value = "/refresh/{username}" )
	public ResponseEntity<String> refreshToken(@PathVariable("username") String username, 
			@RequestHeader("Authorization") String refreshToken) {
		
		if (checkIfParamIsNotNull(username, refreshToken)) {	
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
		}
		
		var token = authService.refreshToken(username, refreshToken);
		
		if (token == null) ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");	
		return token;	
	}

	//poderíamos mover essa lógica para o authService
	private boolean checkIfParamIsNotNull(String username, String refreshToken) {
		return refreshToken == null || refreshToken.trim().isEmpty() ||username == null || username.trim().isEmpty();
	}
	
	private boolean checkIfParamsIsNotNull(AccountCredentialsVO data) {
		return data == null || data.getUsername() == null || data.getUsername().trim().isEmpty()
				|| data.getPassword() == null || data.getPassword().trim().isEmpty();
	}
}