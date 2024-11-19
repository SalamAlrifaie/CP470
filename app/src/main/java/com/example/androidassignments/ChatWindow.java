package com.example.androidassignments;


import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

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
import android.content.res.Configuration;

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
    private Cursor cursor;


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


       /*
Check if FrameLayout exists to determine if using tablet layout
       boolean isTabletLayout = findViewById(R.id.frameLayout) != null;
       if (isTabletLayout) {
           Log.i(TAG, "Tablet layout detected. Screen width is at least 600dp.");
       } else {
           Log.i(TAG, "Phone layout detected. Screen width is less than 600dp.");
       }
*/


        chatView = findViewById(R.id.chatView);
        chatEditText = findViewById(R.id.chatEditText);
        sendButton = findViewById(R.id.sendButton);
        chatMessages = new ArrayList<>();
        //messageAdapter = new ChatAdapter(this, chatMessages);
        chatView.setAdapter(messageAdapter);
        dbHelper = new ChatDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();


        // Query the database for existing chat messages and their IDs
        cursor = db.query(
                ChatDatabaseHelper.TABLE_NAME,    // Table name
                new String[]{ChatDatabaseHelper.KEY_ID, ChatDatabaseHelper.KEY_MESSAGE}, // Columns to retrieve
                null,                             // WHERE clause
                null,                             // WHERE arguments
                null,                             // GROUP BY
                null,                             // HAVING
                null                              // ORDER BY
        );


        // Load messages into the ArrayList from the database
        if (cursor.moveToFirst()) {
            do {
                String message = cursor.getString(cursor.getColumnIndexOrThrow(ChatDatabaseHelper.KEY_MESSAGE));
                chatMessages.add(message);
            } while (cursor.moveToNext());
        }

        // Pass the cursor and chatMessages to the adapter
        messageAdapter = new ChatAdapter(this, chatMessages, cursor);
        chatView.setAdapter(messageAdapter);


        chatView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedMessage = chatMessages.get(position); // Get the message
            long databaseId = id; // Get the database ID from getItemId()

            boolean isLandscape = findViewById(R.id.frameLayout) != null;

            if (isLandscape) {
                // Use newInstance to create the fragment
                MessageFragment messageFragment = MessageFragment.newInstance(selectedMessage, databaseId, this);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, messageFragment)
                        .commit();
            } else {
                // Start MessageDetails activity in portrait mode
                Intent intent = new Intent(ChatWindow.this, MessageDetails.class);
                intent.putExtra("message", selectedMessage);
                intent.putExtra("id", databaseId);
                startActivityForResult(intent, 100);
            }
        });

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null) {
                long idToDelete = data.getLongExtra("idToDelete", -1);


                // Delete the message from the database
                db.delete(ChatDatabaseHelper.TABLE_NAME, ChatDatabaseHelper.KEY_ID + "=?", new String[]{String.valueOf(idToDelete)});


                // Refresh the chatMessages list
                refreshChatMessages();


                // Notify the adapter
                messageAdapter.notifyDataSetChanged();
            }
        }
    }


    private void refreshChatMessages() {
        chatMessages.clear();
        Cursor cursor = db.query(ChatDatabaseHelper.TABLE_NAME, new String[]{ChatDatabaseHelper.KEY_ID, ChatDatabaseHelper.KEY_MESSAGE}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String message = cursor.getString(cursor.getColumnIndexOrThrow(ChatDatabaseHelper.KEY_MESSAGE));
                chatMessages.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }


    public void deleteMessage(long id) {
        // Delete the message from the database
        db.delete(ChatDatabaseHelper.TABLE_NAME, ChatDatabaseHelper.KEY_ID + "=?", new String[]{String.valueOf(id)});


        // Refresh the chatMessages list
        refreshChatMessages();


        // Notify the adapter
        messageAdapter.notifyDataSetChanged();


        // Remove the fragment
        getSupportFragmentManager().beginTransaction()
                .remove(getSupportFragmentManager().findFragmentById(R.id.frameLayout))
                .commit();
    }


    @Override
    protected void onDestroy() {
        // Close the database and the cursor to free resources
        if (db != null && db.isOpen()) {
            db.close();
            Log.i(TAG, "Database closed.");
        }
        if (dbHelper != null) {
            dbHelper.close();
            Log.i(TAG, "Database helper closed.");
        }
        if (cursor != null) {
            cursor.close();
            Log.i(TAG, "Cursor closed.");
        }
        super.onDestroy();
    }


    private class ChatAdapter extends ArrayAdapter<String> {


        private final ArrayList<String> list;
        private final Cursor cursor; // Cursor to hold the query results


        public ChatAdapter(Context ctx, ArrayList<String> chatMessages, Cursor cursor) {
            super(ctx, 0, chatMessages);
            this.list = chatMessages;
            this.cursor = cursor;
        }


        @Override
        public int getCount() {
            return list.size();
        }


        @Override
        public String getItem(int position) {
            return list.get(position);
        }


        @Override
        public long getItemId(int position) {
            // Move the cursor to the given position
            if (cursor != null && cursor.moveToPosition(position)) {
                // Retrieve the ID column value at the current cursor position
                int idIndex = cursor.getColumnIndexOrThrow(ChatDatabaseHelper.KEY_ID);
                return cursor.getLong(idIndex);
            }
            return -1; // Return -1 if the position is invalid or cursor is null
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
            View result;


            if (position % 2 == 0) {
                result = inflater.inflate(R.layout.chat_row_outgoing, null);
            } else {
                result = inflater.inflate(R.layout.chat_row_incoming, null);
            }


            TextView message = result.findViewById(R.id.message_text);
            message.setText(getItem(position));
            return result;
        }
    }
}



