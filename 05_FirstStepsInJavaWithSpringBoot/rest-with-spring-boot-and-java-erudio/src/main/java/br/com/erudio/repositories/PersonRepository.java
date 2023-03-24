package br.com.erudio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.erudio.Person;
import br.com.erudio.data.vo.v1.PersonVO;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>{

}
