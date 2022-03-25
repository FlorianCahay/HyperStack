package fr.uge.hyperstack.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import fr.uge.hyperstack.Logs;
import fr.uge.hyperstack.LogsViewAdapter;
import fr.uge.hyperstack.R;
import fr.uge.hyperstack.model.Stack;

public class LogsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LogsViewAdapter adapter;

    private void updateLayoutManager(View v) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        Intent intent = getIntent();
        recyclerView = findViewById(R.id.logsRecycleView);
        adapter = new LogsViewAdapter();
        ArrayList<Logs> logs = (ArrayList<Logs>) intent.getSerializableExtra("logs");
        recyclerView.setAdapter(adapter);

        adapter.setLogs(logs);
        updateLayoutManager(null);
    }
}