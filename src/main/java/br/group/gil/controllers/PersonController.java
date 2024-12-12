package br.group.gil.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.group.gil.data.vo.v1.PersonVO;
import br.group.gil.data.vo.v2.PersonVOV2;
import br.group.gil.services.PersonService;
import br.group.gil.utils.MediaType;
import io.swagger.v3.oas.annotations.Operation;
	
@RestController
@RequestMapping("api/person")
public class PersonController {
	
	@Autowired
	private PersonService service;
	
	@GetMapping(value="/v1",
	     produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
	@Operation(summary = "Finds all people", description = "Finds all people")	
	public ResponseEntity<PagedModel<EntityModel<PersonVO>>> findAll(	
			@RequestParam(value="page", defaultValue = "0")Integer page,
	        @RequestParam(value="size", defaultValue = "12")Integer size,
	        @RequestParam(value="direction", defaultValue = "asc")String direction ){
		
		var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
		
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
		return ResponseEntity.ok(service.findAll(pageable));		
	};
	
	@GetMapping(value="/v1/findPersonByName/{firstName}",
	     produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
	@Operation(summary = "Finds people by name", description = "Finds people by name")	
	public ResponseEntity<PagedModel<EntityModel<PersonVO>>> findPersonByName(
			@PathVariable(value="firstName")String firstName,
		    @RequestParam(value="page", defaultValue = "0")Integer page,
			@RequestParam(value="size", defaultValue = "12")Integer size,
			@RequestParam(value="direction", defaultValue = "asc")String direction ){
		
				var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
				
				Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));
				return ResponseEntity.ok(service.findPersonByName(firstName, pageable));		
			};
	
	@CrossOrigin(origins = {"http://localhost:8080","https://erudio.com.br"})
	@GetMapping(value="/v1/{id}",
	    produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
	@Operation(summary = "Finds people by id", description = "Finds people by id")	
	public PersonVO findById(@PathVariable(value="id")Long id) throws Exception{
		return service.findById(id);		
	};
	
	@CrossOrigin(origins = {"http://localhost:8080","https://erudio.com.br"})
	@PostMapping(value = "/v1", 
	    produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
		consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
	@Operation(summary = "Create people", description = "Create people")	
	public PersonVO save(@RequestBody PersonVO person) throws Exception{
		return service.create(person);		
	};
	
	@PostMapping(value = "/v2", 
		produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
		consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
	public PersonVOV2 saveV2(@RequestBody PersonVOV2 person) throws Exception{
		return service.createV2(person);		
	};	
		
	@PutMapping(value="/v1/{id}",
		produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
		consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
	public PersonVO update(@PathVariable(value="id") Long id, @RequestBody PersonVO person) throws Exception{
		return service.update(person);		
	};
	
	@DeleteMapping(value="/v1/{id}")
	public ResponseEntity<?> delete(@PathVariable(value="id")String id) {
		return ResponseEntity.noContent().build();
	};	
};	

