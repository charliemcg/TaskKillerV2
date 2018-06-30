package com.violenthoboenterprises.taskkiller;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Note extends MainActivity {

    TextView noteTextView;
    EditText noteEditText;
    Boolean noteBeingEdited;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_layout);

        noteTextView = findViewById(R.id.noteTextView);
        noteEditText = findViewById(R.id.noteEditText);
        noteBeingEdited = false;

        noteEditText.setOnEditorActionListener(new TextView.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                //Keyboard is inactive without this line
                noteEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

                //Actions to occur when user submits note
                if (actionId == EditorInfo.IME_ACTION_DONE && !noteBeingEdited) {

                    //Getting user data
                    String noteName = noteEditText.getText().toString();

                    //Clear text from text box
                    noteEditText.setText("");

                    //Don't allow blank notes
                    if(!noteName.equals("")) {

                        noteTextView.setText(noteName);

                    }

                    noteEditText.setText("");

                    return true;

                //Actions to occur when editing note
                }else if(actionId == EditorInfo.IME_ACTION_DONE && noteBeingEdited){

                    //Hide keyboard
                    keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);

                    //Getting user data
                    String editedNoteString = noteEditText.getText().toString();

                    //Don't allow blank notes
                    if(!editedNoteString.equals("")) {

                        noteTextView.setText(editedNoteString);

                    }

                    noteEditText.setText("");

                    //Marking editing as complete
                    noteBeingEdited = false;

                    return true;

                }

                return false;

            }

        });

    }

}
