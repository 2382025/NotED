package com.example.noted;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private Context context;
    private List<HashMap<String, String>> noteList;

    public NoteAdapter(Context context, List<HashMap<String, String>> noteList) {
        this.context = context;
        this.noteList = noteList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HashMap<String, String> note = noteList.get(position);
        holder.titleTextView.setText(note.get(konfigurasi.TAG_TITLE));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NoteDetail.class);
            intent.putExtra(konfigurasi.NOTE_ID, note.get(konfigurasi.TAG_ID)); // Kirim id note ke NoteDetail
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
        }
    }
}
