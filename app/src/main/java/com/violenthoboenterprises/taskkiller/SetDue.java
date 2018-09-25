package com.violenthoboenterprises.taskkiller;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class SetDue extends MainActivity {

    String TAG;
    private Toolbar dueToolbar;
    LinearLayout dateButton, timeButton;
    Button daily, weekly, monthly, cancelRepeat, killAlarm;
    View pickerRoot;
    TextView dateTextView;
    TextView timeTextView;
    String repeat;
    static boolean setDue;
    String dbTaskId;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.due_picker);
        overridePendingTransition( R.anim.enter_from_left, R.anim.enter_from_left);
        dueToolbar = findViewById(R.id.dueToolbar);

        TAG = "SetDue";
        repeat = "none";
        setDue = false;

        dateButton = findViewById(R.id.dateBtn);
        timeButton = findViewById(R.id.timeBtn);
//        setButton = findViewById(R.id.setBtn);
        pickerRoot = findViewById(R.id.pickerRoot);
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        daily = findViewById(R.id.daily);
        weekly = findViewById(R.id.weekly);
        monthly = findViewById(R.id.monthly);
        cancelRepeat = findViewById(R.id.cancelRepeat);
        killAlarm = findViewById(R.id.removeAlarm);

        dueToolbar.setTitleTextColor(Color.parseColor(highlight));

        //getting task data
        String dbDueTime = "";
        dbTaskId = "";
        String dbTask = "";
        String dbRepeatInterval = "";
        boolean dbRepeat = false;
        Cursor dbTaskResult = MainActivity.db.getUniversalData();
        while (dbTaskResult.moveToNext()) {
            dbTaskId = dbTaskResult.getString(4);
        }
        dbTaskResult = db.getData(Integer.parseInt(dbTaskId));
        while (dbTaskResult.moveToNext()) {
            dbDueTime = dbTaskResult.getString(3);
            dbTask = dbTaskResult.getString(4);
            dbRepeat = dbTaskResult.getInt(8) > 0;
            dbRepeatInterval = dbTaskResult.getString(13);
        }
        dbTaskResult.close();

        dueToolbar.setTitle(dbTask);

        if(dbDueTime.equals("0")){
            dateTextView.setText("Click to add due date");
            timeTextView.setText("Click to add due time");
        }else{
            //getting alarm data
            Cursor alarmResult = MainActivity.db.getAlarmData
                    (Integer.parseInt(dbTaskId));
            String alarmHour = "";
            String alarmMinute = "";
            String alarmAmpm = "";
            String alarmDay = "";
            String alarmMonth = "";
            String alarmYear = "";
            while(alarmResult.moveToNext()){
                alarmHour = alarmResult.getString(1);
                alarmMinute = alarmResult.getString(2);
                alarmAmpm = alarmResult.getString(3);
                alarmDay = alarmResult.getString(4);
                alarmMonth = alarmResult.getString(5);
                alarmYear = alarmResult.getString(6);
            }
            alarmResult.close();
            dateTextView.setText(alarmDay + "/" + alarmMonth + "/" + alarmYear);
            timeTextView.setText(alarmHour + ":" + alarmMinute + alarmAmpm);
        }

        if(!dbRepeat){
            cancelRepeat.setBackgroundColor(Color.parseColor("#0000FF"));
        }else if(dbRepeatInterval.equals("day")){
            daily.setBackgroundColor(Color.parseColor("#0000FF"));
        }else if(dbRepeatInterval.equals("week")){
            weekly.setBackgroundColor(Color.parseColor("#0000FF"));
        }else if(dbRepeatInterval.equals("month")){
            monthly.setBackgroundColor(Color.parseColor("#0000FF"));
        }

        //getting app-wide data
        Cursor dbResult = MainActivity.db.getUniversalData();
        while (dbResult.moveToNext()) {
            mute = dbResult.getInt(1) > 0;
            lightDark = dbResult.getInt(3) > 0;
        }
        dbResult.close();

        if(!lightDark){
            pickerRoot.setBackgroundColor(Color.parseColor("#333333"));
            dueToolbar.setBackgroundColor(Color.parseColor("#333333"));
            dateTextView.setTextColor(Color.parseColor("#AAAAAA"));
            timeTextView.setTextColor(Color.parseColor("#AAAAAA"));
        }else{
            pickerRoot.setBackgroundColor(Color.parseColor("#FFFFFF"));
            dueToolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
            dateTextView.setTextColor(Color.parseColor("#000000"));
            timeTextView.setTextColor(Color.parseColor("#000000"));
        }

        dateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(!mute){
                    blip.start();
                }

                vibrate.vibrate(50);

                DialogFragment dialogfragment = new DatePickerDialogFrag();

                dialogfragment.show(getFragmentManager(), "Date");

            }

        });

        timeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(!mute){
                    blip.start();
                }

                vibrate.vibrate(50);

                DialogFragment dialogfragment = new TimePickerDialogFrag();

                dialogfragment.show(getFragmentManager(), "Time");

            }

        });

        daily.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(!mute){
                    blip.start();
                }

                vibrate.vibrate(50);

                cancelRepeat.setBackgroundColor(Color.parseColor("#AAAAAA"));
                daily.setBackgroundColor(Color.parseColor("#0000FF"));
                weekly.setBackgroundColor(Color.parseColor("#AAAAAA"));
                monthly.setBackgroundColor(Color.parseColor("#AAAAAA"));

                dateRowShowing =true;

                repeatInterval = AlarmManager.INTERVAL_DAY;

//                db.updateRepeatInterval(String.valueOf(
//                        sortedIDs.get(activeTask)),"day");
                repeat = "day";

                repeating =true;

                taskPropertiesShowing =false;

//                db.updateSetAlarm(true);

            }

        });

        weekly.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(!mute){
                    blip.start();
                }

                vibrate.vibrate(50);

                cancelRepeat.setBackgroundColor(Color.parseColor("#AAAAAA"));
                daily.setBackgroundColor(Color.parseColor("#AAAAAA"));
                weekly.setBackgroundColor(Color.parseColor("#0000FF"));
                monthly.setBackgroundColor(Color.parseColor("#AAAAAA"));

                dateRowShowing =true;

                repeatInterval = AlarmManager.INTERVAL_DAY;

//                db.updateRepeatInterval(String.valueOf(
//                        sortedIDs.get(activeTask)),"week");

                repeat = "week";

                repeating =true;

                taskPropertiesShowing =false;

//                db.updateSetAlarm(true);

            }

        });

        monthly.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(!mute){
                    blip.start();
                }

                vibrate.vibrate(50);

                cancelRepeat.setBackgroundColor(Color.parseColor("#AAAAAA"));
                daily.setBackgroundColor(Color.parseColor("#AAAAAA"));
                weekly.setBackgroundColor(Color.parseColor("#AAAAAA"));
                monthly.setBackgroundColor(Color.parseColor("#0000FF"));

                dateRowShowing =true;

                repeatInterval = AlarmManager.INTERVAL_DAY;

//                db.updateRepeatInterval(String.valueOf(
//                        sortedIDs.get(activeTask)),"month");

                repeat = "month";

                repeating =true;

                taskPropertiesShowing =false;

//                db.updateSetAlarm(true);

            }

        });

        cancelRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(!mute){
                    blip.start();
                }

                vibrate.vibrate(50);

                cancelRepeat.setBackgroundColor(Color.parseColor("#0000FF"));
                daily.setBackgroundColor(Color.parseColor("#AAAAAA"));
                weekly.setBackgroundColor(Color.parseColor("#AAAAAA"));
                monthly.setBackgroundColor(Color.parseColor("#AAAAAA"));

                repeat = "none";

            }

        });

        final String finalDbTaskId = dbTaskId;
        killAlarm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(!mute){
                    blip.start();
                }

                vibrate.vibrate(50);

                db.updateDue(finalDbTaskId, false);

                db.updateTimestamp(finalDbTaskId, "0");

                db.updateRepeat(finalDbTaskId, false);

                pendIntent = PendingIntent.getBroadcast(getApplicationContext(),
                        Integer.valueOf(finalDbTaskId),
                        alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.cancel(MainActivity.pendIntent);

                db.updateAlarmData(finalDbTaskId,
                        "", "", "", "", "", "");

                dateTextView.setText("Click to set due date");
                timeTextView.setText("Click to set due time");

                cancelRepeat.setBackgroundColor(Color.parseColor("#0000FF"));
                daily.setBackgroundColor(Color.parseColor("#AAAAAA"));
                weekly.setBackgroundColor(Color.parseColor("#AAAAAA"));
                monthly.setBackgroundColor(Color.parseColor("#AAAAAA"));

            }

        });

    }

    public static class DatePickerDialogFrag extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog;

            if(!lightDark) {
                datePickerDialog = new DatePickerDialog(getActivity(),
                        AlertDialog.THEME_DEVICE_DEFAULT_DARK, this, year, month, day);
            }else{
                datePickerDialog = new DatePickerDialog(getActivity(),
                        AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, this, year, month, day);
            }

            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day){

            TextView textview = getActivity().findViewById(R.id.dateTextView);

            textview.setText(day + ":" + (month+1) + ":" + year);

            MainActivity.vibrate.vibrate(50);

            if(!MainActivity.mute){
                 MainActivity.blip.start();
            }

            db.updateYear(year);
            db.updateMonth(month + 1);
            db.updateDay(day);

            setDue = true;

        }

    }

    public static class TimePickerDialogFrag extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog;

            if(!lightDark) {
                timePickerDialog = new TimePickerDialog(getActivity(),
                        AlertDialog.THEME_DEVICE_DEFAULT_DARK, this, hour, minute, false);
            }else{
                timePickerDialog = new TimePickerDialog(getActivity(),
                        AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, this, hour, minute, false);
            }

            return timePickerDialog;
        }

        public void onTimeSet(TimePicker view, int hour, int minute){

            TextView textview = getActivity().findViewById(R.id.timeTextView);

            textview.setText(hour + ":" + minute);

            MainActivity.vibrate.vibrate(50);

            if(!MainActivity.mute){
                MainActivity.blip.start();
            }

            db.updateHour(hour);
            db.updateMinute(minute);

            setDue = true;

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
        if(!repeat.equals("none")) {
            db.updateRepeatInterval(dbTaskId, repeat);
            db.updateRepeat(dbTaskId, true);

            if(repeat.equals("day")){

                repeatInterval = AlarmManager.INTERVAL_DAY;

                db.updateRepeatInterval(dbTaskId,"day");

                repeating = true;

                taskPropertiesShowing = false;

            }else if(repeat.equals("week")){

                repeatInterval = (AlarmManager.INTERVAL_DAY * 7);

                db.updateRepeatInterval(dbTaskId, "week");

                repeating = true;

                taskPropertiesShowing = false;

            }else if(repeat.equals("month")){

                db.updateRepeatInterval(dbTaskId, "month");

                MainActivity.taskPropertiesShowing = false;

            }

        }

        if(setDue) {
            db.updateSetAlarm(true);
        }

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

    }

}
