package com.muharrem.notebook;

import java.util.Calendar;

public class NoteAlarm {

    private Note note;
    private Calendar alarmDate;

    public NoteAlarm(Note note, Calendar date) {
        this.note = note;
        this.alarmDate = date;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public Calendar getAlarmDate() {
        return alarmDate;
    }

    public void setAlarmDate(Calendar alarmDate) {
        this.alarmDate = alarmDate;
    }
}
