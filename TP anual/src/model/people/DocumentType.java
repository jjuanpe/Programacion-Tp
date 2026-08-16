package model.people;

public enum DocumentType {
    DU,
    PAS;

    public static DocumentType fromFileValue(String fileValue) {
        return DocumentType.valueOf(fileValue.trim().toUpperCase());
    }
}
