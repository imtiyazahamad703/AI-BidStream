package com.bidstream.service;

import com.bidstream.dto.UserRegistrationDto;
import com.bidstream.entity.Role;
import com.bidstream.entity.User;
import com.bidstream.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerUser(UserRegistrationDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setRole(Role.valueOf(dto.getRole().toUpperCase()));
        
        // Password hashing will be implemented in the next commit
        user.setPassword(dto.getPassword()); 

        return userRepository.save(user);
    }
}
