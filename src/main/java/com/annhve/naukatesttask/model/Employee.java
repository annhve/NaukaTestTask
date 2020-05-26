package com.annhve.naukatesttask.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Employee {
    private int id;
    private String name;
    private String surname;
    private String dateOfBirthday;
    private int departmentId;
    private String department;
    private String position;
    private String address;
    private int remoteWork;

    public Employee(int id,
                    String name,
                    String surname,
                    String dateOfBirthday,
                    int departmentId,
                    String department,
                    String position,
                    String address,
                    int remoteWork) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.dateOfBirthday = dateOfBirthday;
        this.departmentId = departmentId;
        this.department = department;
        this.position = position;
        this.address = address;
        this.remoteWork = remoteWork;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDateOfBirthday() {
        return dateOfBirthday;
    }

    public void setDateOfBirthday(String dateOfBirthday) {
        this.dateOfBirthday = dateOfBirthday;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRemoteWork() {
        return remoteWork;
    }

    public void setRemoteWork(int remoteWork) {
        this.remoteWork = remoteWork;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id &&
                departmentId == employee.departmentId &&
                remoteWork == employee.remoteWork &&
                Objects.equals(name, employee.name) &&
                Objects.equals(surname, employee.surname) &&
                Objects.equals(dateOfBirthday, employee.dateOfBirthday) &&
                Objects.equals(department, employee.department) &&
                Objects.equals(position, employee.position) &&
                Objects.equals(address, employee.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, surname, dateOfBirthday, departmentId, department, position, address, remoteWork);
    }

    @Override
    public String toString() {
        return name + " " + surname + ", " + position;
    }

    public static Employee resultSetToEmployee(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(1);
        String name = resultSet.getString(2);
        String surname = resultSet.getString(3);
        String dateOfBirthday = resultSet.getString(4);
        int departmentId = resultSet.getInt(5);
        String department = resultSet.getString(6);
        String position = resultSet.getString(7);
        String address = resultSet.getString(8);
        int remoteWork = resultSet.getInt(9);

        return new Employee(
                id,
                name,
                surname,
                dateOfBirthday,
                departmentId,
                department,
                position,
                address,
                remoteWork
        );
    }
}
