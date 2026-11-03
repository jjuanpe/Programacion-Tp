package report;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public final class IdCard {

    private final String fullName;
    private final String role;
    private final String document;
    private final LocalDate birthDate;
    private final List<String> details;
    private final String barcodeValue;

    public IdCard(
            String fullName,
            String role,
            String document,
            LocalDate birthDate,
            List<String> details,
            String barcodeValue) {
        this.fullName = Objects.requireNonNull(fullName, "The full name is required");
        this.role = Objects.requireNonNull(role, "The role is required");
        this.document = Objects.requireNonNull(document, "The document is required");
        this.birthDate = Objects.requireNonNull(birthDate, "The birth date is required");
        this.details = List.copyOf(details);
        this.barcodeValue = Objects.requireNonNull(barcodeValue, "The barcode value is required");
    }

    public String getFullName() { return fullName; }

    public String getRole() { return role; }

    public String getDocument() { return document; }

    public LocalDate getBirthDate() { return birthDate; }

    public List<String> getDetails() { return details; }

    public String getBarcodeValue() { return barcodeValue; }
}