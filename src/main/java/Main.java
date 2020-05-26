import util.DbConnector;
import util.UserRepository;

import javax.swing.*;
import java.awt.*;

public class Main {
    private static final DbConnector dbConnector = new DbConnector();
    private static final UserRepository userRepository = new UserRepository(dbConnector);

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                AuthorizationFrame frame = new AuthorizationFrame(dbConnector, userRepository);
                frame.createAndShowGui();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Ошибка:\n" + e.getLocalizedMessage());
                e.printStackTrace();
            }
        });
    }
}
