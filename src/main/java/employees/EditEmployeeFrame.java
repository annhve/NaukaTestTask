package employees;

import model.Department;
import model.Employee;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import util.DbConnector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class EditEmployeeFrame extends JFrame {
    private final DbConnector dbConnector;
    private final IEmployeesChangeListener listener;
    private final Department[] departments;
    private final Employee employee;

    private Date selectedDate;
    private Department selectedDepartment;
    private boolean isRemoteWorker = false;

    public EditEmployeeFrame(DbConnector dbConnector, IEmployeesChangeListener listener, Department[] departments, Employee employee) {
        super("Изменение сотрудника " + employee.getName() + " " + employee.getSurname());
        this.dbConnector = dbConnector;
        this.listener = listener;
        this.departments = departments;
        this.employee = employee;
    }

    private void addComponents(final Container pane) {
        pane.setPreferredSize(new Dimension(400, 500));
        pane.add(createBodyPanel(), BorderLayout.CENTER);
    }

    private Container createBodyPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));
        panel.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("Имя: ");
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField nameTextField = new JTextField(20);
        nameTextField.setMaximumSize(new Dimension(300, 30));
        nameTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameTextField.setText(employee.getName());

        JLabel surnameLabel = new JLabel("Фамилия: ");
        surnameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField surnameTextField = new JTextField(20);
        surnameTextField.setMaximumSize(new Dimension(300, 30));
        surnameTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        surnameTextField.setText(employee.getSurname());

        JLabel birthdayLabel = new JLabel("Дата рождения: ");
        birthdayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        UtilDateModel model = new UtilDateModel();
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            java.util.Date d = dateFormat.parse(employee.getDateOfBirthday());
            model.setValue(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JDatePanelImpl datePanel = new JDatePanelImpl(model);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel);
        datePicker.setMaximumSize(new Dimension(300, 30));
        datePicker.setAlignmentX(Component.CENTER_ALIGNMENT);
        datePicker.addActionListener(e -> {
            var d = model.getValue();
            selectedDate = new Date(d.getTime());
        });

        JLabel departmentLabel = new JLabel("Департамент: ");
        departmentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JComboBox<Department> departmentComboBox = new JComboBox<>(departments);
        departmentComboBox.setMaximumSize(new Dimension(300, 30));
        departmentComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        departmentComboBox.addActionListener(e -> selectedDepartment = departmentComboBox.getItemAt(departmentComboBox.getSelectedIndex()));
        for (Department department : departments) {
            if (department.getId() == employee.getDepartmentId()) {
                departmentComboBox.getModel().setSelectedItem(department);
                selectedDepartment = department;
            }
        }

        JLabel positionLabel = new JLabel("Должность: ");
        positionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField positionTextField = new JTextField(20);
        positionTextField.setMaximumSize(new Dimension(300, 30));
        positionTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        positionTextField.setText(employee.getPosition());

        JLabel addressLabel = new JLabel("Адрес: ");
        addressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField addressTextField = new JTextField(20);
        addressTextField.setMaximumSize(new Dimension(300, 30));
        addressTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        addressTextField.setText(employee.getAddress());

        JCheckBox remoteWorkBox = new JCheckBox("Удалённая работа ");
        remoteWorkBox.setMaximumSize(new Dimension(300, 30));
        remoteWorkBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        remoteWorkBox.addItemListener(e -> {
            isRemoteWorker = remoteWorkBox.isSelected();
        });
        remoteWorkBox.setSelected(employee.getRemoteWork() > 0);

        JButton editButton = new JButton();
        editButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        editButton.setMargin(new Insets(10,  40, 10, 40));

        editButton.setText("Изменить");
        editButton.addActionListener(e -> {
            String name = nameTextField.getText() == null ? "" : nameTextField.getText();
            String surname = surnameTextField.getText() == null ? "" : surnameTextField.getText();
            String position = positionTextField.getText() == null ? "" : positionTextField.getText();
            String address = addressTextField.getText() == null ? "" : addressTextField.getText();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Введите имя сотрудника");
                return;
            }
            if (surname.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Введите фамилию сотрудника");
                return;
            }
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(null, "Выберите дату рождения сотрудника");
                return;
            }
            if (selectedDepartment == null) {
                JOptionPane.showMessageDialog(null, "Выберите департамент сотрудника");
                return;
            }
            if (position.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Введите должность сотрудника");
                return;
            }
            if (address.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Введите адрес сотрудника");
                return;
            }

            try {
                int res = dbConnector.updateEmployee(
                        employee.getId(),
                        name,
                        surname,
                        selectedDate,
                        selectedDepartment.getId(),
                        position,
                        address,
                        isRemoteWorker
                );
                if (res > 0) {
                    listener.onRecordAdded();
                    this.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(null, "Не удалось изменить сотрудника");
                }
            } catch (Exception throwable) {
                JOptionPane.showMessageDialog(null, "Ошибка:\n" + throwable.getLocalizedMessage());
            }
        });

        panel.add(nameLabel);
        panel.add(nameTextField);
        panel.add(surnameLabel);
        panel.add(surnameTextField);
        panel.add(birthdayLabel);
        panel.add(datePicker);
        panel.add(departmentLabel);
        panel.add(departmentComboBox);
        panel.add(positionLabel);
        panel.add(positionTextField);
        panel.add(addressLabel);
        panel.add(addressTextField);
        panel.add(remoteWorkBox);
        panel.add(editButton);

        return panel;
    }

    public void createAndShowGui() {
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.addComponents(this.getContentPane());
        this.pack();
        this.setVisible(true);
    }
}
