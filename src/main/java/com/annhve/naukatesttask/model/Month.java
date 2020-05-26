package com.annhve.naukatesttask.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Month {
    private String month;

    public Month(String month) {
        this.month = month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getMonth() {
        return month;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Month month1 = (Month) o;
        return Objects.equals(month, month1.month);
    }

    @Override
    public int hashCode() {
        return Objects.hash(month);
    }

    public static Month resultSetToMonth(ResultSet resultSet) throws SQLException {
        String name = resultSet.getString(1);

        return new Month(name);
    }
}
