package edu.cit.auditor.paluto.services;

import edu.cit.auditor.paluto.core.entities.Service;
import edu.cit.auditor.paluto.infrastructure.common.ApiResponse;
import edu.cit.auditor.paluto.infrastructure.common.ResponseUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
            String email = authentication.getName();

            // MODIFIED: Delegation to Service layer
            serviceService.createService(email, request);

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

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceCreationDTO>> updateService(
            @PathVariable Long id,
            @RequestBody ServiceCreationDTO request,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            serviceService.updateService(id, email, request);
            return ResponseUtility.success(request, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("SRV-005", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteService(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            serviceService.deleteService(id, authentication.getName());
            return ResponseUtility.success("Service deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("SRV-006", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/my-services")
    public ResponseEntity<ApiResponse<List<ServiceResponseDTO>>> getMyServices(Authentication authentication) {
        try {
            String email = authentication.getName();
            List<ServiceResponseDTO> services = serviceService.getServicesByCook(email);
            return ResponseUtility.success(services, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("SRV-002", e.getMessage() + "Error fetching services.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/cook/{cookId}/services")
    public ResponseEntity<ApiResponse<List<ServiceResponseDTO>>> getServicesByCookId(@PathVariable Long cookId) {
        try {
            List<ServiceResponseDTO> services = serviceService.getServicesByCookId(cookId);
            return ResponseUtility.success(services, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("SRV-004", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}