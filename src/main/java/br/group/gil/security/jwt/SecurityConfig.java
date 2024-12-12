package br.group.gil.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

	@Autowired
	private JwtTokenProvider tokenProvider;

	public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
		this.tokenProvider = jwtTokenProvider;
	}
	
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
	AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration )
		throws Exception {
			return authenticationConfiguration.getAuthenticationManager();
	   }
	
	@Bean
	protected SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
		JwtTokenFilter customFilter = new JwtTokenFilter(tokenProvider);
		
		//@formatter:off
		return http
			.httpBasic(basic -> basic.disable())
			.csrf(csrf -> csrf.disable())
			.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(
					   authorizeHttpRequests -> authorizeHttpRequests
							.requestMatchers("/api/auth/signin","/api/auth/refresh/**","/api-docs/**",
									"/swagger-ui/index.html/**","/swagger-ui/**","/v3/api-docs/**").permitAll()
							.requestMatchers("/api/**").authenticated()
							.requestMatchers("/users").denyAll()
					)
					.cors()	
		            .and()
		            .build();
		//@formatter:on
	}	
}