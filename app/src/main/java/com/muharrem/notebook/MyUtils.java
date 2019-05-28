package com.muharrem.notebook;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class MyUtils {

    public static String[] colors = {"#ffffff", "#E6E6E6", "#FFE9E9", "#ffd1fb", "#D8E8FF", "#D0FFEE", "#FFFFC7", "#FFEBBA"};
    public static String[] descrp = {"Beyaz", "Gri", "Pembe", "Mor", "Mavi", "Yeşil", "Sarı", "Turuncu"};

    public static List<Note> allNotes;
    public static Context appContext;

    private MyUtils() {
    }

    public static void initialize(Context context) {
        appContext = context;
    }

    public static void setAllNotes(List<Note> allNotes) {
        MyUtils.allNotes = allNotes;
    }

    public static String colorFromDes(String des) {
        int i = 0;
        while (!descrp[i].equals(des))
            i++;
        if (i >= descrp.length)
            return colors[0];
        return colors[i];
    }

    public static int posFromColor(String colorStr) {
        for (int i = 0; i < colors.length; i++)
            if (colorStr.equals(colors[i]))
                return i;
        return -1;
    }

    public static List<Note> randomNotes(int count) {
        List<Note> notes = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int priority = random.nextInt(Note.MAX_PRIORITY) + 1;
            String title = "Title " + i;
            Calendar date = Calendar.getInstance();
            Note note = new Note(i);
            note.setPriority(priority);
            note.setTitle(title);
            note.setDate(date);
            note.setColorStr(colors[random.nextInt(colors.length)]);
            notes.add(note);
        }
        Collections.sort(notes);
        return notes;
    }

    public static int generateId() {
        int max = -1;
        if (allNotes == null || allNotes.isEmpty())
            return 0;
        for (Note n : allNotes) {
            int nId = n.getId();
            if (nId > max)
                max = nId;
        }
        return max + 1;
    }

    public static Note findNoteById(int nId) {
        if (allNotes == null || allNotes.isEmpty())
            return null;
        for (Note n : allNotes)
            if (nId == n.getId())
                return n;
        return null;
    }
}
