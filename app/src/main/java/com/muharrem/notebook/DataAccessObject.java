package com.muharrem.notebook;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataAccessObject {

    private static final DataAccessObject instance = new DataAccessObject();

    private DataAccessObject() {
    }

    public static DataAccessObject getInstance() {
        return instance;
    }

    public void saveAll() {
        try {
            FileOutputStream fos = MyUtils.appContext.openFileOutput("Notes.db", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(MyUtils.allNotes);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Note> getNotes() {
        List<Note> notes = null;
        try {
            FileInputStream fis = MyUtils.appContext.openFileInput("Notes.db");
            ObjectInputStream is = new ObjectInputStream(fis);
            notes = (List<Note>) is.readObject();
            is.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(notes == null)
            notes = MyUtils.randomNotes(12);
        return notes;
    }
}
