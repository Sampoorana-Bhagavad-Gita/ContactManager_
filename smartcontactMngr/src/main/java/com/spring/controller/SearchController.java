package com.spring.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.spring.dao.ContactRepository;
import com.spring.dao.UserRepository;
import com.spring.entities.Contact;
import com.spring.entities.User;

@RestController
public class SearchController {
	
	@Autowired
	private UserRepository repository;
	@Autowired
	private ContactRepository contactRepository;
	
	//search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal)
	{
		System.out.println(query);
		User user=this.repository.getUserByUserName(principal.getName());
		List<Contact> contacts=this.contactRepository.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(contacts); 
	}

}
