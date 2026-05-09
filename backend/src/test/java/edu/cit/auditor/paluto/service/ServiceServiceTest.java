package edu.cit.auditor.paluto.service;

import edu.cit.auditor.paluto.core.entities.Cook;
import edu.cit.auditor.paluto.core.entities.Service;
import edu.cit.auditor.paluto.core.repositories.CookRepository;
import edu.cit.auditor.paluto.core.repositories.ServiceRepository;
import edu.cit.auditor.paluto.core.repositories.UserRepository;
import edu.cit.auditor.paluto.services.ServiceCreationDTO;
import edu.cit.auditor.paluto.services.ServiceResponseDTO;
import edu.cit.auditor.paluto.services.ServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private CookRepository cookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ServiceService serviceService;

    private Cook mockCook;
    private Service mockService;
    private ServiceCreationDTO mockDTO;

    @BeforeEach
    void setUp() {
        mockCook = new Cook();
        mockCook.setId(1L);
        mockCook.setEmail("cook@example.com");
        mockCook.setFirstname("John");
        mockCook.setLastname("Doe");
        mockCook.setHourlyRate(new BigDecimal("450.00"));

        mockService = new Service();
        mockService.setId(1L);
        mockService.setTitle("Chicken Adobo");
        mockService.setCook(mockCook);
        mockService.setIngredientsCost(new BigDecimal("200.00"));
        mockService.setEstPrepTime(45);
        mockService.setServingSize(4);

        mockDTO = new ServiceCreationDTO();
        mockDTO.setTitle("Chicken Adobo");
        mockDTO.setDescription("A classic Filipino dish");
        mockDTO.setIngredientsList("Chicken, soy sauce, vinegar");
        mockDTO.setIngredientsCost(new BigDecimal("200.00"));
        mockDTO.setServingSize(4);
        mockDTO.setEstPrepTime(45);
        mockDTO.setImageUrl("https://example.com/image.jpg");
    }

    @Test
    void shouldCreateServiceSuccessfully() {
        when(userRepository.findByEmail("cook@example.com"))
                .thenReturn(Optional.of(mockCook));
        when(cookRepository.findById(1L))
                .thenReturn(Optional.of(mockCook));
        when(serviceRepository.save(any(Service.class)))
                .thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() ->
                serviceService.createService("cook@example.com", mockDTO)
        );

        verify(serviceRepository, times(1)).save(any(Service.class));
    }

    @Test
    void shouldFailCreateServiceWhenUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                serviceService.createService("unknown@example.com", mockDTO)
        );

        assertEquals("User not found.", ex.getMessage());
        verify(serviceRepository, never()).save(any(Service.class));
    }

    @Test
    void shouldFailCreateServiceWhenCookProfileNotFound() {
        when(userRepository.findByEmail("cook@example.com"))
                .thenReturn(Optional.of(mockCook));
        when(cookRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                serviceService.createService("cook@example.com", mockDTO)
        );

        assertEquals("Cook profile not found.", ex.getMessage());
        verify(serviceRepository, never()).save(any(Service.class));
    }

    @Test
    void shouldGetServiceByIdSuccessfully() {
        when(serviceRepository.findById(1L))
                .thenReturn(Optional.of(mockService));

        ServiceResponseDTO result = serviceService.getServiceById(1L);

        assertNotNull(result);
        assertEquals("Chicken Adobo", result.getTitle());
        assertEquals(1L, result.getCookId());
        verify(serviceRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenServiceNotFound() {
        when(serviceRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                serviceService.getServiceById(99L)
        );

        assertEquals("Service not found.", ex.getMessage());
    }

    @Test
    void shouldGetAllServices() {
        when(serviceRepository.findAll())
                .thenReturn(List.of(mockService));

        List<Service> result = serviceService.getAllServices();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(serviceRepository, times(1)).findAll();
    }

    @Test
    void shouldGetServicesByCookEmail() {
        when(userRepository.findByEmail("cook@example.com"))
                .thenReturn(Optional.of(mockCook));
        when(cookRepository.findById(1L))
                .thenReturn(Optional.of(mockCook));
        when(serviceRepository.findByCook(mockCook))
                .thenReturn(List.of(mockService));

        List<ServiceResponseDTO> result = serviceService.getServicesByCook("cook@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Chicken Adobo", result.get(0).getTitle());
        verify(serviceRepository, times(1)).findByCook(mockCook);
    }

    @Test
    void shouldGetServicesByCookId() {
        when(cookRepository.findById(1L))
                .thenReturn(Optional.of(mockCook));
        when(serviceRepository.findByCook(mockCook))
                .thenReturn(List.of(mockService));

        List<ServiceResponseDTO> result = serviceService.getServicesByCookId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Chicken Adobo", result.get(0).getTitle());
        verify(serviceRepository, times(1)).findByCook(mockCook);
    }

    @Test
    void shouldFailCreateServiceWithNegativeIngredientsCost() {
        when(userRepository.findByEmail("cook@example.com"))
                .thenReturn(Optional.of(mockCook));
        when(cookRepository.findById(1L))
                .thenReturn(Optional.of(mockCook));

        mockDTO.setIngredientsCost(new BigDecimal("-100.00"));

        assertThrows(RuntimeException.class, () ->
                serviceService.createService("cook@example.com", mockDTO)
        );

        verify(serviceRepository, never()).save(any(Service.class));
    }

    @Test
    void shouldFailCreateServiceWithNegativePrepTime() {
        when(userRepository.findByEmail("cook@example.com"))
                .thenReturn(Optional.of(mockCook));
        when(cookRepository.findById(1L))
                .thenReturn(Optional.of(mockCook));

        mockDTO.setEstPrepTime(-45);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                serviceService.createService("cook@example.com", mockDTO)
        );

        assertEquals("Prep time cannot be negative.", ex.getMessage());
        verify(serviceRepository, never()).save(any(Service.class));
    }

    @Test
    void shouldFailCreateServiceWithNegativeServingSize() {
        when(userRepository.findByEmail("cook@example.com"))
                .thenReturn(Optional.of(mockCook));
        when(cookRepository.findById(1L))
                .thenReturn(Optional.of(mockCook));

        mockDTO.setServingSize(-4);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                serviceService.createService("cook@example.com", mockDTO)
        );

        assertEquals("Serving size cannot be negative.", ex.getMessage());
        verify(serviceRepository, never()).save(any(Service.class));
    }
}