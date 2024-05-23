package com.jaywant.rentify.service;

import com.jaywant.rentify.exception.UserException;
import com.jaywant.rentify.models.User;

public interface UserService {
	
	public User findUserById(Integer userId) throws UserException;
	
	public User findUserProfileByJwt(String jwt) throws UserException;

}
