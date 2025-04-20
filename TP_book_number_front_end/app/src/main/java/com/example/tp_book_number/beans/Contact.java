package com.example.tp_book_number.beans;


public class Contact {
    private Long id;
    private String name;
    private String number;

    // Default constructor (required for Gson)
    public Contact() {
    }

    // Constructor with parameters
    public Contact(String name, String number) {
        this.name = name;
        this.number = number;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}