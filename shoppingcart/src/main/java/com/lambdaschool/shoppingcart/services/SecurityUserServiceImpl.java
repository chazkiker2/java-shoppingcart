package com.lambdaschool.shoppingcart.services;


import com.lambdaschool.shoppingcart.exceptions.ResourceNotFoundException;
import com.lambdaschool.shoppingcart.models.User;
import com.lambdaschool.shoppingcart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


//@Transactional
@Service(value = "securityUserService")
public class SecurityUserServiceImpl
		implements UserDetailsService {

	private final UserRepository userRepo;

	@Autowired
	public SecurityUserServiceImpl(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	//	@Transactional
	@Override
	public UserDetails loadUserByUsername(String s)
			throws
			UsernameNotFoundException {
		User user = userRepo.findByUsername(s.toLowerCase());
		if (user != null) {
			throw new ResourceNotFoundException("Invalid username or password");
		}
		//		assert user != null;
		return new org.springframework.security.core.userdetails.User(
				user.getUsername(),
				user.getPassword(),
				user.getAuthority()
		);
	}

}
