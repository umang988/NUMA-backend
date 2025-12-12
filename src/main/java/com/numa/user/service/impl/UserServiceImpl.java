package com.numa.user.service.impl;

import com.numa.generic.GenericSpecification;
import com.numa.user.dao.UserInfoRepository;
import com.numa.user.dao.UserRoleRepository;
import com.numa.user.entity.User;
import com.numa.user.entity.UserRole;
import com.numa.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private GenericSpecification genericSpecification;



    //User
    @Override
    public User getUserById(Long id) {
        return genericSpecification.getEntityById(id, userRepository, "User");
    }

    @Override
    public Page<User> getAllUsers(int page, int size, String searchQuery, String sortBy, String sortOrder) {
        return genericSpecification.getAllEntities(User.class, userRepository, page, size, searchQuery, sortBy, sortOrder);
    }

    @Override
    public Page<User> getAllUsersByRodeId(Long roleId, int page, int size, String searchQuery, String sortBy, String sortOrder) {
        Map<String, Object> filters = Map.of("roleId", roleId);
        return genericSpecification.getAllWithFilterSearchAndPage(User.class, userRepository, filters, page, size, searchQuery, sortBy, sortOrder);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        return genericSpecification.saveOrUpdateEntityWithResponse(id, user, new User(), userRepository);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }



    //UserRole
    @Override
    public UserRole getUserRoleById(Long id) {
        return genericSpecification.getEntityById(id, userRoleRepository, "User role");
    }

    @Override
    public Page<UserRole> getAllUserRoles(int page, int size, String searchQuery, String sortBy, String sortOrder) {
        return genericSpecification.getAllEntities(UserRole.class, userRoleRepository, page, size, searchQuery, sortBy, sortOrder);
    }

    @Override
    public UserRole createUserRole(UserRole role) {
        return userRoleRepository.save(role);
    }

    @Override
    public UserRole updateUserRole(Long id, UserRole role) {
        return genericSpecification.saveOrUpdateEntityWithResponse(id, role, new UserRole(), userRoleRepository);
    }

    @Override
    public void deleteUserRole(Long id) {userRoleRepository.deleteById(id);}
}