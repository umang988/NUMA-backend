package com.numa.user.controller;

import com.numa.user.entity.User;
import com.numa.user.entity.UserRole;
import com.numa.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;



    //User
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getUserById(id));
    }

    @GetMapping("/get/all")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "q", required = false) String searchQuery,
            @RequestParam(value = "sort", defaultValue = "updatedAt") String sortBy,
            @RequestParam(value = "order", defaultValue = "desc") String sortOrder
    ) {
        return ResponseEntity.ok(service.getAllUsers(page, size, searchQuery, sortBy, sortOrder));
    }

    @GetMapping("/get/all/roleId/{roleId}")
    public ResponseEntity<Page<User>> getAllUsersByRodeId(
            @PathVariable Long roleId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "q", required = false) String searchQuery,
            @RequestParam(value = "sort", defaultValue = "updatedAt") String sortBy,
            @RequestParam(value = "order", defaultValue = "desc") String sortOrder
    ) {
        return ResponseEntity.ok(service.getAllUsersByRodeId(roleId, page, size, searchQuery, sortBy, sortOrder));
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(service.createUser(user));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(service.updateUser(id, user));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.ok().build();
    }



    //UserRole
    @GetMapping("/role/{id}")
    public ResponseEntity<UserRole> getUserRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getUserRoleById(id));
    }

    @GetMapping("/role/get/all")
    public ResponseEntity<Page<UserRole>> getAllUserRoles(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "q", required = false) String searchQuery,
            @RequestParam(value = "sort", defaultValue = "updatedAt") String sortBy,
            @RequestParam(value = "order", defaultValue = "desc") String sortOrder
    ) {
        return ResponseEntity.ok(service.getAllUserRoles(page, size, searchQuery, sortBy, sortOrder));
    }

    @PostMapping("/role/create")
    public ResponseEntity<UserRole> createUserRole(@Valid @RequestBody UserRole role) {
        return ResponseEntity.ok(service.createUserRole(role));
    }

    @PutMapping("/role/update/{id}")
    public ResponseEntity<UserRole> updateUserRole(@PathVariable Long id, @RequestBody UserRole role) {
        return ResponseEntity.ok(service.updateUserRole(id, role));
    }

    @DeleteMapping("/role/delete/{id}")
    public ResponseEntity<Void> deleteUserRole(@PathVariable Long id) {
        service.deleteUserRole(id);
        return ResponseEntity.ok().build();
    }
}