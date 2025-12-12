package com.numa.user.service;


import com.numa.user.entity.UserRole;

import java.util.List;

public interface UserRolesService {
	Long getRoleKey(Long id);

	List<UserRole> getAllUserRoles();

	UserRole getUserRoleById(Long id);

	UserRole createUserRole(UserRole userRole);

	UserRole updateUserRole(Long id, UserRole userRole);

	void deleteUserRole(Long id);
}
