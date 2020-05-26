package com.annhve.naukatesttask.util;

import com.annhve.naukatesttask.model.*;

import java.sql.*;
import java.util.ArrayList;

public class DbConnector {
    public ArrayList<Employee> selectAllDevelopers() throws Exception {
        Connection connection = connect();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from all_employees where id_department = 2");

        ArrayList<Employee> employees = new ArrayList<>();
        while (resultSet.next()) {
            employees.add(Employee.resultSetToEmployee(resultSet));
        }
        resultSet.close();

        disconnect(connection);
        return employees;
    }

    public ArrayList<Employee> selectAllHR() throws Exception {
        Connection connection = connect();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from all_employees where id_department = 3");

        ArrayList<Employee> employees = new ArrayList<>();
        while (resultSet.next()) {
            employees.add(Employee.resultSetToEmployee(resultSet));
        }
        resultSet.close();

        disconnect(connection);
        return employees;
    }

    public ArrayList<Employee> selectAllQAs() throws Exception {
        Connection connection = connect();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from all_employees where id_department = 21");

        ArrayList<Employee> employees = new ArrayList<>();
        while (resultSet.next()) {
            employees.add(Employee.resultSetToEmployee(resultSet));
        }
        resultSet.close();

        disconnect(connection);
        return employees;
    }

    public ArrayList<Employee> selectAllEmployees() throws Exception {
        Connection connection = connect();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from all_employees");

        ArrayList<Employee> employees = new ArrayList<>();
        while (resultSet.next()) {
            employees.add(Employee.resultSetToEmployee(resultSet));
        }
        resultSet.close();

        disconnect(connection);
        return employees;
    }

    public int deleteEmployee(int id) throws Exception {
        Connection connection = connect();
        Statement statement = connection.createStatement();
        int result = statement.executeUpdate("delete from employees where id = " + id);

        disconnect(connection);
        return result;
    }

    public int insertEmployee(
            String name,
            String surname,
            Date birthday,
            int departmentId,
            String position,
            String address,
            boolean remoteWork
    ) throws Exception {
        Connection connection = connect();
        PreparedStatement statement = connection.prepareStatement(
                "insert into employees(name, surname, date_of_birthday, id_department, position, address, remote_work) " +
                "values (?, ?, ?, ?, ?, ?, ?)"
        );
        statement.setString(1, name);
        statement.setString(2, surname);
        statement.setDate(3, birthday);
        statement.setInt(4, departmentId);
        statement.setString(5, position);
        statement.setString(6, address);
        statement.setInt(7, remoteWork ? 1 : 0);

        int result = statement.executeUpdate();

        disconnect(connection);
        return result;
    }

    public int updateEmployee(
            int id,
            String name,
            String surname,
            Date birthday,
            int departmentId,
            String position,
            String address,
            boolean remoteWork
    ) throws Exception {
        Connection connection = connect();
        PreparedStatement statement = connection.prepareStatement(
                "update employees set name = ?, surname = ?, date_of_birthday = ?, id_department = ?, position = ?, " +
                        "address = ?, remote_work = ? where id = " + id
        );
        statement.setString(1, name);
        statement.setString(2, surname);
        statement.setDate(3, birthday);
        statement.setInt(4, departmentId);
        statement.setString(5, position);
        statement.setString(6, address);
        statement.setInt(7, remoteWork ? 1 : 0);

        int result = statement.executeUpdate();

        disconnect(connection);
        return result;
    }

    public ArrayList<Department> selectAllDepartments() throws Exception {
        Connection connection = connect();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from departments");

        ArrayList<Department> departments = new ArrayList<>();
        while (resultSet.next()) {
            departments.add(Department.resultSetToDepartment(resultSet));
        }
        resultSet.close();

        disconnect(connection);
        return departments;
    }

    public int deleteDepartment(int id) throws Exception {
        Connection connection = connect();
        Statement statement = connection.createStatement();
        int result = statement.executeUpdate("delete from departments where id = " + id);

        disconnect(connection);
        return result;
    }

    public int insertDepartment(String name) throws Exception {
        Connection connection = connect();
        Statement statement = connection.createStatement();
        int result = statement.executeUpdate("insert into departments(name) values ('" + name + "')");

        disconnect(connection);
        return result;
    }

    public int updateDepartment(int id, String name) throws Exception {
        Connection connection = connect();
        Statement statement = connection.createStatement();
        int result = statement.executeUpdate("update departments set name = '" + name + "' where id = " + id);

        disconnect(connection);
        return result;
    }

    public ArrayList<Month> selectMonths() throws Exception {
        Connection connection = connect();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("select * from get_months");
        ArrayList<Month> months = new ArrayList<>();
        while (rs.next()) {
            months.add(Month.resultSetToMonth(rs));
        }
        rs.close();

        disconnect(connection);
        return months;
    }

    public ArrayList<Mark> selectMarks() throws Exception {
        Connection connection = connect();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("select * from marks");
        ArrayList<Mark> marks = new ArrayList<>();
        while (rs.next()) {
            marks.add(Mark.resultSetToMark(rs));
        }
        rs.close();

        disconnect(connection);
        return marks;
    }

    public int insertEmployeeAndCalendarDay(int employeeId, int markId, int workCalendarId) throws Exception {
        Connection connection = connect();
        Statement statement = connection.createStatement();
        int result = statement.executeUpdate(
                "insert into employees_and_calendar_days(employee_id, mark_id, work_calendar_id) values (" +
                        employeeId + ", " + markId + ", " + workCalendarId + ")");

        disconnect(connection);
        return result;
    }

    public int updateEmployeeAndCalendarDay(int employeeId, int markId, int workCalendarId) throws Exception {
        Connection connection = connect();
        Statement statement = connection.createStatement();
        int result = statement.executeUpdate(
                "update employees_and_calendar_days set mark_id = " + markId
                        + " where employee_id = " + employeeId + " and work_calendar_id = " + workCalendarId);

        disconnect(connection);
        return result;
    }

    public int selectFullWorkDays(String month, int employeeId) throws Exception {
        Connection connection = connect();
        CallableStatement statement = connection.prepareCall("{call count_of_workdays(?, ?, ?)}");
        statement.setString(1, month);
        statement.setInt(2, employeeId);
        statement.registerOutParameter(3, Types.NUMERIC);

        statement.execute();

        int result = statement.getInt(3);

        disconnect(connection);

        return result;
    }

    public ArrayList<DayInMonth> getDaysInMonth(String month, int employeeId) throws Exception {
        Connection connection = connect();
        CallableStatement statement = connection.prepareCall("{call workdays_in_month(?, ?, ?)}");
        statement.setString(1, month);
        statement.setInt(2, employeeId);
        statement.registerOutParameter(3, Types.REF_CURSOR);

        statement.execute();

        ResultSet rs = statement.getObject(3, ResultSet.class);
        ArrayList<DayInMonth> daysInMonth = new ArrayList<>();
        while (rs.next()) {
            DayInMonth dayInMonth = DayInMonth.resultSetToDayInMonth(rs);
            daysInMonth.add(dayInMonth);
        }
        rs.close();

        disconnect(connection);
        return daysInMonth;
    }

    public User getUser(String login, String password) throws Exception {
        Connection connection = connect();
        CallableStatement statement = connection.prepareCall("{call user_security.valid_user(?, ?, ?)}");
        statement.setString(1, login);
        statement.setString(2, password);
        statement.registerOutParameter(3, Types.NUMERIC);

        statement.execute();

        int id = statement.getInt(3);

        if (id <= 0) {
            disconnect(connection);
            return null;
        }

        User user = null;
        Statement userStatement = connection.createStatement();
        ResultSet rs = userStatement.executeQuery("select * from users where id = " + id);
        if (rs.next()) {
            user = User.resultSetToUser(rs);
        }

        disconnect(connection);
        return user;
    }

    private Connection connect() throws ClassNotFoundException, SQLException {
        Class.forName("oracle.jdbc.OracleDriver");
        return DriverManager.getConnection(
                "jdbc:oracle:thin:@192.168.56.101:1521:XE",
                "c##anna",
                "faithhope05"
        );
    }

    public void disconnect(Connection connection) throws SQLException {
        connection.close();
    }
}
