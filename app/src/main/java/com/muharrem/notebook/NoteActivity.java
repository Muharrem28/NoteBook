package com.muharrem.notebook;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NoteActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int FILE_SELECT_CODE = 0;

    private Note note;
    private Spinner spinner;
    private EditText editTitle;
    private EditText editNote;
    private TextView textFile;
    private LinearLayout layoutNoteColor;
    private DataAccessObject dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Bundle b = getIntent().getExtras();
        int noteId = -1;
        if (b != null)
            noteId = b.getInt("noteId", -1);
        if (noteId == -1) {
            note = new Note(MyUtils.generateId());
        } else {
            note = MyUtils.findNoteById(noteId);
        }
        dao = DataAccessObject.getInstance();

        editTitle = findViewById(R.id.editTitle);
        editNote = findViewById(R.id.editNote);
        spinner = findViewById(R.id.spinnerColor);
        textFile = findViewById(R.id.textFile);
        layoutNoteColor = findViewById(R.id.layoutNoteColor);

        editTitle.setText(note.getTitle());
        editNote.setText(note.getNote());
        if(note.getFileName() != null) {
            Log.d("+++++", note.getFileName());
            textFile.setText(note.getFileName());
        }

        spinner.setOnItemSelectedListener(this);
        List<String> colorList = new ArrayList<>();
        for (String d : MyUtils.descrp)
            colorList.add(d);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, colorList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setSelection(MyUtils.posFromColor(note.getColorStr()));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        String colorStr = MyUtils.colorFromDes(item);
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
        String colorStr = MyUtils.colorFromDes(item);
        note.setColorStr(colorStr);
        note.setTitle(editTitle.getText().toString());
        note.setNote(editNote.getText().toString());
        note.setDate(Calendar.getInstance());
        dao.saveAll();
        onBackPressed();
    }

    public void delete(View view) {
        MyUtils.allNotes.remove(note);
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
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String fileName = note.getFileName();
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        String mimeType = myMime.getMimeTypeFromExtension(extension);
        newIntent.setDataAndType(Uri.fromFile(new File(note.getFilePath())), mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Bu uzantıyı açacak program bulunamadı.", Toast.LENGTH_LONG).show();
        }
    }
}
