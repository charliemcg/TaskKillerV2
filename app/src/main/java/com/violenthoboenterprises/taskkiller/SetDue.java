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
import android.view.Menu;
import android.view.MenuItem;
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

    static String TAG;
    private Toolbar dueToolbar;
    LinearLayout dateButton, timeButton;
    ImageView daily;
    ImageView weekly;
    ImageView monthly;
    ImageView cancelRepeat;
//    static LinearLayout killAlarm;
    View pickerRoot;
    TextView dateTextView;
    TextView timeTextView;
    String repeat;
    static boolean setDue;
    static String dbTaskId;
    static String dbDueTime;
    MenuItem killAlarm;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.due_picker);
        overridePendingTransition( R.anim.enter_from_left, R.anim.enter_from_left);
        dueToolbar = findViewById(R.id.dueToolbar);
        setSupportActionBar(dueToolbar);

        TAG = "SetDue";
        repeat = "none";
        setDue = false;

        dateButton = findViewById(R.id.dateBtn);
        timeButton = findViewById(R.id.timeBtn);
        pickerRoot = findViewById(R.id.pickerRoot);
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        daily = findViewById(R.id.daily);
        weekly = findViewById(R.id.weekly);
        monthly = findViewById(R.id.monthly);
        cancelRepeat = findViewById(R.id.cancelRepeat);
//        killAlarm = findViewById(R.id.removeAlarm);

        dueToolbar.setTitleTextColor(Color.parseColor(highlight));
        cancelRepeat.setBackgroundColor(Color.parseColor("#AAAAAA"));
        daily.setBackgroundColor(Color.parseColor("#AAAAAA"));
        weekly.setBackgroundColor(Color.parseColor("#AAAAAA"));
        monthly.setBackgroundColor(Color.parseColor("#AAAAAA"));

        //getting task data
        dbDueTime = "";
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
//            killAlarm.setVisibility(View.VISIBLE);
            dateTextView.setText(alarmDay + "/" + alarmMonth + "/" + alarmYear);
            timeTextView.setText(alarmHour + ":" + alarmMinute + alarmAmpm);
        }

        if(!dbRepeat){
            cancelRepeat.setBackgroundColor(Color.parseColor(highlight));
        }else if(dbRepeatInterval.equals("day")){
            daily.setBackgroundColor(Color.parseColor(highlight));
        }else if(dbRepeatInterval.equals("week")){
            weekly.setBackgroundColor(Color.parseColor(highlight));
        }else if(dbRepeatInterval.equals("month")){
            monthly.setBackgroundColor(Color.parseColor(highlight));
        }

        //getting app-wide data//TODO aren't these supposed to be global?
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
                daily.setBackgroundColor(Color.parseColor(highlight));
                weekly.setBackgroundColor(Color.parseColor("#AAAAAA"));
                monthly.setBackgroundColor(Color.parseColor("#AAAAAA"));

                dateRowShowing =true;

                repeatInterval = AlarmManager.INTERVAL_DAY;

                repeat = "day";

                repeating =true;

                taskPropertiesShowing =false;

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
                weekly.setBackgroundColor(Color.parseColor(highlight));
                monthly.setBackgroundColor(Color.parseColor("#AAAAAA"));

                dateRowShowing =true;

                repeatInterval = AlarmManager.INTERVAL_DAY;

                repeat = "week";

                repeating =true;

                taskPropertiesShowing =false;

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
                monthly.setBackgroundColor(Color.parseColor(highlight));

                dateRowShowing =true;

                repeatInterval = AlarmManager.INTERVAL_DAY;

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

                cancelRepeat.setBackgroundColor(Color.parseColor(highlight));
                daily.setBackgroundColor(Color.parseColor("#AAAAAA"));
                weekly.setBackgroundColor(Color.parseColor("#AAAAAA"));
                monthly.setBackgroundColor(Color.parseColor("#AAAAAA"));

                repeat = "none";

            }

        });

        final String finalDbTaskId = dbTaskId;

//        killAlarm.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                if(!mute){
//                    blip.start();
//                }
//
//                vibrate.vibrate(50);
//
//                db.updateDue(finalDbTaskId, false);
//
//                db.updateTimestamp(finalDbTaskId, "0");
//
//                db.updateRepeat(finalDbTaskId, false);
//
//                pendIntent = PendingIntent.getBroadcast(getApplicationContext(),
//                        Integer.valueOf(finalDbTaskId),
//                        alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                alarmManager.cancel(MainActivity.pendIntent);
//
//                db.updateAlarmData(finalDbTaskId,
//                        "", "", "", "", "", "");
//
//                dateTextView.setText("Click to set due date");
//                timeTextView.setText("Click to set due time");
//
//                cancelRepeat.setBackgroundColor(Color.parseColor(highlight));
//                daily.setBackgroundColor(Color.parseColor("#AAAAAA"));
//                weekly.setBackgroundColor(Color.parseColor("#AAAAAA"));
//                monthly.setBackgroundColor(Color.parseColor("#AAAAAA"));
//
//                killAlarm.setVisibility(View.GONE);
//
//            }
//
//        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!menu.hasVisibleItems()) {
            getMenuInflater().inflate(R.menu.menu_alarm, menu);
//            killAlarm = this.dueToolbar.getMenu().findItem(R.id.killAlarmItem);
            killAlarm = menu.findItem(R.id.killAlarmItem);
            Log.i(TAG, String.valueOf(killAlarm));
//            killAlarm.setIcon(ContextCompat.getDrawable(this, R.drawable.trash_dark));
            if (!lightDark) {
//                if (mute) {
//                    muteBtn.setIcon(ContextCompat.getDrawable(this, R.drawable.muted));
//                } else {
//                    muteBtn.setIcon(ContextCompat.getDrawable(this, R.drawable.unmuted));
//                }
//                lightDarkBtn.setTitle("Light Mode");
            } else {
//                if (mute) {
//                    muteBtn.setIcon(ContextCompat.getDrawable(this, R.drawable.muted_white));
//                } else {
//                    muteBtn.setIcon(ContextCompat.getDrawable
//                            (this, R.drawable.unmuted_white));
//                }
//                lightDarkBtn.setTitle("Dark Mode");
            }
            return true;
        }else {
            killAlarm.setEnabled(true);
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        vibrate.vibrate(50);

        //TODO find out if return statements are necessary
        //noinspection SimplifiableIfStatement
        if (id == R.id.killAlarmItem) {

//            killAlarm = this.dueToolbar.getMenu().findItem(R.id.killAlarm);

            //getting task data
            dbTaskId = "";
            Cursor dbTaskResult = MainActivity.db.getUniversalData();
            while (dbTaskResult.moveToNext()) {
                dbTaskId = dbTaskResult.getString(4);
            }
            dbTaskResult.close();

            if(!mute){
                blip.start();
            }

            vibrate.vibrate(50);

            db.updateDue(dbTaskId, false);

            db.updateTimestamp(dbTaskId, "0");

            db.updateRepeat(dbTaskId, false);

            pendIntent = PendingIntent.getBroadcast(getApplicationContext(),
                    Integer.valueOf(dbTaskId),
                    alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.cancel(MainActivity.pendIntent);

            db.updateAlarmData(dbTaskId,
                    "", "", "", "", "", "");

            setDue = false;

            dateTextView.setText("Click to add due date");
            timeTextView.setText("Click to add due time");

            cancelRepeat.setBackgroundColor(Color.parseColor(highlight));
            daily.setBackgroundColor(Color.parseColor("#AAAAAA"));
            weekly.setBackgroundColor(Color.parseColor("#AAAAAA"));
            monthly.setBackgroundColor(Color.parseColor("#AAAAAA"));

            return true;
        }

        return super.onOptionsItemSelected(item);
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

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day){

            TextView textview = getActivity().findViewById(R.id.dateTextView);

            String formattedMonth = "";

            int intMonth = Integer.valueOf(month) + 1;
            if(intMonth == 1){
                formattedMonth = getString(R.string.jan);
            }else if(intMonth == 2){
                formattedMonth = getString(R.string.feb);
            }else if(intMonth == 3){
                formattedMonth = getString(R.string.mar);
            }else if(intMonth == 4){
                formattedMonth = getString(R.string.apr);
            }else if(intMonth == 5){
                formattedMonth = getString(R.string.may);
            }else if(intMonth == 6){
                formattedMonth = getString(R.string.jun);
            }else if(intMonth == 7){
                formattedMonth = getString(R.string.jul);
            }else if(intMonth == 8){
                formattedMonth = getString(R.string.aug);
            }else if(intMonth == 9){
                formattedMonth = getString(R.string.sep);
            }else if(intMonth == 10){
                formattedMonth = getString(R.string.oct);
            }else if(intMonth == 11){
                formattedMonth = getString(R.string.nov);
            }else if(intMonth == 12){
                formattedMonth = getString(R.string.dec);
            }

            textview.setText(day + " " + formattedMonth + " " + year);

            vibrate.vibrate(50);

            if(!mute){
                 blip.start();
            }

            db.updateYear(year);
            db.updateMonth(month);
            db.updateDay(day);

            Cursor alarmResult = MainActivity.db.getAlarmData
                    (Integer.parseInt(dbTaskId));
            String alarmHour = "";
            String alarmMinute = "";
            while(alarmResult.moveToNext()){
                alarmHour = alarmResult.getString(1);
                alarmMinute = alarmResult.getString(2);
            }

            alarmResult.close();

            if(alarmHour.equals("") && alarmMinute.equals("")){
                Calendar calendar = Calendar.getInstance();
                int minute = calendar.get(Calendar.MINUTE);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentYear = calendar.get(Calendar.YEAR);
                if((currentYear == year) && (currentMonth == month) && (currentDay == day)
                        && (hour > 10)) {
                    if(hour != 23) {
                        db.updateHour(hour + 1);
                    }else{
                        db.updateHour(hour);
                    }
                    db.updateMinute(minute);
                }else{
                    db.updateHour(10);
                    db.updateMinute(0);
                }
            }

            setDue = true;

//            killAlarm.setVisibility(View.VISIBLE);

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

            String adjustedAmPm = "am";
            String adjustedHour = String.valueOf(hour);
            String adjustedMinute = String.valueOf(minute);

            if(hour == 0) {
                adjustedHour = String.valueOf(12);
            }else if(hour == 12){
                adjustedAmPm = "pm";
            }else if(hour > 12){
                adjustedHour = String.valueOf(hour - 12);
                adjustedAmPm = "pm";
            }

            if(minute < 10){
                adjustedMinute = "0" + String.valueOf(minute);
            }

            textview.setText(adjustedHour + ":" + adjustedMinute + adjustedAmPm);

            MainActivity.vibrate.vibrate(50);

            if(!MainActivity.mute){
                MainActivity.blip.start();
            }

            db.updateHour(hour);
            db.updateMinute(minute);

            Cursor alarmResult = MainActivity.db.getAlarmData
                    (Integer.parseInt(dbTaskId));
            String alarmDay = "";
            String alarmMonth = "";
            String alarmYear = "";
            while(alarmResult.moveToNext()){
                alarmDay = alarmResult.getString(4);
                alarmMonth = alarmResult.getString(5);
                alarmYear = alarmResult.getString(6);
            }

            alarmResult.close();

            if(alarmYear.equals("") && alarmMonth.equals("") && alarmDay.equals("")){
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                db.updateDay(day);
                db.updateMonth(month);
                db.updateYear(year);
            }

            setDue = true;

//            killAlarm.setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected void onPause(){

        super.onPause();

    }

    @Override
    protected void onResume() {

        super.onResume();

    }

    @Override
    //Return to main screen when back pressed
    public void onBackPressed() {

        if(!repeat.equals("none")) {
            db.updateRepeatInterval(dbTaskId, repeat);
            db.updateRepeat(dbTaskId, true);

            if(repeat.equals("day")){

                repeatInterval = AlarmManager.INTERVAL_DAY;

                db.updateRepeatInterval(dbTaskId,"day");

                repeating = true;

                taskPropertiesShowing = false;

                Cursor alarmResult = MainActivity.db.getAlarmData
                        (Integer.parseInt(dbTaskId));
                String alarmHour = "";
                String alarmMinute = "";
                String alarmDay = "";
                String alarmMonth = "";
                String alarmYear = "";
                while(alarmResult.moveToNext()){
                    alarmHour = alarmResult.getString(1);
                    alarmMinute = alarmResult.getString(2);
                    alarmDay = alarmResult.getString(4);
                    alarmMonth = alarmResult.getString(5);
                    alarmYear = alarmResult.getString(6);
                }

                alarmResult.close();

                if(alarmYear.equals("") && alarmMonth.equals("") && alarmDay.equals("") && alarmHour.equals("") && alarmMinute.equals("")){
                    Calendar calendar = Calendar.getInstance();
                    int minute = calendar.get(Calendar.MINUTE);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);
                    if(hour != 23) {
                        db.updateHour(hour + 1);
                    }else{
                        db.updateHour(hour);
                    }
                    db.updateMinute(minute);
                    db.updateDay(day);
                    db.updateMonth(month);
                    db.updateYear(year);
                    setDue = true;
                }

            }else if(repeat.equals("week")){

                repeatInterval = (AlarmManager.INTERVAL_DAY * 7);

                db.updateRepeatInterval(dbTaskId, "week");

                repeating = true;

                taskPropertiesShowing = false;

                Cursor alarmResult = MainActivity.db.getAlarmData
                        (Integer.parseInt(dbTaskId));
                String alarmHour = "";
                String alarmMinute = "";
                String alarmDay = "";
                String alarmMonth = "";
                String alarmYear = "";
                while(alarmResult.moveToNext()){
                    alarmHour = alarmResult.getString(1);
                    alarmMinute = alarmResult.getString(2);
                    alarmDay = alarmResult.getString(4);
                    alarmMonth = alarmResult.getString(5);
                    alarmYear = alarmResult.getString(6);
                }

                alarmResult.close();

                if(alarmYear.equals("") && alarmMonth.equals("") && alarmDay.equals("") && alarmHour.equals("") && alarmMinute.equals("")){
                    Calendar calendar = Calendar.getInstance();
                    int minute = calendar.get(Calendar.MINUTE);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);
                    if(hour != 23) {
                        db.updateHour(hour + 1);
                    }else{
                        db.updateHour(hour);
                    }
                    db.updateMinute(minute);
                    db.updateDay(day);
                    db.updateMonth(month);
                    db.updateYear(year);
                    setDue = true;
                }

            }else if(repeat.equals("month")){

                db.updateRepeatInterval(dbTaskId, "month");

                MainActivity.taskPropertiesShowing = false;

                Cursor alarmResult = MainActivity.db.getAlarmData
                        (Integer.parseInt(dbTaskId));
                String alarmHour = "";
                String alarmMinute = "";
                String alarmDay = "";
                String alarmMonth = "";
                String alarmYear = "";
                while(alarmResult.moveToNext()){
                    alarmHour = alarmResult.getString(1);
                    alarmMinute = alarmResult.getString(2);
                    alarmDay = alarmResult.getString(4);
                    alarmMonth = alarmResult.getString(5);
                    alarmYear = alarmResult.getString(6);
                }

                alarmResult.close();

                if(alarmYear.equals("") && alarmMonth.equals("") && alarmDay.equals("") && alarmHour.equals("") && alarmMinute.equals("")){
                    Calendar calendar = Calendar.getInstance();
                    int minute = calendar.get(Calendar.MINUTE);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);
                    if(hour != 23) {
                        db.updateHour(hour + 1);
                    }else{
                        db.updateHour(hour);
                    }
                    db.updateMinute(minute);
                    db.updateDay(day);
                    db.updateMonth(month);
                    db.updateYear(year);
                    setDue = true;
                }

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
