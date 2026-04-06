package edu.cit.auditor.paluto.controller;

import edu.cit.auditor.paluto.dto.ServiceCreationDTO;
import edu.cit.auditor.paluto.entity.Cook;
import edu.cit.auditor.paluto.entity.Service;
import edu.cit.auditor.paluto.repository.CookRepository;
import edu.cit.auditor.paluto.repository.ServiceRepository;
import edu.cit.auditor.paluto.response.ApiError;
import edu.cit.auditor.paluto.response.ApiResponse;
import edu.cit.auditor.paluto.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ServiceController {
    private final JwtService jwtService;
    private final ServiceRepository serviceRepository;
    private final CookRepository cookRepository;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Service>>> getAllServices() {
        List<Service> services = serviceRepository.findAll();

        ApiResponse<List<Service>> response = ApiResponse.<List<Service>>builder()
                .success(true)
                .data(services)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createMenu(@RequestBody ServiceCreationDTO request, Authentication authentication) {
        try {
            // 1. Get the ID directly from the authentication object.
            // Since the Filter set 'userId' as the name, we just parse it.
            Long userId = Long.parseLong(authentication.getName());

            // 2. Find the Cook associated with this User ID
            Cook cook = cookRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Cook profile not found."));

            // 3. Build and Save the Service
            Service newService = Service.builder()
                    .cook(cook)
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .ingredientsList(request.getIngredientsList())
                    .ingredientsCost(request.getIngredientsCost()) // BigDecimal maps automatically!
                    .imageUrl(request.getImageUrl())
                    .estPrepTime(request.getEstPrepTime())
                    .servingSize(request.getServingSize())
                    .build();

            Service savedService = serviceRepository.save(newService);

            // 4. Wrap in your standard ApiResponse
            ApiResponse<ServiceCreationDTO> response = ApiResponse.<ServiceCreationDTO>builder()
                    .success(true)
                    .data(request)
                    .timestamp(LocalDateTime.now().toString())
                    .build();

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            // 5. Wrap errors in your standard ApiError
            ApiResponse<Void> errorResponse = ApiResponse.<Void>builder()
                    .success(false)
                    .error(ApiError.builder()
                            .code("SRV-001") // Standardized error code
                            .message(e.getMessage())
                            .build())
                    .timestamp(LocalDateTime.now().toString())
                    .build();

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
}