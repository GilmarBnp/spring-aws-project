package br.group.gil.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import br.group.gil.controllers.BookController;
import br.group.gil.data.vo.v1.BookVO;
import br.group.gil.exceptions.NotFoundException;
import br.group.gil.exceptions.RequiredObjetNullException;
import br.group.gil.mapper.BookMapper;
import br.group.gil.models.Book;
import br.group.gil.repository.BookRepository;
import jakarta.transaction.Transactional;

@Service
public class BookService {
    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);
	
	@Autowired
	private BookRepository repository;
	
	@Autowired
	private BookMapper bookMapper;
	
	@Autowired
	PagedResourcesAssembler<BookVO> assembler;
	
	public PagedModel<EntityModel<BookVO>>findAll(Pageable pageable){		
		var bookPage = repository.findAll(pageable);	
		var bookVosPage = bookPage.map(p-> bookMapper.bookToBookVO(p));	
		  
		bookVosPage.map(
				 p-> {
					try {
						return p.add(linkTo(methodOn(BookController.class)
								.findById(p.getId())).withSelfRel());
					} catch (Exception e) {	
						e.printStackTrace();
					}
					return p;
				});
		
		Link link = linkTo(
				  methodOn(BookController.class).
				    findAll(
				    	pageable.getPageNumber(), 
				    	pageable.getPageSize(), 
				    	"asc")).withSelfRel();
		
		return assembler.toModel(bookVosPage, link);
	};
	
	
	public BookVO findById(Long id) throws Exception {
		BookVO bookVO = bookMapper.bookToBookVO(repository.findById(id)
				.orElseThrow(()-> new NotFoundException("Book not found with this id!"))); 
		
		bookVO.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
		return bookVO;
	}
	
public BookVO create(BookVO bookVO) throws Exception {
	if(bookVO == null) throw new RequiredObjetNullException();
	
		Book book = bookMapper.bookVOToBook(bookVO);
		
		
		 Optional<Book> bookOptional = repository.findByTitle(book.getTitle());
		 
		 if (bookOptional.isPresent()) {
	          throw new NotFoundException("Book with title " + book.getTitle() + " Already exist!");
	            		
	        } else {
	        	Book bookSaved = (repository.save(book)); 
		 	    
		 	    BookVO vo = bookMapper.bookToBookVO(bookSaved);
		 		
		 		return vo.add(linkTo(methodOn(BookController.class)
		 				.findById(vo.getId())).withSelfRel());	        	
	        }
	    }
	    	
	public BookVO update(BookVO book) throws Exception {		
		if(book == null) throw new RequiredObjetNullException();
		
		Book entity = repository.findById(book.getId())
				.orElseThrow(()-> new NotFoundException("Book with this id not found!"));
		
		entity.setAuthor(book.getAuthor());
		entity.setLaunchDate(book.getLaunchDate());
		entity.setPrice(book.getPrice());
		entity.setTitle(book.getTitle());
		
		BookVO vo = bookMapper.bookToBookVO(repository.save(entity)); 
		
		try {
			return vo.add(linkTo(methodOn(BookController.class)
					.findById(vo.getId())).withSelfRel());
		} catch (RequiredObjetNullException e) {
			e.printStackTrace();
		}
		return vo;		
	}
	
	 @Transactional
	    public void delete(Long id) {
	        logger.info("Deleting one book!");

	        var entity = repository.findById(id)
	                .orElseThrow(() -> {
	                    logger.error("No records found for this ID!");
	                    return new NotFoundException("No records found for this ID!");
	                });

	        logger.info("Found entity: {}", entity);

	        repository.delete(entity);

	        logger.info("Book with ID {} deleted successfully", id);
	    }
	
	public BookVO findBookByTitle(String title) throws NotFoundException {
        Optional<Book> bookOptional = repository.findByTitle(title);
        if (bookOptional.isPresent()) {
            return 
            		bookMapper.bookToBookVO(bookOptional.get());
        } else {
            throw new NotFoundException("Book with title " + title + " not found");
        }
    }
}
