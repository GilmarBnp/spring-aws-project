package br.group.gil.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.group.gil.data.vo.v1.security.AccountCredentialsVO;
import br.group.gil.data.vo.v1.security.TokenVO;
import br.group.gil.repository.UserRepository;
import br.group.gil.security.jwt.JwtTokenProvider;

@Service
public class AuthService {
 
	@Autowired
	private JwtTokenProvider tokenProvider;
	
	@Autowired	
	private AuthenticationManager autheticationManager;
	
	@Autowired
	private UserRepository userRepository;
	
	
	@SuppressWarnings("rawtypes")
	public ResponseEntity signin(AccountCredentialsVO data) {
		try {
			var username = data.getUsername();
			var password = data.getPassword();
			autheticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(username, password));
			
			var user = userRepository.findByUsername(username);
				
			var tokenResponse = new TokenVO();
			
			if (user != null) {
				tokenResponse = tokenProvider.createAcessToken(username, user.getRoles());
				
			} else {
				throw new UsernameNotFoundException("Username " + username + " Not found!");
			}
			
			return ResponseEntity.ok(tokenResponse);
		} catch (Exception e) {
			throw new BadCredentialsException("Username or Password are incorrectly.");
		}		
	 }
	
	@SuppressWarnings("rawtypes")
	public ResponseEntity refreshToken(String username, String refreshToken) {
		var user = userRepository.findByUsername(username);
				
		var tokenResponse = new TokenVO();
			
		if (user != null) {
			tokenResponse = tokenProvider.refreshToken(refreshToken);
				
		} else {
			throw new UsernameNotFoundException("Username " + username + " Not found!");
		}
			
		return ResponseEntity.ok(tokenResponse);	
	}	
}
		
	
	
