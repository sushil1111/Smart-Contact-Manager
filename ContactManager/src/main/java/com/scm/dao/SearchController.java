package com.scm.dao;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.scm.entities.Contact;
import com.scm.entities.User;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class SearchController {
	//searchHanlder
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@GetMapping("/search/{name}")
	public ResponseEntity<?> search(@PathVariable("name") String name, 
			Principal principal){
		System.out.println(name);
		User user = this.userRepository.getUserByUsername(principal.getName());
		List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(name, user);
		return ResponseEntity.ok(contacts);
	}
}
