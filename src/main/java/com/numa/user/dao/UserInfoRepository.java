package com.numa.user.dao;


import com.numa.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
  
@Repository
public interface UserInfoRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
	List<User> findAllByRoleId(Long roleId);
}