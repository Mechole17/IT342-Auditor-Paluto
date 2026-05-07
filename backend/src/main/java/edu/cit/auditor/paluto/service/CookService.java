package edu.cit.auditor.paluto.service;

import edu.cit.auditor.paluto.dto.CookRegistrationDTO;
import edu.cit.auditor.paluto.entity.Cook;
import edu.cit.auditor.paluto.exception.EmailAlreadyExistsException;
import edu.cit.auditor.paluto.repository.CookRepository;
import edu.cit.auditor.paluto.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CookService {
    private final UserRepository userRepository;
    private final CookRepository cookRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Cook registerCook(CookRegistrationDTO dto){
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use.");
        }
        Cook newCook = Cook.builder()
                //USER attr
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .address(dto.getAddress())
                .role("COOK")
                .authProvider("LOCAL")
                .createdAt(LocalDateTime.now())
                //COOK attr
                .hourlyRate(dto.getHourlyRate() != null ? dto.getHourlyRate() : BigDecimal.ZERO)
                .yearsXp(dto.getYearsXp())
                .bio(dto.getBio())
                .build();

        return cookRepository.save(newCook);
    }
}
