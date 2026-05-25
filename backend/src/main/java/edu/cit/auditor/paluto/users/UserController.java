package edu.cit.auditor.paluto.users;

import edu.cit.auditor.paluto.infrastructure.common.ApiResponse;
import edu.cit.auditor.paluto.infrastructure.common.ResponseUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UpdateProfileDTO>> updateProfile(
            @RequestBody UpdateProfileDTO dto,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            UpdateProfileDTO updated = userService.updateProfile(email, dto);
            return ResponseUtility.success(updated, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtility.error("USR-001", e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}