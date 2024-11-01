package com.example.androidassignments;

import android.content.ContentValues;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// my imports
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class ChatWindow extends AppCompatActivity {

    // Class variables
    private static final String TAG = "ChatWindow";
    private ListView chatView;
    private EditText chatEditText;
    private Button sendButton;
    private ArrayList<String> chatMessages;
    private ChatAdapter messageAdapter;
    private ChatDatabaseHelper dbHelper;
    private SQLiteDatabase db;

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
        Log.i(TAG, "onCreate called in ChatWindow");


        chatView = findViewById(R.id.chatView);
        chatEditText = findViewById(R.id.chatEditText);
        sendButton = findViewById(R.id.sendButton);
        chatMessages = new ArrayList<>();
        messageAdapter = new ChatAdapter(this, chatMessages);
        chatView.setAdapter(messageAdapter);

        dbHelper = new ChatDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Query the database for existing chat messages
        Cursor cursor = null;
        try {
            // Using the query() method to build the query
            cursor = db.query(
                    ChatDatabaseHelper.TABLE_NAME,   // The table to query
                    null,                             // The array of columns to return (null for all)
                    null,                             // The columns for the WHERE clause
                    null,                             // The values for the WHERE clause
                    null,                             // Group the rows
                    null,                             // Filter by row groups
                    null                              // The sort order
            );

            // Log the cursor's column count
            Log.i(TAG, "Cursor’s column count = " + cursor.getColumnCount());

            // Loop through each column and log its name
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                Log.i(TAG, "Column " + i + ": " + cursor.getColumnName(i));
            }

            // Move the cursor to the first row
            if (cursor.moveToFirst()) {
                // Iterate through all rows in the cursor
                while (!cursor.isAfterLast()) {
                    // Retrieve the message from the current row
                    String message = cursor.getString(cursor.getColumnIndexOrThrow(ChatDatabaseHelper.KEY_MESSAGE));

                    // Add the message to ArrayList
                    chatMessages.add(message);

                    // Log the retrieved message
                    Log.i(TAG, "SQL MESSAGE: " + message);

                    // Move to the next row
                    cursor.moveToNext();
                }
            }

            messageAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.e(TAG, "Error reading from database: " + e.getMessage());
        } finally {
            // Always close the cursor to free up resources
            if (cursor != null) {
                cursor.close();
            }
        }

        sendButton.setOnClickListener(v -> {
            String newMessage = chatEditText.getText().toString().trim();
            if (!newMessage.isEmpty()) {
                // Insert the new message into the database
                ContentValues values = new ContentValues();
                values.put(ChatDatabaseHelper.KEY_MESSAGE, newMessage);
                long newRowId = db.insert(ChatDatabaseHelper.TABLE_NAME, null, values);

                if (newRowId != -1) {
                    // Add the new message to the ArrayList and notify the adapter
                    chatMessages.add(newMessage);
                    messageAdapter.notifyDataSetChanged();

                    // Clear the input field
                    chatEditText.setText("");

                    Log.i(TAG, "Inserted new message with ID: " + newRowId);
                } else {
                    Log.e(TAG, "Error inserting new message.");
                }
            } else {
                Log.i(TAG, "Empty message not sent.");
            }
        });
    }

    @Override
    protected void onDestroy() {
        // Close the database and the helper to free resources before calling super.onDestroy()
        if (db != null && db.isOpen()) {
            db.close();
            Log.i(TAG, "Database closed.");
        }
        if (dbHelper != null) {
            dbHelper.close();
            Log.i(TAG, "Database helper closed.");
        }
        super.onDestroy();
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