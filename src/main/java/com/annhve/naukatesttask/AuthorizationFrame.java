package com.annhve.naukatesttask;

import com.annhve.naukatesttask.model.User;
import com.annhve.naukatesttask.util.DbConnector;
import com.annhve.naukatesttask.util.UserRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AuthorizationFrame extends JFrame {
    private final DbConnector dbConnector;
    private final UserRepository userRepository;

    public AuthorizationFrame(DbConnector dbConnector, UserRepository userRepository) {
        super("Авторизация");
        this.dbConnector = dbConnector;
        this.userRepository = userRepository;
    }

    private void addComponents(final Container pane) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        pane.setPreferredSize(new Dimension(400, 300));
        pane.add(createUserPanel(), BorderLayout.CENTER);
    }

    private Container createUserPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));
        panel.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel loginLabel = new JLabel("Логин: ");
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField loginTextField = new JTextField(20);
        loginTextField.setMaximumSize(new Dimension(300, 30));
        loginTextField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passwordLabel = new JLabel("Пароль: ");
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPasswordField passwordTextField = new JPasswordField(20);
        passwordTextField.setMaximumSize(new Dimension(300, 30));
        passwordTextField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton authButton = new JButton();
        authButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        authButton.setMargin(new Insets(10,  40, 10, 40));

        authButton.setText("Авторизоваться");
        authButton.addActionListener(e -> {
            String login = loginTextField.getText() == null ? "" : loginTextField.getText();
            String password = passwordTextField.getPassword() == null ? "" : new String(passwordTextField.getPassword());
            if (login.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Введите логин и пароль");
            } else {
                User user;
                try {
                    user = userRepository.signIn(login, password);
                    if (user == null) {
                        JOptionPane.showMessageDialog(null, "Пользователь не найден");
                    } else {
                        System.out.println(user.toString());
                        MainFrame frame = new MainFrame(dbConnector, userRepository);
                        this.setVisible(false);
                        frame.createAndShowGui();
                    }
                } catch (Exception throwable) {
                    JOptionPane.showMessageDialog(null, "Не удалось войти:\n" + throwable.getLocalizedMessage());
                    throwable.printStackTrace();
                }
            }
        });

        panel.add(loginLabel);
        panel.add(loginTextField);
        panel.add(passwordLabel);
        panel.add(passwordTextField);
        panel.add(authButton);

        return panel;
    }

    public void createAndShowGui() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.addComponents(this.getContentPane());
        this.pack();
        this.setVisible(true);
    }
}