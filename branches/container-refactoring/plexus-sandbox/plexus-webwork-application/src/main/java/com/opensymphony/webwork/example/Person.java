package com.opensymphony.webwork.example;

public class Person {
    String name;
    String email;

    public Person(String name, String email) {
        setName(name);
        setEmail(email);
    }

    public Person() {
        this("Rickard Oberg", "rickard@dreambean.com");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
