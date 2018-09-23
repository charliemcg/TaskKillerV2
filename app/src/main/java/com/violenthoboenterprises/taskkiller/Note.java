package com.violenthoboenterprises.taskkiller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Note extends MainActivity {

    TextView noteTextView;
    EditText noteEditText;
    InputMethodManager keyboard;
    ImageView removeBtnDark;
    ImageView removeBtnLight;
    ImageView removeBtnDarkOpen;
    ImageView removeBtnLightOpen;
    ImageView submitNoteBtnDark;
    ImageView submitNoteBtnLight;
    String TAG;
    String theNote;
    //Indicates that the active task has subtasks
    Boolean checklistExists;
    View noteRoot;
    private Toolbar noteToolbar;
    MediaPlayer trash;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_layout);
        overridePendingTransition( R.anim.enter_from_left, R.anim.enter_from_left);
        noteToolbar = findViewById(R.id.noteToolbar);

        noteTextView = findViewById(R.id.noteTextView);
        noteEditText = findViewById(R.id.noteEditText);
        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        removeBtnDark = findViewById(R.id.removeBtnDark);
        removeBtnLight = findViewById(R.id.removeBtnLight);
        removeBtnDarkOpen = findViewById(R.id.removeBtnDarkOpen);
        removeBtnLightOpen = findViewById(R.id.removeBtnLightOpen);
        submitNoteBtnDark = findViewById(R.id.submitNoteBtnDark);
        submitNoteBtnLight = findViewById(R.id.submitNoteBtnLight);
        TAG = "Note";
        theNote = "";
        checklistExists = false;
        inNote = true;
        noteRoot = findViewById(R.id.noteRoot);
        trash = MediaPlayer.create(this, R.raw.trash);

        noteToolbar.setTitleTextColor(Color.parseColor(highlight));

        //getting task data
        String dbTask = "";
        Cursor dbTaskResult = MainActivity.db.getUniversalData();
        while (dbTaskResult.moveToNext()) {
            dbTask = dbTaskResult.getString(4);
        }
        dbTaskResult = db.getData(Integer.parseInt(dbTask));
        while (dbTaskResult.moveToNext()) {
            dbTask = dbTaskResult.getString(4);
        }
        dbTaskResult.close();

        noteToolbar.setTitle(dbTask);

        noteTextView.setMovementMethod(new ScrollingMovementMethod());

        //getting app-wide data
        Cursor dbResult = MainActivity.db.getUniversalData();
        while (dbResult.moveToNext()) {
            mute = dbResult.getInt(1) > 0;
            lightDark = dbResult.getInt(3) > 0;
        }
        dbResult.close();

        if(mute){
            removeBtnDark.setSoundEffectsEnabled(false);
            removeBtnLight.setSoundEffectsEnabled(false);
            submitNoteBtnDark.setSoundEffectsEnabled(false);
            submitNoteBtnLight.setSoundEffectsEnabled(false);
        }

        if(!lightDark){
            noteRoot.setBackgroundColor(Color.parseColor("#333333"));
            submitNoteBtnLight.setVisibility(View.GONE);
        }else{
            noteRoot.setBackgroundColor(Color.parseColor("#FFFFFF"));
            noteToolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
            noteTextView.setTextColor(Color.parseColor("#000000"));
            submitNoteBtnDark.setVisibility(View.GONE);
        }

        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        noteEditText.setBackgroundColor(Color.parseColor(highlight));

        //Actions to occur when user clicks submit
        submitNoteBtnDark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submit(true);

            }

        });

        //Actions to occur when user clicks submit
        submitNoteBtnLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submit(false);

            }

        });

        //Long click allows editing of text
        noteTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                MainActivity.vibrate.vibrate(50);

                //hide remove button
                if(lightDark){
                    removeBtnLight.setVisibility(View.GONE);
                }else{
                    removeBtnDark.setVisibility(View.GONE);
                }

                //show edit text
                noteEditText.setVisibility(View.VISIBLE);

                //show submit button
                submitNoteBtnDark.setVisibility(View.VISIBLE);

                //Focus on edit text so that keyboard does not cover it up
                noteEditText.requestFocus();

                //show keyboard
                keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                //set text to existing note
                noteEditText.setText(theNote);

                //put cursor at end of text
                noteEditText.setSelection(noteEditText.getText().length());

                noteTextView.setText("");

                return true;
            }
        });

        //Actions to occur if user selects 'Remove'
        removeBtnDark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                remove(true);

            }
        });

        //Actions to occur if user selects 'Remove'
        removeBtnLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                remove(false);

            }
        });

    }

    private void remove(final boolean dark) {

        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {
            public void run() {
                if(dark){
                    removeBtnDark.setVisibility(View.GONE);
                    removeBtnDarkOpen.setVisibility(View.VISIBLE);
                }else{
                    removeBtnLight.setVisibility(View.GONE);
                    removeBtnLightOpen.setVisibility(View.VISIBLE);
                }

                vibrate.vibrate(50);

                if(!mute) {
                    trash.start();
                }

                final Handler handler2 = new Handler();
                final Runnable runnable2 = new Runnable(){
                    public void run(){
                        if(dark){
                            removeBtnDarkOpen.setVisibility(View.GONE);
                            removeBtnDark.setVisibility(View.VISIBLE);
                        }else{
                            removeBtnLightOpen.setVisibility(View.GONE);
                            removeBtnLight.setVisibility(View.VISIBLE);
                        }
                        final Handler handler3 = new Handler();
                        final Runnable runnable3 = new Runnable() {
                            @Override
                            public void run() {
                                if(dark){
                                    removeBtnDark.setVisibility(View.GONE);
                                }else{
                                    removeBtnLight.setVisibility(View.GONE);
                                }
                                        Cursor result = db.getData(Integer.parseInt(
                MainActivity.sortedIdsForNote.get(activeTask)));
        while(result.moveToNext()){
            checklistExists = (result.getInt(2) == 1);
        }
        result.close();

        //setting note in database to nothing
        db.updateData(MainActivity.sortedIdsForNote
                .get(activeTask), "", checklistExists);

        noteTextView.setText("");

        //show add button
        noteEditText.setVisibility(View.VISIBLE);
        if(lightDark) {
            submitNoteBtnLight.setVisibility(View.VISIBLE);
        }else{
            submitNoteBtnDark.setVisibility(View.VISIBLE);
        }
                            }
                        };
                        handler3.postDelayed(runnable3, 100);
                    }
                };
                handler2.postDelayed(runnable2, 100);
            }
        };

        handler.postDelayed(runnable, 100);

    }

    private void submit(boolean dark) {

        //Keyboard is inactive without this line
        noteEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        Cursor result = db.getData(Integer.parseInt(
                MainActivity.sortedIdsForNote.get(activeTask)));
        while(result.moveToNext()){
            checklistExists = (result.getInt(2) == 1);
        }
        result.close();

        if(!noteEditText.getText().toString().equals("")) {
            //new note being added
            db.updateData(MainActivity.sortedIdsForNote.get(activeTask),
                    noteEditText.getText().toString(), checklistExists);
        }

        //Clear text from text box
        noteEditText.setText("");

        //Getting note from database
        result = db.getData(Integer.parseInt(MainActivity.sortedIdsForNote.get(activeTask)));
        while(result.moveToNext()){
            theNote = result.getString(1);
        }

        //Don't allow blank notes
        if(!theNote.equals("")){

            vibrate.vibrate(50);

            if(!mute) {
                blip.start();
            }

            //Set text view to the note content
            noteTextView.setText(theNote);

            //show remove button
            if(lightDark){
                removeBtnLight.setVisibility(View.VISIBLE);
            }else{
                removeBtnDark.setVisibility(View.VISIBLE);
            }

            //Hide text box
            noteEditText.setVisibility(View.GONE);

            if(dark) {
                //Hide submit button
                submitNoteBtnDark.setVisibility(View.GONE);
            }else{
                submitNoteBtnLight.setVisibility(View.GONE);
            }

        }

        //Hide keyboard
        keyboard.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


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

        Cursor result = db.getData(Integer.parseInt(
                MainActivity.sortedIdsForNote.get(activeTask)));
        while(result.moveToNext()){
            theNote = result.getString(1);
        }
        result.close();

        Cursor dbResult = MainActivity.db.getUniversalData();
        while (dbResult.moveToNext()) {
            mute = dbResult.getInt(1) > 0;
            lightDark = dbResult.getInt(3) > 0;
        }
        dbResult.close();

        //Don't allow blank notes
        if(!theNote.equals("")){

            noteTextView.setText(theNote);
            this.getWindow().setSoftInputMode(WindowManager
                    .LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            noteEditText.setVisibility(View.GONE);
            submitNoteBtnDark.setVisibility(View.GONE);
            submitNoteBtnLight.setVisibility(View.GONE);
            if(lightDark){
                removeBtnLight.setVisibility(View.VISIBLE);
            }else{
                removeBtnDark.setVisibility(View.VISIBLE);
            }

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
