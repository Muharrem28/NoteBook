package com.muharrem.notebook;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class AppUtils {

    public static String[] colors = {"#ffffff", "#E6E6E6", "#FFE9E9", "#ffd1fb", "#D8E8FF", "#D0FFEE", "#FFFFC7", "#FFEBBA"};
    public static String[] descrp = {"Beyaz", "Gri", "Pembe", "Mor", "Mavi", "Yeşil", "Sarı", "Turuncu"};

    public static List<Note> allNotes;
    public static List<NoteAlarm> alarms;
    public static Context appContext;
    public static Note notificationNote;

    private AppUtils() {
    }

    public static void initialize(Context context) {
        appContext = context;
    }

    public static void setAllNotes(List<Note> allNotes) {
        AppUtils.allNotes = allNotes;
    }

    public static void loadAllNotes() {
        allNotes = DataAccessObject.getInstance().getNotes();
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

    public static List<Note> randomNotes() {
        List<String> titles = new ArrayList<>();
        titles.add("Ayakkabı Al");
        titles.add("Mobil Programlama Ödevi");
        titles.add("Sinyal İşleme Sınavı");
        titles.add("Sunuma Hazırlan");
        titles.add("Ağtek Ders Notları");
        titles.add("Gezi Resimleri");
        titles.add("Ara Proje Kitabı");

        String lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        List<Note> notes = new ArrayList<>();
        Random random = new Random();
        int id = 1;
        while (!titles.isEmpty()) {
            int priority = random.nextInt(Note.MAX_PRIORITY) + 1;
            String title = titles.remove(random.nextInt(titles.size()));
            Calendar date = Calendar.getInstance();
            Note note = new Note(id++);
            note.setPriority(priority);
            note.setTitle(title);
            date.add(Calendar.SECOND, random.nextInt(2500000)-3000000);
            note.setDate(date);
            note.setNote(lorem);
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

    public static void openFile(Context context, File file) {
        Uri uri = Uri.fromFile(file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (file.toString().contains(".doc") || file.toString().contains(".docx")) {
            intent.setDataAndType(uri, "application/msword");
        } else if (file.toString().contains(".pdf")) {
            intent.setDataAndType(uri, "application/pdf");
        } else if (file.toString().contains(".ppt") || file.toString().contains(".pptx")) {
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (file.toString().contains(".xls") || file.toString().contains(".xlsx")) {
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (file.toString().contains(".zip") || file.toString().contains(".rar")) {
            intent.setDataAndType(uri, "application/zip");
        } else if (file.toString().contains(".rtf")) {
            intent.setDataAndType(uri, "application/rtf");
        } else if (file.toString().contains(".wav") || file.toString().contains(".mp3")) {
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (file.toString().contains(".gif")) {
            intent.setDataAndType(uri, "image/gif");
        } else if (file.toString().contains(".jpg") || file.toString().contains(".jpeg") || file.toString().contains(".png")) {
            intent.setDataAndType(uri, "image/jpeg");
        } else if (file.toString().contains(".txt")) {
            intent.setDataAndType(uri, "text/plain");
        } else if (file.toString().contains(".3gp") || file.toString().contains(".mpg") || file.toString().contains(".mpeg") || file.toString().contains(".mpe") || file.toString().contains(".mp4") || file.toString().contains(".avi")) {
            intent.setDataAndType(uri, "video/*");
        } else {
            intent.setDataAndType(uri, "*/*");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void addAlarm(Note note, Calendar date) {
        if (alarms == null)
            alarms = new ArrayList<>();
        alarms.add(new NoteAlarm(note, date));
    }

    public static void saveAlarms(Context context) {
        Log.d("+++++", "SavingAlarms");
        if(alarms == null)
            return;
        for (NoteAlarm na : alarms) {
            Log.d("+++++", "Note ID : " + na.getNote().getId());
            //long when = na.getAlarmDate().getTimeInMillis() + 20000;
            long when = System.currentTimeMillis() + 10000;
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("noteId", na.getNote().getId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            am.set(AlarmManager.RTC_WAKEUP, when, pendingIntent);
        }
    }
}
