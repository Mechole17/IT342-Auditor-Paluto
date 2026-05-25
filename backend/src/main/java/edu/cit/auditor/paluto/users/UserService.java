package edu.cit.auditor.paluto.users;

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

    public UpdateProfileDTO updateProfile(String email, UpdateProfileDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        user.setAddress(dto.getAddress());

        userRepository.save(user);

        return dto;
    }
}


