package com.spring.dao;

import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.spring.entities.User;

public interface UserRepository extends JpaRepository<User,Integer>
{
	@Query("select u FROM User u WHERE u.email = :email")
    public User getUserByUserName(@Param("email") String email);
}
