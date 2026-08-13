package model.people;

import java.time.LocalDate;

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

    public DocumentType getDocumentType() {
        return documentType;
    }

    public abstract String getRole();

}
