package com.spring.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.spring.dao.ContactRepository;
import com.spring.dao.UserRepository;
import com.spring.entities.Contact;
import com.spring.entities.User;
import com.spring.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	// method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("USERNAME " + userName);

		User user = userRepository.getUserByUserName(userName);

		System.out.println("USER " + user);
		System.out.println("This is login ");

		model.addAttribute("user", user);
	}

	// dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {

		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	// open add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

//	processing add contact form handler

	// principal help to get particular user details which is processed by below
	// (here email will get by "principal"
	// @RequestParam is used to seperatly take data from UI form which doesn't
	// belong in entities class or belong like "imagefileImage"
	// MultipartFile for take different file like img or other file for UI form

	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {
		try {

			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

//		if(3>2)
//		{
//			throw new Exception();
//		}

			// processing and uploaduing file

			if (file.isEmpty()) {
				// if the file(image) is empty then try our message
				System.out.println("File is empty  ");
				contact.setImage("default.png");

			} else {
				// file to folder and update the name to contact
				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/image").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("File upload successful");

			}

			contact.setUser(user);
			user.getList().add(contact);
			this.userRepository.save(user);

//			System.out.println("Contact data save successfully ");
//			System.out.println("Data of contact " + contact);

			// if try block run successfully then print some message
			session.setAttribute("message", new Message("Your contact added successffully !! Add more..", "success"));

		} catch (Exception e) {
			System.out.println("ERROR " + e.getMessage());
			e.printStackTrace();

			// if try block give some error then it will print on message
			session.setAttribute("message", new Message("Your contact not added !! Try again..", "danger"));

		}
		return "normal/add_contact_form";
	}

//	show contact handler
	// per page = 5[n]
	// current page = 0 [page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "Show User Contacts");

		// now we retrieve data from database and send the list of contact
		// principal for get username(email) of current user

		String userName = principal.getName();

		// below code for extracting username details
		User user = this.userRepository.getUserByUserName(userName);

//		 List<Contact> contacts=user.getList();

		// current-page
		// contact per page - 5
		Pageable pageable = PageRequest.of(page, 3);

		Page<Contact> contacts = this.contactRepository.findContactbyUser(user.getId(), pageable);

		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contact";
	}

	// showing particular user contact details

	@RequestMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		Optional<Contact> cOptional = this.contactRepository.findById(cId);
		Contact contact = cOptional.get();

		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);
		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}

		return "normal/contact_details";
	}

	// delete contact handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId, Model model, HttpSession httpSession,Principal principal) {
		Contact contact = this.contactRepository.findById(cId).get();

		// check...assignment
		
         User user=this.userRepository.getUserByUserName(principal.getName());
        	user.getList().remove(contact);	 
        	this.userRepository.save(user);

		// remove->img->contact.getImage()

		this.contactRepository.delete(contact);
		System.out.println("deleted contact is :::" + contact);

		httpSession.setAttribute("message", new Message("Contact deleted successfully...", "success"));
		return "redirect:/user/show-contacts/0";
	}

	// open update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model model) {
		model.addAttribute("title", "Update Contact");

		Contact contact = this.contactRepository.findById(cid).get();
		model.addAttribute("contact", contact);

		return "normal/update_form";
	}

	// update contact handler
	@RequestMapping(value = "/process-update", method = RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model model, HttpSession httpSession, Principal principal) {
		try {

//			old contact details
			Contact oldContact = this.contactRepository.findById(contact.getcId()).get();

			if (!file.isEmpty()) {
				// file work..
				// rewrite

				// delete old photo
				File deleteFile = new ClassPathResource("static/image").getFile();
				File file1=new File(deleteFile,oldContact.getImage());
				file1.delete();
				
				

				// first update new photo
				File saveFile = new ClassPathResource("static/image").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				contact.setImage(file.getOriginalFilename());

			} else {
				contact.setImage(oldContact.getImage());
			}
			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);

			this.contactRepository.save(contact);

//			send message
			httpSession.setAttribute("message", new Message("Your contact is updated...", "success"));

		} catch (Exception e) {
			e.printStackTrace();
		}

//		System.out.println("id :" + contact.getcId());
		return "redirect:/user/" + contact.getcId() + "/contact";
	}
	
	
	//your profile handler
	
	@GetMapping("/profile")
	public String yourfile(Model model)
	{
		model.addAttribute("title","Profile Page");
		return "normal/frofile";
	}
}
