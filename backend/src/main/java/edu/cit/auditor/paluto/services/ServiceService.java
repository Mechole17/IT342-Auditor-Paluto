package edu.cit.auditor.paluto.service;

import edu.cit.auditor.paluto.dto.ServiceCreationDTO;
import edu.cit.auditor.paluto.dto.ServiceResponseDTO;
import edu.cit.auditor.paluto.core.entities.Cook;
import edu.cit.auditor.paluto.core.entities.Service;
import edu.cit.auditor.paluto.core.entities.User;
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

    public List<Service> getAllServices() {//needs to updae to return dto instead of entity
        return serviceRepository.findAll();
    }

    public ServiceResponseDTO getServiceById(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found."));
        return mapToDTO(service);
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
                .map(this::mapToDTO)
                .toList();
    }

    public List<ServiceResponseDTO> getServicesByCookId(Long cookId) {
        Cook cook = cookRepository.findById(cookId)
                .orElseThrow(() -> new RuntimeException("Cook not found."));

        return serviceRepository.findByCook(cook)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
}
