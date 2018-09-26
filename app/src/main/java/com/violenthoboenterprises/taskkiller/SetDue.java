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
    static ImageView time;
    static ImageView timeFaded;
    static ImageView calendar;
    static ImageView calendarFaded;
    ImageView daily;
    ImageView weekly;
    ImageView monthly;
    ImageView cancelRepeat;
    View pickerRoot;
    TextView dateTextView;
    TextView timeTextView;
    String repeat;
    static boolean setDue;
    static String dbTaskId;
    String dbTask;
    static String dbDueTime;
    MenuItem killAlarm;
    static boolean datePicked;
    static boolean timePicked;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.due_picker);
        overridePendingTransition( R.anim.enter_from_left, R.anim.enter_from_left);
        dueToolbar = findViewById(R.id.dueToolbar);
        setSupportActionBar(dueToolbar);

        TAG = "SetDue";
        repeat = "none";
        setDue = false;
        datePicked = false;
        timePicked = false;

        time = findViewById(R.id.time);
        timeFaded = findViewById(R.id.timeFaded);
        calendar = findViewById(R.id.calendar);
        calendarFaded = findViewById(R.id.calendarFaded);
        dateButton = findViewById(R.id.dateBtn);
        timeButton = findViewById(R.id.timeBtn);
        pickerRoot = findViewById(R.id.pickerRoot);
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        daily = findViewById(R.id.daily);
        weekly = findViewById(R.id.weekly);
        monthly = findViewById(R.id.monthly);
        cancelRepeat = findViewById(R.id.cancelRepeat);

        dueToolbar.setTitleTextColor(Color.parseColor("#AAAAAA"));
        cancelRepeat.setBackgroundColor(Color.parseColor("#AAAAAA"));
        daily.setBackgroundColor(Color.parseColor("#AAAAAA"));
        weekly.setBackgroundColor(Color.parseColor("#AAAAAA"));
        monthly.setBackgroundColor(Color.parseColor("#AAAAAA"));

        //getting task data
        dbDueTime = "";
        dbTaskId = "";
        dbTask = "";
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

        //Inform user that they can set an alarm
        if(dbDueTime.equals("0")){
            dateTextView.setText("Add due date");
            timeTextView.setText("Add due time");
        //Showing existing due date and time
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

            String formattedMonth = "";

            int intMonth = Integer.valueOf(alarmMonth) + 1;
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

            dateTextView.setText(alarmDay + " " + formattedMonth + " " + alarmYear);

            String adjustedAmPm = "am";
            String adjustedHour = String.valueOf(alarmHour);
            String adjustedMinute = String.valueOf(alarmMinute);

            if(Integer.parseInt(alarmHour) == 0) {
                adjustedHour = String.valueOf(12);
            }else if(Integer.parseInt(alarmHour) == 12){
                adjustedAmPm = "pm";
            }else if(Integer.parseInt(alarmHour) > 12){
                adjustedHour = String.valueOf(Integer.parseInt(alarmHour) - 12);
                adjustedAmPm = "pm";
            }

            if(Integer.parseInt(alarmMinute) < 10){
                adjustedMinute = "0" + alarmMinute;
            }

            timeTextView.setText(adjustedHour + ":" + adjustedMinute + adjustedAmPm);

            calendarFaded.setVisibility(View.GONE);
            calendar.setVisibility(View.VISIBLE);
            timeFaded.setVisibility(View.GONE);
            time.setVisibility(View.VISIBLE);
            datePicked = true;
            timePicked = true;
            dateTextView.setTextSize(25);
            timeTextView.setTextSize(25);

        }

        //Highlight the repeat type or highlight No Repeat if none exists
        if(!dbRepeat){
            cancelRepeat.setBackgroundColor(Color.parseColor(highlight));
        }else if(dbRepeatInterval.equals("day")){
            daily.setBackgroundColor(Color.parseColor(highlight));
        }else if(dbRepeatInterval.equals("week")){
            weekly.setBackgroundColor(Color.parseColor(highlight));
        }else if(dbRepeatInterval.equals("month")){
            monthly.setBackgroundColor(Color.parseColor(highlight));
        }

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

        //Actions to occur when user selects to set/change date
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

        //Actions to occur when user selects to set/change time
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

        //Actions to occur if user selects to set a daily recurring alarm
        daily.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(!mute){
                    blip.start();
                }

                vibrate.vibrate(50);

                //Show user which button they selected by highlighting it
                cancelRepeat.setBackgroundColor(Color.parseColor("#AAAAAA"));
                daily.setBackgroundColor(Color.parseColor(highlight));
                weekly.setBackgroundColor(Color.parseColor("#AAAAAA"));
                monthly.setBackgroundColor(Color.parseColor("#AAAAAA"));

//                dateRowShowing =true;

                repeatInterval = AlarmManager.INTERVAL_DAY;

                repeat = "day";

                repeating =true;

                taskPropertiesShowing =false;

            }

        });

        //Actions to occur if user selects to set a weekly recurring alarm
        weekly.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(!mute){
                    blip.start();
                }

                vibrate.vibrate(50);

                //Show user which button they selected by highlighting it
                cancelRepeat.setBackgroundColor(Color.parseColor("#AAAAAA"));
                daily.setBackgroundColor(Color.parseColor("#AAAAAA"));
                weekly.setBackgroundColor(Color.parseColor(highlight));
                monthly.setBackgroundColor(Color.parseColor("#AAAAAA"));

//                dateRowShowing =true;

                repeatInterval = AlarmManager.INTERVAL_DAY;

                repeat = "week";

                repeating =true;

                taskPropertiesShowing =false;

            }

        });

        //Actions to occur if user selects to set a monthly  recurring alarm
        monthly.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(!mute){
                    blip.start();
                }

                vibrate.vibrate(50);

                //Show user which button they selected by highlighting it
                cancelRepeat.setBackgroundColor(Color.parseColor("#AAAAAA"));
                daily.setBackgroundColor(Color.parseColor("#AAAAAA"));
                weekly.setBackgroundColor(Color.parseColor("#AAAAAA"));
                monthly.setBackgroundColor(Color.parseColor(highlight));

//                dateRowShowing =true;

                repeatInterval = AlarmManager.INTERVAL_DAY;

                repeat = "month";

                repeating =true;

                taskPropertiesShowing =false;

            }

        });

        //Actions to occur when user chooses to cancel the repeat
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

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!menu.hasVisibleItems()) {
            getMenuInflater().inflate(R.menu.menu_alarm, menu);
            killAlarm = menu.findItem(R.id.killAlarmItem);
            this.setTitle(dbTask);
            return true;
        }else {
            killAlarm.setEnabled(true);
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //Resetting alarm to off
        //TODO find out if return statements are necessary
        //noinspection SimplifiableIfStatement
        if ((id == R.id.killAlarmItem) && (timePicked || datePicked || !repeat.equals("none"))) {

            if(!mute){
                trash.start();
            }

            vibrate.vibrate(50);

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

            db.updateRepeatInterval(dbTaskId, "");

            db.updateRepeat(dbTaskId, false);

            pendIntent = PendingIntent.getBroadcast(getApplicationContext(),
                    Integer.valueOf(dbTaskId),
                    alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.cancel(MainActivity.pendIntent);

            db.updateAlarmData(dbTaskId,
                    "", "", "", "", "", "");

            setDue = false;
            datePicked = false;
            timePicked = false;

            time.setVisibility(View.GONE);
            timeFaded.setVisibility(View.VISIBLE);
            calendar.setVisibility(View.GONE);
            calendarFaded.setVisibility(View.VISIBLE);

            dateTextView.setText("Add due date");
            timeTextView.setText("Add due time");
            dateTextView.setTextSize(15);
            timeTextView.setTextSize(15);

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

            //Set default values of date picker to current date
            final Calendar calendar = Calendar.getInstance();
            int year;
            int month;
            int day;

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

            //getting universal data
            Cursor uniResult = MainActivity.db.getUniversalData();
            int uniYear = 0;
            int uniMonth = 0;
            int uniDay = 0;
            while(uniResult.moveToNext()){
                uniYear = uniResult.getInt(11);
                uniMonth = uniResult.getInt(12);
                uniDay = uniResult.getInt(13);
            }
            uniResult.close();

            if(datePicked){
                year = uniYear;
                month = uniMonth;
                day = uniDay;
            }else if(!alarmDay.equals("") && !alarmMonth.equals("") && !alarmYear.equals("")){
                year = Integer.parseInt(alarmYear);
                month = Integer.parseInt(alarmMonth);
                day = Integer.parseInt(alarmDay);
            }else{
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
            }

            DatePickerDialog datePickerDialog;

            //Initialise date picker based on light or dark mode
            if(!lightDark) {
                datePickerDialog = new DatePickerDialog(getActivity(),
                        AlertDialog.THEME_DEVICE_DEFAULT_DARK, this, year, month, day);
            }else{
                datePickerDialog = new DatePickerDialog(getActivity(),
                        AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, this, year, month, day);
            }

            //Make so all previous dates are inactive. User shouldn't be able to set due date to in the past
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day){

            TextView dateTextView = getActivity().findViewById(R.id.dateTextView);

            //Format and display chosen date
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

            dateTextView.setText(day + " " + formattedMonth + " " + year);

            vibrate.vibrate(50);

            if(!mute){
                 blip.start();
            }

            //Updating the database
            db.updateYear(year);
            db.updateMonth(month);
            db.updateDay(day);

            //Set default time values if user not selected time values already
            if(!timePicked){
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
            datePicked = true;
            calendarFaded.setVisibility(View.GONE);
            calendar.setVisibility(View.VISIBLE);

            dateTextView.setTextSize(25);

        }

    }

    public static class TimePickerDialogFrag extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){

            //Setting default time picker values to current time
//            final Calendar calendar = Calendar.getInstance();
//            int hour = calendar.get(Calendar.HOUR_OF_DAY);
//            int minute = calendar.get(Calendar.MINUTE);

            //Set default values of date picker to current date
            final Calendar calendar = Calendar.getInstance();
            int hour;
            int minute;

            Cursor alarmResult = MainActivity.db.getAlarmData
                    (Integer.parseInt(dbTaskId));
            String alarmHour = "";
            String alarmMinute = "";
            while(alarmResult.moveToNext()){
                alarmHour = alarmResult.getString(1);
                alarmMinute = alarmResult.getString(2);
            }

            alarmResult.close();

            //getting universal data
            Cursor uniResult = MainActivity.db.getUniversalData();
            int uniHour = 0;
            int uniMinute = 0;
            while(uniResult.moveToNext()){
                uniHour = uniResult.getInt(14);
                uniMinute = uniResult.getInt(15);
            }
            uniResult.close();

            if(timePicked){
                minute = uniMinute;
                hour = uniHour;
            }else if(!alarmHour.equals("") && !alarmMinute.equals("")){
                minute = Integer.parseInt(alarmMinute);
                hour = Integer.parseInt(alarmHour);
            }else{
                minute = calendar.get(Calendar.MINUTE);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
            }

            TimePickerDialog timePickerDialog;

            //Initialising time picker based on light or dark
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

            TextView timeTextView = getActivity().findViewById(R.id.timeTextView);

            //Formatting and displaying selected time
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

            timeTextView.setText(adjustedHour + ":" + adjustedMinute + adjustedAmPm);

            MainActivity.vibrate.vibrate(50);

            if(!MainActivity.mute){
                MainActivity.blip.start();
            }

            //Updating database
            db.updateHour(hour);
            db.updateMinute(minute);

            //set default date values if user not already selected
            if(!datePicked){
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                db.updateDay(day);
                db.updateMonth(month);
                db.updateYear(year);
            }

            setDue = true;
            timePicked = true;
            timeFaded.setVisibility(View.GONE);
            time.setVisibility(View.VISIBLE);

            timeTextView.setTextSize(25);

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

        //Determine if repeat needs to be set
        if(!repeat.equals("none")) {
            db.updateRepeatInterval(dbTaskId, repeat);
            db.updateRepeat(dbTaskId, true);

            if(repeat.equals("day")){

                repeatInterval = AlarmManager.INTERVAL_DAY;

                db.updateRepeatInterval(dbTaskId,"day");

                repeating = true;

                taskPropertiesShowing = false;

                //set default date values if user not already selected
                if(!datePicked){
                    Calendar calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);
                    db.updateDay(day);
                    db.updateMonth(month);
                    db.updateYear(year);
                }

                //Set default time values if user not selected time values already
                if(!timePicked){
                    Calendar calendar = Calendar.getInstance();
                    int minute = calendar.get(Calendar.MINUTE);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    if(hour > 10) {
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
                datePicked = true;
                timePicked = true;

            }else if(repeat.equals("week")){

                repeatInterval = (AlarmManager.INTERVAL_DAY * 7);

                db.updateRepeatInterval(dbTaskId, "week");

                repeating = true;

                taskPropertiesShowing = false;

                //set default date values if user not already selected
                if(!datePicked){
                    Calendar calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);
                    db.updateDay(day);
                    db.updateMonth(month);
                    db.updateYear(year);
                }

                //Set default time values if user not selected time values already
                if(!timePicked){
                    Calendar calendar = Calendar.getInstance();
                    int minute = calendar.get(Calendar.MINUTE);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    if(hour > 10) {
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
                datePicked = true;
                timePicked = true;

            }else if(repeat.equals("month")){

                db.updateRepeatInterval(dbTaskId, "month");

                MainActivity.taskPropertiesShowing = false;

                //set default date values if user not already selected
                if(!datePicked){
                    Calendar calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);
                    db.updateDay(day);
                    db.updateMonth(month);
                    db.updateYear(year);
                }

                //Set default time values if user not selected time values already
                if(!timePicked){
                    Calendar calendar = Calendar.getInstance();
                    int minute = calendar.get(Calendar.MINUTE);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    if(hour > 10) {
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
                datePicked = true;
                timePicked = true;

            }

        }

        //updating the alarm in myAdapter
        if(setDue) {
            db.updateSetAlarm(true);
        }

        //return to mainActivity
        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

    }

}
