package com.innowise.service;

import com.innowise.model.entity.User;
import com.innowise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(User user){
        return userRepository.save(user);
    }

    public Optional<User> getById(Long id){
        return userRepository.findById(id);
    }

    public List<User> getUserByIds(List<Long> ids){
        return userRepository.findByIds(ids);
    }

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User updateUser(Long id, User updated){
        return userRepository.findById(id).map(user -> {
            user.setName(updated.getName());
            user.setSurname(updated.getSurname());
            user.setEmail(updated.getEmail());
            user.setBirthDate(updated.getBirthDate());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }
}
