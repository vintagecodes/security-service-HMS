package sec.model.service;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import exception.CustomException;
import io.jsonwebtoken.security.InvalidKeyException;
import jakarta.security.auth.message.AuthException;
import sec.Controller.AuthController;
import sec.model.ERole;
import sec.model.Role;
import sec.model.User;
import sec.payLoad.JwtResponse;
import sec.payLoad.LoginRequest;
import sec.payLoad.MessageResponse;
import sec.payLoad.SignupRequest;
import sec.repository.LogonInfoRepository;
import sec.repository.RoleRepository;
import sec.repository.UserRepository;
import sec.security.JwtUtils;
import sec.security.UserDetailsImpl;

@Service
public class AuthService {
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	LogonInfoRepository logonInfoRepository;
	@Autowired
	PasswordEncoder encoder;
	@Autowired
	JwtUtils jwtUtils;

	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private SequenceGeneratorService generatorService;

	
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	
//	usage: for authenticate the user by login and in response it will give the JWT token 
	
	public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) throws InvalidKeyException, UnsupportedEncodingException{
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		if(userRepository.existsByUsername(loginRequest.getUsername()) == false) {
			AuthException authException = new AuthException();
			logger.info(authException.getMessage());
			return ResponseEntity.badRequest().body(new MessageResponse("UserName not found!"));
		}
			
		List<String> roles = userDetails.getAuthorities().stream()
					.map(item -> item.getAuthority())
					.collect(Collectors.toList());
		JwtResponse jwtResponse = new JwtResponse(jwt, 
				 userDetails.getUserId(), 
				 userDetails.getUsername(), 
				 userDetails.getEmail(),
				 roles);
		logonInfoRepository.save(jwtResponse);
			return ResponseEntity.ok(jwtResponse);
	}
	
	
	public ResponseEntity<?> registerUser(SignupRequest signUpRequest) throws CustomException, Exception{
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}
		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}
		// Create new user's account
		User user = new User(signUpRequest.getUserId(),
							signUpRequest.getUsername(), 
							 signUpRequest.getEmail(),
							 encoder.encode(signUpRequest.getPassword())
							 
				);
		
		
		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);
					break;
				case "doctor":
					Role doctorRole = roleRepository.findByName(ERole.ROLE_DOCTOR)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(doctorRole);
					break;
				default:
					Role patientRole = roleRepository.findByName(ERole.ROLE_PATIENT)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(patientRole);
				}
			});
		user.setUserId(String.valueOf(generatorService.generateSequence(User.SEQUENCE_NAME)));
		user.setRoles(roles);
		userRepository.save(user);
		
//		for sending the email to the signup user
		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
	
	
	public List<User> getAllUsers(){
		return userRepository.findAll();
	}
	
	public ResponseEntity<?> getLogonInfoAsPerUserName(String username)
	{
		JwtResponse jwtResponse =  logonInfoRepository.findByUsername(username);
		if(null == jwtResponse)
		{
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().body(jwtResponse);
	}
	
	public Optional<User> getDetailsByUsername(String username) {
		return userRepository.findByUsername(username);
	}
	
	public void deleteUsers(String username) {
		userRepository.deleteByUsername(username);
	}
	
	public String logoutUser(String username)
	{
		logonInfoRepository.deleteByUsername(username);
		return "Logged Off Successfully!";
	}
	
	public LogoutConfigurer<HttpSecurity> logout(HttpSecurity http) throws Exception {
		return http.logout();
	}

}
