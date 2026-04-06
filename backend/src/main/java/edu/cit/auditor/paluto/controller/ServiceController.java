package edu.cit.auditor.paluto.controller;

import edu.cit.auditor.paluto.dto.ServiceCreationDTO;
import edu.cit.auditor.paluto.dto.ServiceResponseDTO;
import edu.cit.auditor.paluto.entity.Cook;
import edu.cit.auditor.paluto.entity.Service;
import edu.cit.auditor.paluto.repository.CookRepository;
import edu.cit.auditor.paluto.repository.ServiceRepository;
import edu.cit.auditor.paluto.response.ApiError;
import edu.cit.auditor.paluto.response.ApiResponse;
import edu.cit.auditor.paluto.service.JwtService;
import edu.cit.auditor.paluto.service.ServiceService;
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

    // MODIFIED: Removed Repositories and JwtService. Added ServiceService.
    private final ServiceService serviceService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Service>>> getAllServices() {
        // MODIFIED: Using Service layer instead of direct Repository call
        return ResponseEntity.ok(ApiResponse.<List<Service>>builder()
                .success(true)
                .data(serviceService.getAllServices())
                .timestamp(LocalDateTime.now().toString())
                .build());
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ServiceCreationDTO>> createMenu(
            @RequestBody ServiceCreationDTO request,
            Authentication authentication) {
        try {
            // MODIFIED: Using Spring Security 'Authentication' instead of 'JwtService'
            Long userId = Long.parseLong(authentication.getName());

            // MODIFIED: Delegation to Service layer
            serviceService.createService(userId, request);

            return new ResponseEntity<>(ApiResponse.<ServiceCreationDTO>builder()
                    .success(true)
                    .data(request)
                    .timestamp(LocalDateTime.now().toString())
                    .build(), HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.<ServiceCreationDTO>builder()
                    .success(false)
                    .error(ApiError.builder()
                            .code("SRV-001")
                            .message(e.getMessage())
                            .build())
                    .timestamp(LocalDateTime.now().toString())
                    .build(), HttpStatus.BAD_REQUEST);
        }
    }

    // NEW: The specific endpoint for your React ServiceDetails page
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceResponseDTO>> getService(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.<ServiceResponseDTO>builder()
                    .success(true)
                    .data(serviceService.getServiceById(id))
                    .timestamp(LocalDateTime.now().toString())
                    .build());
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.<ServiceResponseDTO>builder()
                    .success(false)
                    .error(ApiError.builder().code("SVC-404").message(e.getMessage()).build())
                    .timestamp(LocalDateTime.now().toString())
                    .build(), HttpStatus.NOT_FOUND);
        }
    }
}