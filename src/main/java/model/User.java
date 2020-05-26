package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class User {
    private int id;
    private String login;
    private String password;
    private int idRole;

    public User(int id, String login, String password, int idRole) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.idRole = idRole;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIdRole() {
        return idRole;
    }

    public void setIdRole(int idRole) {
        this.idRole = idRole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                idRole == user.idRole &&
                Objects.equals(login, user.login) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, password, idRole);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", idRole=" + idRole +
                '}';
    }

    public static User resultSetToUser(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(1);
        String login = resultSet.getString(2);
        String password = resultSet.getString(3);
        int id_role = resultSet.getInt(4);

        return new User(
                id,
                login,
                password,
                id_role
        );
    }
}