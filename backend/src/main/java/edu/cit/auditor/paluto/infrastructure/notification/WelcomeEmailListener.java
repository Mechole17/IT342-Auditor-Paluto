package edu.cit.auditor.paluto.infrastructure.notification;

import edu.cit.auditor.paluto.core.entities.User;
import edu.cit.auditor.paluto.core.events.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WelcomeEmailListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void handleUserRegistration(UserRegisteredEvent event) {
        User user = event.getUser();
        String role = user.getRole() != null ? user.getRole().toUpperCase() : "CUSTOMER";

        System.out.println("Welcome Email Observer caught registration for Role: " + role + " (" + user.getEmail() + ")");

        String subject;
        String onboardingContent;

        // Dynamic content separation based on Marketplace Roles
        if ("COOK".equals(role)) {
            subject = "Welcome to the PALUTO Kitchen, Chef %s! 🍳".formatted(user.getFirstname());
            onboardingContent = """
                <p>We are thrilled to have you join our elite culinary community as a verified home cook!</p>
                <p>Here are your crucial next steps to begin earning on our platform:</p>
                <ol style="line-height: 1.6;">
                    <li><b>Upload Certifications:</b> Submit your mandatory health certificates and clearances in the dashboard to pass administrative review.</li>
                    <li><b>Publish Your Services:</b> List your signature dishes, ingredients costs, and estimated preparation times.</li>
                </ol>
                <p>Get ready to showcase your skills and manage your earnings wallet directly through your platform dashboard!</p>
                """;
        } else {
            // Default customer messaging layout path
            subject = "Welcome to PALUTO, %s! 🍽️".formatted(user.getFirstname());
            onboardingContent = """
                <p>Thank you for creating an account with us. Your registration was successful!</p>
                <p>Are you hungry for something spectacular? Here is how to get started on your food journey:</p>
                <ol style="line-height: 1.6;">
                    <li><b>Explore Dishes:</b> Browse traditional specialties and custom culinary services tailored to your taste.</li>
                    <li><b>Book a Private Cook:</b> Select a date and schedule standard or scaled meals directly to your address.</li>
                    <li><b>Secure Checkout:</b> Pay securely with GCash, PayMaya, or Cards via our integrated payment portal.</li>
                </ol>
                <p>Your personal home cook experience is just a few clicks away!</p>
                """;
        }

        // Standardized visual wrap wrapper to maintain UI style guidelines
        String emailBody = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eee; border-radius: 8px;">
                <div style="text-align: center; margin-bottom: 20px;">
                    <h2 style="color: #FF5722; margin: 0;">PALUTO Marketplace</h2>
                </div>
                
                <p>Hi <strong>%s</strong>,</p>
                
                %s
                
                <div style="background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin: 20px 0;">
                    <p style="margin: 0;"><b>Your Registered Credentials:</b></p>
                    <p style="margin: 5px 0 0 0;">📧 Registered Email: %s</p>
                    <p style="margin: 5px 0 0 0;">👤 Workspace Profile: %s</p>
                </div>

                <p>We're absolutely thrilled to have you as part of our expanding community.</p>
                
                <hr style="border: none; border-top: 1px solid #eee; margin: 20px 0;" />
                <p style="font-size: 12px; color: #777; text-align: center;">This is an automated system notification from PALUTO. Please do not reply directly to this message.</p>
            </div>
            """.formatted(user.getFirstname(), onboardingContent, user.getEmail(), role);

        emailService.sendHtmlEmail(user.getEmail(), subject, emailBody);
    }
}