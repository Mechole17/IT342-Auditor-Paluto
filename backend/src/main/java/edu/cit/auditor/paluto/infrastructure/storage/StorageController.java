package edu.cit.auditor.paluto.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/storage")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class StorageController {

    private final SupabaseStorageService supabaseStorageService;

    @PostMapping("/service-upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String publicUrl = supabaseStorageService.uploadFile(file);
            return ResponseEntity.ok(Map.of("url", publicUrl));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/certificate-upload")
    public ResponseEntity<?> uploadCertificate(@RequestParam("file") MultipartFile file) {
        try {
            String publicUrl = supabaseStorageService.uploadCertificate(file);
            return ResponseEntity.ok(Map.of("url", publicUrl));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
