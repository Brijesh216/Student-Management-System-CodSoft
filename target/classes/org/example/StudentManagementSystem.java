package org.example;

import java.io.*;
import java.util.*;

public class StudentManagementSystem {
    private List<Student> students;
    private static final String FILE_NAME = "students.dat";

    public StudentManagementSystem() {
        students = loadStudents();
    }

    // Add Student (with duplicate check)
    public boolean addStudent(int rollNumber, String name, String grade, String email, String phone) {
        if (searchStudent(rollNumber) != null) {
            return false; // duplicate roll number
        }
        students.add(new Student(rollNumber, name, grade, email, phone));
        saveStudents();
        return true;
    }

    // Remove Student
    public boolean removeStudent(int rollNumber) {
        boolean removed = students.removeIf(s -> s.getRollNumber() == rollNumber);
        if (removed) saveStudents();
        return removed;
    }

    // Search Student
    public Student searchStudent(int rollNumber) {
        return students.stream()
                .filter(s -> s.getRollNumber() == rollNumber)
                .findFirst()
                .orElse(null);
    }

    // Update Student
    public boolean updateStudent(int rollNumber, String name, String grade, String email, String phone) {
        Student s = searchStudent(rollNumber);
        if (s != null) {
            s.setName(name);
            s.setGrade(grade);
            s.setEmail(email);
            s.setPhone(phone);
            saveStudents();
            return true;
        }
        return false;
}

    // Get All Students
    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    // Save to file
    private void saveStudents() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(students);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load from file
    private List<Student> loadStudents() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Student>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
