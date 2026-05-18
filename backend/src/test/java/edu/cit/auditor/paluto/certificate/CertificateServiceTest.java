package edu.cit.auditor.paluto.certificate;

import edu.cit.auditor.paluto.core.entities.Certificate;
import edu.cit.auditor.paluto.core.entities.Cook;
import edu.cit.auditor.paluto.core.entities.User;
import edu.cit.auditor.paluto.core.repositories.CertificateRepository;
import edu.cit.auditor.paluto.core.repositories.CookRepository;
import edu.cit.auditor.paluto.core.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificateServiceTest {

    @Mock
    private CertificateRepository certificateRepository;
    @Mock
    private CookRepository cookRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CertificateService certificateService;

    private User sampleUser;
    private Cook sampleCook;
    private Certificate sampleCertificate;
    private final String userEmail = "chef.angelou@gmail.com";

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .email(userEmail)
                .build();

        sampleCook = Cook.builder()
                .id(1L)
                .firstname("Mechole")
                .lastname("Auditor")
                .build();

        sampleCertificate = Certificate.builder()
                .id(100L)
                .cook(sampleCook)
                .title("Culinary Arts Diploma")
                .fileUrl("https://supabase-bucket.url/cert.pdf")
                .status("PENDING")
                .uploadedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Upload Certificate Tests")
    class UploadCertificateTests {

        @Test
        @DisplayName("Should successfully upload certificate when user and cook exist")
        void shouldUploadCertificateSuccessfully() {
            // Arrange
            CertificateUploadDTO dto = new CertificateUploadDTO();
            dto.setTitle("Culinary Arts Diploma");
            dto.setFileUrl("https://supabase-bucket.url/cert.pdf");

            when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(sampleUser));
            when(cookRepository.findById(sampleUser.getId())).thenReturn(Optional.of(sampleCook));
            when(certificateRepository.save(any(Certificate.class))).thenReturn(sampleCertificate);

            // Act
            CertificateResponseDTO result = certificateService.uploadCertificate(userEmail, dto);

            // Assert
            assertNotNull(result);
            assertEquals(100L, result.getId());
            assertEquals("PENDING", result.getStatus());
            assertEquals("Mechole Auditor", result.getCookName());
            verify(certificateRepository, times(1)).save(any(Certificate.class));
        }

        @Test
        @DisplayName("Should throw exception during upload when user is not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange
            CertificateUploadDTO dto = new CertificateUploadDTO();
            dto.setTitle("Title");
            dto.setFileUrl("url");

            when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

            // Act & Assert
            Exception exception = assertThrows(RuntimeException.class, () ->
                    certificateService.uploadCertificate(userEmail, dto)
            );

            assertEquals("User not found.", exception.getMessage());
            verify(certificateRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get My Certificates Tests")
    class GetMyCertificatesTests {

        @Test
        @DisplayName("Should return active certificates filtering out INACTIVE status")
        void shouldReturnCookCertificates() {
            // Arrange
            when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(sampleUser));
            when(certificateRepository.findByCookIdAndStatusNot(sampleUser.getId(), "INACTIVE"))
                    .thenReturn(List.of(sampleCertificate));

            // Act
            List<CertificateResponseDTO> results = certificateService.getMyCertificates(userEmail);

            // Assert
            assertFalse(results.isEmpty());
            assertEquals(1, results.size());
            assertEquals("Culinary Arts Diploma", results.get(0).getTitle());
        }
    }

    @Nested
    @DisplayName("Delete Certificate (Soft Delete) Tests")
    class DeleteCertificateTests {

        @Test
        @DisplayName("Should update status to INACTIVE and populate deletedAt when owner requests removal")
        void shouldSoftDeleteCertificateSuccessfully() {
            // Arrange
            when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(sampleUser));
            when(certificateRepository.findById(100L)).thenReturn(Optional.of(sampleCertificate));

            // Act
            certificateService.deleteCertificate(100L, userEmail);

            // Assert & Verify structural modifications
            ArgumentCaptor<Certificate> certificateCaptor = ArgumentCaptor.forClass(Certificate.class);
            verify(certificateRepository, times(1)).save(certificateCaptor.capture());

            Certificate savedCertificate = certificateCaptor.getValue();
            assertEquals("INACTIVE", savedCertificate.getStatus());
            assertNotNull(savedCertificate.getDeletedAt());
        }

        @Test
        @DisplayName("Should throw exception when user attempts to delete someone else's certificate")
        void shouldThrowExceptionWhenUnauthorizedToDelete() {
            // Arrange
            User maliciousUser = User.builder().id(999L).email("malicious@gmail.com").build();
            when(userRepository.findByEmail("malicious@gmail.com")).thenReturn(Optional.of(maliciousUser));
            when(certificateRepository.findById(100L)).thenReturn(Optional.of(sampleCertificate));

            // Act & Assert
            Exception exception = assertThrows(RuntimeException.class, () ->
                    certificateService.deleteCertificate(100L, "malicious@gmail.com")
            );

            assertEquals("Unauthorized access to certificate.", exception.getMessage());
            verify(certificateRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Review Certificate Tests")
    class ReviewCertificateTests {

        @Test
        @DisplayName("Should transition status and add administrative feedback notes")
        void shouldReviewCertificateSuccessfully() {
            // Arrange
            CertificateReviewDTO reviewDTO = new CertificateReviewDTO();
            reviewDTO.setStatus("REJECTED");
            reviewDTO.setAdminNote("Document scanning resolution is too blurry.");

            Certificate updatedCertificate = Certificate.builder()
                    .id(100L)
                    .cook(sampleCook)
                    .title("Culinary Arts Diploma")
                    .fileUrl("https://supabase-bucket.url/cert.pdf")
                    .status("REJECTED")
                    .adminNote("Document scanning resolution is too blurry.")
                    .reviewedAt(LocalDateTime.now())
                    .build();

            when(certificateRepository.findById(100L)).thenReturn(Optional.of(sampleCertificate));
            when(certificateRepository.save(any(Certificate.class))).thenReturn(updatedCertificate);

            // Act
            CertificateResponseDTO result = certificateService.reviewCertificate(100L, reviewDTO);

            // Assert
            assertNotNull(result);
            assertEquals("REJECTED", result.getStatus());
            assertEquals("Document scanning resolution is too blurry.", result.getAdminNote());
            assertNotNull(result.getReviewedAt());
        }
    }
}