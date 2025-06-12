package com.becoder.service.impl;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.becoder.config.security.CustomUserDetails;
import com.becoder.dto.EmailRequest;
import com.becoder.dto.LoginRequest;
import com.becoder.dto.LoginResponse;
import com.becoder.dto.UserRequest;
import com.becoder.dto.UserResponse;
import com.becoder.entity.AccountStatus;
import com.becoder.entity.Role;
import com.becoder.entity.User;
import com.becoder.repository.RoleRepository;
import com.becoder.repository.UserRepository;
import com.becoder.service.EmailService;
import com.becoder.service.JwtService;
import com.becoder.service.AuthService;
import com.becoder.util.Validation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private Validation validation;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private EmailService emailService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;

	@Override
	public Boolean register(UserRequest userRequest, String url) throws Exception {
		log.info("AuthServiceImpl : register() : Execution Start");
		validation.userValidation(userRequest);
		User user = mapper.map(userRequest, User.class);
		setRole(userRequest, user);
		AccountStatus status = AccountStatus.builder().isActive(false).verificationCode(UUID.randomUUID().toString())
				.build();
		user.setStatus(status);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User saveUser = userRepo.save(user);
		if (!ObjectUtils.isEmpty(saveUser)) {
			log.info("Error : {}", "user not saved");
			return false;
		}
		log.info("Message : {}","User Register success");
		// send email
		emailSendForRegister(saveUser, url);
		log.info("Message : {}","email send success");
		log.info("AuthServiceImpl : register() : Execution End");
		return true;
	}

	private void emailSendForRegister(User saveUser, String url) throws Exception {

		String message = "Hi,<b>[[username]]</b> " + "<br> Your account register sucessfully.<br>"
				+ "<br> Click the below link verify & Active your account <br>"
				+ "<a href='[[url]]'>Click Here</a> <br><br>" + "Thanks,<br>Enotes.com";

		message = message.replace("[[username]]", saveUser.getFirstName());
		message = message.replace("[[url]]", url + "/api/v1/home/verify?uid=" + saveUser.getId() + "&&code="
				+ saveUser.getStatus().getVerificationCode());

		EmailRequest emailRequest = EmailRequest.builder().to(saveUser.getEmail())
				.title("Account Creating Confirmation").subject("Account Created Success").message(message).build();
		emailService.sendEmail(emailRequest);
	}

	private void setRole(UserRequest userRequest, User user) {
		List<Integer> reqRoleId = userRequest.getRoles().stream().map(r -> r.getId()).toList();
		List<Role> roles = roleRepo.findAllById(reqRoleId);
		user.setRoles(roles);
	}

	@Override
	public LoginResponse login(LoginRequest loginRequest) {

		Authentication authenticate = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		if (authenticate.isAuthenticated()) {
			CustomUserDetails customUserDetails = (CustomUserDetails) authenticate.getPrincipal();

			String token = jwtService.GenerateToken(customUserDetails.getUser());

			LoginResponse loginResponse = LoginResponse.builder()
					.user(mapper.map(customUserDetails.getUser(), UserResponse.class)).token(token).build();
			return loginResponse;
		}
		return null;
	}

}
