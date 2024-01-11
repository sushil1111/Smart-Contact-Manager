package com.scm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.dao.UserRepository;
import com.scm.entities.User;
import com.scm.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class homeController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@RequestMapping("/")
	public String homeHandler(Model model) {
		model.addAttribute("title", "Smart Contact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String aboutHandler(Model model) {
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signupHandler(Model model) {
		model.addAttribute("title", "Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	@PostMapping("/doRegister")
	public String registerUserHandler(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
			@RequestParam(value="agreement", defaultValue = "false") boolean agreement, Model model, 
			HttpSession session) {
		try {
			
			if(!agreement) {
				System.out.println("You have not agreed the T&C");
				throw new Exception();
			}
			if(bindingResult.hasErrors()) {
				model.addAttribute("user",user);
				System.out.println("Error : "+bindingResult.toString());
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			User userResult =  this.userRepository.save(user);
			model.addAttribute("user",new User());
			System.out.println(agreement+" "+user);
			session.setAttribute("msg", new Message("Successfully registration..!! ", "alert-success"));
			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("msg", new Message("something went wrong..!!  Try Again", "alert-error"));
			return "signup";
		}
		
	}
	
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title","Login Page");
		return "login";
	}
	
}
