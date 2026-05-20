package edu.cit.auditor.paluto.infrastructure.notification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // Setting the second parameter to true flags this as a multipart message (allows HTML)
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // True flags the content layout as rich HTML text
            helper.setFrom("no-reply@paluto.com");

            mailSender.send(message);
            System.out.println("Email successfully sent out to: " + to);
        } catch (MessagingException e) {
            System.err.println("Failed to transmit SMTP mail packets to: " + to);
            e.printStackTrace();
            // Do not crash the parent transaction thread if an email fails to deliver
        }
    }
}