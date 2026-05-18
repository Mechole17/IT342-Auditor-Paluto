package edu.cit.auditor.paluto.certificate;

import edu.cit.auditor.paluto.infrastructure.common.ApiResponse;
import edu.cit.auditor.paluto.infrastructure.common.ResponseUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CertificateResponseDTO>>> getAllCertificates() {
        try {
            List<CertificateResponseDTO> result = certificateService.getAllCertificates();
            return ResponseUtility.success(result, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("CERT-007", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Cook uploads a certificate
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<CertificateResponseDTO>> uploadCertificate(
            @RequestBody CertificateUploadDTO dto,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            CertificateResponseDTO result = certificateService.uploadCertificate(email, dto);
            return ResponseUtility.success(result, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseUtility.error("CERT-001", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Cook views their own certificates
    @GetMapping("/my-certificates")
    public ResponseEntity<ApiResponse<List<CertificateResponseDTO>>> getMyCertificates(
            Authentication authentication) {
        try {
            String email = authentication.getName();
            List<CertificateResponseDTO> result = certificateService.getMyCertificates(email);
            return ResponseUtility.success(result, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("CERT-002", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Public — view approved certificates of a cook
    @GetMapping("/cook/{cookId}")
    public ResponseEntity<ApiResponse<List<CertificateResponseDTO>>> getCookCertificates(
            @PathVariable Long cookId) {
        try {
            List<CertificateResponseDTO> result = certificateService.getCookCertificates(cookId);
            return ResponseUtility.success(result, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("CERT-003", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Admin views all pending certificates
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<CertificateResponseDTO>>> getPendingCertificates() {
        try {
            List<CertificateResponseDTO> result = certificateService.getPendingCertificates();
            return ResponseUtility.success(result, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("CERT-004", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Admin approves or rejects a certificate
    @PatchMapping("/{id}/review")
    public ResponseEntity<ApiResponse<CertificateResponseDTO>> reviewCertificate(
            @PathVariable Long id,
            @RequestBody CertificateReviewDTO dto) {
        try {
            CertificateResponseDTO result = certificateService.reviewCertificate(id, dto);
            return ResponseUtility.success(result, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("CERT-005", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<CertificateResponseDTO>> deleteCertificate(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            certificateService.deleteCertificate(id, email);
            return ResponseUtility.success(null, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("CERT-006", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}