package br.com.erudio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.erudio.Book;

public interface BookRepository extends JpaRepository<Book, Integer> {

}
