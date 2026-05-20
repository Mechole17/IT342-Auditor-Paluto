package edu.cit.auditor.paluto.infrastructure.notification;

import edu.cit.auditor.paluto.core.entities.Booking;
import edu.cit.auditor.paluto.core.entities.Service;
import edu.cit.auditor.paluto.core.entities.Cook;
import edu.cit.auditor.paluto.core.events.BookingPaidEvent;
import edu.cit.auditor.paluto.core.repositories.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingNotificationListener {

    private final EmailService emailService;
    private final ServiceRepository serviceRepository;

    @Async
    @EventListener
    public void handleBookingPaymentSuccess(BookingPaidEvent event) {
        Booking booking = event.getBooking();
        System.out.println("Booking Observer processing payment confirmation receipt for: " + event.getCustomerEmail());

        String dishTitle = "Paluto Culinary Service";
        String chefName = "Our Assigned Home Cook";

        try {
            Long serviceId = Long.valueOf(event.getMetadata().get("serviceId").toString());

            //  USE THE OPTIMIZED EAGER FETCH QUERY
            Service service = serviceRepository.findByIdWithCook(serviceId).orElse(null);

            if (service != null) {
                if (service.getTitle() != null) {
                    dishTitle = service.getTitle();
                }

                if (service.getCook() != null) {
                    Cook cook = service.getCook();

                    String firstname = cook.getFirstname() != null ? cook.getFirstname() : "";
                    String lastname = cook.getLastname() != null ? cook.getLastname() : "";

                    if (!firstname.isEmpty() || !lastname.isEmpty()) {
                        chefName =  firstname +" " + lastname;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not load service details on background thread.");
            e.printStackTrace(); // <-- Temporary trace to capture any inner parameter mismatches
        }

        String subject = "Payment Confirmed! Your PALUTO Booking Summary 🍽️ (#" + (booking != null ? booking.getId() : "N/A") + ")";

        String emailBody = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eee; border-radius: 8px;">
                <div style="text-align: center; margin-bottom: 25px;">
                    <h2 style="color: #4CAF50; margin: 0;">Payment Successful!</h2>
                    <p style="color: #666; margin: 5px 0 0 0;">Thank you for your order. Your personal cook booking is now confirmed!</p>
                </div>

                <div style="background-color: #f8f9fa; border: 1px solid #e9ecef; padding: 15px; border-radius: 6px; margin-bottom: 20px;">
                    <h4 style="margin: 0 0 10px 0; color: #333; text-transform: uppercase; font-size: 13px; letter-spacing: 0.5px;">📋 Booking Summary</h4>
                    <p style="margin: 0; font-size: 15px; color: #495057; line-height: 1.6;">
                        Booked Service: <b style="color: #FF5722;">%s</b> <br>
                        Cook: <b>%s</b> <br>
                        Service Quantity: <b>%s</b>
                    </p>
                </div>

                <table style="width: 100%%; border-collapse: collapse; margin-bottom: 20px;">
                    <thead>
                        <tr style="background-color: #f1f3f5;">
                            <th style="padding: 10px; border: 1px solid #ddd; text-align: left; font-size: 14px;">Schedule & Fulfillment Details</th>
                            <th style="padding: 10px; border: 1px solid #ddd; text-align: right; font-size: 14px;">Information</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td style="padding: 10px; border: 1px solid #ddd; font-size: 14px;"><b>Booking ID</b></td>
                            <td style="padding: 10px; border: 1px solid #ddd; text-align: right; font-size: 14px;">#%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 10px; border: 1px solid #ddd; font-size: 14px;"><b>Scheduled Date</b></td>
                            <td style="padding: 10px; border: 1px solid #ddd; text-align: right; font-size: 14px;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 10px; border: 1px solid #ddd; font-size: 14px;"><b>Scheduled Time</b></td>
                            <td style="padding: 10px; border: 1px solid #ddd; text-align: right; font-size: 14px;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 10px; border: 1px solid #ddd; font-size: 14px;"><b>Service Address</b></td>
                            <td style="padding: 10px; border: 1px solid #ddd; text-align: right; font-size: 14px;">%s</td>
                        </tr>
                        <tr style="background-color: #fdfaf6;">
                            <td style="padding: 10px; border: 1px solid #ddd; color: #d35400; font-size: 14px;"><b>Total Amount Paid</b></td>
                            <td style="padding: 10px; border: 1px solid #ddd; text-align: right; font-weight: bold; color: #d35400; font-size: 14px;">PHP %s</td>
                        </tr>
                    </tbody>
                </table>

                <div style="background-color: #fcf8e3; border-left: 4px solid #f0ad4e; padding: 15px; margin-bottom: 20px; border-radius: 4px;">
                    <p style="margin: 0; font-size: 13px; color: #8a6d3b; line-height: 1.5;">
                        <b>💡 What happens next?</b><br>
                        Your personal cook (<b>%s</b>) has been alerted of your reservation and is preparing for your scheduled appointment. You can track your request status directly via your platform dashboard.
                    </p>
                </div>
            </div>
            """.formatted(
                dishTitle,
                chefName,
                event.getMetadata().getOrDefault("quantity", "1").toString(),
                booking != null ? booking.getId().toString() : "N/A",
                event.getMetadata().getOrDefault("scheduledDate", "N/A").toString(),
                event.getMetadata().getOrDefault("scheduledTime", "N/A").toString(),
                event.getMetadata().getOrDefault("serviceAddress", "N/A").toString(),
                event.getAmountPaid().toString(),
                chefName
        );

        emailService.sendHtmlEmail(event.getCustomerEmail(), subject, emailBody);
    }
}