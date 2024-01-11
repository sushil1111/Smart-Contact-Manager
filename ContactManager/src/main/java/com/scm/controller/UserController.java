package com.scm.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.scm.dao.ContactRepository;
import com.scm.dao.UserRepository;
import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@ModelAttribute
	public void addCommonData(Model model, Principal principal){
		String username = principal.getName();
		User user = this.userRepository.getUserByUsername(username);
		model.addAttribute("user", user);
	}
	
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal){	
		return "normal/userDashboard";
	}
	
	@GetMapping("/addContact")
	public String openaddContactForm(Model model) {
		model.addAttribute("title","title");
		model.addAttribute("contact", new Contact());
		return "normal/addContactPage";
	}
	
	@PostMapping("/processContact")
	public String processContactForm(@ModelAttribute Contact contact, Principal principal,
			@RequestParam("image") MultipartFile multipartFile, HttpSession session) {
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUsername(name);
			//image saving in folder -> they saving name in database
			if(multipartFile.isEmpty()) {
				contact.setImageurl("contact.png");
				
			}else {
				contact.setImageurl(multipartFile.getOriginalFilename());
				File file = new ClassPathResource("static/image").getFile();
				
				Files.copy(multipartFile.getInputStream(), 
						Paths.get(file.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename()), 
						StandardCopyOption.REPLACE_EXISTING);
			}
			
			user.getContacts().add(contact);
			contact.setUser(user);
			this.userRepository.save(user);
			System.out.println("added to databases");
			session.setAttribute("msg", new Message("Your Contact is added succesfully", "success"));
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("msg", new Message("Something went wrong..!", "danger"));
		}
		
		return "normal/addContactPage";
	}
	
	@GetMapping("/showContacts/{page}")
	public String showContact(@PathVariable("page") Integer page,Model model, Principal principal) {
		model.addAttribute("title", "Show Contacts");
		User user = this.userRepository.getUserByUsername(principal.getName());
		
		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contactlist = this.contactRepository.findContactsByUser(user.getUserid(), pageable);
		
		// 3 things required for pagination
		model.addAttribute("contactlist", contactlist);
		model.addAttribute("currentpage", page);
		model.addAttribute("totalPages", contactlist.getTotalPages());
		
		return "normal/showContacts";
	}
	
	
	//show perticular contact detail
	@RequestMapping("/contact/{contactId}")
	public String showContactDetails(@PathVariable("contactId") Integer contactId, Model model, Principal principal) {
		System.out.println(contactId);
		Optional<Contact> contactOptional = this.contactRepository.findById(contactId);
		Contact contact = contactOptional.get();
		String username = principal.getName();
		User user = this.userRepository.getUserByUsername(username);
		if(user.getUserid() == contact.getUser().getUserid()) {
			model.addAttribute("contact", contact);
		}
		return "normal/contactDetail";
	}
	
	@GetMapping("/delete/{contactId}")
	public String deleteContactHandler(@PathVariable("contactId") Integer contactId, Model model, 
			Principal principal, HttpSession httpSession) {
		Optional<Contact> cOptional= this.contactRepository.findById(contactId);
		Contact contact = cOptional.get();
		System.out.println(contact.getUser().getUserid());
		
		String username = principal.getName();
		User user = this.userRepository.getUserByUsername(username);
		System.out.println(user.getUserid());
		
		if(user.getUserid() == contact.getUser().getUserid()) {
			this.contactRepository.delete(contact);
			httpSession.setAttribute("msg", new Message("Contact Succesfully Deleted", "success"));
		}else {
			httpSession.setAttribute("msg", new Message("Invalid Request", "danger"));
		}
		return "redirect:/user/showContacts/0";
	}
	
	
	//update form
	@PostMapping("/openContact/{contactId}")
	public String openUpdateFromHandler(@PathVariable("contactId") Integer contactId, Model model) {
		model.addAttribute("title","update");
		Contact contact = this.contactRepository.findById(contactId).get();
		model.addAttribute("contact",contact);
		return "normal/updateform";
	}
	
	//processing updated form
	@PostMapping("/processUpdate")
	public String processUpdateForm(@ModelAttribute Contact contact,@RequestParam("image") MultipartFile multipartFile,
			Model model, HttpSession httpSession, Principal principal) {
		System.out.println(contact.getName());
		try {
			
			Contact oldcontact = this.contactRepository.findById(contact.getContactId()).get();
			if( !multipartFile.isEmpty() ) {
				//delete old photo
				File deletefile = new ClassPathResource("static/image").getFile();
				File file1 =  new File(deletefile, oldcontact.getImageurl());
				file1.delete();
				
				//save new image
				File file = new ClassPathResource("static/image").getFile();
				Files.copy(multipartFile.getInputStream(), 
						Paths.get(file.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename()), 
						StandardCopyOption.REPLACE_EXISTING);
				contact.setImageurl(multipartFile.getOriginalFilename());
			}else {
				contact.setImageurl(oldcontact.getImageurl());
			}
			User user = this.userRepository.getUserByUsername(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			httpSession.setAttribute("msg", new Message("Your Contact is Updated Succesfully...!", "success"));

		} catch (Exception e) {
			e.printStackTrace();
			httpSession.setAttribute("msg", new Message("Invalid Request", "danger"));
		}
		
		return "redirect:/user/contact/"+contact.getContactId();
	}
	
	
	//your profile
	@GetMapping("/profile")
	public String yourProfileHandler() {
		return "normal/profile";
	}
}
