package com.violenthoboenterprises.taskkiller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

//        noteEditText.setOnEditorActionListener(new TextView.OnEditorActionListener(){
//
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//                //Keyboard is inactive without this line
//                noteEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
//
//                //Actions to occur when user submits note
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//
//                    Cursor result = noteDb.getData(activeTask);
//                    while(result.moveToNext()){
//                        checklistExists = (result.getInt(2) == 1);
//                        Log.i(TAG, String.valueOf(checklistExists));
//                    }
//
//                    //new note being added
//                    noteDb.updateData(String.valueOf(activeTask),
//                            noteEditText.getText().toString(), checklistExists);
//
//                    ////////For showing table date////////
////                    Cursor res = noteDb.getAllData();
////                    if(res.getCount() == 0){
////                        showMessage("Error", "Nothing found");
////                    }
////                    StringBuffer buffer = new StringBuffer();
////                    while(res.moveToNext()){
////                        buffer.append("ID: " + res.getString(0) + "\n");
////                        buffer.append("NOTE: " + res.getString(1) + "\n");
////                        buffer.append("CHECKLIST: " + res.getString(2) + "\n\n");
////                    }
////
////                    showMessage("Data", buffer.toString());
//                    ///////////////////////////////////////
//
//                    //Clear text from text box
//                    noteEditText.setText("");
//
//                    //Getting note from database
//                    result = noteDb.getData(activeTask);
//                    while(result.moveToNext()){
//                        theNote = result.getString(1);
//                    }
//
//                    //Don't allow blank notes
//                    if(!theNote.equals("")){
//
//                        //Set text view to the note content
//                        noteTextView.setText(theNote);
//
//                    }
//
//                    //Hide text box
//                    noteEditText.setVisibility(View.GONE);
//
//                    //Hide keyboard
//                    keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//
//                    //Show edit button
//                    editBtn.setVisibility(View.VISIBLE);
//
//                    //show remove button
//                    removeBtn.setVisibility(View.VISIBLE);
//
//                    return true;
//
//                }
//
//                return false;
//
//            }
//
//        });

        //Actions to occur when user clicks submit
        submitNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Keyboard is inactive without this line
                noteEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

                Cursor result = noteDb.getData(activeTask);
                while(result.moveToNext()){
                    checklistExists = (result.getInt(2) == 1);
                }

                //new note being added
                noteDb.updateData(String.valueOf(activeTask),
                        noteEditText.getText().toString(), checklistExists);

                ////////For showing table date////////
//                Cursor res = noteDb.getAllData();
//                if(res.getCount() == 0){
//                    showMessage("Error", "Nothing found");
//                }
//                StringBuffer buffer = new StringBuffer();
//                while(res.moveToNext()){
//                    buffer.append("ID: " + res.getString(0) + "\n");
//                    buffer.append("NOTE: " + res.getString(1) + "\n");
//                    buffer.append("CHECKLIST: " + res.getString(2) + "\n\n");
//                }
//
//                showMessage("Data", buffer.toString());
                ///////////////////////////////////////
                ////////For showing table date////////
//                Cursor res = noteDb.getAllAlarmData();
//                if(res.getCount() == 0){
//                    showMessage("Error", "Nothing found");
//                }
//                StringBuffer buffer = new StringBuffer();
//                while(res.moveToNext()){
//                    buffer.append("ID: " + res.getString(0) + "\n");
//                    buffer.append("HOUR: " + res.getString(1) + "\n");
//                    buffer.append("MINUTE: " + res.getString(2) + "\n");
//                    buffer.append("AM/PM: " + res.getString(3) + "\n");
//                    buffer.append("DAY: " + res.getString(4) + "\n");
//                    buffer.append("MONTH: " + res.getString(5) + "\n");
//                    buffer.append("YEAR: " + res.getString(6) + "\n\n");
//                }
//
//                showMessage("Data", buffer.toString());
                ///////////////////////////////////////

                //Clear text from text box
                noteEditText.setText("");

                //Getting note from database
                result = noteDb.getData(activeTask);
                while(result.moveToNext()){
                    theNote = result.getString(1);
                }

                //Don't allow blank notes
                if(!theNote.equals("")){

                    //Set text view to the note content
                    noteTextView.setText(theNote);

                }

                //Hide text box
                noteEditText.setVisibility(View.GONE);

                //Hide submit button
                submitNoteBtn.setVisibility(View.GONE);

                //Hide keyboard
                keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                //Show edit button
                editBtn.setVisibility(View.VISIBLE);

                //show remove button
                removeBtn.setVisibility(View.VISIBLE);

            }

        });

        //Actions to occur if user selects 'Edit'
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.vibrate.vibrate(50);

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

                MainActivity.vibrate.vibrate(50);

                Cursor result = noteDb.getData(activeTask);
                while(result.moveToNext()){
                    checklistExists = (result.getInt(2) == 1);
                    Log.i(TAG, String.valueOf(checklistExists));
                }

                //setting note in database to nothing
                noteDb.updateData(String.valueOf(activeTask), "", checklistExists);

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

    //////////For showing table results///////////////
//    public void showMessage(String title, String message){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(true);
//        builder.setTitle(title);
//        builder.setMessage(message);
//        builder.show();
//    }
    ////////////////////////////////////////////////

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

        Cursor result = noteDb.getData(activeTask);
        while(result.moveToNext()){
            theNote = result.getString(1);
        }

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
