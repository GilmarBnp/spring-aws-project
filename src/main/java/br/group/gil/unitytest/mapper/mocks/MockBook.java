package br.group.gil.unitytest.mapper.mocks;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import br.group.gil.data.vo.v1.BookVO;
import br.group.gil.models.Book;

public class MockBook {

    public Book mockEntity() {
        return mockEntity();
    }
    
    public BookVO mockVO() {
        return mockVO();
    }
    
    public List<Book> mockEntityList() {
        List<Book> books = new ArrayList<Book>();
        for (int i = 0; i < 14; i++) {
            books.add(mockEntity((double) i));
        }
        return books;
    }

    public List<BookVO> mockVOList() {
        List<BookVO> books = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            books.add(mockVO((double) i));
        }
        return books;
    }
    
    public Book mockEntity(Double i) {
        Book book = new Book();
        book.setTitle("Title" + i);
        book.setAuthor("Author" + i);
        book.setLaunchDate(LocalDate.now());
        book.setId(i.longValue());
        book.setPrice(i);
        return book;
    }

    public BookVO mockVO(Double i) {
        BookVO book = new BookVO();
        book.setTitle("Title" + i);
        book.setAuthor("Author" + i);
        book.setLaunchDate(LocalDate.now());
        book.setId(i.longValue());
        book.setPrice(i);
        return book;
    }
}
