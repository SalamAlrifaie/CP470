package com.example.androidassignments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidassignments.databinding.ActivityTestToolbarBinding;

public class TestToolbar extends AppCompatActivity {

    private String customMessage;
    // defining an enum for menu items
    private enum MenuItemEnum {
        CHOICE1(R.id.choice1),
        CHOICE2(R.id.choice2),
        CHOICE3(R.id.choice3),
        ABOUT(R.id.about);

        private final int id;

        MenuItemEnum(int id) {
            this.id = id;
        }

        public static MenuItemEnum fromId(int id) {
            for (MenuItemEnum item : values()) {
                if (item.id == id) {
                    return item;
                }
            }
            return null;
        }
    }
    private AppBarConfiguration appBarConfiguration;
    private ActivityTestToolbarBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTestToolbarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        customMessage = getString(R.string.customMsg);


        setSupportActionBar(binding.toolbar);

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_test_toolbar);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.changedMsg), Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_test_toolbar);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {

        int id = mi.getItemId();
        MenuItemEnum menuItem = MenuItemEnum.fromId(id);

        // switch case with enums
        switch (menuItem) {
            case CHOICE1:
                Log.d("Toolbar", "Choice 1 selected");
                //default message
                Snackbar.make(findViewById(R.id.nav_host_fragment_content_test_toolbar), customMessage, Snackbar.LENGTH_SHORT).show();
                return true;
            case CHOICE2:
                Log.d("Toolbar", "Choice 2 selected");

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.dialog_question)
                        .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            case CHOICE3:
                Log.d("Toolbar", "Choice 3 selected");
                // inflate custom layout
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);

                // creating the dialog using the custom layout
                AlertDialog.Builder customDialogBuilder = new AlertDialog.Builder(this);
                customDialogBuilder.setView(dialogView)
                        .setTitle(R.string.customDialogPrompt)
                        .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                EditText messageInput = dialogView.findViewById(R.id.edittext_message);
                                String newMessage = messageInput.getText().toString();

                                if (!newMessage.isEmpty()) {
                                    customMessage = newMessage; // Update customMessage with the user's input
                                    Snackbar.make(findViewById(R.id.nav_host_fragment_content_test_toolbar), getString(R.string.msgConfirm), Snackbar.LENGTH_SHORT).show();
                                } else {
                                    // if the user did not enter a message, show a toast warning
                                    Toast.makeText(getApplicationContext(), getString(R.string.noMsg), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog customDialog = customDialogBuilder.create();
                customDialog.show();
                return true;

            case ABOUT:
                Toast.makeText(this, getString(R.string.version), Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(mi);
        }
    }
}