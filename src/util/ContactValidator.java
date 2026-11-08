package util;

import java.time.LocalDate;
import java.util.Set;
import java.util.regex.Pattern;

public final class ContactValidator {

    // Edit this list to allow or block email providers
    private static final Set<String> ALLOWED_DOMAINS = Set.of(
            "gmail.com",
            "hotmail.com", "hotmail.es",
            "outlook.com", "outlook.es",
            "yahoo.com", "yahoo.es",
            "live.com",
            "icloud.com"
    );

    // Letters/digits separated by a single . _ % + or -  (no leading, trailing or double symbols)
    private static final Pattern LOCAL_PART =
            Pattern.compile("^[A-Za-z0-9]+([._%+-][A-Za-z0-9]+)*$");

    private static final int MIN_PHONE_DIGITS = 7;
    private static final int MAX_PHONE_DIGITS = 15;
    private static final int MIN_BIRTH_YEAR = 1900;

    private ContactValidator() { }

    /** Returns an error message, or null if the email is valid. Empty is allowed (optional field). */
    public static String validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }
        if (email.contains(" ")) {
            return "The email cannot contain spaces.";
        }

        int at = email.indexOf('@');
        if (at == -1 || at != email.lastIndexOf('@')) {
            return "The email must contain exactly one @.";
        }
        if (email.contains("..")) {
            return "The email cannot contain two consecutive dots.";
        }

        String localPart = email.substring(0, at);
        String domain = email.substring(at + 1).toLowerCase();

        if (localPart.isEmpty() || localPart.length() > 64 || !LOCAL_PART.matcher(localPart).matches()) {
            return "The part before @ is not valid.";
        }
        if (!ALLOWED_DOMAINS.contains(domain)) {
            return "Only these providers are allowed: " + String.join(", ", ALLOWED_DOMAINS) + ".";
        }
        return null;
    }

    /** Returns an error message, or null if valid. Digits only, no spaces, letters or symbols. */
    public static String validatePhone(String label, String phone, boolean required) {
        if (phone == null || phone.isEmpty()) {
            return required ? label + " is required." : null;
        }
        if (!phone.matches("\\d+")) {
            return label + " must contain only digits (no letters, spaces or symbols).";
        }
        if (phone.length() < MIN_PHONE_DIGITS || phone.length() > MAX_PHONE_DIGITS) {
            return label + " must have between " + MIN_PHONE_DIGITS + " and " + MAX_PHONE_DIGITS + " digits.";
        }
        return null;
    }

    /** Returns an error message, or null if valid. Empty (null) is allowed. */
    public static String validateDateOfBirth(LocalDate date) {
        if (date == null) {
            return null;
        }
        if (date.isAfter(LocalDate.now())) {
            return "The date of birth cannot be in the future (" + date.getYear() + ").";
        }
        if (date.getYear() < MIN_BIRTH_YEAR) {
            return "The date of birth cannot be before " + MIN_BIRTH_YEAR + ".";
        }
        return null;
    }
}