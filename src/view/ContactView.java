package view;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;

public class ContactView extends JFrame {

    // Panels
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel formPanel;

    // Labels
    private JLabel lblImage;
    private JLabel lblTitle;
    private JLabel lblSubTitle;
    private JLabel lblFullName;
    private JLabel lblPhone1;
    private JLabel lblPhone2;
    private JLabel lblAddress;
    private JLabel lblDateOfBirth;
    private JLabel lblEmail;
    private JLabel lblSearch;

    // TextFields
    private JTextField txtFullName;
    private JTextField txtPhone1;
    private JTextField txtPhone2;
    private JTextField txtAddress;
    private JDateChooser dtDateOfBirth;
    private JTextField txtEmail;
    private JTextField txtSearch;

    // Buttons
    private JButton btnSave;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnEdit;

    // Table
    private JTable tblContacts;
    private JScrollPane scrollPane;

    public ContactView() {
        setSize(698, 532);
        setTitle("Contact Management System");
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addPanels();
        addLabels();
        addTextFields();
        addButtons();
        addTable();
    }

    private void addPanels() {
        mainPanel = new JPanel();
        mainPanel.setBackground(Color.white);
        mainPanel.setLayout(null);
        this.add(mainPanel);

        headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 51, 255));
        headerPanel.setBounds(0, 0, this.getWidth(), 70);
        headerPanel.setLayout(null);
        mainPanel.add(headerPanel);

        formPanel = new JPanel();
        formPanel.setBackground(Color.lightGray);
        formPanel.setBounds(10, 80, 665, 140);
        formPanel.setLayout(null);
        mainPanel.add(formPanel);
    }

    private void addLabels() {
        lblImage = createLabel(headerPanel, "", 10, 10, 60, 60);

        java.net.URL imageUrl = getClass().getResource("/images/contact.png");

        if (imageUrl != null) {
            ImageIcon iconUrl = new ImageIcon(imageUrl);
            Image scaledImage = iconUrl.getImage().getScaledInstance(lblImage.getWidth(), lblImage.getHeight(), Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(scaledImage));
        }else {
            System.err.println("Image not found ");
        }

        lblTitle = new JLabel("Contact Management");
        lblTitle.setFont(new Font("Calibri", Font.BOLD, 16));
        lblTitle.setForeground(Color.white);
        lblTitle.setBounds(70, 15, 200, 20);
        headerPanel.add(lblTitle);

        lblSubTitle = new JLabel("manage your contacts easily");
        lblSubTitle.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSubTitle.setForeground(Color.white);
        lblSubTitle.setBounds(70, 32, 200, 20);
        headerPanel.add(lblSubTitle);

        lblFullName = createLabel(formPanel,"Full name: ", 15, 5, 200, 20);
        lblPhone1 = createLabel(formPanel, "Phone 1: ", 235, 5, 200, 20);
        lblPhone2 = createLabel(formPanel, "Phone 2: ", 450, 5, 200, 20);
        lblAddress = createLabel(formPanel, "Address: ", 15, 70, 200, 20);
        lblDateOfBirth = createLabel(formPanel, "Date of Birth: ", 235, 70, 200, 20);
        lblEmail = createLabel(formPanel, "Email: ", 450, 70, 200, 20);
        lblSearch = createLabel(mainPanel, "Search (name/phone): ", 10, 275, 300, 20);
    }

    private void addTextFields() {
        txtFullName = createTextField(formPanel,15, 25, 200, 35);
        txtPhone1 = createTextField(formPanel, 235, 25, 200, 35);
        txtPhone2 = createTextField(formPanel, 450, 25, 200, 35);
        txtAddress = createTextField(formPanel, 15, 90, 200, 35);
        txtEmail = createTextField(formPanel, 450, 90, 200, 35);

        dtDateOfBirth = new JDateChooser();
        dtDateOfBirth.setDateFormatString("yyyy-MM-dd");
        dtDateOfBirth.setBounds(235, 90, 195, 35);
        formPanel.add(dtDateOfBirth);

        txtSearch = createTextField(mainPanel, 10, 295, 500, 35);
    }

    private void addButtons() {
        btnSave = createButton("Save", 10, 230, 160, 35, Color.green, "/images/save.png");
        btnUpdate = createButton("Update", 178, 230, 160, 35, Color.blue, "/images/update.png");
        btnDelete = createButton("Delete", 348, 230, 160, 35, Color.red, "/images/delete.png");
        btnClear = createButton("Clear", 515, 230, 160, 35, Color.darkGray, "/images/clear.png");
        btnEdit = createButton("Edit", 515, 295, 160, 35, Color.orange, "/images/edit.png");
    }

    private void addTable() {
        tblContacts = new JTable();
        scrollPane = new JScrollPane(tblContacts);
        scrollPane.setBounds(10, 340, 662, 150);
        mainPanel.add(scrollPane);
    }

    private JLabel createLabel(JPanel parent, String text, int x, int y, int w, int h) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, w, h);
        label.setFont(new Font("Calibri", Font.PLAIN, 13));
        parent.add(label);
        return label;
    }

    private JTextField createTextField(JPanel parent, int x, int y, int width, int height) {
        JTextField textField = new JTextField();
        textField.setBounds(x, y, width, height);
        parent.add(textField);
        return textField;
    }

    private JButton createButton(String text, int x, int y, int w, int h, Color background, String iconPath) {
        JButton button = new JButton(text);
        button.setBounds(x, y, w, h);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(background);
        button.setForeground(Color.white);
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        java.net.URL iconUrl = getClass().getResource(iconPath);

        if (iconUrl != null){
            button.setIcon(new ImageIcon(iconUrl));
        }else{
            System.err.println("Image not found");
        }
        mainPanel.add(button);

        return button;
    }

    public JTextField getTxtFullName() { return txtFullName; }
    public JTextField getTxtPhone1() { return txtPhone1; }
    public JTextField getTxtPhone2() { return txtPhone2; }
    public JTextField getTxtAddress() { return txtAddress; }
    public JDateChooser getDtDateOfBirth() { return dtDateOfBirth; }
    public JTextField getTxtEmail() { return txtEmail; }
    public JTextField getTxtSearch() { return txtSearch; }

    public JButton getBtnSave() { return btnSave; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
    public JButton getBtnClear() { return btnClear; }
    public JButton getBtnEdit() { return btnEdit; }

    public JTable getTblContacts() { return tblContacts; }
}