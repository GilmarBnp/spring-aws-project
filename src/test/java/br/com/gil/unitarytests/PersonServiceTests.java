package br.com.gil.unitarytests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.test.context.ContextConfiguration;

import br.group.gil.Startup;
import br.group.gil.data.vo.v1.PersonVO;
import br.group.gil.exceptions.RequiredObjetNullException;
import br.group.gil.models.Person;
import br.group.gil.repository.PersonRepository;
import br.group.gil.services.PersonService;
import br.group.gil.unitytest.mapper.mocks.MockPerson;

@SpringBootTest
@ContextConfiguration(classes = Startup.class)
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PersonServiceTests {
	
	@SpyBean
	MockPerson input;
	
	@Autowired
	private PersonService service;
	
	@MockBean
	PersonRepository repository;
	
	@BeforeEach
	void setUpMockes() throws Exception {
		System.out.println();
		input = new MockPerson();
		MockitoAnnotations.openMocks(this);
		
	}

	@Test
	void testFindById() throws Exception {
		Person person = input.mockEntity(2);
		
		person.setId(1L);
		
		when(repository.findById(1L)).thenReturn(Optional.of(person));
			
		PersonVO result = service.findById(person.getUuid());
		
		assertNotNull(result);
		assertNotNull(result.getId());
		assertNotNull(result.getLinks());
			
		assertTrue(result.toString().contains("links: [<http://localhost/api/person/1>;rel=\"self\"]"));	
		
		assertEquals("Addres Test2", result.getAddress());
		assertEquals("First Name Test2", result.getFirstName());
		assertEquals("Last Name Test2", result.getLastName());
		assertEquals("Male", result.getGender());
	}

	@Test
	void testCreate() throws Exception {
		Person entity = input.mockEntity(2);
		Person persisted = entity;
		entity.setId(1L);
		
		PersonVO vo = input.mockVO(2);
		vo.setId(1L);
		
		when(repository.save(entity)).thenReturn(persisted);
		
		PersonVO result = service.create(vo);
	
		assertNotNull(result);
		assertNotNull(result.getId());
		assertNotNull(result.getLinks());
			
		assertTrue(result.toString().contains("links: [<http://localhost/api/person/1>;rel=\"self\"]"));	
		
		assertEquals("Addres Test2", result.getAddress());
		assertEquals("First Name Test2", result.getFirstName());
		assertEquals("Last Name Test2", result.getLastName());
		assertEquals("Male", result.getGender());	
	}
	
	@Test
	void testCreateWithNullPerson() throws Exception {
		
		Exception exception = assertThrows(RequiredObjetNullException.class, () -> {
			service.create(null);
		});
		
		String expectedMessage = "It is not allowed to persist a null object!";
		String messageReceived = exception.getMessage();
		
		assertTrue(messageReceived.contains(expectedMessage));			
	}

	@Test
	void testUpdate() throws Exception {
		Person entity = input.mockEntity(2);
		entity.setId(1L);
		
		Person persisted = entity;
		entity.setId(1L);
		
		PersonVO vo = input.mockVO(2);
		vo.setId(1L);
		
		when(repository.findById(1L)).thenReturn(Optional.of(entity));
		when(repository.save(entity)).thenReturn(persisted);
		
		PersonVO result = service.update(vo);
	
		assertNotNull(result);
		assertNotNull(result.getId());
		assertNotNull(result.getLinks());
			
		assertTrue(result.toString().contains("links: [<http://localhost/api/person/1>;rel=\"self\"]"));	
		
		assertEquals("Addres Test2", result.getAddress());
		assertEquals("First Name Test2", result.getFirstName());
		assertEquals("Last Name Test2", result.getLastName());
		assertEquals("Male", result.getGender());	
	}
	
	@Test
	void testUpdateWithNullPerson() throws Exception {
		
		Exception exception = assertThrows(RequiredObjetNullException.class, () -> {
			service.update(null);
		});
		
		String expectedMessage = "It is not allowed to persist a null object!";
		String messageReceived = exception.getMessage();
		
		assertTrue(messageReceived.contains(expectedMessage));			
	}

	@Test
	void testDelete() {
		Person entity = input.mockEntity(2);
		entity.setId(1L);
		
		Person persisted = entity;
		persisted.setId(1L);
		
		PersonVO vo = input.mockVO(2);
		vo.setId(1L);
		
		when(repository.findById(1L)).thenReturn(Optional.of(persisted));
		return;
	}
	
}
					
				

		

				
		

