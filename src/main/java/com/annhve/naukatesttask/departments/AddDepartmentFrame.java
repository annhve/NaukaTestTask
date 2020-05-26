package com.annhve.naukatesttask.departments;

import com.annhve.naukatesttask.util.DbConnector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddDepartmentFrame extends JFrame {
    private final DbConnector dbConnector;
    private final IDepartmentsChangeListener listener;

    public AddDepartmentFrame(DbConnector dbConnector, IDepartmentsChangeListener listener) {
        super("Добавление департамена");
        this.dbConnector = dbConnector;
        this.listener = listener;
    }

    private void addComponents(final Container pane) {
        pane.setPreferredSize(new Dimension(400, 300));
        pane.add(createBodyPanel(), BorderLayout.CENTER);
    }

    private Container createBodyPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));
        panel.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("Наименование: ");
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField nameTextField = new JTextField(20);
        nameTextField.setMaximumSize(new Dimension(300, 30));
        nameTextField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton addButton = new JButton();
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.setMargin(new Insets(10,  40, 10, 40));

        addButton.setText("Добавить");
        addButton.addActionListener(e -> {
            String name = nameTextField.getText() == null ? "" : nameTextField.getText();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Введите наименование департамента");
            } else {
                try {
                    int res = dbConnector.insertDepartment(name);
                    if (res > 0) {
                        listener.onRecordAdded();
                        this.setVisible(false);
                    } else {
                        JOptionPane.showMessageDialog(null, "Не удалось добавить департамент");
                    }
                } catch (Exception throwable) {
                    JOptionPane.showMessageDialog(null, "Ошибка:\n" + throwable.getLocalizedMessage());
                }
            }
        });

        panel.add(nameLabel);
        panel.add(nameTextField);
        panel.add(addButton);

        return panel;
    }

    public void createAndShowGui() {
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.addComponents(this.getContentPane());
        this.pack();
        this.setVisible(true);
    }
}
