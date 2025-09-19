package org.example;

import java.io.Serializable;

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private int rollNumber;
    private String name;
    private String grade;
    private String email;
    private String phone;

    public Student(int rollNumber, String name, String grade, String email, String phone) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.grade = grade;
        this.email = email;
        this.phone = phone;
    }

    // Getters
    public int getRollNumber() { return rollNumber; }
    public String getName() { return name; }
    public String getGrade() { return grade; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    // Setters
    public void setRollNumber(int rollNumber) { this.rollNumber = rollNumber; }
    public void setName(String name) { this.name = name; }
    public void setGrade(String grade) { this.grade = grade; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return "Roll: " + rollNumber + ", Name: " + name + ", Grade: " + grade +
                ", Email: " + email + ", Phone: " + phone;
    }
}
