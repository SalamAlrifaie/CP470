package com.example.androidassignments;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    protected static final int REQUEST_CODE_LIST_ITEMS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // uncomment the following line for debugging (toast msg)
        //print(getString(R.string.MainActivityStart));

        Log.i(TAG, "onCreate called in MainActivity");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button button = findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListItemsActivity.class);

                startActivityForResult(intent, REQUEST_CODE_LIST_ITEMS);
            }
        });
        findViewById(R.id.chatButton).setOnClickListener(v -> {
            Log.i(TAG, "User clicked Start Chat");


            // Create an intent to open the ChatWindow activity
            Intent intent = new Intent(MainActivity.this, ChatWindow.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle the "Up" button click event
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();  // Navigate to the parent activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void print(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume called in MainActivity");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart called in MainActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause called in MainActivity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop called in MainActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called in MainActivity");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState called in MainActivity");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestore called in MainActivity");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LIST_ITEMS) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Returned to MainActivity.onActivityResult");

                if (data != null) {
                    String messagePassed = data.getStringExtra("Response");

                    if (messagePassed != null) {
                        String toastMessage = getString(R.string.response) + ": " + messagePassed;
                        Toast toast = Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        String toastMessage = getString(R.string.response) + ": " + getString(R.string.responseActual);
                        Toast toast = Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        }
    }
}