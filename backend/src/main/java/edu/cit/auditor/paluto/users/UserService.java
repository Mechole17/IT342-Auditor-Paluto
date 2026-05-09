package edu.cit.auditor.paluto.service;

import edu.cit.auditor.paluto.core.entities.User;
import edu.cit.auditor.paluto.core.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
