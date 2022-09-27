package com.dhanush.tweetapp.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.dhanush.tweetapp.model.*;
import com.dhanush.tweetapp.repository.TokenRepository;
import com.dhanush.tweetapp.repository.UserRepository;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1.0/tweets/")
@Slf4j
public class AuthenticationController {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private CustomUserDetailsManager customUserDetailsManager;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("username/{key}")
    public ResponseEntity<Boolean> findIfUsernameExist(@PathVariable String key){
        log.info("Finding username already existing with the key {}",key);
        return ResponseEntity.ok().body(userRepository.existsByUsername(key));
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("Attempting login");
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequest.getUsername(), loginRequest.getPassword()
                            )
                    );
            UserDetails user = (CustomUserDetails) authenticate.getPrincipal();
            log.info("Creating refresh Token");
            Token refreshToken = new Token(user.getUsername(), UUID.randomUUID().toString());
            tokenRepository.save(refreshToken);
            log.info("Creating access Token");
            String token = jwtTokenProvider.createToken(user.getUsername());
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("refresh-token", refreshToken.getToken());
            responseBody.put("access-token", token);
            log.info("Sending tokens to the user");
            return ResponseEntity.ok().body(responseBody);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PostMapping("register")
    public ResponseEntity<?> signup(@RequestBody @Valid User userRequest) throws RuntimeException {
        log.info("Creating a new user");
        User newUser = new User();
        newUser.setUsername(userRequest.getUsername());
        newUser.setPassword(userRequest.getPassword());
        newUser.setFirstName(userRequest.getFirstName());
        newUser.setLastName(userRequest.getLastName());
        newUser.setEmail(userRequest.getEmail());
        CustomUserDetails user = new CustomUserDetails(newUser);
        customUserDetailsManager.createUser(user);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("{username}/forgot")
    public ResponseEntity<?> changePassword(@PathVariable String username,@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        log.info("Changing the password for an user");
        customUserDetailsManager.changePassword("", changePasswordRequest.getNewPassword());
        return ResponseEntity.ok().build();
    }


    @PostMapping("sign-out")
    public ResponseEntity<?> signout(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        log.info("Signing out an user and Removing their refresh token from DB");
        tokenRepository.deleteByToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok().build();
    }


    @PostMapping ("createAccessToken")
    public ResponseEntity<String> createAccessToken(@RequestBody @Valid RefreshTokenRequest refreshToken) {
        log.info("Creating an access token");
        Optional<Token> optionalToken = tokenRepository.findByToken(refreshToken.getRefreshToken());
        if (optionalToken.isEmpty()) {
            return ResponseEntity.badRequest().body("unauthorized");
        }
        UserDetails userDetails = customUserDetailsManager.loadUserByUsername(optionalToken.get().getUsername());
        log.info("Found the user belonging to the refresh token and creating new access token for them");
        String token = jwtTokenProvider.createToken(userDetails.getUsername());

        return ResponseEntity.ok().body(token);
    }


}
