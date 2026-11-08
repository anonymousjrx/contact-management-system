package model;

import java.time.LocalDate;

public class Contact {

    private int id;
    private final String fullName;
    private final String phone1;
    private final String phone2;
    private final String address;
    private final LocalDate dateOfBirth;
    private final String email;

    public Contact(String fullName, String phone1, String phone2, String address, LocalDate dateOfBirth, String email) {
       this.id = 0;
        this.fullName = fullName;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
    }

    public Contact(int id, String fullName, String phone1, String phone2, String address, LocalDate dateOfBirth, String email) {
        this.id = id;
        this.fullName = fullName;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getFullName() {
        return fullName;
    }
    public String getPhone1() {
        return phone1;
    }
    public String getPhone2() {
        return phone2;
    }
    public String getAddress() {
        return address;
    }
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public String getEmail() {
        return email;
    }
}