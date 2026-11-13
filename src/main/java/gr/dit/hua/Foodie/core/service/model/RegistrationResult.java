package gr.dit.hua.Foodie.core.service.model;

public record RegistrationResult(
        boolean created,
        String reason
) {
    public static RegistrationResult success() {
        return new RegistrationResult(true, null);
    }

    public static RegistrationResult failure(String reason) {
        if (reason == null) throw new NullPointerException();
        if (reason.isBlank()) throw new IllegalArgumentException();
        return new RegistrationResult(false, reason);
    }
}