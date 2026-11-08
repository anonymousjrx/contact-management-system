package controller;

import dao.ContactDAO;
import model.Contact;
import util.ContactValidator;
import view.ContactView;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class ContactController {

    private final ContactView view;
    private final ContactDAO dao;
    private DefaultTableModel tableModel;

    // Contacts currently shown in the table (row index == list index)
    private List<Contact> currentContacts;

    // ID of the contact loaded with the Edit button (0 = none)
    private int selectedId = 0;

    public ContactController(ContactView view, ContactDAO dao) {
        this.view = view;
        this.dao = dao;

        setupTable();
        addListeners();
        loadContacts();
    }

    // ---------------------------------------------------------------- setup

    private void setupTable() {
        String[] columns = {"Full name", "Phone 1", "Phone 2", "Address", "Date of Birth", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        view.getTblContacts().setModel(tableModel);
        view.getTblContacts().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void addListeners() {
        view.getBtnSave().addActionListener(e -> saveContact());
        view.getBtnUpdate().addActionListener(e -> updateContact());
        view.getBtnDelete().addActionListener(e -> deleteContact());
        view.getBtnClear().addActionListener(e -> clearForm());
        view.getBtnEdit().addActionListener(e -> editContact());

        view.getTxtSearch().getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { searchContacts(); }
            @Override public void removeUpdate(DocumentEvent e) { searchContacts(); }
            @Override public void changedUpdate(DocumentEvent e) { searchContacts(); }
        });
    }

    // -------------------------------------------------------------- actions

    private void saveContact() {
        // The form was filled with Edit, so this contact is already in the database
        if (selectedId != 0) {
            showMessage("This contact already exists in the database.\n"
                    + "Press Update to modify it, or Clear to add a new one.");
            return;
        }

        Contact contact = readForm();
        if (contact == null) return;

        try {
            if (isDuplicate(contact)) return;
            dao.insert(contact);
            showMessage("Contact saved successfully.");
            clearForm();
            loadContacts();
        } catch (SQLException ex) {
            showError("Could not save the contact", ex);
        }
    }

    private void updateContact() {
        if (selectedId == 0) {
            showMessage("Select a contact and press Edit first.");
            return;
        }

        Contact contact = readForm();
        if (contact == null) return;
        contact.setId(selectedId);

        try {
            if (isDuplicate(contact)) return;
            if (dao.update(contact)) {
                showMessage("Contact updated successfully.");
                clearForm();
                loadContacts();
            } else {
                showMessage("The contact no longer exists.");
            }
        } catch (SQLException ex) {
            showError("Could not update the contact", ex);
        }
    }

    private void deleteContact() {
        int row = view.getTblContacts().getSelectedRow();
        if (row < 0) {
            showMessage("Select a contact from the table to delete.");
            return;
        }

        Contact contact = currentContacts.get(row);
        int option = JOptionPane.showConfirmDialog(view,
                "Delete \"" + contact.getFullName() + "\"?",
                "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (option != JOptionPane.YES_OPTION) return;

        try {
            dao.delete(contact.getId());
            clearForm();
            loadContacts();
        } catch (SQLException ex) {
            showError("Could not delete the contact", ex);
        }
    }

    private void editContact() {
        int row = view.getTblContacts().getSelectedRow();
        if (row < 0) {
            showMessage("Select a contact from the table to edit.");
            return;
        }

        Contact contact = currentContacts.get(row);
        selectedId = contact.getId();

        view.getTxtFullName().setText(contact.getFullName());
        view.getTxtPhone1().setText(contact.getPhone1());
        view.getTxtPhone2().setText(nullToEmpty(contact.getPhone2()));
        view.getTxtAddress().setText(nullToEmpty(contact.getAddress()));
        view.getTxtEmail().setText(nullToEmpty(contact.getEmail()));
        view.getDtDateOfBirth().setDate(toDate(contact.getDateOfBirth()));
    }

    private void searchContacts() {
        String text = view.getTxtSearch().getText().trim();
        try {
            List<Contact> result = text.isEmpty() ? dao.findAll() : dao.search(text);
            fillTable(result);
        } catch (SQLException ex) {
            showError("Could not search contacts", ex);
        }
    }

    // -------------------------------------------------------------- helpers

    private void loadContacts() {
        try {
            fillTable(dao.findAll());
        } catch (SQLException ex) {
            showError("Could not load contacts", ex);
        }
    }

    private void fillTable(List<Contact> contacts) {
        currentContacts = contacts;
        tableModel.setRowCount(0);
        for (Contact c : contacts) {
            tableModel.addRow(new Object[]{
                    c.getFullName(),
                    c.getPhone1(),
                    nullToEmpty(c.getPhone2()),
                    nullToEmpty(c.getAddress()),
                    c.getDateOfBirth() == null ? "" : c.getDateOfBirth().toString(),
                    nullToEmpty(c.getEmail())
            });
        }
    }

    // Reads and validates the form. Returns null if something is invalid.
    private Contact readForm() {
        String fullName = view.getTxtFullName().getText().trim();
        String phone1 = view.getTxtPhone1().getText().trim();
        String phone2 = view.getTxtPhone2().getText().trim();
        String address = view.getTxtAddress().getText().trim();
        String email = view.getTxtEmail().getText().trim().toLowerCase();
        LocalDate dob = toLocalDate(view.getDtDateOfBirth().getDate());

        if (fullName.isEmpty()) {
            return invalid("Full name is required.", view.getTxtFullName());
        }

        String error = ContactValidator.validatePhone("Phone 1", phone1, true);
        if (error != null) return invalid(error, view.getTxtPhone1());

        error = ContactValidator.validatePhone("Phone 2", phone2, false);
        if (error != null) return invalid(error, view.getTxtPhone2());

        if (!phone2.isEmpty() && phone1.equals(phone2)) {
            return invalid("Phone 1 and Phone 2 cannot be the same.", view.getTxtPhone2());
        }

        error = ContactValidator.validateEmail(email);
        if (error != null) return invalid(error, view.getTxtEmail());

        error = ContactValidator.validateDateOfBirth(dob);
        if (error != null) return invalid(error, view.getDtDateOfBirth());

        return new Contact(fullName, phone1, emptyToNull(phone2), emptyToNull(address), dob, emptyToNull(email));
    }

    // Shows the error, focuses the field and returns null so readForm can "return invalid(...)"
    private Contact invalid(String message, JComponent field) {
        showMessage(message);
        field.requestFocus();
        return null;
    }

    // Checks phones and email against other contacts (the one being edited is excluded by id)
    private boolean isDuplicate(Contact c) throws SQLException {
        if (dao.existsByPhone(c.getPhone1(), c.getId())) {
            showMessage("A contact with the phone " + c.getPhone1() + " already exists.");
            return true;
        }
        if (c.getPhone2() != null && dao.existsByPhone(c.getPhone2(), c.getId())) {
            showMessage("A contact with the phone " + c.getPhone2() + " already exists.");
            return true;
        }
        if (c.getEmail() != null && dao.existsByEmail(c.getEmail(), c.getId())) {
            showMessage("A contact with the email " + c.getEmail() + " already exists.");
            return true;
        }
        return false;
    }

    private void clearForm() {
        view.getTxtFullName().setText("");
        view.getTxtPhone1().setText("");
        view.getTxtPhone2().setText("");
        view.getTxtAddress().setText("");
        view.getTxtEmail().setText("");
        view.getDtDateOfBirth().setDate(null);
        view.getTblContacts().clearSelection();
        selectedId = 0;
    }

    private LocalDate toLocalDate(Date date) {
        return date == null ? null
                : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private Date toDate(LocalDate date) {
        return date == null ? null
                : Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private String emptyToNull(String s) {
        return s.isEmpty() ? null : s;
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(view, message);
    }

    private void showError(String message, SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(view, message + ":\n" + ex.getMessage(),
                "Database error", JOptionPane.ERROR_MESSAGE);
    }
}