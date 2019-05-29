package com.muharrem.notebook;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;

public class Note implements Serializable, Comparable<Note> {

    public static final int MIN_PRIORITY = 1;
    public static final int MAX_PRIORITY = 5;

    private final int id;
    private String title;
    private String note;
    private Calendar date;
    private String address;
    private String colorStr;
    private int priority;
    private String filePath;
    private String fileName;

    public Note(int id) {
        this.id = id;
        title = "";
        note = "";
        date = Calendar.getInstance();
        address = "";
        colorStr = AppUtils.colors[0];
        priority = 3;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getColorStr() {
        return colorStr;
    }

    public void setColorStr(String colorStr) {
        this.colorStr = colorStr;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) throws PriorityException {
        if (priority < MIN_PRIORITY || priority > MAX_PRIORITY)
            throw new PriorityException();
        this.priority = priority;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int compareTo(Note o) {
        return o.date.compareTo(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return id == note.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
