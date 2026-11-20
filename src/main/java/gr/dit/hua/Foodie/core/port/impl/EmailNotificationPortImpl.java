package gr.dit.hua.Foodie.core.port.impl;

import gr.dit.hua.Foodie.core.port.EmailNotificationPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * adapter implementation of {@link EmailNotificationPort} that sends
 * emails via Spring's {@link JavaMailSender} with an inline logo image.
 */
@Service
public class EmailNotificationPortImpl implements EmailNotificationPort {


    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final String senderName;

    /**
     * Constructs the email notification adapter with configured sender details.
     */
    public EmailNotificationPortImpl(JavaMailSender mailSender, @Value("${spring.mail.username:foodie.hua.dit.notifications@gmail.com}") String fromAddress,
                                     @Value("${app.mail.sender-name:Foodie}") String senderName) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.senderName = senderName;
    }

    /**
     * Sends an email message with the plain-text body placed first and the
     * Foodie logo embedded inline below it.
     */
    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            // Create a MIME message to support HTML content and inline attachments
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            // Use MIXED_RELATED multipart mode with UTF-8 encoding for inline CID images
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    "UTF-8"
            );

            // Set sender display name, recipient, and subject line
            helper.setFrom(fromAddress, senderName);
            helper.setTo(to);
            helper.setSubject(subject);

            //! ----------- ΑΙ helped us with this format -------------------
            // Escape HTML special characters and convert newlines to <br> for safe rendering
            String escapedBody = body
                    .replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\n", "<br>");

            // Render the message text above the inline logo referenced via cid:foodieLogo
            String html = """
                    <div>%s</div>
                    <br>
                    <img src="cid:foodieLogo" alt="Foodie logo" />
                    """.formatted(escapedBody);

            // Enable HTML mode for the message body
            helper.setText(html, true);

            // Load the logo from the classpath and attach it inline if present
            ClassPathResource logoResource = new ClassPathResource("static/img/foodie-logo.png");
            if (logoResource.exists()) {
                helper.addInline("foodieLogo", logoResource, "image/png");
            }

            //!--------------------------------------------------------------------

            // Dispatch the message through the mail server
            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new MailSendException("Failed to build or send email", e);
        }
    }
}