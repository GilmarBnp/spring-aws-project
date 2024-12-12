package br.group.gil.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import br.group.gil.data.vo.v1.BookVO;
import br.group.gil.models.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {

	BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);
	
	@Mapping(source = "author", target = "author")
	BookVO bookToBookVO(Book book);
	
	@Mapping(source = "author", target = "author")
	Book bookVOToBook(BookVO book);
	
	@Mapping(source = "author", target = "author")
	List<BookVO> bookVoListToBookList(List<Book> list);
	
	@Mapping(source = "author", target = "author")
	List<BookVO> bookListToBookVOList(List<Book> ist);	
}
