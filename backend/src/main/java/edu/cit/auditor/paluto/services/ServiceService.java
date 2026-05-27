package edu.cit.auditor.paluto.services;

import edu.cit.auditor.paluto.core.entities.Cook;
import edu.cit.auditor.paluto.core.entities.Service;
import edu.cit.auditor.paluto.core.entities.User;
import edu.cit.auditor.paluto.core.repositories.BookingRepository;
import edu.cit.auditor.paluto.core.repositories.CookRepository;
import edu.cit.auditor.paluto.core.repositories.ServiceRepository;
import edu.cit.auditor.paluto.core.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final CookRepository cookRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public List<Service> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsActive()))
                .toList();
    }

    public ServiceResponseDTO getServiceById(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found."));

        if (!Boolean.TRUE.equals(service.getIsActive())) {
            throw new RuntimeException("Service not found.");
        }

        return mapToDTO(service);
    }

    public void deleteService(Long serviceId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found."));

        if (!service.getCook().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized.");
        }

        // Check for active bookings
        List<String> activeStatuses = List.of("PAID_PENDING", "ACCEPTED");
        boolean hasActiveBookings = bookingRepository
                .existsByServiceIdAndStatusIn(serviceId, activeStatuses);

        if (hasActiveBookings) {
            throw new RuntimeException("Cannot delete a service with active bookings.");
        }

        service.setIsActive(false);
        serviceRepository.save(service);
    }

    // Helper method to convert Entity -> DTO and inject the Cook's Rate
    private ServiceResponseDTO mapToDTO(Service service) {
        return ServiceResponseDTO.builder()
                .id(service.getId())
                .cookId(service.getCook().getId())
                .title(service.getTitle())
                .description(service.getDescription())
                .ingredientsList(service.getIngredientsList())
                .ingredientsCost(service.getIngredientsCost())
                .imageUrl(service.getImageUrl())
                .estPrepTime(service.getEstPrepTime())
                .servingSize(service.getServingSize())
                // Pulling the Double rate from the hidden Cook object
                .cookHourlyRate(service.getCook() != null ? service.getCook().getHourlyRate() : BigDecimal.ZERO)
                .build();
    }

    public void createService(String email, ServiceCreationDTO request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        Cook cook = cookRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Cook profile not found."));

        if (request.getIngredientsCost() != null &&
                request.getIngredientsCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Ingredients cost cannot be negative.");
        }
        if (request.getEstPrepTime() != null && request.getEstPrepTime() < 0) {
            throw new RuntimeException("Prep time cannot be negative.");
        }
        if (request.getServingSize() != null && request.getServingSize() < 0) {
            throw new RuntimeException("Serving size cannot be negative.");
        }

        Service newService = Service.builder()
                .cook(cook)
                .title(request.getTitle())
                .description(request.getDescription())
                .ingredientsList(request.getIngredientsList())
                .ingredientsCost(request.getIngredientsCost())
                .imageUrl(request.getImageUrl())
                .estPrepTime(request.getEstPrepTime())
                .servingSize(request.getServingSize())
                .build();

        serviceRepository.save(newService);
    }

    public List<ServiceResponseDTO> getServicesByCook(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        Cook cook = cookRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Cook profile not found."));

        return serviceRepository.findByCook(cook)
                .stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsActive()))
                .map(this::mapToDTO)
                .toList();
    }

    public List<ServiceResponseDTO> getServicesByCookId(Long cookId) {
        Cook cook = cookRepository.findById(cookId)
                .orElseThrow(() -> new RuntimeException("Cook not found."));

        return serviceRepository.findByCook(cook)
                .stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsActive()))
                .map(this::mapToDTO)
                .toList();
    }

    public void updateService(Long serviceId, String email, ServiceCreationDTO request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found."));

        // Ownership check
        if (!service.getCook().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized — this is not your service.");
        }

        // Validate negative values
        if (request.getIngredientsCost() != null && request.getIngredientsCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Ingredients cost cannot be negative.");
        }
        if (request.getEstPrepTime() != null && request.getEstPrepTime() < 0) {
            throw new RuntimeException("Prep time cannot be negative.");
        }
        if (request.getServingSize() != null && request.getServingSize() < 0) {
            throw new RuntimeException("Serving size cannot be negative.");
        }

        service.setTitle(request.getTitle());
        service.setDescription(request.getDescription());
        service.setIngredientsList(request.getIngredientsList());
        service.setIngredientsCost(request.getIngredientsCost());
        service.setEstPrepTime(request.getEstPrepTime());
        service.setServingSize(request.getServingSize());

        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            service.setImageUrl(request.getImageUrl());
        }

        serviceRepository.save(service);
    }
}
