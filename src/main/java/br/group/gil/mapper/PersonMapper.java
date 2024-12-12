package br.group.gil.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import br.group.gil.data.vo.v1.PersonVO;
import br.group.gil.data.vo.v2.PersonVOV2;
import br.group.gil.models.Person;

    @Mapper(componentModel = "spring")
	public interface PersonMapper {
	 
	    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class ); 
	 
	    //@Mapping(target = "key", source = "id")	
	    @Mapping(source = "address", target = "address")
	    PersonVO personToPersonVO(Person person); 
	    
	    @Mapping(source = "address", target = "address")
	    PersonVOV2 personToPersonVOV2(Person person); 
	       
	    @Mapping(source = "gender", target = "gender")
	    Person personVoToPerson(PersonVO personvo);
	    
	    @Mapping(source = "gender", target = "gender")
	    Person personVoV2ToPerson(PersonVOV2 personvo);
	    
	    @Mapping(source = "address", target = "address")
	    List<PersonVO> personVoListToPersonList(List<Person> personList);
	    
};


