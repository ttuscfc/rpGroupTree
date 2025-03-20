package org.example.grouptree.model;

public class NumberResponse {

    private int number;
    private String message;

    public NumberResponse(int number, String message) {
        this.number = number;
        this.message = message;
    }

    // Getters e Setters
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
