package com.annhve.naukatesttask.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Mark {
    private int id;
    private String title;
    private String transcription;

    public Mark(int id, String title, String transcription) {
        this.id = id;
        this.title = title;
        this.transcription = transcription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mark mark = (Mark) o;
        return id == mark.id &&
                Objects.equals(title, mark.title) &&
                Objects.equals(transcription, mark.transcription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, transcription);
    }

    @Override
    public String toString() {
        return title + " (" + transcription + ")";
    }

    public static Mark resultSetToMark(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(1);
        String title = resultSet.getString(2);
        String transcription = resultSet.getString(3);

        return new Mark(id, title, transcription);
    }
}
