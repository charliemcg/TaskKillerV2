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
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
import java.util.Locale;

public class SetDue extends MainActivity {

    static String TAG;
    private Toolbar dueToolbar;
    LinearLayout dateButton;
    LinearLayout timeButton;
    static LinearLayout darkRepeat;
    LinearLayout lightRepeat;
    static ImageView time, timeFadedDark, timeFadedLight, calendar, calendarFadedDark, calendarFadedLight;
    ImageView dailyDark, weeklyDark, monthlyDark, cancelRepeatDark,
            dailyLight, weeklyLight, monthlyLight, cancelRepeatLight;
    View pickerRoot;
    TextView dateTextView;
    TextView timeTextView;
    TextView divOne;
    TextView divTwo;
    static TextView divThree;
    String repeat;
    static boolean setDue;
    static String dbTaskId;
    String dbTask;
    static String dbDueTime;
    static MenuItem killAlarm, trashAlarmOpen;
    static boolean datePicked, timePicked;
    static int screenSize;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.due_picker);
//        }else{
//            setContentView(R.layout.due_picker);
//        }
        //TODO are these supposed to be the same animation
        overridePendingTransition( R.anim.enter_from_left, R.anim.enter_from_left);
        dueToolbar = findViewById(R.id.dueToolbar);
        setSupportActionBar(dueToolbar);

        TAG = "SetDue";
        repeat = "none";
        setDue = false;
        datePicked = false;
        timePicked = false;

        time = findViewById(R.id.time);
        timeFadedLight = findViewById(R.id.timeFadedLight);
        timeFadedDark = findViewById(R.id.timeFadedDark);
        calendar = findViewById(R.id.calendar);
        calendarFadedLight = findViewById(R.id.calendarFadedLight);
        calendarFadedDark = findViewById(R.id.calendarFadedDark);
        dateButton = findViewById(R.id.dateBtn);
        timeButton = findViewById(R.id.timeBtn);
        darkRepeat = findViewById(R.id.darkRepeatLayout);
        lightRepeat = findViewById(R.id.lightRepeatLayout);
        pickerRoot = findViewById(R.id.pickerRoot);
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        dailyDark = findViewById(R.id.dailyDark);
        weeklyDark = findViewById(R.id.weeklyDark);
        monthlyDark = findViewById(R.id.monthlyDark);
        cancelRepeatDark = findViewById(R.id.cancelRepeatDark);
        dailyLight = findViewById(R.id.dailyLight);
        weeklyLight = findViewById(R.id.weeklyLight);
        monthlyLight = findViewById(R.id.monthlyLight);
        cancelRepeatLight = findViewById(R.id.cancelRepeatLight);
//        divOne = findViewById(R.id.divOne);
        divTwo = findViewById(R.id.divTwo);
        divThree = findViewById(R.id.divThree);

        screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;


//        divOne.setBackgroundColor(Color.parseColor(highlight));
        divTwo.setBackgroundColor(Color.parseColor(highlight));
        divThree.setBackgroundColor(Color.parseColor(highlight));

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

        onCreateOptionsMenu(dueToolbar.getMenu());

//        dueToolbar.setSubtitle(dbTask);
//        getSupportActionBar().setTitle("Set Dat/Time");
//        dueToolbar.setTitle("Set Date/Time");

        //Inform user that they can set an alarm
        if(dbDueTime.equals("0")){
            dateTextView.setText(R.string.addDate);
            timeTextView.setText(R.string.addTime);

            if(lightDark){
                calendarFadedLight.setVisibility(View.VISIBLE);
                timeFadedLight.setVisibility(View.VISIBLE);
            }else{
                calendarFadedDark.setVisibility(View.VISIBLE);
                timeFadedDark.setVisibility(View.VISIBLE);
            }

        //Showing existing due date and time
        }else{
            //getting alarm data
            Cursor alarmResult = MainActivity.db.getAlarmData
                    (Integer.parseInt(dbTaskId));
            String alarmHour = "";
            String alarmMinute = "";
            String alarmAmPm = "";
            String alarmDay = "";
            String alarmMonth = "";
            String alarmYear = "";
            while(alarmResult.moveToNext()){
                alarmHour = alarmResult.getString(1);
                alarmMinute = alarmResult.getString(2);
                alarmAmPm = alarmResult.getString(3);
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

            String lang = String.valueOf(Locale.getDefault());
            if(lang.equals("en_AS") || lang.equals("en_BM")
                    || lang.equals("en_CA") || lang.equals("en_GU")
                    || lang.equals("en_PH")
                    || lang.equals("en_PR") || lang.equals("en_UM")
                    || lang.equals("en_US") || lang.equals("en_VI")) {
                dateTextView.setText(formattedMonth + " " + alarmDay);
            }else{
                dateTextView.setText(alarmDay + " " + formattedMonth);
            }

            String adjustedAmPm = String.valueOf(alarmAmPm)/*getString(R.string.am)*/;
            String adjustedHour = String.valueOf(alarmHour);
            String adjustedMinute = String.valueOf(alarmMinute);

            if(Integer.parseInt(alarmHour) == 0) {
                adjustedHour = String.valueOf(12);
            }else if(Integer.parseInt(alarmHour) == 12){
//                adjustedAmPm = getString(R.string.pm);
            }else if(Integer.parseInt(alarmHour) > 12){
                adjustedHour = String.valueOf(Integer.parseInt(alarmHour) - 12);
//                adjustedAmPm = getString(R.string.pm);
            }

            if(Integer.parseInt(alarmMinute) < 10){
                adjustedMinute = "0" + alarmMinute;
            }

            if(adjustedAmPm.equals("0")){
                adjustedAmPm = "am";
            }else{
                adjustedAmPm = "pm";
            }

            timeTextView.setText(adjustedHour + ":" + adjustedMinute + adjustedAmPm);

            calendar.setVisibility(View.VISIBLE);
            time.setVisibility(View.VISIBLE);
            if(lightDark){
                calendarFadedLight.setVisibility(View.GONE);
                timeFadedLight.setVisibility(View.GONE);
            }else{
                calendarFadedDark.setVisibility(View.GONE);
                timeFadedDark.setVisibility(View.GONE);
            }
            datePicked = true;
            timePicked = true;
            if(screenSize == 3){
                dateTextView.setTextSize(65);
                timeTextView.setTextSize(65);
            }else if(screenSize == 4){
                dateTextView.setTextSize(85);
                timeTextView.setTextSize(85);
            }else {
                dateTextView.setTextSize(25);
                timeTextView.setTextSize(25);
            }

        }

        if(!lightDark){
            dueToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
            dueToolbar.setSubtitleTextColor(Color.parseColor("#AAAAAA"));
            pickerRoot.setBackgroundColor(Color.parseColor("#333333"));
            dueToolbar.setBackgroundColor(Color.parseColor("#333333"));
            dateTextView.setTextColor(Color.parseColor("#AAAAAA"));
            timeTextView.setTextColor(Color.parseColor("#AAAAAA"));
            cancelRepeatDark.setBackgroundColor(Color.parseColor("#AAAAAA"));
            dailyDark.setBackgroundColor(Color.parseColor("#AAAAAA"));
            weeklyDark.setBackgroundColor(Color.parseColor("#AAAAAA"));
            monthlyDark.setBackgroundColor(Color.parseColor("#AAAAAA"));
            darkRepeat.setVisibility(View.VISIBLE);
//            calendarFadedLight.setVisibility(View.GONE);
//            timeFadedLight.setVisibility(View.GONE);
        }else{
            dueToolbar.setTitleTextColor(Color.parseColor("#000000"));
            dueToolbar.setSubtitleTextColor(Color.parseColor("#666666"));
            pickerRoot.setBackgroundColor(Color.parseColor("#FFFFFF"));
            dueToolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
            dateTextView.setTextColor(Color.parseColor("#000000"));
            timeTextView.setTextColor(Color.parseColor("#000000"));
            cancelRepeatLight.setBackgroundColor(Color.parseColor("#000000"));
            dailyLight.setBackgroundColor(Color.parseColor("#000000"));
            weeklyLight.setBackgroundColor(Color.parseColor("#000000"));
            monthlyLight.setBackgroundColor(Color.parseColor("#000000"));
            lightRepeat.setVisibility(View.VISIBLE);
//            calendarFadedLight.setVisibility(View.GONE);
//            timeFadedLight.setVisibility(View.GONE);
        }

        //Highlight the repeat type or highlight No Repeat if none exists
        if(!dbRepeat){
            cancelRepeatDark.setBackgroundColor(Color.parseColor(highlight));
            cancelRepeatLight.setBackgroundColor(Color.parseColor(highlight));
        }else if(dbRepeatInterval.equals("day")){
            dailyDark.setBackgroundColor(Color.parseColor(highlight));
            dailyLight.setBackgroundColor(Color.parseColor(highlight));
        }else if(dbRepeatInterval.equals("week")){
            weeklyDark.setBackgroundColor(Color.parseColor(highlight));
            weeklyLight.setBackgroundColor(Color.parseColor(highlight));
        }else if(dbRepeatInterval.equals("month")){
            monthlyDark.setBackgroundColor(Color.parseColor(highlight));
            monthlyLight.setBackgroundColor(Color.parseColor(highlight));
        }

        //Actions to occur when user selects to set/change date
        dateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                if(!mute){
//                    blip.start();
//                }

                vibrate.vibrate(50);

                DialogFragment dialogfragment = new DatePickerDialogFrag();

                dialogfragment.show(getFragmentManager(), "Date");

            }

        });

        //Actions to occur when user selects to set/change time
        timeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                if(!mute){
//                    blip.start();
//                }

                vibrate.vibrate(50);

                DialogFragment dialogfragment = new TimePickerDialogFrag();

                dialogfragment.show(getFragmentManager(), "Time");

            }

        });

        //Actions to occur if user selects to set a daily recurring alarm
        dailyDark.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                if(!mute){
//                    blip.start();
//                }


                if(!mute && !repeat.equals("day")){
                    blip.start();
                }


                vibrate.vibrate(50);

                //Show user which button they selected by highlighting it
                cancelRepeatDark.setBackgroundColor(Color.parseColor("#AAAAAA"));
                dailyDark.setBackgroundColor(Color.parseColor(highlight));
                weeklyDark.setBackgroundColor(Color.parseColor("#AAAAAA"));
                monthlyDark.setBackgroundColor(Color.parseColor("#AAAAAA"));

                repeatInterval = AlarmManager.INTERVAL_DAY;//TODO might not need this variable any more
                db.updateRepeatIntervalTemp(String.valueOf(AlarmManager.INTERVAL_DAY));

                repeat = "day";

                repeating = true;

                setDue = true;

                killAlarm.setVisible(true);
//                dueToolbar.getMenu().getItem(0).setVisible(true);

            }

        });

        //Actions to occur if user selects to set a daily recurring alarm
        dailyLight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                if(!mute){
//                    blip.start();
//                }

                if(!mute && !repeat.equals("day")){
                    blip.start();
                }

                vibrate.vibrate(50);

                //Show user which button they selected by highlighting it
                cancelRepeatLight.setBackgroundColor(Color.parseColor("#000000"));
                dailyLight.setBackgroundColor(Color.parseColor(highlight));
                weeklyLight.setBackgroundColor(Color.parseColor("#000000"));
                monthlyLight.setBackgroundColor(Color.parseColor("#000000"));

                repeatInterval = AlarmManager.INTERVAL_DAY;
                db.updateRepeatIntervalTemp(String.valueOf(AlarmManager.INTERVAL_DAY));

                repeat = "day";

                repeating =true;

                setDue = true;

                killAlarm.setVisible(true);
//                dueToolbar.getMenu().getItem(0).setVisible(true);

            }

        });

        //Actions to occur if user selects to set a weekly recurring alarm
        weeklyDark.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                if(!mute){
//                    blip.start();
//                }

                if(!mute && !repeat.equals("week")){
                    blip.start();
                }

                vibrate.vibrate(50);

                //Show user which button they selected by highlighting it
                cancelRepeatDark.setBackgroundColor(Color.parseColor("#AAAAAA"));
                dailyDark.setBackgroundColor(Color.parseColor("#AAAAAA"));
                weeklyDark.setBackgroundColor(Color.parseColor(highlight));
                monthlyDark.setBackgroundColor(Color.parseColor("#AAAAAA"));

                repeatInterval = AlarmManager.INTERVAL_DAY * 7;

                repeat = "week";

                repeating =true;

                setDue = true;

                killAlarm.setVisible(true);
//                dueToolbar.getMenu().getItem(0).setVisible(true);

            }

        });

        //Actions to occur if user selects to set a weekly recurring alarm
        weeklyLight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                if(!mute){
//                    blip.start();
//                }

                if(!mute && !repeat.equals("week")){
                    blip.start();
                }

                vibrate.vibrate(50);

                //Show user which button they selected by highlighting it
                cancelRepeatLight.setBackgroundColor(Color.parseColor("#000000"));
                dailyLight.setBackgroundColor(Color.parseColor("#000000"));
                weeklyLight.setBackgroundColor(Color.parseColor(highlight));
                monthlyLight.setBackgroundColor(Color.parseColor("#000000"));

                repeatInterval = AlarmManager.INTERVAL_DAY * 7;

                repeat = "week";

                repeating =true;

                setDue = true;

                killAlarm.setVisible(true);
//                dueToolbar.getMenu().getItem(0).setVisible(true);

            }

        });

        //Actions to occur if user selects to set a monthly  recurring alarm
        monthlyDark.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                if(!mute){
//                    blip.start();
//                }

                if(!mute && !repeat.equals("month")){
                    blip.start();
                }

                vibrate.vibrate(50);

                //Show user which button they selected by highlighting it
                cancelRepeatDark.setBackgroundColor(Color.parseColor("#AAAAAA"));
                dailyDark.setBackgroundColor(Color.parseColor("#AAAAAA"));
                weeklyDark.setBackgroundColor(Color.parseColor("#AAAAAA"));
                monthlyDark.setBackgroundColor(Color.parseColor(highlight));

//                repeatInterval = AlarmManager.INTERVAL_DAY;//TODO adjust for months

                repeat = "month";

                repeating =true;

                setDue = true;

                killAlarm.setVisible(true);
//                dueToolbar.getMenu().getItem(0).setVisible(true);

            }

        });

        //Actions to occur if user selects to set a monthly  recurring alarm
        monthlyLight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                if(!mute){
//                    blip.start();
//                }

                if(!mute && !repeat.equals("month")){
                    blip.start();
                }

                vibrate.vibrate(50);

                //Show user which button they selected by highlighting it
                cancelRepeatLight.setBackgroundColor(Color.parseColor("#000000"));
                dailyLight.setBackgroundColor(Color.parseColor("#000000"));
                weeklyLight.setBackgroundColor(Color.parseColor("#000000"));
                monthlyLight.setBackgroundColor(Color.parseColor(highlight));

//                repeatInterval = AlarmManager.INTERVAL_DAY;//TODO adjust for months

                repeat = "month";

                repeating =true;

                setDue = true;

                killAlarm.setVisible(true);
//                dueToolbar.getMenu().getItem(0).setVisible(true);

            }

        });

        //Actions to occur when user chooses to cancel the repeat
        cancelRepeatDark.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                if(!mute){
//                    blip.start();
//                }

                if(!mute && !repeat.equals("none")){
                    blip.start();
                }

                vibrate.vibrate(50);

                cancelRepeatDark.setBackgroundColor(Color.parseColor(highlight));
                dailyDark.setBackgroundColor(Color.parseColor("#AAAAAA"));
                weeklyDark.setBackgroundColor(Color.parseColor("#AAAAAA"));
                monthlyDark.setBackgroundColor(Color.parseColor("#AAAAAA"));

                repeat = "none";

                setDue = false;

                if(!timePicked && !datePicked) {
                    killAlarm.setVisible(false);
//                    dueToolbar.getMenu().getItem(0).setVisible(false);
                }

            }

        });

        //Actions to occur when user chooses to cancel the repeat
        cancelRepeatLight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                if(!mute){
//                    blip.start();
//                }

                if(!mute && !repeat.equals("none")){
                    blip.start();
                }

                vibrate.vibrate(50);

                cancelRepeatLight.setBackgroundColor(Color.parseColor(highlight));
                dailyLight.setBackgroundColor(Color.parseColor("#000000"));
                weeklyLight.setBackgroundColor(Color.parseColor("#000000"));
                monthlyLight.setBackgroundColor(Color.parseColor("#000000"));

                repeat = "none";

                setDue = false;

                if(!timePicked && !datePicked) {
                    killAlarm.setVisible(false);
//                    dueToolbar.getMenu().getItem(0).setVisible(false);
                }

            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!menu.hasVisibleItems()) {
            getMenuInflater().inflate(R.menu.menu_alarm, dueToolbar.getMenu());
            killAlarm = this.dueToolbar.getMenu().findItem(R.id.killAlarmItem);
            trashAlarmOpen = this.dueToolbar.getMenu().findItem(R.id.trashAlarmOpen);
            this.dueToolbar.setTitle(R.string.setDateTime);
            this.dueToolbar.setSubtitle(dbTask);
            if(Integer.parseInt(dbDueTime) == 0){
                killAlarm.setVisible(false);
            }else {
                killAlarm.setVisible(true);
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

        //Resetting alarm to off
        //TODO find out if return statements are necessary
        //noinspection SimplifiableIfStatement
        if ((id == R.id.killAlarmItem) && (timePicked || datePicked || !repeat.equals("none"))) {

            final Handler handler = new Handler();

            final Runnable runnable = new Runnable() {
                public void run() {

                    killAlarm.setVisible(false);
                    trashAlarmOpen.setVisible(true);

//                    vibrate.vibrate(50);

                    final Handler handler2 = new Handler();
                    final Runnable runnable2 = new Runnable(){
                        public void run(){
                            killAlarm.setVisible(true);
                            trashAlarmOpen.setVisible(false);
                            final Handler handler3 = new Handler();
                            final Runnable runnable3 = new Runnable() {
                                @Override
                                public void run() {
                                    killAlarm.setVisible(false);

                                    //getting task data
                                    dbTaskId = "";
                                    Cursor dbTaskResult = MainActivity.db.getUniversalData();
                                    while (dbTaskResult.moveToNext()) {
                                        dbTaskId = dbTaskResult.getString(4);
                                    }
                                    dbTaskResult.close();

                                    vibrate.vibrate(50);

                                    if(!mute) {
                                        trash.start();
                                    }

                                    repeat = "none";

                                    repeating = false;

                                    db.updateDue(dbTaskId, false);

                                    db.updateTimestamp(dbTaskId, "0");

                                    db.updateRepeatInterval(dbTaskId, "");

                                    db.updateRepeat(dbTaskId, false);

                                    db.updateOverdue(dbTaskId, false);

                                    db.updateSnooze(dbTaskId, false);

                                    db.updateKilledEarly(dbTaskId, false);

                                    db.updateManualKill(dbTaskId, false);

                                    pendIntent = PendingIntent.getBroadcast(getApplicationContext(),
                                            Integer.valueOf(dbTaskId),
                                            alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                    alarmManager.cancel(MainActivity.pendIntent);

                                    db.updateAlarmData(dbTaskId,
                                            "", "", "", "", "", "");

                                    db.updateSnoozeData(dbTaskId, "", "", "", "", "", "");

                                    setDue = false;
                                    datePicked = false;
                                    timePicked = false;

                                    time.setVisibility(View.GONE);
                                    calendar.setVisibility(View.GONE);
                                    if(lightDark){
                                        calendarFadedLight.setVisibility(View.VISIBLE);
                                        timeFadedLight.setVisibility(View.VISIBLE);
                                    }else{
                                        calendarFadedDark.setVisibility(View.VISIBLE);
                                        timeFadedDark.setVisibility(View.VISIBLE);
                                    }

                                    dateTextView.setText(R.string.addDate);
                                    timeTextView.setText(R.string.addTime);
                                    if(screenSize == 3){
                                        dateTextView.setTextSize(25);
                                        timeTextView.setTextSize(25);
                                    }else if(screenSize == 4){
                                        dateTextView.setTextSize(35);
                                        timeTextView.setTextSize(35);
                                    }else {
                                        dateTextView.setTextSize(15);
                                        timeTextView.setTextSize(15);
                                    }

                                    if(!lightDark) {
                                        cancelRepeatDark.setBackgroundColor(Color.parseColor(highlight));
                                        dailyDark.setBackgroundColor(Color.parseColor("#AAAAAA"));
                                        weeklyDark.setBackgroundColor(Color.parseColor("#AAAAAA"));
                                        monthlyDark.setBackgroundColor(Color.parseColor("#AAAAAA"));
                                    }else{
                                        cancelRepeatLight.setBackgroundColor(Color.parseColor(highlight));
                                        dailyLight.setBackgroundColor(Color.parseColor("#000000"));
                                        weeklyLight.setBackgroundColor(Color.parseColor("#000000"));
                                        monthlyLight.setBackgroundColor(Color.parseColor("#000000"));
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
            /////////////////////////////////////////////////////////////////////////
//            if(!mute){
//                trash.start();
//            }
//
//            vibrate.vibrate(50);



//            killAlarm.setVisible(false);

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

            //If previously picked a date without leaving the activity
            if(datePicked && (uniYear != 0)){
                year = uniYear;
                month = uniMonth;
                day = uniDay;
            //If user previously picked a date but then left the activity
            }else if(!alarmDay.equals("") && !alarmMonth.equals("") && !alarmYear.equals("")){
                year = Integer.parseInt(alarmYear);
                month = Integer.parseInt(alarmMonth);
                day = Integer.parseInt(alarmDay);
            //If no date set
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

            if(!mute){
                blip.start();
            }

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

            String lang = String.valueOf(Locale.getDefault());
            if(lang.equals("en_AS") || lang.equals("en_BM")
                    || lang.equals("en_CA") || lang.equals("en_GU")
                    || lang.equals("en_PH")
                    || lang.equals("en_PR") || lang.equals("en_UM")
                    || lang.equals("en_US") || lang.equals("en_VI")) {
                dateTextView.setText(formattedMonth + " " + day);
            }else{
                dateTextView.setText(day + " " + formattedMonth);
            }

            vibrate.vibrate(50);

//            if(!mute){
//                 blip.start();
//            }

            //Updating the database
            db.updateYear(year);
            db.updateMonth(month);
            db.updateDay(day);
            db.updateOriginalDayTemp(String.valueOf(day));

            //Set default time values if user not selected time values already
            if(!timePicked){
                Calendar calendar = Calendar.getInstance();
                int minute = calendar.get(Calendar.MINUTE);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int ampm = calendar.get(Calendar.AM_PM);
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentYear = calendar.get(Calendar.YEAR);

                if((currentYear == year) && (currentMonth == month) && (currentDay == day)
                        && (hour >= 10)) {
                    if(hour == 23){
                        db.updateHour(11);
                    }else if(hour >= 13) {
                        db.updateHour(hour - 11);
                    }else if(hour == 12){
                        db.updateHour(1);
                    }else{
                        db.updateHour(hour + 1);
                        if (hour == 11) {
                            ampm = 1;
                        }
                    }
                    db.updateAmPm(ampm);
                    db.updateMinute(minute);
                }else if(hour >= 10){
                    if(hour == 23){
                        db.updateHour(11);
                    }else if(hour >= 13) {
                        db.updateHour(hour - 12);
//                    }else if(hour == 12){
//                        db.updateHour(12);
                    }else{
                        db.updateHour(hour);
//                        if (hour == 11) {
//                            ampm = 1;
//                        }
                    }
                    db.updateAmPm(ampm);
                    db.updateMinute(minute);
                }else{
                    db.updateHour(10);
                    db.updateAmPm(ampm);
                    db.updateMinute(0);
                }

                Log.i(TAG, "day: " + day + " hour: " + hour + " minute: " + minute + " ampm: " + ampm);

            }else{
                Cursor alarmResult = MainActivity.db.getAlarmData
                        (Integer.parseInt(dbTaskId));
                String alarmMinute = "";
                String alarmHour = "";
                String alarmAmPm = "";
                while(alarmResult.moveToNext()){
                    alarmHour = alarmResult.getString(1);
                    alarmMinute = alarmResult.getString(2);
                    alarmAmPm = alarmResult.getString(3);
                }
                alarmResult.close();
                if(!alarmMinute.equals("")) {
                    db.updateMinute(Integer.parseInt(alarmMinute));
                    db.updateHour(Integer.parseInt(alarmHour));
                    db.updateAmPm(Integer.parseInt(alarmAmPm));
                }
            }

            setDue = true;
            datePicked = true;
            if(lightDark){
                calendarFadedLight.setVisibility(View.GONE);
            }else{
                calendarFadedDark.setVisibility(View.GONE);
            }
            calendar.setVisibility(View.VISIBLE);

            if(screenSize == 3){
                dateTextView.setTextSize(65);
            }else if(screenSize == 4){
                dateTextView.setTextSize(85);
            }else{
                dateTextView.setTextSize(25);
            }

            killAlarm.setVisible(true);

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
            String alarmAmPm = "";
            while(alarmResult.moveToNext()){
                alarmHour = alarmResult.getString(1);
                alarmMinute = alarmResult.getString(2);
                alarmAmPm = alarmResult.getString(3);
            }

            alarmResult.close();

            //getting universal data
            Cursor uniResult = MainActivity.db.getUniversalData();
            int uniHour = 0;
            int uniMinute = 0;
            int uniAmPm = 0;
            while(uniResult.moveToNext()){
                uniHour = uniResult.getInt(14);
                uniMinute = uniResult.getInt(15);
                uniAmPm = uniResult.getInt(17);
            }
            uniResult.close();

//            int twentyFourHour = hour;

            int defaultTimePickerHour;

            if(timePicked && (uniHour != 0)){
                minute = uniMinute;
                hour = uniHour;
                if(uniAmPm == 1){
                    hour += 12;
                    defaultTimePickerHour = hour;
                }else if(uniAmPm == 0 && hour == 12){
                    defaultTimePickerHour = 0;
                }else{
                    defaultTimePickerHour = hour;
                }
            }else if(!alarmHour.equals("") && !alarmMinute.equals("")){
                minute = Integer.parseInt(alarmMinute);
                hour = Integer.parseInt(alarmHour);
                if(alarmAmPm.equals("1")){
                    hour += 12;
                    defaultTimePickerHour = hour;
                }else if (alarmAmPm.equals("0") && hour == 12){
                    defaultTimePickerHour = 0;
                }else{
                    defaultTimePickerHour = hour;
                }
            }else{
                minute = calendar.get(Calendar.MINUTE);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                defaultTimePickerHour = hour;
            }

            TimePickerDialog timePickerDialog;

            //Initialising time picker based on light or dark
            if(!lightDark) {
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    timePickerDialog = new TimePickerDialog(/*getActivity()*/this.getActivity(),
                            AlertDialog.THEME_HOLO_DARK, this, defaultTimePickerHour, minute, false);
                }else{
                    timePickerDialog = new TimePickerDialog(/*getActivity()*/this.getActivity(),
                            AlertDialog.THEME_DEVICE_DEFAULT_DARK, this, defaultTimePickerHour, minute, false);
                }
            }else{
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    timePickerDialog = new TimePickerDialog(/*getActivity()*/this.getActivity(),
                            AlertDialog.THEME_HOLO_LIGHT, this, defaultTimePickerHour, minute, false);
                }else {
                    timePickerDialog = new TimePickerDialog(getActivity(),
                            AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, this, defaultTimePickerHour, minute, false);
                }
            }

            return timePickerDialog;

        }

        public void onTimeSet(TimePicker view, int hour, int minute){

            TextView timeTextView = getActivity().findViewById(R.id.timeTextView);

            //Formatting and displaying selected time
            String adjustedAmPm = /*String.valueOf(R.string.am)*/"am";
            String adjustedHour = String.valueOf(hour);
            String adjustedMinute/* = String.valueOf(minute)*/;

            if(!mute){
                blip.start();
            }

            if(hour == 0) {
                adjustedHour = String.valueOf(12);
                db.updateAmPm(0);
            }else if(hour == 12){
                adjustedAmPm = /*String.valueOf(R.string.pm)*/"pm";
                db.updateAmPm(1);
            }else if(hour > 12){
                adjustedHour = String.valueOf(hour - 12);
                adjustedAmPm = /*String.valueOf(R.string.pm)*/"pm";
                db.updateAmPm(1);
            }else{
                adjustedHour = String.valueOf(hour);
                db.updateAmPm(0);
            }

            if(minute < 10){
                adjustedMinute = ":0" + minute;
            }else{
                adjustedMinute = ":" + minute;
            }

            timeTextView.setText(adjustedHour + /*":" +*/adjustedMinute + adjustedAmPm);

            MainActivity.vibrate.vibrate(50);

//            if(!MainActivity.mute){
//                MainActivity.blip.start();
//            }

            //Updating database
            db.updateHour(Integer.parseInt(adjustedHour));
            db.updateMinute(minute);

            //set default date values if user not already selected
            if(!datePicked){
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                db.updateDay(day);
                db.updateOriginalDayTemp(String.valueOf(day));
                db.updateMonth(month);
                db.updateYear(year);
//                if(adjustedAmPm.equals("am")) {
//                    db.updateAmPm(0);
//                }else{
//                    db.updateAmPm(1);
//                }
            }else{
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
                Cursor dbTaskResult = db.getData(Integer.parseInt(dbTaskId));
                String dbOriginalDay = "";
                while (dbTaskResult.moveToNext()) {
                    dbOriginalDay = dbTaskResult.getString(20);
                }
                dbTaskResult.close();
                if(!alarmDay.equals("")) {
                    db.updateDay(Integer.parseInt(alarmDay));
                    db.updateOriginalDayTemp(String.valueOf(dbOriginalDay));
                    db.updateMonth(Integer.parseInt(alarmMonth));
                    db.updateYear(Integer.parseInt(alarmYear));
                }
            }

            setDue = true;
            timePicked = true;
            if(lightDark) {
                timeFadedLight.setVisibility(View.GONE);
            }else{
                timeFadedDark.setVisibility(View.GONE);
            }
            time.setVisibility(View.VISIBLE);

            int screenSize = getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK;

            if(screenSize == 3){
                timeTextView.setTextSize(65);
            }else if(screenSize == 4){
                timeTextView.setTextSize(85);
            }else {
                timeTextView.setTextSize(25);
            }

            killAlarm.setVisible(true);

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

        //updating the alarm in myAdapter
        if(setDue) {
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
                        db.updateOriginalDayTemp(String.valueOf(day));
                        db.updateMonth(month);
                        db.updateYear(year);
                    }else{
                        getDateFromDB();
                    }

                    //Set default time values if user not selected time values already
                    if(!timePicked){

//                        Calendar calendar = Calendar.getInstance();
//                        int minute = calendar.get(Calendar.MINUTE);
//                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//                        int ampm = calendar.get(Calendar.AM_PM);
//
//                        if(hour >= 10) {
//                            if(hour != 23) {
//                                db.updateHour(hour + 1);
//                            }else{
//                                db.updateHour(hour);
//                            }
//                            db.updateMinute(minute);
//                            db.updateAmPm(ampm);
//                        }else{
//                            db.updateHour(10);
//                            db.updateAmPm(ampm);
//                            db.updateMinute(0);
//                        }
                        Calendar calendar = Calendar.getInstance();
                        int minute = calendar.get(Calendar.MINUTE);
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int ampm = calendar.get(Calendar.AM_PM);
                        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                        int currentMonth = calendar.get(Calendar.MONTH);
                        int currentYear = calendar.get(Calendar.YEAR);

//                        if((currentYear == year) && (currentMonth == month) && (currentDay == day)
//                                && (hour >= 10)) {
                            if(hour == 23){
                                db.updateHour(11);
                            }else if(hour >= 13) {
                                db.updateHour(hour - 11);
                            }else if(hour == 12){
                                db.updateHour(1);
                            }else{
                                db.updateHour(hour + 1);
                                if (hour == 11) {
                                    ampm = 1;
                                }
                            }
                            db.updateAmPm(ampm);
                            db.updateMinute(minute);
//                        }else{
//                            db.updateHour(10);
//                            db.updateAmPm(ampm);
//                            db.updateMinute(0);
//                        }

                    }else{

                        getTimeFromDB();
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
                        db.updateOriginalDayTemp(String.valueOf(day));
                        db.updateMonth(month);
                        db.updateYear(year);
                    }else{
                        getDateFromDB();
                    }

                    //Set default time values if user not selected time values already
//                    if(!timePicked){
//                        Calendar calendar = Calendar.getInstance();
//                        int minute = calendar.get(Calendar.MINUTE);
//                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//                        int ampm = calendar.get(Calendar.AM_PM);
//                        if(hour >= 10) {
//                            if(hour != 23) {
//                                db.updateHour(hour + 1);
//                            }else{
//                                db.updateHour(hour);
//                            }
//                            db.updateAmPm(ampm);
//                            db.updateMinute(minute);
//                        }else{
//                            db.updateHour(10);
//                            db.updateAmPm(ampm);
//                            db.updateMinute(0);
//                        }
//                    }else{
//                        getTimeFromDB();
//                    }

                    if(!timePicked){

                        Calendar calendar = Calendar.getInstance();
                        int minute = calendar.get(Calendar.MINUTE);
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int ampm = calendar.get(Calendar.AM_PM);

                        if(hour == 23){
                            db.updateHour(11);
                        }else if(hour >= 13) {
                            db.updateHour(hour - 11);
                        }else if(hour == 12){
                            db.updateHour(1);
                        }else{
                            db.updateHour(hour + 1);
                            if (hour == 11) {
                                ampm = 1;
                            }
                        }
                        db.updateAmPm(ampm);
                        db.updateMinute(minute);
                    }else{

                        getTimeFromDB();
                    }

                    setDue = true;
                    datePicked = true;
                    timePicked = true;

                }else if(repeat.equals("month")){

                    db.updateRepeatInterval(dbTaskId, "month");

                    repeating = true;

                    taskPropertiesShowing = false;

                    //set default date values if user not already selected
                    if(!datePicked){
                        Calendar calendar = Calendar.getInstance();
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int month = calendar.get(Calendar.MONTH);
                        int year = calendar.get(Calendar.YEAR);
                        db.updateDay(day);
                        db.updateOriginalDayTemp(String.valueOf(day));
                        db.updateMonth(month);
                        db.updateYear(year);
                    }else{
                        getDateFromDB();
                    }

                    //Set default time values if user not selected time values already
//                    if(!timePicked){
//                        Calendar calendar = Calendar.getInstance();
//                        int minute = calendar.get(Calendar.MINUTE);
//                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//                        int ampm = calendar.get(Calendar.AM_PM);
//                        if(hour >= 10) {
//                            if(hour != 23) {
//                                db.updateHour(hour + 1);
//                            }else{
//                                db.updateHour(hour);
//                            }
//                            db.updateAmPm(ampm);
//                            db.updateMinute(minute);
//                        }else{
//                            db.updateHour(10);
//                            db.updateAmPm(ampm);
//                            db.updateMinute(0);
//                        }
//                    }else{
//                        getTimeFromDB();
//                    }

                    if(!timePicked){

                        Calendar calendar = Calendar.getInstance();
                        int minute = calendar.get(Calendar.MINUTE);
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int ampm = calendar.get(Calendar.AM_PM);

                        if(hour == 23){
                            db.updateHour(11);
                        }else if(hour >= 13) {
                            db.updateHour(hour - 11);
                        }else if(hour == 12){
                            db.updateHour(1);
                        }else{
                            db.updateHour(hour + 1);
                            if (hour == 11) {
                                ampm = 1;
                            }
                        }
                        db.updateAmPm(ampm);
                        db.updateMinute(minute);
                    }else{

                        getTimeFromDB();
                    }

                    setDue = true;
                    datePicked = true;
                    timePicked = true;

                }

            }else{
                db.updateRepeatInterval(dbTaskId, "");
                db.updateRepeat(dbTaskId, false);
            }
            db.updateSetAlarm(true);
            db.updateIgnored(dbTaskId, false);
            db.updateOverdue(dbTaskId, false);
            if(!remindersAvailable && dbDueTime.equals("0")) {
                duesSet++;
                db.updateDuesSet(duesSet);
            }
            db.updateSnooze(dbTaskId, false);
            db.updateSnoozeData(dbTaskId, "", "", "", "", "", "");
        }else if(!remindersAvailable && !dbDueTime.equals("0") && !datePicked && !timePicked){
            duesSet--;
            db.updateDuesSet(duesSet);
        }

        //return to mainActivity
        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

    }

    private void getDateFromDB() {
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
        if(!alarmDay.equals("")) {
            Cursor dbTaskResult = db.getData(Integer.parseInt(dbTaskId));
            String dbOriginalDay = "";
            while (dbTaskResult.moveToNext()) {
                dbOriginalDay = dbTaskResult.getString(20);
            }
            dbTaskResult.close();
            db.updateDay(Integer.parseInt(alarmDay));
            db.updateOriginalDayTemp(String.valueOf(dbOriginalDay));
            db.updateMonth(Integer.parseInt(alarmMonth));
            db.updateYear(Integer.parseInt(alarmYear));
        }
    }

    private void getTimeFromDB() {
        Cursor alarmResult = MainActivity.db.getAlarmData
                (Integer.parseInt(dbTaskId));
        String alarmMinute = "";
        String alarmHour = "";
        String alarmAmPm = "";
        while(alarmResult.moveToNext()){
            alarmHour = alarmResult.getString(1);
            alarmMinute = alarmResult.getString(2);
            alarmAmPm = alarmResult.getString(3);
        }
        alarmResult.close();
        if(!alarmMinute.equals("")) {
            db.updateMinute(Integer.parseInt(alarmMinute));
            db.updateHour(Integer.parseInt(alarmHour));
            db.updateAmPm(Integer.parseInt(alarmAmPm));
        }
    }

}
