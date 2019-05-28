package com.muharrem.notebook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<Note> noteList;
    private LayoutInflater inflater;
    private View.OnClickListener mOnClickListener;

    public MyAdapter(final Context context, List<Note> notes, final RecyclerView recyclerView) {
        inflater = LayoutInflater.from(context);
        this.noteList = notes;
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemPosition = recyclerView.getChildLayoutPosition(v);
                Note note = noteList.get(itemPosition);
                Intent i = new Intent(context, NoteActivity.class);
                i.putExtra("noteId", note.getId());
                context.startActivity(i);
            }
        };
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.note_item, parent, false);
        view.setOnClickListener(mOnClickListener);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setData(noteList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView priority, title, date;
        LinearLayout layout;

        public MyViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.noteDate);
            priority = itemView.findViewById(R.id.notePriority);
            title = itemView.findViewById(R.id.noteTitle);
            layout = itemView.findViewById(R.id.noteLayout);
        }

        public void setData(Note note, int position) {
            String dateStr = new SimpleDateFormat("dd MMM HH:mm").format(note.getDate().getTime());
            date.setText("Last edited "+dateStr);
            priority.setText("" + note.getPriority());
            title.setText(note.getTitle());
            int color = Color.parseColor(note.getColorStr());
            layout.setBackgroundColor(color);
        }

        @Override
        public void onClick(View v) {
        }
    }
}