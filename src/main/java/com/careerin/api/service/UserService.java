package com.careerin.api.service;

import com.careerin.api.dto.RegisterUserDto;
import com.careerin.api.dto.UsersDto;
import com.careerin.api.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.security.auth.login.LoginException;
import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

public interface UserService {

	Page<UsersDto> getUsers(Pageable page, Integer pageNo, Integer pageSize, String sortBy, String sortOrder);

	UsersDto save(@Valid UsersDto userdto);

	Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    User createUser(UsersDto usersDto);

	void logoutUser(Long id) throws LoginException;
	
	User saveUsers(User users);

	UsersDto userFindById(Long id);

	UsersDto save(String roleName, UsersDto usersDto);
}
