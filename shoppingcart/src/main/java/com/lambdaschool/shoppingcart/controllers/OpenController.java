package com.lambdaschool.shoppingcart.controllers;


import com.lambdaschool.shoppingcart.models.User;
import com.lambdaschool.shoppingcart.models.UserMinimum;
import com.lambdaschool.shoppingcart.models.UserRoles;
import com.lambdaschool.shoppingcart.services.RoleService;
import com.lambdaschool.shoppingcart.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Transactional
@RestController
public class OpenController {

	private final UserService userService;
	private final RoleService roleService;

	@Autowired
	public OpenController(
			UserService userService,
			RoleService roleService
	) {
		this.userService = userService;
		this.roleService = roleService;
	}

	/**
	 * This endpoint always anyone to create an account with the default role of USER. That role is hardcoded in this method.
	 *
	 * @param httpServletRequest the request that comes in for creating the new user
	 * @param newMinUser         A special minimum set of data that is needed to create a new user
	 *
	 * @return The token access and other relevant data to token access. Status of CREATED. The location header to look
	 * 		up the new user.
	 *
	 * @throws URISyntaxException we create some URIs during this method. If anything goes wrong with that creation, an exception is thrown.
	 */
	@Transactional
	@PostMapping(value = "/createnewuser",
	             consumes = "application/json",
	             produces = "application/json")
	public ResponseEntity<?> addSelf(
			HttpServletRequest httpServletRequest,
			@Valid
			@RequestBody
					UserMinimum newMinUser
	)
			throws
			URISyntaxException {
		User newUser = new User();

		newUser.setUsername(newMinUser.getUsername());
		newUser.setPassword(newMinUser.getPassword());
		newUser.setPrimaryemail(newMinUser.getPrimaryemail());

		// add the default role of user
		Set<UserRoles> newRoles = new HashSet<>();
		newRoles.add(new UserRoles(
				newUser,
				roleService.findByName("user")
		));
		newUser.setRoles(newRoles);

		newUser = userService.save(newUser);

		// set the location header for the newly created resource
		// The location comes from a different controller!
		HttpHeaders responseHeaders = new HttpHeaders();
		URI newUserURI = ServletUriComponentsBuilder.fromUriString(
				httpServletRequest.getServerName() + ":" + httpServletRequest.getLocalPort() + "/users/user/{userId}")
		                                            .buildAndExpand(newUser.getUserid())
		                                            .toUri();
		responseHeaders.setLocation(newUserURI);

		// return the access token
		// To get the access token, surf to the endpoint /login (which is always on the server where this is running)
		// just as if a client had done this.
		RestTemplate restTemplate = new RestTemplate();
		String       requestURI   = "http://localhost" + ":" + httpServletRequest.getLocalPort() + "/login";

		List<MediaType> acceptableMediaTypes = new ArrayList<>();
		acceptableMediaTypes.add(MediaType.APPLICATION_JSON);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setAccept(acceptableMediaTypes);
		//        headers.setBasicAuth(System.getenv("OAUTHCLIENTID"),
		//            System.getenv("OAUTHCLIENTSECRET"));

		headers.setBasicAuth(
				"lambda-client",
				"lambda-secret"
		);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add(
				"grant_type",
				"password"
		);
		map.add(
				"scope",
				"read write trust"
		);
		map.add(
				"username",
				newMinUser.getUsername()
		);
		map.add(
				"password",
				newMinUser.getPassword()
		);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(
				map,
				headers
		);

		String theToken = restTemplate.postForObject(
				requestURI,
				request,
				String.class
		);

		return new ResponseEntity<>(
				theToken,
				responseHeaders,
				HttpStatus.CREATED
		);

	}

	/**
	 * Prevents no favicon.ico warning from appearing in the logs. @ApiIgnore tells Swagger to ignore documenting this as an endpoint.
	 */
	@ApiIgnore
	@GetMapping("favicon.ico")
	public void returnNoFavicon() {}

}
