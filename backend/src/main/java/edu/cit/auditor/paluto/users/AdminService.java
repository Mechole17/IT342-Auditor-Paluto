package edu.cit.auditor.paluto.users;

import edu.cit.auditor.paluto.core.repositories.BookingRepository;
import edu.cit.auditor.paluto.core.repositories.CertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final CertificateRepository certificateRepository;
    private final BookingRepository bookingRepository;

    public AdminDashboardDTO getDashboardStats() {
        long pendingCount = certificateRepository.countByStatus("PENDING");
        long totalSuccess = bookingRepository.countByStatus("COMPLETED");
        BigDecimal totalRevenue = bookingRepository.sumTotalRevenueForCompletedBookings();

        return AdminDashboardDTO.builder()
                .pendingCount(pendingCount)
                .totalSuccess(totalSuccess)
                .totalRevenue(totalRevenue)
                .build();
    }
}
