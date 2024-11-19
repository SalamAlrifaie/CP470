package com.example.androidassignments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.widget.Button;
import android.widget.TextView;


public class MessageFragment extends Fragment {
    private ChatWindow chatWindow; // Reference to ChatWindow for tablets

    public MessageFragment() {
        // Required empty public constructor
    }

    public static MessageFragment newInstance(String message, long id, @Nullable ChatWindow chatWindow) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putLong("id", id);
        fragment.setArguments(args);
        fragment.chatWindow = chatWindow; // Set the ChatWindow reference directly
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_details, container, false);


        // Retrieve arguments
        Bundle args = getArguments();
        if (args != null) {
            String message = args.getString("message");
            long id = args.getLong("id");


            // Set the message and ID in the TextViews
            TextView messageTextView = view.findViewById(R.id.messageTextView);
            TextView idTextView = view.findViewById(R.id.idTextView);
            messageTextView.setText(message);
            idTextView.setText("ID: " + id);


            // Set up the Delete button
            Button deleteButton = view.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(v -> {
                if (chatWindow != null) {
                    // Tablet: Use ChatWindow reference to delete the message and remove the fragment
                    chatWindow.deleteMessage(id);
                } else {
                    // Phone: Pass result back to the activity and finish it
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("idToDelete", id);
                    getActivity().setResult(AppCompatActivity.RESULT_OK, resultIntent);
                    getActivity().finish();
                }
            });
        }


        return view;
    }
}