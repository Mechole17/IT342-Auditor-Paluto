package edu.cit.auditor.paluto.users;

import edu.cit.auditor.paluto.core.entities.User;
import edu.cit.auditor.paluto.infrastructure.common.ApiResponse;
import edu.cit.auditor.paluto.infrastructure.common.ResponseUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> fetchAllUsers(Authentication authentication) {
        try {
            // You can extract the admin's email for auditing/logging if needed
            String adminEmail = authentication.getName();

            // Delegation to Service layer
            List<User> users = userService.getAllUsers();

            return ResponseUtility.success(users, HttpStatus.OK);

        } catch (Exception e) {
            // Using an appropriate error code and status for a fetch failure
            return ResponseUtility.error("ADM-001", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
