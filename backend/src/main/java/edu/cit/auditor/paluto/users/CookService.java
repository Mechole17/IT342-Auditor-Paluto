package edu.cit.auditor.paluto.users;

import edu.cit.auditor.paluto.core.entities.Cook;
import edu.cit.auditor.paluto.core.events.UserRegisteredEvent;
import edu.cit.auditor.paluto.core.repositories.RatingRepository;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CookService {
    private final UserRepository userRepository;
    private final CookRepository cookRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final RatingRepository ratingRepository;

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
        // 1. Fetch all cooks (Query 1)
        List<Cook> cooks = cookRepository.findAll();

        // 2. Fetch all averages in bulk (Query 2)
        Map<Long, Double> averageRatingsMap = ratingRepository.getAllAverageRatings().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],  // cookId
                        row -> (Double) row[1] // average rating
                ));

        // 3. Map to DTO cleanly in memory with NO extra database hits
        return cooks.stream()
                .map(cook -> CookResponseDTO.builder()
                        .id(cook.getId())
                        .firstname(cook.getFirstname())
                        .lastname(cook.getLastname())
                        .hourlyRate(cook.getHourlyRate())
                        .yearsXp(cook.getYearsXp())
                        .bio(cook.getBio())
                        // Look up from memory map; default to 0.0 if missing
                        .averageRating(averageRatingsMap.getOrDefault(cook.getId(), 0.0))
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
