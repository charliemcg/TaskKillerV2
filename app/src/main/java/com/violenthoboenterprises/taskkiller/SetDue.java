package com.violenthoboenterprises.taskkiller;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class SetDue extends MainActivity {

    String TAG;
    private Toolbar dueToolbar;
    Button dateButton, timeButton;
    View pickerRoot;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.due_picker);
        overridePendingTransition( R.anim.enter_from_left, R.anim.enter_from_left);
        dueToolbar = findViewById(R.id.dueToolbar);

        TAG = "SetDue";

        dateButton = findViewById(R.id.dateBtn);
        timeButton = findViewById(R.id.timeBtn);
//        setButton = findViewById(R.id.setBtn);
        pickerRoot = findViewById(R.id.pickerRoot);

        dateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DialogFragment dialogfragment = new DatePickerDialogTheme1();

                dialogfragment.show(getFragmentManager(), "Date");

            }

        });

        timeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DialogFragment dialogfragment = new DatePickerDialogTheme2();

                dialogfragment.show(getFragmentManager(), "Time");

            }

        });

//        setButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                db.updateSetAlarm(true);
//
//                Intent intent = new Intent();
//
//                intent.setClass(getApplicationContext(), MainActivity.class);
//
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//                startActivity(intent);
//
//            }
//
//        });

        dueToolbar.setTitleTextColor(Color.parseColor(highlight));

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

        dueToolbar.setTitle(dbTask);

        //getting app-wide data
        Cursor dbResult = MainActivity.db.getUniversalData();
        while (dbResult.moveToNext()) {
            mute = dbResult.getInt(1) > 0;
            lightDark = dbResult.getInt(3) > 0;
        }
        dbResult.close();

        if(mute){
            //TODO mute sounds and add vibrations
        }

        if(!lightDark){
            pickerRoot.setBackgroundColor(Color.parseColor("#333333"));
            dueToolbar.setBackgroundColor(Color.parseColor("#333333"));
        }else{
            pickerRoot.setBackgroundColor(Color.parseColor("#FFFFFF"));
            dueToolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

    }

    public static class DatePickerDialogTheme1 extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,year,month,day);

            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day){

            TextView textview = getActivity().findViewById(R.id.dateTextView);

            textview.setText(day + ":" + (month+1) + ":" + year);

            MainActivity.vibrate.vibrate(50);

            if(!MainActivity.mute){
                 MainActivity.blip.start();
            }

//            selectedDay = day;
//            selectedMonth = (month + 1);
//            selectedYear = year;

            db.updateYear(year);
            db.updateMonth(month + 1);
            db.updateDay(day);

        }

    }

    public static class DatePickerDialogTheme2 extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK, this, hour, minute, true);

            return timePickerDialog;
        }

        public void onTimeSet(TimePicker view, int hour, int minute){

            TextView textview = getActivity().findViewById(R.id.timeTextView);

            textview.setText(hour + ":" + minute);

            MainActivity.vibrate.vibrate(50);

            if(!MainActivity.mute){
                MainActivity.blip.start();
            }

//            selectedHour = hour;
//            selectedMinute = minute;

            db.updateHour(hour);
            db.updateMinute(minute);

        }
    }

    @Override
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
//
//        Cursor result = db.getData(Integer.parseInt(
//                MainActivity.sortedIdsForNote.get(activeTask)));
//        while(result.moveToNext()){
//            theNote = result.getString(1);
//        }
//        result.close();
//
//        Cursor dbResult = MainActivity.db.getUniversalData();
//        while (dbResult.moveToNext()) {
//            mute = dbResult.getInt(1) > 0;
//            lightDark = dbResult.getInt(3) > 0;
//        }
//        dbResult.close();
//
//        //Don't allow blank notes
//        if(!theNote.equals("")){
//
//            noteTextView.setText(theNote);
//            this.getWindow().setSoftInputMode(WindowManager
//                    .LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//            noteEditText.setVisibility(View.GONE);
//            submitNoteBtnDark.setVisibility(View.GONE);
//            submitNoteBtnLight.setVisibility(View.GONE);
//            if(lightDark){
//                removeBtnLight.setVisibility(View.VISIBLE);
//            }else{
//                removeBtnDark.setVisibility(View.VISIBLE);
//            }
//
//        }

    }

    @Override
    //Return to main screen when back pressed
    public void onBackPressed() {
//
//        Intent intent = new Intent();
//
//        intent.setClass(getApplicationContext(), MainActivity.class);
//
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        startActivity(intent);
//

        db.updateSetAlarm(true);

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

    }

}
