package edu.cit.auditor.paluto.service;

import edu.cit.auditor.paluto.dto.ServiceCreationDTO;
import edu.cit.auditor.paluto.entity.Cook;
import edu.cit.auditor.paluto.entity.Service;
import edu.cit.auditor.paluto.repository.CookRepository;
import edu.cit.auditor.paluto.repository.ServiceRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {
    private final CookRepository cookRepository;
    private final ServiceRepository serviceRepository;

    @Transactional
    public Service createService(@Valid Long cookId, ServiceCreationDTO request){
        Cook cook = cookRepository.findById(cookId)
                .orElseThrow(() -> new RuntimeException("Cook not found"));

        Service newService = edu.cit.auditor.paluto.entity.Service.builder()
                .cook(cook)
                .title(request.getTitle())
                .description(request.getDescription())
                .servingSize(request.getServingSize())
                .ingredientsList(request.getIngredientsList())
                .ingredientsCost(request.getIngredientsCost())
                .estPrepTime(request.getEstPrepTime())
                .imageUrl(request.getImageUrl()) // Map the image URL here
                .build();

        return serviceRepository.save(newService);
    }
}
