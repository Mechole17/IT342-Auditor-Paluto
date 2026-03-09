package edu.cit.auditor.paluto.controller;

import edu.cit.auditor.paluto.dto.CookRegistrationDTO;
import edu.cit.auditor.paluto.entity.Cook;
import edu.cit.auditor.paluto.response.ApiResponse;
import edu.cit.auditor.paluto.service.CookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/cook")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CookController {
    private final CookService cookService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Cook>> registerCook(@RequestBody CookRegistrationDTO dto){
        Cook registeredCook = cookService.registerCook(dto);

        ApiResponse<Cook> response = ApiResponse.<Cook>builder()
                .success(true)
                .data(registeredCook)
                .error(null)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
