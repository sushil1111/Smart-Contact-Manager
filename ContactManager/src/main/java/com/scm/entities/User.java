package com.scm.entities;


import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "UserDetails")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int userid;
	
	@NotBlank(message = "Name should not be blank..!")
	@Size(min=2, max=25, message = "Name must contain 2 to 25 charcters..!")
	private String name;
	
	@Column(unique = true)
	@NotBlank(message = "Name should not be blank..!")
	@Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Invalid Email")
	private String email;
	
	
	@Column(length = 100)
	private String password;
	
	private String role;
	
	private boolean enabled;
	
	private String imageurl;
	
	@Column(length = 500)
	private String about;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	List<Contact> contacts = new ArrayList<>();
	
	@Override
	public String toString() {
		return "User [userid=" + userid + ", name=" + name + ", email=" + email + ", password=" + password + ", role="
				+ role + ", enabled=" + enabled + ", imageurl=" + imageurl + ", about=" + about + ", contacts="
				+ contacts + "]";
	}

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getImageurl() {
		return imageurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public User(int userid,
			@NotBlank(message = "Name should not be blank..!") @Size(min = 2, max = 25, message = "Name must contain 2 to 25 charcters..!") String name,
			@NotBlank(message = "Name should not be blank..!") @Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Invalid Email") String email,
			String password, String role, boolean enabled, String imageurl, String about, List<Contact> contacts) {
		super();
		this.userid = userid;
		this.name = name;
		this.email = email;
		this.password = password;
		this.role = role;
		this.enabled = enabled;
		this.imageurl = imageurl;
		this.about = about;
		this.contacts = contacts;
	}
	
	
}
