package sec.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sec.model.User;
import sec.model.service.AuthService;
@CrossOrigin(origins = "https://thehealthconsultor.netlify.app", maxAge = 7200, allowCredentials = "true")
@RestController
@RequestMapping("/api/user-auth")
public class TestController {
	
	@Autowired
	private AuthService authService;
	
	@GetMapping("/users")
	public List<User> getUsers(){
		return authService.getAllUsers();
	}
	
	@GetMapping("/logonDetails/{username}")
	public ResponseEntity<?> getLogonInfo(@PathVariable("username") String username)
	{
		return authService.getLogonInfoAsPerUserName(username);
	}
	
	@GetMapping("/view/{username}")
	public Optional<User> getByUsername(@PathVariable String username){
		return authService.getDetailsByUsername(username);
	}
	
	@DeleteMapping("/delete/{username}")
	public void deleteUsers(@PathVariable("username") String username) {
		authService.deleteUsers(username);
	}
	
	@PutMapping("/logout/{username}")
	public String logout(@PathVariable("username") String username)
	{
		return authService.logoutUser(username);
	}
}
