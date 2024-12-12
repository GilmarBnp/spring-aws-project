package br.group.gil.security.jwt;


import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.group.gil.data.vo.v1.security.TokenVO;
import br.group.gil.exceptions.AuthenticationJwtException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JwtTokenProvider {
	
	@Value("${security.jwt.token.secret-key:secret}")
	private String secretKey = "secret";
	
	@Value("${security.jwt.token.expire-length:7200000}")
	private long validityInMilliseconds = 7200000; //2h
	
	@Autowired
	private UserDetailsService userDetailService;
	
	Algorithm algorithm = null;
	
	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
		algorithm = Algorithm.HMAC256(secretKey.getBytes());		
	}
	
	public TokenVO createAcessToken(String username, List<String> roles) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);
		var acessToken = getAcessToken(username, roles, now, validity);
		var refreshToken = getRefreshToken(username, roles, now);
			
		return new TokenVO(username, true, now, validity, acessToken, refreshToken);
	}

	public TokenVO refreshToken(String refreshToken) {
		if(refreshToken.contains("Bearer ")) refreshToken =
				refreshToken.substring("Bearer ".length());
		
		JWTVerifier verifier = JWT.require(algorithm).build();
		DecodedJWT decodedJWT = verifier.verify(refreshToken);
		
		String username = decodedJWT.getSubject();
		List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
				
		return createAcessToken(username, roles);
		
	}
	
	private String getAcessToken(String username, List<String> roles, Date now, Date validity) {
		String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
		
		return JWT.create()
					.withClaim("roles", roles)
					.withIssuedAt(now)
					.withExpiresAt(validity)
					.withSubject(username)
					.withIssuer(issuerUrl)
					.sign(algorithm)
					.trim();
	}
	
	private String getRefreshToken(String username, List<String> roles, Date now) {	
		Date validityFreshToken = new Date(now.getTime() + validityInMilliseconds * 2); // 4h
		
		return JWT.create()
				.withClaim("roles", roles)
				.withIssuedAt(now)
				.withExpiresAt(validityFreshToken)
				.withSubject(username)
				.sign(algorithm)
				.trim();
	}
	
	public Authentication getAuthentication(String token) {
		DecodedJWT decodedJWT = decodedToken(token);	
		UserDetails userDetails = this.userDetailService
				.loadUserByUsername(decodedJWT.getSubject());
		
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	private DecodedJWT decodedToken(String token) {
		Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());	
		JWTVerifier verifier = JWT.require(algorithm).build();
		DecodedJWT decodeJWT = verifier.verify(token);
		
		return decodeJWT;
	}
	
	public String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader("Authorization");
		
		if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring("Bearer ".length());
		}
		
		return null;
	}

	public boolean validateToken(String token) throws AuthenticationJwtException {
		DecodedJWT decodedJWT = decodedToken(token);
		
		try {
			if (decodedJWT.getExpiresAt().before(new Date())) {
				
			  return false;
			}
			
			return true;
		} catch (Exception e) {
			throw new AuthenticationJwtException("Expired or invalid token!");
		}
	}	
}
