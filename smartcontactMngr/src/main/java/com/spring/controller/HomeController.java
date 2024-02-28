package com.spring.controller;

import javax.servlet.http.HttpSession;

import javax.validation.Valid;

import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.dao.UserRepository;
import com.spring.entities.Contact;
import com.spring.entities.User;
import com.spring.helper.Message;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home - smart contact manager");
		return "home";

	}

	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "Home - smart contact manager");
		return "about";

	}

	@RequestMapping("/signup")
	public String singup(Model model) {
		model.addAttribute("title", "Register - smart contact manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result1,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,HttpSession session) {
		
		try {
			if (!agreement) {
				System.out.println("Your have not agreed the terms and conditions");
				throw new Exception("Your have not agreed the terms and conditions");

			}
			if(result1.hasErrors())
			{
				System.out.println("ERROR "+result1.toString());
				model.addAttribute("user",user);
				return "signup";
				
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println("USER SUCCESSFULL  "+ user);
			User result = this.userRepository.save(user);

			model.addAttribute("user", new User());
			
			session.setAttribute("message", new Message("successfully registered","alert-success"));
			return "signup";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("Something went wrong !! ","alert-danger"));
			return "signup";
		}
	}

	@RequestMapping("8 ;")
	public String FormSignUp() {

		return "FormSingUp";

	}

	//handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model model)
	{
		model.addAttribute("title","Login Page");
		return "login";
	}
}
