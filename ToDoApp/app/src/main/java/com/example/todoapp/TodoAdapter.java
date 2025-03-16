package com.example.todoapp;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {
    private List<TodoItem> todoList;
    private Context context;
    private TextToSpeech textToSpeech;

    public TodoAdapter(List<TodoItem> todoList, Context context, TextToSpeech textToSpeech) {
        this.todoList = todoList;
        this.context = context;
        this.textToSpeech = textToSpeech;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TodoItem item = todoList.get(position);
        holder.title.setText(item.getTitle());
        holder.checkBox.setChecked(item.isCompleted());

        // ✅ Fix: Speak only when task is completed and TTS is initialized
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setCompleted(isChecked);
            if (isChecked) {
                String message = "You have completed " + item.getTitle();
                Log.d("TTS", "Speaking: " + message);
                textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        // ✅ Delete Individual Task
        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                todoList.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, todoList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        CheckBox checkBox;
        ImageButton btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.todoTitle);
            checkBox = itemView.findViewById(R.id.todoCheckBox);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
