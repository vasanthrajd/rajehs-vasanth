package com.careerin.api.service;

import com.careerin.api.dto.RegisterUserDto;
import com.careerin.api.dto.UsersDto;
import com.careerin.api.exception.ResourceNotFoundException;
import com.careerin.api.model.Role;
import com.careerin.api.model.User;
import com.careerin.api.model.UserTokenStatus;
import com.careerin.api.repository.RoleRepository;
import com.careerin.api.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.LoginException;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	/*@Autowired
	private UserRoleMapRepository userrolemapRepository;

	@Autowired
	private RolePermissionMapRepository rolePermissionMapRepository;
*/
	private final UserTokenStatusService userTokenStatusService;

	private final RefreshTokenService refreshTokenService;

	public UserServiceImpl(final UserTokenStatusService userTokenStatusService,
                           final RefreshTokenService refreshTokenService) {
		this.userTokenStatusService = userTokenStatusService;
		this.refreshTokenService = refreshTokenService;
	}

	@Override
	public Page<UsersDto> getUsers(Pageable page, Integer pageNo, Integer pageSize, String sortby, String sortOrder) {
		
		if (sortOrder.equalsIgnoreCase("Asc")) {
			page = PageRequest.of(pageNo, pageSize, Sort.by(sortby).ascending());
		} else {
			page = PageRequest.of(pageNo, pageSize, Sort.by(sortby).descending());
		}
		return userRepository.findAll(page).map(users -> {
			UsersDto uDto = new UsersDto();
			BeanUtils.copyProperties(users, uDto);
			return uDto;
		});
	}

	@Override
	public UsersDto save(@Valid UsersDto userdto) {
		final ModelMapper modelmapper = new ModelMapper();
		User user = modelmapper.map(userdto, User.class);
		String passwordEncrypt = passwordEncoder.encode(user.getPassword());
		user.setPassword(passwordEncrypt);
		user.setActive(Boolean.TRUE);
		user = userRepository.save(user);
		modelmapper.map(user, userdto);
		return userdto;
	}

	/*@Override
	public UserInfoDto userByEmailId(String emailId) {
		final Users userfoundEmail = userRepository.findByEmail(emailId)
				.orElseThrow(() -> new RuntimeException("emailId not found"));
		UserInfoDto userInfoDto = new UserInfoDto();
		CustomUserInfo customUserInfo = new CustomUserInfo();
		RoleDto roleDtoTemp = new RoleDto();
		PermissionDto rpDto = new PermissionDto();
		BeanUtils.copyProperties(userfoundEmail, customUserInfo);
		userInfoDto.setUser(customUserInfo);

		final List<UserRoleMap> useRoleMapList = userrolemapRepository.findByUserId(userfoundEmail.getId());

		List<Long> roleId = useRoleMapList.stream().map(UserRoleMap::getRoleId).collect(Collectors.toList());

		if (!useRoleMapList.isEmpty()) {
			useRoleMapList.forEach(roleMap -> {
				BeanUtils.copyProperties(roleMap.getRole(), roleDtoTemp);
				userInfoDto.setRoleName(roleDtoTemp.getRoleName());
			});
		} else {
			throw new RuntimeException("User is not assigned to any role.");
		}
		List<RolePermissionMap> rolepermission = rolePermissionMapRepository.findByRoleIdIn(roleId);
		List<String> ex = new ArrayList<String>();
		rolepermission.forEach(permission -> {
			BeanUtils.copyProperties(permission.getPermission(), rpDto);
			ex.add(rpDto.getPermissionName());
		});
		userInfoDto.setPermissionName(ex);
		return userInfoDto;
	}

*/	@Override
	public Optional<User> findByEmail(final String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public UsersDto userFindById(Long userid) {
		// TODO Auto-generated method stub
		Optional<User> userfoundId = userRepository.findById(userid);
		if (userfoundId.isPresent()) {
			User user = userRepository.findById(userid).get();
			UsersDto dto = new UsersDto();
			ModelMapper modelmapper = new ModelMapper();
			modelmapper.map(user, dto);
			return dto;
		} else {
			throw new RuntimeException("Id not found");
		}
	}

	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}

	/*@Override
	public UsersDto update(UsersDto userdto) {
		Optional<User> userfoundId = userRepository.findById(userdto.getId());
		if (userfoundId.isPresent()) {
			ModelMapper modelmapper = new ModelMapper();
			User user = modelmapper.map(userdto, User.class);
			int mobileCount = 10;
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user = userRepository.save(user);
			return userdto;
		} else {
			throw new RuntimeException("Id not found");
		}
	}

	@Override
	public Map<String, Boolean> deleteAll() {
		// TODO Auto-generated method stub
		Map<String, Boolean> response = new HashMap<>();
		userRepository.deleteAll();
		response.put("deleted", Boolean.TRUE);
		return response;
	}

	@Override
	public Map<String, Boolean> delete(Long userid) {
		// TODO Auto-generated method stub
		Map<String, Boolean> response = new HashMap<>();
		Optional<Users> user = userRepository.findById(userid);
		if (user.isPresent()) {
			userRepository.deleteById(userid);
			response.put("deleted", Boolean.TRUE);
		} else {
			response.put("not success - UserId not found", Boolean.FALSE);
		}
		return response;
	}

	private PermissionDto buildPermissions(UserInfoDto userInfoDto, PermissionDto rpDto, List<Long> roleId) {
		List<RolePermissionMap> rolepermission = rolePermissionMapRepository.findByRoleIdIn(roleId);
		List<String> ex = new ArrayList<String>();
		rolepermission.forEach(permission -> {
			BeanUtils.copyProperties(permission.getPermission(), rpDto);
			ex.add(rpDto.getPermissionName());
		});
		userInfoDto.setPermissionName(ex);

		return rpDto;
	}

	private CustomUserInfo buildUserInfo(String emailId, CustomUserInfo customUserInfo, UserInfoDto userInfoDto) {
		Users userfoundEmail = userRepository.findByEmail(emailId)
				.orElseThrow(() -> new RuntimeException("emailId not found"));
		BeanUtils.copyProperties(userfoundEmail, customUserInfo);
		userInfoDto.setUser(customUserInfo);
		return customUserInfo;
	}

	private List<Long> buildRoles(Long id, RoleDto roleDtoTemp, UserInfoDto userInfoDto) {
		List<UserRoleMap> useRoleMapList = userrolemapRepository.findByUserId(id);
		List<Long> roleId = useRoleMapList.stream().map(UserRoleMap::getRoleId).collect(Collectors.toList());
		if (!useRoleMapList.isEmpty()) {
			useRoleMapList.forEach(roleMap -> {
//				roleMap.getRole().forEach(role -> {
//					BeanUtils.copyProperties(role, roleDtoTemp);
//					userInfoDto.setRoleName(roleDtoTemp.getRoleName());
//				});
			});

		} else {
			throw new RuntimeException("User is not assigned to any role.");
		}
		return roleId;
	}
*/
	@Override
	public Boolean existsByEmail(final String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public User createUser(final UsersDto usersDto) {
		// @Todo: Create the usersDto
		return null;
	}

	@Override
	public void logoutUser(final Long id) throws LoginException {
		final UserTokenStatus userTokenStatus = userTokenStatusService.findByUserId(id)
				.orElseThrow(() -> new LoginException("user logged out already"));
		log.info("Removing refresh token associated with user [" + id + "]");
		refreshTokenService.deleteById(userTokenStatus.getRefreshToken().getId());
	}

	@Override
	public User saveUsers(User users) {
		return userRepository.save(users);
	}

	@Override
	@Transactional
	public UsersDto save(@Valid String roleName, UsersDto usersDto) {
		Role role = roleRepository.findByRoleDescription(roleName)
				.orElseThrow(()-> new ResourceNotFoundException("Role", roleName, "Not Found"));

		final ModelMapper modelmapper = new ModelMapper();
		User user = modelmapper.map(usersDto, User.class);
		String passwordEncrypt = passwordEncoder.encode(user.getPassword());
		user.setUsername(usersDto.getEmail());
		user.setPassword(passwordEncrypt);
		user.setActive(Boolean.TRUE);
		user.addRole(role);
		user = userRepository.save(user);
		modelmapper.map(user, usersDto);
		return usersDto;
	}


}
