package com.example.androidassignments;

import android.os.Bundle;

import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import android.content.Intent;
import android.widget.Button;

public class MessageDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_message_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the message and ID from the Intent
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        long id = intent.getLongExtra("id", -1);

        // Create a new instance of MessageFragment with the required data
        if (savedInstanceState == null) {
            MessageFragment messageFragment = MessageFragment.newInstance(message, id, null); // Pass null for phones

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, messageFragment)
                    .commit();
        }
    }
}