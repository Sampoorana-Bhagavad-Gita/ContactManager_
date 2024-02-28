package com.spring.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.spring.entities.Contact;
import com.spring.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer>{
	
	//pagination ki repository ke ye interface banaaya gya hai
	
	//currentPage-page
	//Contct Per page 5 
	@Query("from Contact as c where c.user.id=:userId")
	public Page<Contact> findContactbyUser(@Param("userId") int userId,Pageable pageable );
	
      //search
	public List<Contact> findByNameContainingAndUser(String name,User user);
	
}
