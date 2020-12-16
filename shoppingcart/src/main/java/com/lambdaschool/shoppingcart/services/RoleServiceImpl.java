package com.lambdaschool.shoppingcart.services;


import com.lambdaschool.shoppingcart.exceptions.ResourceFoundException;
import com.lambdaschool.shoppingcart.exceptions.ResourceNotFoundException;
import com.lambdaschool.shoppingcart.models.Role;
import com.lambdaschool.shoppingcart.repository.RoleRepository;
import com.lambdaschool.shoppingcart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * Implements the RoleService Interface
 */
@Transactional
@Service(value = "roleService")
public class RoleServiceImpl
		implements RoleService {

	/**
	 * Connects this service to the Role Model
	 */
	private final RoleRepository roleRepo;

	/**
	 * Connect this service to the User Model
	 */
	private final UserRepository userRepo;

	/**
	 * Connects this service to the auditing service in order to get current user name
	 */
	private final UserAuditing userAuditing;

	@Autowired
	public RoleServiceImpl(
			UserRepository userRepo,
			RoleRepository roleRepo,
			UserAuditing userAuditing
	) {
		this.userRepo     = userRepo;
		this.roleRepo     = roleRepo;
		this.userAuditing = userAuditing;
	}


	@Override
	public List<Role> findAll() {
		List<Role> list = new ArrayList<>();
		/*
		 * findAll returns an iterator set.
		 * iterate over the iterator set and add each element to an array list.
		 */
		roleRepo.findAll()
		        .iterator()
		        .forEachRemaining(list::add);
		return list;
	}


	@Override
	public Role findRoleById(long id) {
		return roleRepo.findById(id)
		               .orElseThrow(() -> new ResourceNotFoundException("Role id " + id + " not found!"));
	}

	@Transactional
	@Override
	public Role save(Role role) {
		if (role.getUsers()
		        .size() > 0) {
			throw new ResourceFoundException("User Roles are not updated through Role.");
		}

		return roleRepo.save(role);
	}

	@Override
	public Role findByName(String name) {
		Role rr = roleRepo.findByNameIgnoreCase(name);

		if (rr != null) {
			return rr;
		} else {
			throw new ResourceNotFoundException(name);
		}
	}

	@Transactional
	@Override
	public void deleteAll() {
		roleRepo.deleteAll();
	}

	@Transactional
	@Override
	public Role update(
			long id,
			Role role
	) {
		if (role.getName() == null) {
			throw new ResourceNotFoundException("No role name found to update!");
		}

		if (role.getUsers()
		        .size() > 0) {
			throw new ResourceFoundException("User Roles are not updated through Role. See endpoint POST: users/user/{userid}/role/{roleid}");
		}

		Role newRole = findRoleById(id); // see if id exists

		roleRepo.updateRoleName(
				userAuditing.getCurrentAuditor()
				            .get(),
				id,
				role.getName()
		);
		return findRoleById(id);
	}

}
