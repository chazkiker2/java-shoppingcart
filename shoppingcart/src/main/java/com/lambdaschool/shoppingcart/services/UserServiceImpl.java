package com.lambdaschool.shoppingcart.services;


import com.lambdaschool.shoppingcart.exceptions.ResourceFoundException;
import com.lambdaschool.shoppingcart.exceptions.ResourceNotFoundException;
import com.lambdaschool.shoppingcart.models.Role;
import com.lambdaschool.shoppingcart.models.User;
import com.lambdaschool.shoppingcart.models.UserRoles;
import com.lambdaschool.shoppingcart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * Implements UserService Interface
 */
@Transactional
@Service(value = "userService")
public class UserServiceImpl
		implements UserService {

	/**
	 * Connects this service to the User table.
	 */
	private final UserRepository userRepo;

	/**
	 * Connects this service to the Role table
	 */
	private final RoleService roleService;

	@Autowired
	public UserServiceImpl(
			UserRepository userRepo,
			RoleService roleService
	) {
		this.userRepo    = userRepo;
		this.roleService = roleService;
	}

	@Override
	public List<User> findAll() {
		List<User> list = new ArrayList<>();
		/*
		 * findAll returns an iterator set.
		 * iterate over the iterator set and add each element to an array list.
		 */
		userRepo.findAll()
		        .iterator()
		        .forEachRemaining(list::add);
		return list;
	}

	@Override
	public List<User> findByNameContaining(String username) {
		return userRepo.findByUsernameContainingIgnoreCase(username.toLowerCase());
	}

	public User findUserById(long id)
			throws
			ResourceNotFoundException {
		return userRepo.findById(id)
		               .orElseThrow(() -> new ResourceNotFoundException("User id " + id + " not found!"));
	}

	@Override
	public User findByName(String name) {
		User uu = userRepo.findByUsername(name.toLowerCase());
		if (uu == null) {
			throw new ResourceNotFoundException("User name " + name + " not found!");
		}
		return uu;
	}

	@Transactional
	@Override
	public void delete(long id) {
		userRepo.findById(id)
		        .orElseThrow(() -> new ResourceNotFoundException("User id " + id + " not found!"));
		userRepo.deleteById(id);
	}

	@Transactional
	@Override
	public User save(User user) {
		if (user.getCarts()
		        .size() > 0) {
			throw new ResourceFoundException("Carts are not created via Users");
		}

		User newUser = new User();

		if (user.getUserid() != 0) {
			newUser = userRepo.findById(user.getUserid())
			                  .orElseThrow(() -> new ResourceNotFoundException(
					                  "User id " + user.getUserid() + " not found!"));
		}

		newUser.setUsername(user.getUsername()
		                        .toLowerCase());
		newUser.setPassword(user.getPassword());
		newUser.setPrimaryemail(user.getPrimaryemail()
		                            .toLowerCase());

		newUser.getRoles()
		       .clear();
		for (UserRoles ur : user.getRoles()) {
			Role addRole = roleService.findRoleById(ur.getRole()
			                                          .getRoleid());
			newUser.getRoles()
			       .add(new UserRoles(
					       newUser,
					       addRole
			       ));
		}

		return userRepo.save(newUser);
	}

	@Transactional
	@Override
	public User update(
			User user,
			long id
	) {
		if (user.getCarts()
		        .size() > 0) {
			throw new ResourceFoundException("Carts are not updated via Users");
		}

		User currentUser = findUserById(id);

		if (user.getUsername() != null) {
			currentUser.setUsername(user.getUsername()
			                            .toLowerCase());
		}

		if (user.getPassword() != null) {
			currentUser.setPassword(user.getPassword());
		}

		if (user.getPrimaryemail() != null) {
			currentUser.setPrimaryemail(user.getPrimaryemail()
			                                .toLowerCase());
		}

		if (user.getComments() != null) {
			currentUser.setComments(user.getComments());
		}

		if (user.getRoles()
		        .size() > 0) {
			currentUser.getRoles()
			           .clear();
			for (UserRoles ur : user.getRoles()) {
				Role addRole = roleService.findRoleById(ur.getRole()
				                                          .getRoleid());

				currentUser.getRoles()
				           .add(new UserRoles(
						           currentUser,
						           addRole
				           ));
			}
		}

		return userRepo.save(currentUser);
	}

	@Transactional
	@Override
	public void deleteAll() {
		userRepo.deleteAll();
	}

}
