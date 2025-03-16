package com.example.todoapp;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TodoAdapter todoAdapter;
    private List<TodoItem> todoList;
    private EditText inputTask;
    private ImageButton btnAdd, btnDeleteCompleted;
    private TextToSpeech textToSpeech;
    private boolean isTtsInitialized = false; // ✅ Track if TTS is ready

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ Fix: Initialize TTS and wait for success
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported!");
                    Toast.makeText(this, "TTS Language Not Supported!", Toast.LENGTH_SHORT).show();
                } else {
                    isTtsInitialized = true; // ✅ Now we can use speak()
                    Log.d("TTS", "Text-to-Speech is READY!");
                }
            } else {
                Log.e("TTS", "TTS Initialization failed!");
                Toast.makeText(this, "TTS Initialization Failed!", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        inputTask = findViewById(R.id.inputTask);
        btnAdd = findViewById(R.id.btnAdd);
        btnDeleteCompleted = findViewById(R.id.btnDeleteCompleted);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        todoList = new ArrayList<>();
        todoList.add(new TodoItem("DSA", false));
        todoList.add(new TodoItem("SQL", false));
        todoList.add(new TodoItem("MAD (Mobile App Development)", false));

        todoAdapter = new TodoAdapter(todoList, this, textToSpeech);
        recyclerView.setAdapter(todoAdapter);

        // ✅ Add Task
        btnAdd.setOnClickListener(v -> {
            String taskText = inputTask.getText().toString().trim();
            if (!taskText.isEmpty()) {
                todoList.add(new TodoItem(taskText, false));
                todoAdapter.notifyItemInserted(todoList.size() - 1);
                inputTask.setText("");
                recyclerView.scrollToPosition(todoList.size() - 1);
            }
        });

        // ✅ Delete Completed Tasks
        btnDeleteCompleted.setOnClickListener(v -> {
            Iterator<TodoItem> iterator = todoList.iterator();
            while (iterator.hasNext()) {
                TodoItem item = iterator.next();
                if (item.isCompleted()) {
                    iterator.remove();
                }
            }
            todoAdapter.notifyDataSetChanged();
        });
    }

    // ✅ Fix: Only Speak if TTS is Ready
    public void speakText(String message) {
        if (isTtsInitialized) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            Log.e("TTS", "TTS Not Ready Yet!");
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
