package com.example.androidassignments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Switch;
import android.widget.Toast;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.widget.ImageButton;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import android.content.DialogInterface;
import android.widget.CheckBox;
import androidx.appcompat.app.AlertDialog;

public class ListItemsActivity extends AppCompatActivity {
    private static final String TAG = "ListItemsActivity";
    private ImageButton imageButton;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private Switch mySwitch;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_items);

        // uncomment the following line for debugging (toast msg)
        // print(getString(R.string.ListItemsActivityStart));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkBox = findViewById(R.id.checkBox1);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showFinishDialog();
        });
        Log.i(TAG, "onCreate called in ListItemsActivity");
        mySwitch = findViewById(R.id.switch1);
        imageButton = findViewById(R.id.imageButton1);

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");

                        imageButton.setImageBitmap(imageBitmap);
                    }
                });

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission granted, open the camera
                        openCamera();
                    } else {
                        // Permission denied, show a message
                        Toast.makeText(this, getString(R.string.cameraPermission), Toast.LENGTH_SHORT).show();
                    }
                });

        imageButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });
        mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CharSequence text;
            int duration;

            if (isChecked) {
                text = getString(R.string.switchOn);
                duration = Toast.LENGTH_SHORT;
            } else {
                text = getString(R.string.switchOff);
                duration = Toast.LENGTH_LONG;
            }

            Toast toast = Toast.makeText(ListItemsActivity.this, text, duration);
            toast.show();
        });
    }

    public void print(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(this, getString(R.string.noCamera), Toast.LENGTH_SHORT).show();
        }
    }
    private void showFinishDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListItemsActivity.this);
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(getString(R.string.response), getString(R.string.responseActual));
                        setResult(Activity.RESULT_OK, resultIntent); // Set the result
                        finish(); // End the activity
                        Log.i(TAG, "onFinish called in ListItemsActivity");
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume called in ListItemsActivity");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart called in ListItemsActivity");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause called in ListItemsActivity");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop called in ListItemsActivity");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called in ListItemsActivity");
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState called in ListItemsActivity");
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState called in ListItemsActivity");
    }
}