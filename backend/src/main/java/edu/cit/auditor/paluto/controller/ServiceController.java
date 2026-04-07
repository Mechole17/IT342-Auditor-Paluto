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
import edu.cit.auditor.paluto.utils.ResponseUtility;
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
            try {
                List<Service> services = serviceService.getAllServices();

                // Factory Method handles successful list retrieval
                return ResponseUtility.success(services, HttpStatus.OK);

            } catch (Exception e) {
                // Factory Method handles database or processing errors
                // Code "SRV-002" identifies a retrieval failure
                return ResponseUtility.error("SRV-002", "Failed to retrieve services: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
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

            return ResponseUtility.success(request,HttpStatus.CREATED);

        } catch (Exception e) {
            return ResponseUtility.error("SRV-001", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // NEW: The specific endpoint for your React ServiceDetails page
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceResponseDTO>> getService(@PathVariable Long id) {
        try {
            ServiceResponseDTO data = serviceService.getServiceById(id);
            return ResponseUtility.success(data, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("SRV-003", e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}