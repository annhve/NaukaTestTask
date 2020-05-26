package model;

import javax.swing.*;
import java.util.ArrayList;

public class DepartmentListModel extends AbstractListModel<Department> {
    // Коллекция для хранения данных
    private final ArrayList<Department> data = new ArrayList<>();

    // Загрузка данных из БД
    public void setDataSource(ArrayList<Department> list) {
        try {
            // Очистка коллекции
            data.clear();
            data.addAll(list);
            // Оповещение видов об изменении
            fireContentsChanged(this, 0, data.size());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // Функция размера массива данных в списке
    @Override
    public int getSize() {
        synchronized (data) {
            return data.size();
        }
    }

    // Функция извлечения элемента
    @Override
    public Department getElementAt(int idx) {
        synchronized (data) {
            return data.get(idx);
        }
    }
}
