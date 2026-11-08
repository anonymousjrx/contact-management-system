import controller.ContactController;
import dao.ContactDAO;
import view.ContactView;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        setLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            ContactView view = new ContactView();
            ContactDAO dao = new ContactDAO();
            new ContactController(view, dao);
            view.setVisible(true);
        });
    }

    private static void setLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("Nimbus not available: " + e.getMessage());
        }

        // Fallback: system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("System look and feel not available: " + e.getMessage());
        }
    }
}