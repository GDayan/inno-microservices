package com.innowise.service;

import com.innowise.model.entity.User;
import com.innowise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(User user){
        return userRepository.save(user);
    }

    public Optional<User> getById(Long id){
        return userRepository.findById(id);
    }

}
