package edu.cit.auditor.paluto.certificate;

import edu.cit.auditor.paluto.core.entities.Certificate;
import edu.cit.auditor.paluto.core.entities.Cook;
import edu.cit.auditor.paluto.core.entities.User;
import edu.cit.auditor.paluto.core.repositories.CertificateRepository;
import edu.cit.auditor.paluto.core.repositories.CookRepository;
import edu.cit.auditor.paluto.core.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final CookRepository cookRepository;
    private final UserRepository userRepository;

    public List<CertificateResponseDTO> getAllCertificates() {
        return certificateRepository.findByStatusNot("INACTIVE")
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public CertificateResponseDTO uploadCertificate(String email, CertificateUploadDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        Cook cook = cookRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Cook profile not found."));

        Certificate certificate = Certificate.builder()
                .cook(cook)
                .title(dto.getTitle())
                .fileUrl(dto.getFileUrl())
                .status("PENDING")
                .build();

        Certificate saved = certificateRepository.save(certificate);
        return mapToDTO(saved);
    }

    public List<CertificateResponseDTO> getCookCertificates(Long cookId) {
        return certificateRepository.findByCookIdAndStatusNot(cookId, "INACTIVE")
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<CertificateResponseDTO> getMyCertificates(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        // Exclude INACTIVE certificates
        return certificateRepository.findByCookIdAndStatusNot(user.getId(), "INACTIVE")
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public void deleteCertificate(Long certificateId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found."));

        // Ownership check
        if (!certificate.getCook().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to certificate.");
        }

        certificate.setStatus("INACTIVE");
        certificate.setDeletedAt(LocalDateTime.now());
        certificateRepository.save(certificate);
    }

    public List<CertificateResponseDTO> getPendingCertificates() {
        return certificateRepository.findByStatus("PENDING")
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public CertificateResponseDTO reviewCertificate(Long certificateId, CertificateReviewDTO dto) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found."));

        certificate.setStatus(dto.getStatus());
        certificate.setAdminNote(dto.getAdminNote());
        certificate.setReviewedAt(LocalDateTime.now());

        Certificate saved = certificateRepository.save(certificate);
        return mapToDTO(saved);
    }

    private CertificateResponseDTO mapToDTO(Certificate certificate) {
        return CertificateResponseDTO.builder()
                .id(certificate.getId())
                .cookId(certificate.getCook().getId())
                .cookName(certificate.getCook().getFirstname() + " " + certificate.getCook().getLastname())
                .title(certificate.getTitle())
                .fileUrl(certificate.getFileUrl())
                .status(certificate.getStatus())
                .adminNote(certificate.getAdminNote())
                .uploadedAt(certificate.getUploadedAt())
                .reviewedAt(certificate.getReviewedAt())
                .build();
    }
}