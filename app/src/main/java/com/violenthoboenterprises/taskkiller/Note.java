package com.violenthoboenterprises.taskkiller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Note extends MainActivity {

    TextView noteTextView;
    EditText noteEditText;
    InputMethodManager keyboard;
    Button editBtn;
    Button removeBtn;
    Button addNoteBtn;
    Button submitNoteBtn;
    String TAG;
    String theNote;
    //Indicates that the active task has subtasks
    Boolean checklistExists;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_layout);

        noteTextView = findViewById(R.id.noteTextView);
        noteEditText = findViewById(R.id.noteEditText);
        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        editBtn = findViewById(R.id.editBtn);
        removeBtn = findViewById(R.id.removeBtn);
        addNoteBtn = findViewById(R.id.addNoteBtn);
        submitNoteBtn = findViewById(R.id.submitNoteBtn);
        TAG = "Note";
        theNote = "";
        checklistExists = false;
        inNote = true;

        mute = mSharedPreferences.getBoolean("muteKey", false);

        //getting app-wide data
        Cursor dbResult = MainActivity.noteDb.getUniversalData();
        while (dbResult.moveToNext()) {
            mute = dbResult.getInt(1) > 0;
        }

        if(mute){
            editBtn.setSoundEffectsEnabled(false);
            removeBtn.setSoundEffectsEnabled(false);
            addNoteBtn.setSoundEffectsEnabled(false);
            submitNoteBtn.setSoundEffectsEnabled(false);
        }

        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        noteEditText.setBackgroundColor(Color.parseColor(highlight));

        //Actions to occur when user clicks submit
        submitNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Keyboard is inactive without this line
                noteEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

                Cursor result = noteDb.getData(Integer.parseInt(
                        MainActivity.sortedIdsForNote.get(activeTask)));
                while(result.moveToNext()){
                    checklistExists = (result.getInt(2) == 1);
                }

                if(!noteEditText.getText().toString().equals("")) {
                    //new note being added
                    noteDb.updateData(MainActivity.sortedIdsForNote.get(activeTask),
                            noteEditText.getText().toString(), checklistExists);
                }

                //Clear text from text box
                noteEditText.setText("");

                //Getting note from database
                result = noteDb.getData(Integer.parseInt(MainActivity.sortedIdsForNote.get(activeTask)));
                while(result.moveToNext()){
                    theNote = result.getString(1);
                }

                //Don't allow blank notes
                if(!theNote.equals("")){

                    //Set text view to the note content
                    noteTextView.setText(theNote);

                    //show remove button
                    removeBtn.setVisibility(View.VISIBLE);

                    editBtn.setText("Edit");

                }else{
                    editBtn.setText("Add Note");
                }

                //Hide text box
                noteEditText.setVisibility(View.GONE);

                //Hide submit button
                submitNoteBtn.setVisibility(View.GONE);

                //Hide keyboard
                keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                //Show edit button
                editBtn.setVisibility(View.VISIBLE);

            }

        });

        //Actions to occur if user selects 'Edit'
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                MainActivity.vibrate.vibrate(50);

                //show edit text
                noteEditText.setVisibility(View.VISIBLE);

                //show submit button
                submitNoteBtn.setVisibility(View.VISIBLE);

                //set text to existing note
                noteEditText.setText(theNote);

                //put cursor at end of text
                noteEditText.requestFocus();
                noteEditText.setSelection(noteEditText.getText().length());

                //show keyboard
                keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                //hide edit button
                editBtn.setVisibility(View.GONE);

                //hide remove button
                removeBtn.setVisibility(View.GONE);

            }
        });

        //Actions to occur if user selects 'Remove'
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                MainActivity.vibrate.vibrate(50);

                Cursor result = noteDb.getData(Integer.parseInt(
                        MainActivity.sortedIdsForNote.get(activeTask)));
                while(result.moveToNext()){
                    checklistExists = (result.getInt(2) == 1);
                }

                //setting note in database to nothing
                noteDb.updateData(MainActivity.sortedIdsForNote
                        .get(activeTask), "", checklistExists);

                noteTextView.setText("");

                //hide edit button
                editBtn.setVisibility(View.GONE);

                //hide remove button
                removeBtn.setVisibility(View.GONE);

                //show add button
                addNoteBtn.setVisibility(View.VISIBLE);

            }
        });

        //actions to occur if user selects 'add'
        addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                noteEditText.setVisibility(View.VISIBLE);
                submitNoteBtn.setVisibility(View.VISIBLE);
                noteEditText.requestFocus();
                keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                addNoteBtn.setVisibility(View.GONE);

            }
        });

    }

    @Override
    protected void onPause(){

        super.onPause();

        inNote = false;

    }

    @Override
    protected void onResume() {

        super.onResume();

        inNote = true;

        getSavedData();

    }

    //Existing notes are recalled when app opened
    private void getSavedData() {

        Cursor result = noteDb.getData(Integer.parseInt(
                MainActivity.sortedIdsForNote.get(activeTask)));
        while(result.moveToNext()){
            theNote = result.getString(1);
        }

        mute = mSharedPreferences.getBoolean("muteKey", false);

        //Don't allow blank notes
        if(!theNote.equals("")){

            noteTextView.setText(theNote);
            this.getWindow().setSoftInputMode(WindowManager
                    .LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            noteEditText.setVisibility(View.GONE);
            submitNoteBtn.setVisibility(View.GONE);
            editBtn.setVisibility(View.VISIBLE);
            removeBtn.setVisibility(View.VISIBLE);

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
