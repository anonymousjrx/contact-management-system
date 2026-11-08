package dao;

import config.DatabaseConnection;
import model.Contact;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContactDAO {

    public void insert(Contact c) throws SQLException {
        String sql = "INSERT INTO contacts (full_name, phone1, phone2, address, date_of_birth, email) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fillStatement(ps, c);
            ps.executeUpdate();
        }
    }

    public boolean update(Contact c) throws SQLException {
        String sql = "UPDATE contacts SET full_name = ?, phone1 = ?, phone2 = ?, address = ?, "
                + "date_of_birth = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fillStatement(ps, c);
            ps.setInt(7, c.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM contacts WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Contact> findAll() throws SQLException {
        String sql = "SELECT * FROM contacts ORDER BY full_name";
        List<Contact> contacts = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                contacts.add(mapRow(rs));
            }
        }
        return contacts;
    }

    public List<Contact> search(String text) throws SQLException {
        String sql = "SELECT * FROM contacts WHERE full_name LIKE ? OR phone1 LIKE ? OR phone2 LIKE ? "
                + "ORDER BY full_name";
        List<Contact> contacts = new ArrayList<>();
        String pattern = "%" + text + "%";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contacts.add(mapRow(rs));
                }
            }
        }
        return contacts;
    }

    // Sets the 6 shared parameters (id is handled separately in update)
    private void fillStatement(PreparedStatement ps, Contact c) throws SQLException {
        ps.setString(1, c.getFullName());
        ps.setString(2, c.getPhone1());
        ps.setString(3, c.getPhone2());
        ps.setString(4, c.getAddress());
        if (c.getDateOfBirth() == null) {
            ps.setNull(5, Types.DATE);
        } else {
            ps.setDate(5, Date.valueOf(c.getDateOfBirth()));
        }
        ps.setString(6, c.getEmail());
    }

    // Converts one database row into a Contact
    private Contact mapRow(ResultSet rs) throws SQLException {
        Date sqlDate = rs.getDate("date_of_birth");
        LocalDate dob = (sqlDate == null) ? null : sqlDate.toLocalDate();
        return new Contact(
                rs.getInt("id"),
                rs.getString("full_name"),
                rs.getString("phone1"),
                rs.getString("phone2"),
                rs.getString("address"),
                dob,
                rs.getString("email")
        );
    }

    public boolean existsByPhone(String phone, int excludeId) throws SQLException {
        String sql = "SELECT 1 FROM contacts WHERE (phone1 = ? OR phone2 = ?) AND id <> ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setString(2, phone);
            ps.setInt(3, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsByEmail(String email, int excludeId) throws SQLException {
        String sql = "SELECT 1 FROM contacts WHERE email = ? AND id <> ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}