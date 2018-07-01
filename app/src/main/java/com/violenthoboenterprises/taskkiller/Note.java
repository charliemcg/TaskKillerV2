package com.violenthoboenterprises.taskkiller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Note extends MainActivity {

    TextView noteTextView;
    EditText noteEditText;
    InputMethodManager keyboard;
    Button editBtn;
    ArrayList<String> noteContent;
    String TAG;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_layout);
        MainActivity.oSharedPreferences = getPreferences(MODE_PRIVATE);

        noteTextView = findViewById(R.id.noteTextView);
        noteEditText = findViewById(R.id.noteEditText);
        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        editBtn = findViewById(R.id.editBtn);
        noteContent = new ArrayList<>();
        TAG = "Note";

        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        noteEditText.setOnEditorActionListener(new TextView.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                //Keyboard is inactive without this line
                noteEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

                //Actions to occur when user submits note
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    //Getting user data
//                    noteContent = noteEditText.getText().toString();
                    noteContent.add(MainActivity.activeTask, noteEditText.getText().toString());

                    //Clear text from text box
                    noteEditText.setText("");

                    //Don't allow blank notes
                    if (!noteContent.equals("")) {

//                        noteTextView.setText(noteContent);
                        noteTextView.setText(noteContent.get(MainActivity.activeTask));

                    }

                    noteEditText.setText("");

                    //Hide text box
                    noteEditText.setVisibility(View.GONE);

                    //Hide keyboard
                    keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                    //Show edit button
                    editBtn.setVisibility(View.VISIBLE);

                    return true;

                }

                return false;

            }

        });

        //Actions to occur if user selects 'Edit'
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.vibrate.vibrate(50);

                //show keyboard
                keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                //show edit text
                noteEditText.setVisibility(View.VISIBLE);

//                noteEditText.setText(noteContent);
                noteEditText.setText(noteContent.get(MainActivity.activeTask));

                //hide edit button
                editBtn.setVisibility(View.GONE);

            }
        });

    }

    @Override
    //Notes are saved in a manner so that they don't vanish when app closed
    protected void onPause(){

        super.onPause();

        //Getting and saving the size of the note list
        MainActivity.noteListSize = noteContent.size();

        MainActivity.oSharedPreferences.edit().putInt("noteListSizeKey",
                MainActivity.noteListSize).apply();

        //Saving each individual note
        for(int i = 0; i < MainActivity.noteListSize; i++){

            MainActivity.oSharedPreferences.edit().putString("noteItemKey" + String.valueOf(i),
                    noteContent.get(i)).apply();

        }

    }

    @Override
    protected void onResume() {

        super.onResume();

        getSavedData();

    }

    //Existing notes are recalled when app opened
    private void getSavedData() {

        noteContent.clear();

        MainActivity.noteListSize = MainActivity.oSharedPreferences
                .getInt("noteListSizeKey", 0);

        for (int i = 0; i < MainActivity.noteListSize; i++) {

            noteContent.add(i, MainActivity.oSharedPreferences
                    .getString("noteItemKey" + String.valueOf(i), ""));

        }

        try{
            noteTextView.setText(noteContent.get(MainActivity.activeTask));
            noteEditText.setVisibility(View.GONE);
            editBtn.setVisibility(View.VISIBLE);
        }catch (IndexOutOfBoundsException e){
            noteTextView.setText("");
        }

        try {
            //Only show keyboard if there is no existing note
            if (!noteContent.get(MainActivity.activeTask).equals("")) {

                this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            }
        }catch(IndexOutOfBoundsException e){
            //TODO put something here
        }

    }

    @Override
    //Return to main screen when back pressed
    public void onBackPressed() {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

    }

}
