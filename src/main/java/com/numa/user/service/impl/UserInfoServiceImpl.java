package com.numa.user.service.impl;

import com.numa.user.dao.UserInfoRepository;
import com.numa.user.entity.User;
import com.numa.user.service.UserRolesService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserInfoServiceImpl implements UserDetailsService {

	@Autowired
	private UserInfoRepository repository;

	@Autowired
	private UserRolesService roleService;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	public String addUser(User user) {
		if (repository.findByUsername(user.getUsername()).isPresent()) {
			return "User already exists with same username";
		} else {
			// Encode the password before saving
			user.setPassword(encoder.encode(user.getPassword()));
			repository.save(user);
			return "User created";
		}
	}

	public String updateUser(User user) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		try {
			transaction.begin();
			User existingUser = entityManager.find(User.class, user.getId());
			if (existingUser != null) {
				existingUser.setUsername(user.getUsername());
				existingUser.setEmail(user.getEmail());
				existingUser.setRoleId(user.getRoleId());
				existingUser.setPassword(user.getPassword());
				existingUser.setName(user.getName());
				existingUser.setSurname(user.getSurname());
				existingUser.setCountry(user.getCountry());
				existingUser.setDob(user.getDob());
				existingUser.setMobileNumber(user.getMobileNumber());
			}
			entityManager.merge(existingUser);
			transaction.commit();
			return "User updated";
		} catch (Exception ex) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			ex.printStackTrace();
			return "Request failed";
		} finally {
			entityManager.close();
		}
	}

	public HashMap<String, Object> findUserByUserName(String username) throws UsernameNotFoundException {
		HashMap<String, Object> userMap = new HashMap<>();
		User userDetail = repository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
		userMap.put("displayname", userDetail.getUsername());
		userMap.put("email", userDetail.getEmail());
		userMap.put("roles", roleService.getRoleKey(userDetail.getRoleId()));
		return userMap;
	}

	public boolean userStatus(String email) {
		return true;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
		return new UserDetailsImpl(user);
	}
}
