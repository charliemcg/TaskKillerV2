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
    String TAG;
    String theNote;
    //Indicates if new note is being added or if existing note is being edited
    Boolean setEdit;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_layout);

        noteTextView = findViewById(R.id.noteTextView);
        noteEditText = findViewById(R.id.noteEditText);
        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        editBtn = findViewById(R.id.editBtn);
        TAG = "Note";
        theNote = "";
        setEdit = false;

        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        noteEditText.setOnEditorActionListener(new TextView.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                //Keyboard is inactive without this line
                noteEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

                //Actions to occur when user submits note
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    //new note being added
                    if(!setEdit) {
                        noteDb.insertData(activeTask, noteEditText.getText().toString());
                    //existing note is being edited.
                    }else{
                        noteDb.updateData(String.valueOf(activeTask), noteEditText.getText().toString());
                        setEdit = false;
                    }

                    ////////For showing table date////////
//                    Cursor res = noteDb.getAllData();
//                    if(res.getCount() == 0){
//                        showMessage("Error", "Nothing found");
//                    }
//                    StringBuffer buffer = new StringBuffer();
//                    while(res.moveToNext()){
//                        buffer.append("ID: " + res.getString(0) + "\n");
//                        buffer.append("NOTE: " + res.getString(1) + "\n\n");
//                    }
//
//                    showMessage("Data", buffer.toString());
                    ///////////////////////////////////////

                    //Clear text from text box
                    noteEditText.setText("");

                    //Getting note from database
                    Cursor result = noteDb.getData(activeTask);
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

                setEdit = true;

                //show edit text
                noteEditText.setVisibility(View.VISIBLE);

                //set text to existing note
                noteEditText.setText(theNote);

                //put cursor at end of text
                noteEditText.requestFocus();
                noteEditText.setSelection(noteEditText.getText().length());

                //show keyboard
                keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                //hide edit button
                editBtn.setVisibility(View.GONE);

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
    //Notes are saved in a manner so that they don't vanish when app closed
    protected void onPause(){

        super.onPause();

    }

    @Override
    protected void onResume() {

        super.onResume();

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
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            noteEditText.setVisibility(View.GONE);
            editBtn.setVisibility(View.VISIBLE);

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
