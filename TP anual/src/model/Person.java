package model;

import java.time.LocalDate;

public abstract class Person {
    private String name;
    private LocalDate birthday;
    private String type;
    private String document;

    public Person(String name,LocalDate birthday,String type,String document){
        this.name = name;
        this.birthday = birthday;
        this.type = type;
        this.document = document;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getDocument() {
        return document;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
