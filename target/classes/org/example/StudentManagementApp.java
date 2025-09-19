package org.example;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StudentManagementApp extends JFrame {
    private StudentManagementSystem sms;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField rollField, nameField, gradeField, emailField, phoneField, searchField;

    public StudentManagementApp() {
        sms = new StudentManagementSystem();

        setTitle("🎓 Student Management System");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Set Nimbus Look & Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    SwingUtilities.updateComponentTreeUI(this);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Nimbus theme not available, using default.");
        }

        //  Top Panel (Form)
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));

        formPanel.add(new JLabel("Roll Number:"));
        rollField = new JTextField();
        formPanel.add(rollField);

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Grade:"));
        gradeField = new JTextField();
        formPanel.add(gradeField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        add(formPanel, BorderLayout.NORTH);

        //  Center Panel with Search + Table
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));

        // Live Search Bar
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("🔍 Search Students (by Name)"));
        searchField = new JTextField();
        searchPanel.add(searchField, BorderLayout.CENTER);
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] cols = {"Roll No", "Name", "Grade", "Email", "Phone"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        //  Bottom Panel (Buttons)
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        JButton displayBtn = new JButton("Display All");

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(displayBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        //  Event Listeners
        addBtn.addActionListener(e -> addStudent());
        editBtn.addActionListener(e -> editStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        displayBtn.addActionListener(e -> displayAll());

        //  Live Search Functionality
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { liveSearch(); }
            public void removeUpdate(DocumentEvent e) { liveSearch(); }
            public void changedUpdate(DocumentEvent e) { liveSearch(); }
        });

        // Load existing students
        displayAll();
    }

    private void addStudent() {
        try {
            int roll = Integer.parseInt(rollField.getText().trim());
            String name = nameField.getText().trim();
            String grade = gradeField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            if (!validateInput(name, grade, email, phone)) return;

            if (!sms.addStudent(roll, name, grade, email, phone)) {
                JOptionPane.showMessageDialog(this, "⚠ Roll Number already exists!");
                return;
            }

            JOptionPane.showMessageDialog(this, "✅ Student Added!");
            clearFields();
            displayAll();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "⚠ Roll Number must be numeric!");
        }
    }

    private void editStudent() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "⚠ Select a student to edit.");
            return;
        }

        int oldRoll = Integer.parseInt(tableModel.getValueAt(selected, 0).toString());
        Student s = sms.searchStudent(oldRoll);

        JTextField rollF = new JTextField(String.valueOf(s.getRollNumber()));
        JTextField nameF = new JTextField(s.getName());
        JTextField gradeF = new JTextField(s.getGrade());
        JTextField emailF = new JTextField(s.getEmail());
        JTextField phoneF = new JTextField(s.getPhone());

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.add(new JLabel("Roll Number:")); panel.add(rollF);
        panel.add(new JLabel("Name:")); panel.add(nameF);
        panel.add(new JLabel("Grade:")); panel.add(gradeF);
        panel.add(new JLabel("Email:")); panel.add(emailF);
        panel.add(new JLabel("Phone:")); panel.add(phoneF);

        int option = JOptionPane.showConfirmDialog(this, panel, "Edit Student",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            try {
                int newRoll = Integer.parseInt(rollF.getText().trim());
                String newName = nameF.getText().trim();
                String newGrade = gradeF.getText().trim();
                String newEmail = emailF.getText().trim();
                String newPhone = phoneF.getText().trim();

                if (!validateInput(newName, newGrade, newEmail, newPhone)) return;

                Student existing = sms.searchStudent(newRoll);
                if (existing != null && existing.getRollNumber() != oldRoll) {
                    JOptionPane.showMessageDialog(this, "⚠ Roll Number already exists!");
                    return;
                }

                if (newRoll != oldRoll) {
                    sms.removeStudent(oldRoll);
                    sms.addStudent(newRoll, newName, newGrade, newEmail, newPhone);
                } else {
                    sms.updateStudent(newRoll, newName, newGrade, newEmail, newPhone);
                }

                JOptionPane.showMessageDialog(this, "✅ Student Updated!");
                displayAll();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "⚠ Roll Number must be numeric!");
            }
        }
    }

    private void deleteStudent() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "⚠ Select a student to delete.");
            return;
        }

        int roll = Integer.parseInt(tableModel.getValueAt(selected, 0).toString());
        if (sms.removeStudent(roll)) {
            JOptionPane.showMessageDialog(this, "✅ Student Deleted!");
            displayAll();
        }
    }

    //  Live Search (filters as you type)
    private void liveSearch() {
        String query = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);

        for (Student s : sms.getAllStudents()) {
            if (s.getName().toLowerCase().contains(query)) {
                tableModel.addRow(new Object[]{s.getRollNumber(), s.getName(),
                        s.getGrade(), s.getEmail(), s.getPhone()});
            }
        }
    }

    private void displayAll() {
        tableModel.setRowCount(0);
        for (Student s : sms.getAllStudents()) {
            tableModel.addRow(new Object[]{s.getRollNumber(), s.getName(), s.getGrade(), s.getEmail(), s.getPhone()});
        }
    }

    private boolean validateInput(String name, String grade, String email, String phone) {
        if (name.isEmpty() || grade.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ All fields are required!");
            return false;
        }
        if (!phone.matches("\\d{10,}")) {
            JOptionPane.showMessageDialog(this, "⚠ Phone must be at least 10 digits!");
            return false;
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "⚠ Invalid email format!");
            return false;
        }
        return true;
    }

    private void clearFields() {
        rollField.setText("");
        nameField.setText("");
        gradeField.setText("");
        emailField.setText("");
        phoneField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagementApp().setVisible(true));
    }
}
