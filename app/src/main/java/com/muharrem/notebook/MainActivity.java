package com.muharrem.notebook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Note> allNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyUtils.initialize(getApplicationContext());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    public void addNote(View v) {

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
        DataAccessObject dao = DataAccessObject.getInstance();
        allNotes = dao.getNotes();
        Collections.sort(allNotes);

        if(allNotes == null)
            allNotes = MyUtils.randomNotes(8);
        mAdapter = new MyAdapter(this, allNotes, recyclerView);
        recyclerView.setAdapter(mAdapter);

        MyUtils.setAllNotes(allNotes);
    }
}
