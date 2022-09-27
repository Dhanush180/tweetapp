package com.dhanush.tweetapp.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import com.dhanush.tweetapp.model.User;
import com.dhanush.tweetapp.repository.TokenRepository;
import com.dhanush.tweetapp.repository.UserRepository;

import java.util.Optional;

@Service
@Slf4j
public class CustomUserDetailsManager implements UserDetailsManager {

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsManager(UserRepository repository, PasswordEncoder encoder, TokenRepository tokenRepository) {
        this.userRepository = repository;
        this.passwordEncoder = encoder;
        this.tokenRepository = tokenRepository;
    }


    @Override
    public void createUser(UserDetails user) {

    }

    public void createUser(CustomUserDetails user) {
        try {
            User newUser = new User();
            newUser.setUsername(user.getUsername());
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            newUser.setPassword(encodedPassword);
            newUser.setFirstName(user.getFirstName());
            newUser.setLastName(user.getLastName());
            newUser.setEmail(user.getEmail());
            log.info("Saving the new user into DB");
            userRepository.save(newUser);
        } catch (DuplicateKeyException exception) {
            throw new RuntimeException("duplicate key");
        }
    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        user.ifPresent(value -> userRepository.delete(value));
    }


    @Override
    public void changePassword(String oldPassword, String newPassword) throws AuthenticationException {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userRepository.findByUsername(currentUser.getName());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("user does not exists");
        }
        User updateUser = user.get();

        String encodedPassword = passwordEncoder.encode(newPassword);
        updateUser.setPassword(encodedPassword);

        tokenRepository.deleteAllByUsername(updateUser.getUsername());
        userRepository.save(updateUser);
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("user does not exist");
        }
        return new CustomUserDetails(user.get());
    }
}
