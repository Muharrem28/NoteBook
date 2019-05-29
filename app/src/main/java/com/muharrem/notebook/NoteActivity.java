package com.muharrem.notebook;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NoteActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int FILE_SELECT_CODE = 0;

    private Note note;
    private Spinner spinner;
    private EditText editTitle, editNote;
    private TextView textFile, textAlarm;
    private LinearLayout layoutNoteColor;
    private DataAccessObject dao;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Calendar alarmCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Bundle b = getIntent().getExtras();
        int noteId = -1;
        if (b != null)
            noteId = b.getInt("noteId", -1);
        if (noteId == -1) {
            note = new Note(AppUtils.generateId());
        } else {
            note = AppUtils.findNoteById(noteId);
        }
        dao = DataAccessObject.getInstance();

        editTitle = findViewById(R.id.editTitle);
        editNote = findViewById(R.id.editNote);
        spinner = findViewById(R.id.spinnerColor);
        textFile = findViewById(R.id.textFile);
        textAlarm = findViewById(R.id.textAlarm);
        layoutNoteColor = findViewById(R.id.layoutNoteColor);

        editTitle.setText(note.getTitle());
        editNote.setText(note.getNote());
        if (note.getFileName() != null) {
            Log.d("+++++", note.getFileName());
            textFile.setText(note.getFileName());
        }

        spinner.setOnItemSelectedListener(this);
        List<String> colorList = new ArrayList<>();
        for (String d : AppUtils.descrp)
            colorList.add(d);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, colorList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setSelection(AppUtils.posFromColor(note.getColorStr()));

        alarmCalendar = Calendar.getInstance();
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                alarmCalendar.set(Calendar.YEAR, year);
                alarmCalendar.set(Calendar.MONTH, monthOfYear);
                alarmCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                textAlarm.setText(new SimpleDateFormat("dd MMM HH:mm").format(alarmCalendar.getTime()));
                AppUtils.addAlarm(note, alarmCalendar);
            }

        };
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        String colorStr = AppUtils.colorFromDes(item);
        spinner.setBackgroundColor(Color.parseColor(colorStr));
        layoutNoteColor.setBackgroundColor(Color.parseColor(colorStr));
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    public void attach(View view) {
        selectFile();
    }

    public void save(View view) {
        String item = (String) spinner.getSelectedItem();
        String colorStr = AppUtils.colorFromDes(item);
        note.setColorStr(colorStr);
        note.setTitle(editTitle.getText().toString());
        note.setNote(editNote.getText().toString());
        note.setDate(Calendar.getInstance());
        dao.saveAll();
        onBackPressed();
    }

    public void delete(View view) {
        AppUtils.allNotes.remove(note);
        dao.saveAll();
        onBackPressed();
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a file to attach"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String path = uri.getPath();
                    String filename = path.substring(path.lastIndexOf("/") + 1);
                    Log.d("+++++", "File Uri: " + uri.toString());
                    Log.d("+++++", "File Path: " + path);
                    Log.d("+++++", "File Name: " + filename);
                    note.setFilePath(path);
                    note.setFileName(filename);
                    textFile.setText(filename);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public void openFile(View view) {
        File file = new File(note.getFilePath());
        AppUtils.openFile(this, file);
    }

    public void setAlarm(View view) {
        new DatePickerDialog(this, dateSetListener, alarmCalendar
                .get(Calendar.YEAR), alarmCalendar.get(Calendar.MONTH),
                alarmCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppUtils.saveAlarms(this);
    }
}