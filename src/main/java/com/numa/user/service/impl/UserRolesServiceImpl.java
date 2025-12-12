package com.numa.user.service.impl;


import com.numa.user.dao.UserRoleRepository;
import com.numa.user.entity.UserRole;
import com.numa.user.service.UserRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRolesServiceImpl implements UserRolesService {

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Override
	public Long getRoleKey(Long id) {
		return userRoleRepository.findById(id).map(UserRole::getId).orElse(null);
	}

	@Override
	public List<UserRole> getAllUserRoles() {
		return userRoleRepository.findAll();
	}

	@Override
	public UserRole getUserRoleById(Long id) {
		return userRoleRepository.findById(id).orElse(null);
	}

	@Override
	public UserRole createUserRole(UserRole userRole) {
		return userRoleRepository.save(userRole);
	}

	@Override
	public UserRole updateUserRole(Long id, UserRole userRole) {
		if (userRoleRepository.existsById(id)) {
			userRole.setId(id);
			return userRoleRepository.save(userRole);
		}
		return null;
	}

	@Override
	public void deleteUserRole(Long id) {
		userRoleRepository.deleteById(id);
	}
}
