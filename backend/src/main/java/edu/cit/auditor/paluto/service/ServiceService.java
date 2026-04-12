package edu.cit.auditor.paluto.service;

import edu.cit.auditor.paluto.dto.ServiceCreationDTO;
import edu.cit.auditor.paluto.dto.ServiceResponseDTO;
import edu.cit.auditor.paluto.entity.Cook;
import edu.cit.auditor.paluto.entity.Service;
import edu.cit.auditor.paluto.repository.CookRepository;
import edu.cit.auditor.paluto.repository.ServiceRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final CookRepository cookRepository;

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
                .cookHourlyRate(service.getCook() != null ? service.getCook().getHourly_rate() : 0.0)
                .build();
    }

    public void createService(Long userId, ServiceCreationDTO request) {
        Cook cook = cookRepository.findById(userId)
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
}
