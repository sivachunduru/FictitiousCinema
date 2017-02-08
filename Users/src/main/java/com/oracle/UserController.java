package com.oracle;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

	UsersDAO edao = new UsersListDAO();

	// Get all users
	@RequestMapping(method = RequestMethod.GET)
	public User[] getAll() {
		return (User[]) edao.getAllUsers().toArray(new User[0]);
	}

	// Get an user
	@RequestMapping(method = RequestMethod.GET, value = "{id}")
	public ResponseEntity get(@PathVariable String id) {

		User match = null;
		match = edao.getUser(id);

		if (match != null) {
			return new ResponseEntity<>(match, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	// Get user by Name
	@RequestMapping(method = RequestMethod.GET, value = "/username/{name}")
	public ResponseEntity getByUserName(@PathVariable String name) {

		List matchList = edao.getUserByName(name);

		if (matchList.size() > 0) {
			return new ResponseEntity<>(matchList.toArray(new User[0]), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}

	}

	// Add an user
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity add(@RequestBody User user) {

		if (edao.add(user)) {
			return new ResponseEntity<>("{'status':'Successfully inserted...'}", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>("{'status':'Failed to insert record...'}", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Update an user
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public ResponseEntity update(@PathVariable String id, @RequestBody User user) {

		if (edao.update(id, user)) {
			return new ResponseEntity<>("{'status':'Successfully updated...'}", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("{'status':'Error in updating the record...'}", HttpStatus.NOT_FOUND);
		}
	}

	// Delete an user
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity delete(@PathVariable String id) {

		boolean result = edao.delete(id);

		if (result) {
			return new ResponseEntity<>("{'status':'Successfully deleted...'}", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("{'status':'Error in deleting the record...'}", HttpStatus.NOT_FOUND);
		}
	}
}