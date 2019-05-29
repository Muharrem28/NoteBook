package com.muharrem.notebook;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Note> allNotes;
    private List<Note> screenNotes;
    private EditText editSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppUtils.initialize(getApplicationContext());
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        screenNotes = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        editSearch = findViewById(R.id.editSearch);

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                screenNotes.clear();
                for(Note note : allNotes) {
                    if(compareNoteToString(s, note))
                        screenNotes.add(note);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public void addNote(View v) {
        Note note = new Note(-1);
        Intent i = new Intent(this, NoteActivity.class);
        i.putExtra("noteId", note.getId());
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DataAccessObject dao = DataAccessObject.getInstance();
        dao.saveAll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.loadAllNotes();
        allNotes = AppUtils.allNotes;
        Collections.sort(allNotes);

        if(allNotes == null)
            allNotes = AppUtils.randomNotes();

        screenNotes.clear();
        screenNotes.addAll(allNotes);
        mAdapter = new MyAdapter(this, screenNotes, recyclerView);
        recyclerView.setAdapter(mAdapter);

        AppUtils.setAllNotes(allNotes);
    }

    private boolean compareNoteToString(CharSequence cs, Note note) {
        String str = cs.toString().toLowerCase();
        if(note.getFileName() != null && note.getFileName().toLowerCase().startsWith(str))
            return true;
        if(note.getColorStr().toLowerCase().startsWith(str))
            return true;
        if(note.getTitle().toLowerCase().contains(str))
            return true;
        if(note.getDate().toString().contains(str))
            return true;
        return false;
    }
}
