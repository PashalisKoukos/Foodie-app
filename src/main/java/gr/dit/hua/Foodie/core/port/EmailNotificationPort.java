package gr.dit.hua.Foodie.core.port;

public interface EmailNotificationPort {
    void sendEmail(String to, String subject, String body);
}