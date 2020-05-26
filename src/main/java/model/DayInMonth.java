package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class DayInMonth {
    private int dayId;
    private String day;
    private String commonTitle;
    private int markId;
    private String mark;
    private String transcription;

    public DayInMonth(int dayId, String day, String commonTitle, int markId, String mark, String transcription) {
        this.dayId = dayId;
        this.day = day;
        this.commonTitle = commonTitle;
        this.markId = markId;
        this.mark = mark;
        this.transcription = transcription;
    }

    public int getDayId() {
        return dayId;
    }

    public void setDayId(int dayId) {
        this.dayId = dayId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getCommonTitle() {
        return commonTitle;
    }

    public void setCommonTitle(String commonTitle) {
        this.commonTitle = commonTitle;
    }

    public int getMarkId() {
        return markId;
    }

    public void setMarkId(int markId) {
        this.markId = markId;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
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
        DayInMonth that = (DayInMonth) o;
        return dayId == that.dayId &&
                markId == that.markId &&
                Objects.equals(day, that.day) &&
                Objects.equals(commonTitle, that.commonTitle) &&
                Objects.equals(mark, that.mark) &&
                Objects.equals(transcription, that.transcription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayId, day, commonTitle, markId, mark, transcription);
    }

    @Override
    public String toString() {
        return "DayInMonth{" +
                "dayId=" + dayId +
                ", day='" + day + '\'' +
                ", commonTitle='" + commonTitle + '\'' +
                ", markId=" + markId +
                ", mark='" + mark + '\'' +
                ", transcription='" + transcription + '\'' +
                '}';
    }

    public static DayInMonth resultSetToDayInMonth(ResultSet resultSet) throws SQLException {
        int dayId = resultSet.getInt(1);
        String day = resultSet.getString(2);
        String dayType = resultSet.getString(3);
        int markId = resultSet.getInt(4);
        String markTitle = resultSet.getString(5);
        String martkDescription = resultSet.getString(6);

        return new DayInMonth(
                dayId,
                day,
                dayType,
                markId,
                markTitle,
                martkDescription
        );
    }
}
