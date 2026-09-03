package model.people;

import java.time.LocalDate;
import java.time.Period;

public abstract class Person {
    private String name;
    private LocalDate birthDate;
    private DocumentType documentType;
    private String document;

    public Person(String name,LocalDate birthday,DocumentType documentType,String document){
        this.name = name;
        this.birthDate = birthday;
        this.documentType = documentType;
        this.document = document;
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

    /**
     * Age in completed years at the given date, or 0 when the birth date is
     * not recorded.
     */
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
