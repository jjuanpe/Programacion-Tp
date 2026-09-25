package model.people;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;

public abstract class Person implements Serializable {
    private String name;
    private LocalDate birthDate;
    private DocumentType documentType;
    private String document;

    public Person(String name,LocalDate birthday,DocumentType documentType,String document){
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("The person's name is required");
        }
        if (birthday == null || birthday.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A valid birth date is required");
        }
        if (documentType == null) {
            throw new IllegalArgumentException("The document type is required");
        }
        if (document == null || document.isBlank()) {
            throw new IllegalArgumentException("The document number is required");
        }
        this.name = name.trim();
        this.birthDate = birthday;
        this.documentType = documentType;
        this.document = document.trim();
    }

    public String getDocument() {
        return document;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public int getAge(LocalDate referenceDate) {
        int age = 0;
        if (birthDate != null && referenceDate != null) {
            age = Period.between(birthDate, referenceDate).getYears();
        }
        return age;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public abstract String getRole();

}
