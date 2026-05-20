package edu.cit.auditor.paluto.users;

import edu.cit.auditor.paluto.core.entities.Cook;
import edu.cit.auditor.paluto.core.events.UserRegisteredEvent;
import edu.cit.auditor.paluto.infrastructure.exception.EmailAlreadyExistsException;
import edu.cit.auditor.paluto.core.repositories.CookRepository;
import edu.cit.auditor.paluto.core.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CookService {
    private final UserRepository userRepository;
    private final CookRepository cookRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

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

        Cook savedCook = cookRepository.save(newCook);
        eventPublisher.publishEvent(new UserRegisteredEvent(savedCook));
        System.out.println("Registration event successfully published for Cook: " + savedCook.getEmail());

        return savedCook;
    }

    public List<CookResponseDTO> getAllCooks() {
        return cookRepository.findAll().stream()
                .map(cook -> CookResponseDTO.builder()
                        .id(cook.getId())
                        .firstname(cook.getFirstname())
                        .lastname(cook.getLastname())
                        .hourlyRate(cook.getHourlyRate())
                        .yearsXp(cook.getYearsXp())
                        .bio(cook.getBio())
                        .build())
                .toList();
    }

    public CookResponseDTO getCookById(Long id) {
        Cook cook = cookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cook not found."));
        return CookResponseDTO.builder()
                .id(cook.getId())
                .firstname(cook.getFirstname())
                .lastname(cook.getLastname())
                .hourlyRate(cook.getHourlyRate())
                .yearsXp(cook.getYearsXp())
                .bio(cook.getBio())
                .build();
    }
}
