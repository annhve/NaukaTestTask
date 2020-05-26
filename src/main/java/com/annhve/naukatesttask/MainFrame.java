package com.annhve.naukatesttask;

import com.annhve.naukatesttask.departments.DepartmentsFrame;
import com.annhve.naukatesttask.employees.EmployeesFrame;
import com.annhve.naukatesttask.model.*;
import com.annhve.naukatesttask.util.DbConnector;
import com.annhve.naukatesttask.util.UserRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

class MainFrame extends JFrame {
    DepartmentListModel departmentListModel = new DepartmentListModel();
    EmployeeListModel employeeListModel = new EmployeeListModel();

    private Employee currentSelectedEmployee;
    private String currentSelectedTabTitle;
    private JPanel currentSelectedTab;

    private final DbConnector dbConnector;
    private final UserRepository userRepository;

    private Mark[] marksArr = new Mark[0];

    public MainFrame(DbConnector dbConnector, UserRepository userRepository) {
        super("Табель");
        this.dbConnector = dbConnector;
        this.userRepository = userRepository;

        setMinimumSize(new Dimension(600, 460));
    }

    private void fetchCalendarDays() {
        try {
            if (currentSelectedTabTitle == null || currentSelectedTab == null) {
                return;
            }
            ArrayList<DayInMonth> days = dbConnector.getDaysInMonth(
                    currentSelectedTabTitle, currentSelectedEmployee != null ? currentSelectedEmployee.getId() : 0
            );

            ((JPanel) currentSelectedTab.getComponent(0)).removeAll();
            ((JPanel) currentSelectedTab.getComponent(1)).removeAll();

            for (DayInMonth day : days) {
                JLabel dayLabel = new JLabel(day.getDay());
                dayLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
                dayLabel.setHorizontalAlignment(JLabel.CENTER);
                JLabel typeLabel = new JLabel(day.getCommonTitle());
                typeLabel.setHorizontalAlignment(JLabel.CENTER);

                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(0, 1, 0, 0));
                panel.setBorder(new EmptyBorder(10, 0, 0, 0));
                if (day.getCommonTitle().equalsIgnoreCase("рабочий")) {
                    panel.setBackground(new Color(141, 174, 175));
                }
                if (day.getCommonTitle().equalsIgnoreCase("выходной")) {
                    panel.setBackground(new Color(239, 175, 252));
                }
                if (day.getCommonTitle().equalsIgnoreCase("праздничный")) {
                    panel.setBackground(new Color(237, 71, 74));
                }
                if (day.getCommonTitle().equalsIgnoreCase("предпраздничный")) {
                    panel.setBackground(new Color(168, 116, 122));
                }

                panel.add(dayLabel);
                panel.add(typeLabel);

                if (currentSelectedEmployee != null) {
                    if (userRepository.getLoggedUser().getIdRole() == 1) {
                        JComboBox<Mark> markComboBox = new JComboBox<>(marksArr);
                        markComboBox.setToolTipText("Выберите отметку");
                        markComboBox.setSize(new Dimension(10, 30));
                        if (day.getMarkId() > 0) {
                            for (Mark mark : marksArr) {
                                if (mark.getId() == day.getMarkId()) {
                                    markComboBox.getModel().setSelectedItem(mark);
                                }
                            }
                        } else {
                            markComboBox.setSelectedIndex(-1);
                        }
                        markComboBox.addActionListener(e -> {
                            try {
                                Mark selected = markComboBox.getItemAt(markComboBox.getSelectedIndex());
                                if (day.getMarkId() > 0) {
                                    // update
                                    dbConnector.updateEmployeeAndCalendarDay(
                                            currentSelectedEmployee.getId(),
                                            selected.getId(),
                                            day.getDayId()
                                    );
                                } else {
                                    // insert
                                    dbConnector.insertEmployeeAndCalendarDay(
                                            currentSelectedEmployee.getId(),
                                            selected.getId(),
                                            day.getDayId()
                                    );
                                }
                                fetchCalendarDays();
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        });
                        JLabel markTitleLabel = new JLabel("Отметка: ");
                        markTitleLabel.setHorizontalAlignment(JLabel.CENTER);
                        panel.add(markTitleLabel);
                        panel.add(markComboBox);
                    } else {
                        System.out.println(day.toString());
                        if (day.getMarkId() > 0) {
                            JLabel markTitleLabel = new JLabel("Отметка: ");
                            markTitleLabel.setHorizontalAlignment(JLabel.CENTER);
                            JLabel markLabel = new JLabel(day.getMark());
                            markLabel.setHorizontalAlignment(JLabel.CENTER);
                            JLabel markTranscriptionLabel = new JLabel("(" + day.getTranscription() + ")");
                            markTranscriptionLabel.setHorizontalAlignment(JLabel.CENTER);
                            panel.add(markTitleLabel);
                            panel.add(markLabel);
                            panel.add(markTranscriptionLabel);
                        }
                    }
                }

                ((JPanel) currentSelectedTab.getComponent(0)).add(panel);
                currentSelectedTab.revalidate();
            }
            if (currentSelectedEmployee != null) {
                fetchTotalWorkedDays();
            }
        } catch (Exception exception) {
            System.out.println(exception.getLocalizedMessage());
            exception.printStackTrace();
        }
    }

    private void fetchTotalWorkedDays() {
        try {
            int workDaysInMonth = dbConnector.selectFullWorkDays(
                    currentSelectedTabTitle, currentSelectedEmployee.getId()
            );
            ((JPanel) currentSelectedTab.getComponent(1)).add(new JLabel(
                    "Отработано дней в этом месяце: " + workDaysInMonth
            ), BorderLayout.CENTER);
            currentSelectedTab.revalidate();
        } catch (Exception exception) {
            System.out.println(exception.getLocalizedMessage());
            exception.printStackTrace();
        }
    }

    private void fetchAllMarks() {
        try {
            ArrayList<Mark> marks = dbConnector.selectMarks();
            marksArr = new Mark[marks.size()];
            marksArr = marks.toArray(marksArr);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    private Container createDepartmentsPanel() throws Exception {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(230, 620));

        JPanel titlePanel = new JPanel();
        titlePanel.setMaximumSize(new Dimension(230, 50));
        titlePanel.setLayout(new FlowLayout());
        JLabel label = new JLabel("Департаменты");
        JButton button = new JButton("справочник");
        button.addActionListener(e -> {
            DepartmentsFrame frame = new DepartmentsFrame(dbConnector);
            frame.createAndShowGui();
        });
        titlePanel.add(label);
        if (userRepository.getLoggedUser().getIdRole() == 2) {
            titlePanel.add(button);
        }

        departmentListModel.setDataSource(dbConnector.selectAllDepartments());
        JList<Department> listOfDepartments = new JList<>(departmentListModel);
        listOfDepartments.addListSelectionListener(e -> {
            try {
                Department department = listOfDepartments.getSelectedValue();
                if (department.getName().equalsIgnoreCase("разработка")) {
                    employeeListModel.setDataSource(dbConnector.selectAllDevelopers());
                } else if (department.getName().equalsIgnoreCase("hr")) {
                    employeeListModel.setDataSource(dbConnector.selectAllHR());
                } else {
                    employeeListModel.setDataSource(dbConnector.selectAllQAs());
                }

                fetchCalendarDays();
            } catch (Exception throwable) {
                throwable.printStackTrace();
            }
        });

        panel.add(titlePanel);
        panel.add(listOfDepartments);

        return panel;
    }

    private Container createEmployeesPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(220, 620));

        JPanel titlePanel = new JPanel();
        titlePanel.setMaximumSize(new Dimension(220, 50));
        titlePanel.setLayout(new FlowLayout());
        JLabel label = new JLabel("Сотрудники");
        JButton button = new JButton("справочник");
        button.addActionListener(e -> {
            EmployeesFrame frame = new EmployeesFrame(dbConnector);
            frame.createAndShowGui();
        });
        titlePanel.add(label);
        if (userRepository.getLoggedUser().getIdRole() == 3) {
            titlePanel.add(button);
        }

        JList<Employee> listOfEmployees = new JList<>(employeeListModel);
        currentSelectedEmployee = employeeListModel.getSize() > 0 ? employeeListModel.getElementAt(0) : null;
        listOfEmployees.addListSelectionListener(e -> {
            currentSelectedEmployee = listOfEmployees.getSelectedValue();

            fetchCalendarDays();
        });

        panel.add(titlePanel);
        panel.add(listOfEmployees);

        return panel;
    }

    private Container createTabbedPanel() throws Exception {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setPreferredSize(new Dimension(900, 760));

        ArrayList<Month> months = dbConnector.selectMonths();
        JTabbedPane tabbedPane = new JTabbedPane();
        for (Month month : months) {
            JPanel tab = new JPanel();
            tab.setLayout(new BorderLayout(0, 20));
            JPanel tabPanel = new JPanel();
            tabPanel.setLayout(new GridLayout(0, 7, 1, 1));
            JPanel workDaysPanel = new JPanel();
            workDaysPanel.setLayout(new BorderLayout());
            tab.add(tabPanel, BorderLayout.CENTER);
            tab.add(workDaysPanel, BorderLayout.SOUTH);
            tabbedPane.addTab(month.getMonth(), tab);
        }
        currentSelectedTabTitle = tabbedPane.getTitleAt(0);
        currentSelectedTab = (JPanel) tabbedPane.getComponentAt(0);

        tabbedPane.addChangeListener(e -> {
            currentSelectedTabTitle = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
            currentSelectedTab = (JPanel) tabbedPane.getSelectedComponent();

            fetchCalendarDays();
        });

        panel.add(tabbedPane);

        return panel;
    }

    private void addComponents(final Container pane) throws Exception {
        final JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(1400, 820));
        panel.setLayout(new BorderLayout());

        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEADING);
        final JPanel centerPanel = new JPanel();
        centerPanel.setLayout(flowLayout);

        Container departmentsPanel = createDepartmentsPanel();
        Container employeesPanel = createEmployeesPanel();
        Container tabbedPane = createTabbedPanel();

        fetchAllMarks();
        fetchCalendarDays();

        centerPanel.add(departmentsPanel);
        centerPanel.add(employeesPanel);
        centerPanel.add(tabbedPane);

        final JPanel southPanel = new JPanel();
        southPanel.setLayout(flowLayout);

        User user = userRepository.getLoggedUser();
        JLabel userLabel = new JLabel("Вы зашли как: " + (user == null ? "null" : user.getLogin()));
        JButton signOutButton = new JButton("Выйти");
        signOutButton.addActionListener(e -> {
            userRepository.signOut();
            AuthorizationFrame frame = new AuthorizationFrame(dbConnector, userRepository);
            this.setVisible(false);
            frame.createAndShowGui();
        });

        southPanel.add(userLabel);
        southPanel.add(signOutButton);

        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        pane.add(scrollPane, BorderLayout.CENTER);
    }

    public void createAndShowGui() throws Exception {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.addComponents(this.getContentPane());
        this.pack();
        this.setVisible(true);
    }
}