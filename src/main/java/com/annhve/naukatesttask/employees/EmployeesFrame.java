package com.annhve.naukatesttask.employees;

import com.annhve.naukatesttask.model.Department;
import com.annhve.naukatesttask.model.Employee;
import com.annhve.naukatesttask.util.DbConnector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class EmployeesFrame extends JFrame implements IEmployeesChangeListener {
    private final DbConnector dbConnector;

    private JTable table;
    private JButton deleteButton;
    private JButton editButton;
    private JTextField txtFilter;
    private TableRowSorter<EmployeeTableModel> sorter;
    private EmployeeTableModel tableModel;
    private Employee selectedEntity;

    private void addComponents(final Container pane) {
        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new GridBagLayout());
        rootPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        GridBagConstraints c = new GridBagConstraints();

        pane.setPreferredSize(new Dimension(1200, 400));

        Container bodyPanel = createBodyPanel();
        Container titlePanel = createTitlePanel();
        Container buttonsPanel = createButtonsPanel();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        rootPanel.add(titlePanel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        rootPanel.add(buttonsPanel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 300;
        c.weightx = 0.0;
        c.gridwidth = 4;
        c.gridx = 0;
        c.gridy = 1;
        rootPanel.add(bodyPanel, c);

        pane.add(rootPanel);
    }

    private Container createTitlePanel() {
        JLabel lblBookFilter = new JLabel("Поиск ");
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout());
        titlePanel.add(lblBookFilter);
        titlePanel.add(txtFilter);

        return titlePanel;
    }

    private Container createButtonsPanel() {
        final JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        JButton addButton = new JButton("Добавить");
        addButton.addActionListener(e -> {
            try {
                ArrayList<Department> departments = dbConnector.selectAllDepartments();
                Department[] departmentsArr = new Department[departments.size()];
                departmentsArr = departments.toArray(departmentsArr);
                AddEmployeeFrame frame = new AddEmployeeFrame(dbConnector, this, departmentsArr);
                frame.createAndShowGui();
            } catch (Exception exception) {
                exception.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ошибка:\n" + exception.getLocalizedMessage());
            }
        });
        deleteButton = new JButton("Удалить");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> {
            try {
                if (selectedEntity == null) {
                    JOptionPane.showMessageDialog(null, "Выберите сотрудника");
                    return;
                }
                System.out.println("deleting " + selectedEntity.getId());
                int id = selectedEntity.getId();
                int result = dbConnector.deleteEmployee(id);
                if (result > 0) {
                    refreshTableData();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ошибка:\n" + exception.getLocalizedMessage());
            }
        });
        editButton = new JButton("Редактировать");
        editButton.setEnabled(false);
        editButton.addActionListener(e -> {
            try {
                if (selectedEntity == null) {
                    JOptionPane.showMessageDialog(null, "Выберите сотрудника");
                    return;
                }
                ArrayList<Department> departments = dbConnector.selectAllDepartments();
                Department[] departmentsArr = new Department[departments.size()];
                departmentsArr = departments.toArray(departmentsArr);
                EditEmployeeFrame frame = new EditEmployeeFrame(dbConnector, this, departmentsArr, selectedEntity);
                frame.createAndShowGui();
            } catch (Exception exception) {
                exception.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ошибка:\n" + exception.getLocalizedMessage());
            }
        });

        buttonsPanel.add(addButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(editButton);

        return buttonsPanel;
    }

    private Container createBodyPanel() {
        tableModel = new EmployeeTableModel(fetchEntities());
        table = new JTable();
        sorter = new TableRowSorter<>(tableModel);
        table.setModel(tableModel);
        table.setAutoCreateRowSorter(true);
        table.setRowSorter(sorter);
        final JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer(table));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel listSelectionModel = table.getSelectionModel();
        listSelectionModel.addListSelectionListener(new ListSelectionHandler());
        table.setSelectionModel(listSelectionModel);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                System.out.println("mouse clicked");
                initDetail(table.getSelectedRow());
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);

        sorter.setSortsOnUpdates(true);
        List<RowSorter.SortKey> sortKeyList = new ArrayList<>();
        sortKeyList.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeyList);
        sorter.sort();

        txtFilter = new JTextField();
        txtFilter.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                filterBook();
            }

            public void insertUpdate(DocumentEvent e) {
                filterBook();
            }

            public void removeUpdate(DocumentEvent e) {
                filterBook();
            }
        });
        txtFilter.setColumns(10);

        return scrollPane;
    }

    /**
     * Create the frame.
     */
    public EmployeesFrame(DbConnector dbConnector) {
        super("Сотрудники");
        this.dbConnector = dbConnector;

        setMinimumSize(new Dimension(800, 400));
        setMaximumSize(new Dimension(1920, 400));
    }

    public void createAndShowGui() {
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.addComponents(this.getContentPane());
        this.pack();
        this.setVisible(true);
    }

    private ArrayList<Employee> fetchEntities() {
        ArrayList<Employee> list = new ArrayList<>();
        try {
            list.addAll(dbConnector.selectAllEmployees());
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return list;
    }

    protected void initDetail(int selectedRow) {
        if (selectedRow < 0 || selectedRow >= tableModel.list.size()) {
            selectedEntity = null;
        } else {
            selectedEntity = tableModel.list.get(table.convertRowIndexToModel(selectedRow));
        }
        editButton.setEnabled(selectedEntity != null);
        deleteButton.setEnabled(selectedEntity != null);
    }

    private void refreshTableData() {
        System.out.println("Refreshing");
        tableModel.list.clear();
        tableModel.list.addAll(fetchEntities());
        table.revalidate();
        table.repaint();
        table.getSelectionModel().clearSelection();
        tableModel.fireTableDataChanged();
    }

    private void filterBook() {
        RowFilter<TableModel, Object> rf;
        try {
            rf = RowFilter.regexFilter("(?i)" + txtFilter.getText());
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        sorter.setRowFilter(rf);
    }

    static class EmployeeTableModel extends AbstractTableModel {

        List<Employee> list;

        String[] headerList = {
                "ID",
                "Имя",
                "Фамилия",
                "Дата рождения",
                "Департамент",
                "Должность",
                "Адрес",
                "Удалённо"
        };

        Class[] classes = {
                Integer.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class
        };

        public EmployeeTableModel(List<Employee> list) {
            this.list = list;
        }

        @Override
        public int getColumnCount() {
            return headerList.length;
        }

        @Override
        public int getRowCount() {
            return list.size();
        }

        @Override
        public Class<?> getColumnClass(int arg0) {
            return classes[arg0];
        }

        @Override
        public Object getValueAt(int row, int column) {
            Employee entity = list.get(row);
            switch (column) {
                case 0:
                    return entity.getId();
                case 1:
                    return entity.getName();
                case 2:
                    return entity.getSurname();
                case 3:
                    return entity.getDateOfBirthday();
                case 4:
                    return entity.getDepartment();
                case 5:
                    return entity.getPosition();
                case 6:
                    return entity.getAddress();
                case 7:
                    return entity.getRemoteWork() > 0 ? "Да" : "Нет";
                default:
                    return "";
            }
        }

        @Override
        public String getColumnName(int col) {
            return headerList[col];
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            Employee entity = list.get(row);
            switch (col) {
                case 0:
                    entity.setId((Integer) value);
                    break;
                case 1:
                    entity.setName((String) value);
                    break;
                case 2:
                    entity.setSurname((String) value);
                    break;
                case 3:
                    entity.setDateOfBirthday((String) value);
                    break;
                case 4:
                    entity.setDepartment((String) value);
                    break;
                case 5:
                    entity.setPosition((String) value);
                    break;
                case 6:
                    entity.setAddress((String) value);
                    break;
                case 7:
                    entity.setRemoteWork(((String) value).equalsIgnoreCase("Да") ? 1 : 0);
                    break;
                default:
                    break;

            }
            fireTableCellUpdated(row, col);
        }
    }

    private static class HeaderRenderer implements TableCellRenderer {
        TableCellRenderer renderer;

        public HeaderRenderer(JTable table) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int col
        ) {
            return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        }
    }

    class ListSelectionHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                ListSelectionModel model = table.getSelectionModel();
                int index = model.getLeadSelectionIndex();
                initDetail(index);
            }
        }
    }

    @Override
    public void onRecordAdded() {
        refreshTableData();
    }

    @Override
    public void onRecordEdited() {
        refreshTableData();
    }
}