package com.annhve.naukatesttask.departments;

import com.annhve.naukatesttask.model.Department;
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

public class DepartmentsFrame extends JFrame implements IDepartmentsChangeListener {
    private final DbConnector dbConnector;

    private JTable table;
    private JButton deleteButton;
    private JButton editButton;
    private JTextField txtFilter;
    private TableRowSorter<DepartmentTableModel> sorter;
    private DepartmentTableModel tableModel;
    private Department selectedEntity;

    private void addComponents(final Container pane) {
        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new GridBagLayout());
        rootPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        GridBagConstraints c = new GridBagConstraints();

        pane.setPreferredSize(new Dimension(800, 300));

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
        c.ipady = 200;
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
            AddDepartmentFrame frame = new AddDepartmentFrame(dbConnector, this);
            frame.createAndShowGui();
        });
        deleteButton = new JButton("Удалить");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> {
            try {
                System.out.println("deleting " + selectedEntity.getId());
                int id = selectedEntity.getId();
                int result = dbConnector.deleteDepartment(id);
                if (result > 0) {
                    refreshTableData();
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                JOptionPane.showMessageDialog(null, "Не удалось удалить департамент");
            }
        });
        editButton = new JButton("Редактировать");
        editButton.setEnabled(false);
        editButton.addActionListener(e -> {
            if (selectedEntity == null) {
                JOptionPane.showMessageDialog(null, "Выберите департамент");
                return;
            }
            EditDepartmentFrame frame = new EditDepartmentFrame(dbConnector, this, selectedEntity);
            frame.createAndShowGui();
        });

        buttonsPanel.add(addButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(editButton);

        return buttonsPanel;
    }

    private Container createBodyPanel() {
        tableModel = new DepartmentTableModel(fetchEntities());
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
            @Override
            public void mouseClicked(MouseEvent e) {
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
    public DepartmentsFrame(DbConnector dbConnector) {
        super("Департаменты");
        this.dbConnector = dbConnector;

        setMinimumSize(new Dimension(800, 300));
        setMaximumSize(new Dimension(1000, 300));
    }

    public void createAndShowGui() {
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.addComponents(this.getContentPane());
        this.pack();
        this.setVisible(true);
    }

    private ArrayList<Department> fetchEntities() {
        ArrayList<Department> list = new ArrayList<>();
        try {
            list.addAll(dbConnector.selectAllDepartments());
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

    static class DepartmentTableModel extends AbstractTableModel {

        List<Department> list;

        String[] headerList = {
                "ID",
                "Название"
        };

        Class[] classes = {
                Integer.class,
                String.class
        };

        public DepartmentTableModel(List<Department> list) {
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
            Department entity = list.get(row);
            switch (column) {
                case 0:
                    return entity.getId();
                case 1:
                    return entity.getName();
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
            Department entity = list.get(row);
            switch (col) {
                case 0:
                    entity.setId((Integer) value);
                    break;
                case 1:
                    entity.setName((String) value);
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
