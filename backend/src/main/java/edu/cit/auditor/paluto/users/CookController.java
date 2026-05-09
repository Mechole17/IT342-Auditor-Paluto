package edu.cit.auditor.paluto.users;

import edu.cit.auditor.paluto.authentication.LoginDataResponseDTO;
import edu.cit.auditor.paluto.core.entities.Cook;
import edu.cit.auditor.paluto.infrastructure.exception.EmailAlreadyExistsException;
import edu.cit.auditor.paluto.infrastructure.common.ApiResponse;
import edu.cit.auditor.paluto.infrastructure.security.JwtService;
import edu.cit.auditor.paluto.infrastructure.common.ResponseUtility;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cook")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CookController {
    private final CookService cookService;
    private final JwtService jwtService; // Add this for token generation

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginDataResponseDTO>> registerCook(@Valid @RequestBody CookRegistrationDTO dto){
        //used LoginDataResponseDTO to handle auto-login
        try {
            Cook registeredCook = cookService.registerCook(dto);

            // Generate token immediately for Auto-Login
            String token = jwtService.generateToken(registeredCook);

            // Wrap user and token together
            LoginDataResponseDTO authData = new LoginDataResponseDTO(registeredCook, token, null);

            return ResponseUtility.success(authData, HttpStatus.CREATED);

        }catch (EmailAlreadyExistsException e){

            return ResponseUtility.error("DB-002", e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CookResponseDTO>>> getAllCooks() {
        try {
            List<CookResponseDTO> cooks = cookService.getAllCooks();
            return ResponseUtility.success(cooks, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("CK-001", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CookResponseDTO>> getCookById(@PathVariable Long id) {
        try {
            CookResponseDTO cook = cookService.getCookById(id);
            return ResponseUtility.success(cook, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("CK-002", e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
