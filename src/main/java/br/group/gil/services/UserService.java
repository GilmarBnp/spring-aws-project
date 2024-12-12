package br.group.gil.services;


import org.jboss.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.group.gil.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {
	
	private Logger logger = Logger.getLogger(UserService.class.getClass());
	
	
	UserRepository repository;
	
	@Autowired
	public UserService(UserRepository repository) {
		this.repository = repository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("Finding one user by name " + username + " !" );
		
		var user = repository.findByUsername(username);
		
		if (user != null) {
			return user;
		} else {
			throw new UsernameNotFoundException("User with " + username + " not found!");
		}	
	};		
};	
	