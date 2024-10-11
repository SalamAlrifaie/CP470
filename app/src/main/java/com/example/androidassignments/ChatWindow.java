package com.example.androidassignments;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// my imports
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class ChatWindow extends AppCompatActivity {

    // Class variables
    private ListView chatView;
    private EditText chatEditText;
    private Button sendButton;
    private ArrayList<String> chatMessages;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_window);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        chatView = findViewById(R.id.chatView);
        chatEditText = findViewById(R.id.chatEditText);
        sendButton = findViewById(R.id.sendButton);

        chatMessages = new ArrayList<>();

        ChatAdapter messageAdapter = new ChatAdapter(this, chatMessages);
        chatView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = chatEditText.getText().toString().trim();

                if (!message.isEmpty()) {
                    chatMessages.add(message);

                    messageAdapter.notifyDataSetChanged();

                    chatEditText.setText("");
                }
            }
        });
    }

    private class ChatAdapter extends ArrayAdapter<String> {

        private final ArrayList<String> list;

        public ChatAdapter(Context ctx, ArrayList<String> chatMessages) {
            super(ctx, 0, chatMessages);
            this.list = chatMessages;
        }

        // Get the number of messages in the list
        @Override
        public int getCount() {
            return list.size();
        }

        // Get the message at a specific position
        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        // Get the view for each row (message) in the list
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Create a LayoutInflater instance
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();

            View result = null;

            // Check if the current position is even or odd and inflate the correct layout
            if (position % 2 == 0) {
                // Inflate incoming message layout for even positions
                result = inflater.inflate(R.layout.chat_row_outgoing, null);
            } else {
                // Inflate outgoing message layout for odd positions
                result = inflater.inflate(R.layout.chat_row_incoming, null);
            }

            // Get the TextView from the inflated layout to set the message text
            TextView message = (TextView) result.findViewById(R.id.message_text);

            // Set the message text at the current position in the ListView
            message.setText(getItem(position));  // getItem returns the string message

            // Return the final view for this row
            return result;
        }
    }
}