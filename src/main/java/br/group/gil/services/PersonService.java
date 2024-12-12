package br.group.gil.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import br.group.gil.controllers.PersonController;
import br.group.gil.data.vo.v1.PersonVO;
import br.group.gil.data.vo.v2.PersonVOV2;
import br.group.gil.exceptions.NotFoundException;
import br.group.gil.exceptions.RequiredObjetNullException;
import br.group.gil.mapper.PersonMapper;
import br.group.gil.models.Person;
import br.group.gil.repository.PersonRepository;
import jakarta.transaction.Transactional;

/*without static import
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
   public void someMethod() {
   
     Link link = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MyController.class).myMethod()).withSelfRel();
}

with static import
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

	public void someMethod() {
  
     Link link = linkTo(methodOn(MyController.class).myMethod()).withSelfRel();
    }
*/

@Service
public class PersonService {
    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);

	@Autowired
	private PersonRepository personRepository;
	
	@Autowired
    private PersonMapper personMapper;
	
	@Autowired
	PagedResourcesAssembler<PersonVO> assembler;
	
	public PagedModel<EntityModel<PersonVO>>findAll(Pageable pageable) {		
	var personPage = personRepository.findAll(pageable);
	 
	 var personVosPage = personPage.map(p-> personMapper.personToPersonVO(p));
	 
	 personVosPage.map(
			 p-> {
				try {
					return p.add(linkTo(methodOn(PersonController.class)
							.findById(p.getId())).withSelfRel());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return p;
			});
	
	  Link link = linkTo(
			  methodOn(PersonController.class).
			    findAll(
			    	pageable.getPageNumber(), 
			    	pageable.getPageSize(), 
			    	"asc")).withSelfRel();
	  
	return assembler.toModel(personVosPage, link);
	};	
	
	public PagedModel<EntityModel<PersonVO>>findPersonByName(String firstName, Pageable pageable) {	
		 var personPage = personRepository.findPersonByName(firstName, pageable); 	
		 var personVosPage = personPage.map(p-> personMapper.personToPersonVO(p));
		 
		 personVosPage.map(
				 p-> {
					try {
						return p.add(linkTo(methodOn(PersonController.class)
								.findById(p.getId())).withSelfRel());
					} catch (Exception e) {
						e.printStackTrace();
					}
					return p;
				});
		
		  Link link = linkTo(
				  methodOn(PersonController.class).
				    findAll(
				    	pageable.getPageNumber(), 
				    	pageable.getPageSize(), 
				    	"asc")).withSelfRel();
		  
		return assembler.toModel(personVosPage, link);
	};	
	
	public PersonVO findById(Long id) throws Exception {	
		PersonVO personVO = personMapper.personToPersonVO((personRepository.findById(id)
				.orElseThrow(()-> new NotFoundException("User with this id not found"))));
		
		personVO.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		return personVO;
	};	
	
	
	
	public PersonVO create(PersonVO personvo) throws Exception {
		if(personvo == null) throw new RequiredObjetNullException();
		
		Person person = personMapper.personVoToPerson(personvo);
		
		Person savedPerson = personRepository.save(person);

		PersonVO savedPersonVO = personMapper.personToPersonVO(savedPerson);
		

	    return savedPersonVO.add(linkTo(methodOn(PersonController.class).findById(savedPersonVO.getId())).withSelfRel());
	};//o hateoas api só add se for uma classe VO se tentar usar o add linkTo com o retorno da classe Person não irá funcionar!
		//porque o add pertence a herança na classe PersonVO extends RepresentationModel<PersonVO>
	public PersonVOV2 createV2(PersonVOV2 personvo) throws Exception {
		//logger.info("Creating a person with api version 2");	
		if(personvo == null) throw new RequiredObjetNullException();
		
		Person person = personMapper.personVoV2ToPerson(personvo);
		
		Person savedPerson = personRepository.save(person);

		PersonVOV2 savedPersonVOV2 = personMapper.personToPersonVOV2(savedPerson);

	    return savedPersonVOV2.add(linkTo(methodOn(PersonController.class).findById(savedPersonVOV2.getId())).withSelfRel());
	};
	
	public PersonVO update(PersonVO person) throws Exception {	 		 	
		var entity = personRepository.findById(person.getId())
		.orElseThrow(()-> new NotFoundException("User with this id not found"));
				
		entity.setFirstName(person.getFirstName()); 	
		entity.setLastName(person.getLastName());
		entity.setGender(person.getGender());
		entity.setAddress(person.getAddress());
			 
		var vo = personMapper.personToPersonVO(personRepository.save(entity));
		
	    return vo.add(linkTo(methodOn(PersonController.class).findById(vo.getId())).withSelfRel());
	};
	
	@Transactional
	public void delete(Long id) {	
		Person personFound = personRepository.findById(id).
		orElseThrow(() -> new NotFoundException("User with this id not found"));		
		
		logger.info("Found entity: {}", personFound);

        personRepository.delete(personFound);

        logger.info("Book with ID {} deleted successfully", id);
	};
};	
	