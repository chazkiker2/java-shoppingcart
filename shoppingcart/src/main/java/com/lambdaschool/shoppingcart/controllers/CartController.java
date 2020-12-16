package com.lambdaschool.shoppingcart.controllers;


import com.lambdaschool.shoppingcart.models.CartItem;
import com.lambdaschool.shoppingcart.models.User;
import com.lambdaschool.shoppingcart.services.CartItemService;
import com.lambdaschool.shoppingcart.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/carts")
public class CartController {

	private final CartItemService cartItemService;
	private final UserService     userService;

	@Autowired
	public CartController(
			CartItemService cartItemService,
			UserService userService
	) {
		this.cartItemService = cartItemService;
		this.userService     = userService;
	}

	@GetMapping(value = "/user",
	            produces = {"application/json"})
	public ResponseEntity<?> listCartItemsByUserId(Authentication authentication) {
		User u = userService.findByName(authentication.getName());
		return new ResponseEntity<>(
				u,
				HttpStatus.OK
		);
	}

	@PutMapping(value = "/add/product/{productid}",
	            produces = {"application/json"})
	public ResponseEntity<?> addToCart(
			@PathVariable
					long productid,
			Authentication authentication
	) {
		User u = userService.findByName(authentication.getName());
		CartItem addCartItem = cartItemService.addToCart(
				u.getUserid(),
				productid,
				"I am not working"
		);
		return new ResponseEntity<>(
				addCartItem,
				HttpStatus.OK
		);
	}

	@DeleteMapping(value = "/remove/product/{productid}",
	               produces = {"application/json"})
	public ResponseEntity<?> removeFromCart(
			@PathVariable
					long productid,
			Authentication authentication
	) {
		User u = userService.findByName(authentication.getName());
		CartItem removeCartItem = cartItemService.removeFromCart(
				u.getUserid(),
				productid,
				"I am still not working"
		);
		return new ResponseEntity<>(
				removeCartItem,
				HttpStatus.OK
		);
	}

}
