package br.group.gil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.group.gil.models.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
	
	 Optional<Book> findByTitle(String title);
}
