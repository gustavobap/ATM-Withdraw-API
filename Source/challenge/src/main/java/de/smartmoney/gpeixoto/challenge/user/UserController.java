package de.smartmoney.gpeixoto.challenge.user;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import de.smartmoney.gpeixoto.challenge.shared.ApplicationController;

@RestController
@RequestMapping("api/users")
public class UserController extends ApplicationController {

	private final UserService service;

	public UserController(UserService service) {
		this.service = service;
	}
	
    @GetMapping("{code}")
    public ResponseEntity<User> find(@PathVariable("code") Long code) {
        Optional<User> user= service.find(code);
        return ResponseEntity.of(user);
    }
    
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
    	user = service.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{code}")
                .buildAndExpand(user.getCode())
                .toUri();
        return ResponseEntity.created(location).body(user);
    }
    
	@GetMapping()
	public ResponseEntity<List<User>> list() {
		return ResponseEntity.ok().body(service.list());
	}

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<Map<String, String>> handleConstraintExceptions(DataIntegrityViolationException ex) {
        Map<String, String> map = new HashMap<>(1);
        //TODO create a parameter specifically for this check
        if(ex.getMostSpecificCause().getMessage().contains(User.UNIQUE_EMAIL_CONSTRAINT_NAME)) {
        	map.put("email", "e-mail is already registered");
        }
        return ResponseEntity.badRequest().body(map);
    }
}


