package edu.cit.auditor.paluto.service;

import edu.cit.auditor.paluto.dto.CookRegistrationDTO;
import edu.cit.auditor.paluto.entity.Cook;
import edu.cit.auditor.paluto.repository.CookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookService {
    private final CookRepository cookRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Cook registerCook(CookRegistrationDTO dto){
        Cook newCook = Cook.builder()
                //USER attr
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .address(dto.getAddress())
                .role("COOK")

                //COOK attr
                .hourly_rate(dto.getHourly_rate())
                .years_xp(dto.getYears_xp())
                .bio(dto.getBio())
                .build();

        return cookRepository.save(newCook);
    }
}
