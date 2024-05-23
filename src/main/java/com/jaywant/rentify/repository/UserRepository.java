package com.jaywant.rentify.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jaywant.rentify.models.User;

public interface UserRepository extends JpaRepository<User,Integer> {

	User findByEmail(String email);
	
}
