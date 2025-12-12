package com.numa.user.service;

import com.numa.user.entity.User;
import com.numa.user.entity.UserRole;
import org.springframework.data.domain.Page;

public interface UserService {

    //User
    User getUserById(Long id);
    Page<User> getAllUsers(int page, int size, String searchQuery, String sortBy, String sortOrder);
    Page<User> getAllUsersByRodeId(Long roleId, int page, int size, String searchQuery, String sortBy, String sortOrder);
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);

    //UserRole
    UserRole getUserRoleById(Long id);
    Page<UserRole> getAllUserRoles(int page, int size, String searchQuery, String sortBy, String sortOrder);
    UserRole createUserRole(UserRole role);
    UserRole updateUserRole(Long id, UserRole role);
    void deleteUserRole(Long id);
}
