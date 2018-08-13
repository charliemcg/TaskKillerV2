package com.violenthoboenterprises.taskkiller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

class MyAdapter extends ArrayAdapter<String> {

    //String for debugging
    final String TAG = "MyAdapter";

    public MyAdapter(Context context, ArrayList<String> values) {
        super(context, R.layout.task_layout, values);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        //get data from array
        final String task = getItem(position);
        //Uses unique layout for the new item
        final LayoutInflater theInflater = LayoutInflater.from(getContext());
        final View taskView = theInflater.inflate(R.layout.task_layout, parent, false);
        final TextView theTextView = taskView.findViewById(R.id.textView);
        final Intent intent = new Intent(getContext(), Checklist.class);
        final Intent noteIntent = new Intent(getContext(), Note.class);
        final TableRow propertyRow = taskView.findViewById(R.id.properties);
        final TableRow dateRow = taskView.findViewById(R.id.dateTime);
        final TableRow optionsRow = taskView.findViewById(R.id.options);
        final TableRow alarmOptionsRow = taskView.findViewById(R.id.alarmOptions);
        final TableRow repeatRow = taskView.findViewById(R.id.repeat);
        final TableRow adRow = taskView.findViewById(R.id.adRow);
        final TableRow snoozeRow = taskView.findViewById(R.id.snoozeRow);
        final TableRow taskOverdueRow = taskView.findViewById(R.id.taskIsOverdue);
        final DatePicker datePicker = taskView.findViewById(R.id.datePicker);
        final TimePicker timePicker = taskView.findViewById(R.id.timePicker);
        TextView dueTextView = taskView.findViewById(R.id.dueTextView);
        Button complete = taskView.findViewById(R.id.complete);
        final Button alarm = taskView.findViewById(R.id.alarm);
        Button more = taskView.findViewById(R.id.more);
        final Button rename = taskView.findViewById(R.id.rename);
        Button subTasks = taskView.findViewById(R.id.subTasks);
        Button note = taskView.findViewById(R.id.note);
        final Button dateButton = taskView.findViewById(R.id.date);
        final Button daily = taskView.findViewById(R.id.daily);
        final Button weekly = taskView.findViewById(R.id.weekly);
        final Button monthly = taskView.findViewById(R.id.monthly);
        final Button killAlarmBtn = taskView.findViewById(R.id.killAlarmBtn);
        final Button resetAlarmBtn = taskView.findViewById(R.id.resetAlarmBtn);
        final Button repeatAlarmBtn = taskView.findViewById(R.id.repeatBtn);
        ImageView due = taskView.findViewById(R.id.due);
        ImageView dueGrey = taskView.findViewById(R.id.dueGrey);
        ImageView overdue = taskView.findViewById(R.id.dueRed);
        ImageView snoozed = taskView.findViewById(R.id.snoozeIcon);
//        ImageView repeat = taskView.findViewById(R.id.repeatIcon);
        ImageView repeatDay = taskView.findViewById(R.id.repeatIconDay);
        ImageView repeatWeek = taskView.findViewById(R.id.repeatIconWeek);
        ImageView repeatMonth = taskView.findViewById(R.id.repeatIconMonth);
        ImageView repeatGrey = taskView.findViewById(R.id.repeatGreyIcon);
        ImageView noteImg = taskView.findViewById(R.id.noteIcon);
        ImageView noteGrey = taskView.findViewById(R.id.noteIconGrey);
        ImageView checklistImg = taskView.findViewById(R.id.checklistIcon);
        ImageView checklistGrey = taskView.findViewById(R.id.checklistIconGrey);
        Button snoozeTask = taskView.findViewById(R.id.snoozeTask);
        Button taskDone = taskView.findViewById(R.id.taskDone);
        Button taskIgnore = taskView.findViewById(R.id.taskIgnore);
        final Button oneHourBtn = taskView.findViewById(R.id.oneHour);
        final Button fourHourBtn = taskView.findViewById(R.id.fourHours);
        final Button tomorrowBtn = taskView.findViewById(R.id.tomorrow);

        //getting task data
        String dbNote = "";
        Boolean dbChecklist = false;
        String dbTimestamp = "";
        Boolean dbDue = false;
        Boolean dbKilled = false;
        int dbBroadcast = 0;
        Boolean dbRepeat = false;
        Boolean dbOverdue = false;
        Boolean dbSnooze = false;
        Boolean dbShowOnce = false;
        int dbInterval = 0;
        String dbRepeatInterval = "";
        Boolean dbIgnored = false;
        Cursor dbResult = MainActivity.noteDb.getData(Integer.parseInt(
                MainActivity.sortedIDs.get(position)));
        while (dbResult.moveToNext()) {
            dbNote = dbResult.getString(1);
            dbChecklist = dbResult.getInt(2) > 0;
            dbTimestamp = dbResult.getString(3);
            dbDue = dbResult.getInt(5) > 0;
            dbKilled = dbResult.getInt(6) > 0;
            dbBroadcast = dbResult.getInt(7);
            dbRepeat = dbResult.getInt(8) > 0;
            dbOverdue = dbResult.getInt(9) > 0;
            dbSnooze = dbResult.getInt(10) > 0;
            dbShowOnce = dbResult.getInt(11) > 0;
            dbInterval = dbResult.getInt(12);
            dbRepeatInterval = dbResult.getString(13);
            dbIgnored = dbResult.getInt(14) > 0;
        }

        //getting alarm data
        Cursor alarmResult = MainActivity.noteDb.getAlarmData(
                Integer.parseInt(MainActivity.sortedIDs.get(position)));
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

        final String finalDbNote = dbNote;
        final Boolean finalDbChecklist = dbChecklist;
        final String finalDbTimestamp = dbTimestamp;
        final Boolean finalDbDue = dbDue;
        final Boolean finalDbKilled = dbKilled;
        final int finalDbBroadcast = dbBroadcast;
        final Boolean finalDbRepeat = dbRepeat;
        final Boolean finalDbOverdue = dbOverdue;
        final Boolean finalDbSnooze = dbSnooze;
        final Boolean finalDbShowOnce = dbShowOnce;
        final int finalDbInterval = dbInterval;
        final String finalDbRepeatInterval = dbRepeatInterval;
        final Boolean finalDbIgnored = dbIgnored;

        final String finalAlarmHour = alarmHour;
        final String finalAlarmMinute = alarmMinute;
        final String finalAlarmAmpm = alarmAmpm;
        final String finalAlarmDay = alarmDay;
        final String finalAlarmMonth = alarmMonth;
        final String finalAlarmYear = alarmYear;

        //Displaying ad if there are five or more tasks
        if(position == 4) {
            adRow.setVisibility(View.VISIBLE);
            boolean networkAvailable = false;
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                networkAvailable = true;
            }

            //Initialising banner ad
            final AdView adView = taskView.findViewById(R.id.adView);
            ImageView banner = taskView.findViewById(R.id.banner);

            if (networkAvailable) {
                adView.setVisibility(View.VISIBLE);
                final AdRequest banRequest = new AdRequest.Builder()
                        .addTestDevice("7A57C74D0EDE338C302869CB538CD3AC")/*.addTestDevice
                    (AdRequest.DEVICE_ID_EMULATOR)*/.build();//TODO remove .addTestDevice()
                adView.loadAd(banRequest);
            } else {
                banner.setVisibility(View.VISIBLE);
            }
        }

        //TODO make sure hard coded values work on all devices
        //Task cannot be centered unless it's in view. Moving selected task into view
        // if not already in view in portrait.
        if ((MainActivity.activeTask > 6) && MainActivity.centerTask && (getContext()
                .getResources().getConfiguration().orientation == 1)) {
            MainActivity.theListView.post(new Runnable() {
                @Override
                public void run() {
                    MainActivity.theListView.setSelection(MainActivity.activeTask);
                }
            });
            MainActivity.centerTask = false;
        //Same as above but for landscape.
        } else if ((MainActivity.activeTask > 3) && MainActivity.centerTask && (getContext()
                .getResources().getConfiguration().orientation == 2)) {
            MainActivity.theListView.post(new Runnable() {
                @Override
                public void run() {
                    MainActivity.theListView.setSelection(MainActivity.activeTask);
                }
            });
            MainActivity.centerTask = false;
        }

        //Check if list needs to be reordered
        if(MainActivity.reorderList){
            reorderList();
            MainActivity.reorderList = false;
        }

        //TODO decide if alarm reinstatement is to be a thing in the first place
//        if(MainActivity.reinstateAlarm){
//
//            Boolean isDue = false;
//            Boolean isRepeating = false;
//            Cursor alarmResult = MainActivity.noteDb.getData(Integer.parseInt(
//                    MainActivity.sortedIDs.get(position)));
//            while (alarmResult.moveToNext()) {
//                isDue = alarmResult.getInt(5) > 0;
//                isRepeating = alarmResult.getInt(8) > 0;
//            }
//
//            Calendar prevCalendar = new GregorianCalendar();
//            //Getting time data
//            Cursor prevCalResult = MainActivity.noteDb.getAlarmData(Integer.parseInt(
//                    MainActivity.sortedIDs.get(MainActivity.activeTask)));
//            String prevHour = "";
//            String prevMinute = "";
//            String prevAmpm = "";
//            String prevDay = "";
//            String prevMonth = "";
//            String prevYear = "";
//            while (prevCalResult.moveToNext()) {
//                prevHour = prevCalResult.getString(1);
//                prevMinute = prevCalResult.getString(2);
//                prevAmpm = prevCalResult.getString(3);
//                prevDay = prevCalResult.getString(4);
//                prevMonth = prevCalResult.getString(5);
//                prevYear = prevCalResult.getString(6);
//            }
//            if(prevAmpm.equals("1")){
//                int tempHour = Integer.parseInt(prevHour) + 12;
//                prevHour = String.valueOf(tempHour);
//            }
//            if(!prevHour.equals("")) {
//                //TODO adjust for am or pm
//                prevCalendar.set(Integer.parseInt(prevYear), Integer.parseInt(prevMonth),
//                        Integer.parseInt(prevDay), Integer.parseInt(prevHour),
//                        Integer.parseInt(prevMinute));
//            }
//
//            //check if there's an alarm in the first place
//            if(isDue) {
//
//                //setting a repeating notification
//                if (isRepeating) {
//
//
//                //setting a one-time notification
//                } else {
//
//                    Calendar currentDate = new GregorianCalendar();
//
//                    ImageView due = taskView.findViewById(R.id.due);
//                    ImageView overdue = taskView.findViewById(R.id.dueRed);
//
//                    Cursor result = MainActivity.noteDb.getAlarmData(Integer.parseInt(
//                            MainActivity.sortedIDs.get(position)));;
//                    String hour = "";
//                    String minute = "";
//                    String ampm = "";
//                    String day = "";
//                    String month = "";
//                    String year = "";
//
//                    while(result.moveToNext()){
//                        hour = result.getString(1);
//                        minute = result.getString(2);
//                        ampm = result.getString(3);
//                        day = result.getString(4);
//                        month = result.getString(5);
//                        year = result.getString(6);
//                    }
//
//                    //Checking for overdue tasks
//                    Boolean sameDay = false;
//                    Boolean markAsOverdue = false;
//                    //Overdue
//                    if (currentDate.get(Calendar.YEAR) > Integer.valueOf(year)) {
//                        overdue.setVisibility(View.VISIBLE);
//                        markAsOverdue = true;
//                        //Overdue
//                    } else if (currentDate.get(Calendar.YEAR) == Integer.valueOf(year)
//                            && currentDate.get(Calendar.MONTH) > Integer.valueOf(month)) {
//                        overdue.setVisibility(View.VISIBLE);
//                        markAsOverdue = true;
//                        //Overdue
//                    } else if (currentDate.get(Calendar.YEAR) == Integer.valueOf(year)
//                            && currentDate.get(Calendar.MONTH) == Integer.valueOf(month)
//                            && currentDate.get(Calendar.DAY_OF_MONTH) > Integer.valueOf(day)) {
//                        overdue.setVisibility(View.VISIBLE);
//                        markAsOverdue = true;
//                    } else if (currentDate.get(Calendar.YEAR) == Integer.valueOf(year)
//                            && currentDate.get(Calendar.MONTH) == Integer.valueOf(month)
//                            && currentDate.get(Calendar.DAY_OF_MONTH) == Integer.valueOf(day)) {
//                        sameDay = true;
//                        //Saved hours are in 12 hour time. Accounting for am/pm.
//                        int adjustedHour = 0;
//                        if (Integer.valueOf(ampm) == 1) {
//                            adjustedHour = Integer.valueOf(hour) + 12;
//                        } else {
//                            adjustedHour = Integer.valueOf(hour);
//                        }
//                        //Overdue
//                        if (currentDate.get(Calendar.HOUR_OF_DAY) > adjustedHour) {
//                            overdue.setVisibility(View.VISIBLE);
//                            markAsOverdue = true;
//                            //Overdue
//                        } else if (currentDate.get(Calendar.HOUR_OF_DAY) == adjustedHour
//                                && currentDate.get(Calendar.MINUTE) >= Integer.valueOf(minute)) {
//                            overdue.setVisibility(View.VISIBLE);
//                            markAsOverdue = true;
//                            //Not overdue
//                        } else {
//                            due.setVisibility(View.VISIBLE);
//                        }
//                        //Not overdue
//                    } else {
//                        due.setVisibility(View.VISIBLE);
//                    }
//
//                    if(!markAsOverdue) {
//                        MainActivity.alarmManager.set(AlarmManager.RTC,
//                                prevCalendar.getTimeInMillis(), MainActivity.pendIntent);
//                    }
//
//                }
//
//            }
//
//            MainActivity.reinstateAlarm = false;
//
//        }

        //Determining if ignored repeating task has reached next repeat time
        Calendar nowness = new GregorianCalendar();
        if(dbRepeatInterval.equals("day") && !dbTimestamp.equals("")){
            if((nowness.getTimeInMillis() / 1000) >= (Integer.parseInt(dbTimestamp) + 86400)) {
                //setting new timestamp to a day from previous
                int newStamp = Integer.parseInt(dbTimestamp) + 86400;
                MainActivity.noteDb.updateTimestamp(MainActivity.sortedIDs
                        .get(position), String.valueOf(newStamp));

                if(!alarmDay.equals("")) {
                    int newDay = Integer.parseInt(alarmDay);
                    int newMonth = Integer.parseInt(alarmMonth);
                    int newYear = Integer.parseInt(alarmYear);
                    //Incrementing day
                    if (((newMonth == 0) || (newMonth == 2) || (newMonth == 4) || (newMonth == 6)
                            || (newMonth == 7) || (newMonth == 9)) && (newDay == 31)) {
                        newDay = 1;
                        newMonth++;
                    } else if (((newMonth == 1) || (newMonth == 3) || (newMonth == 5)
                            || (newMonth == 8) || (newMonth == 10)) && (newDay == 30)) {
                        newDay = 1;
                        newMonth++;
                    } else if ((newMonth == 11) && (newDay == 31)) {
                        newDay = 1;
                        newMonth = 0;
                        newYear++;
                    } else if ((newMonth == 1) && (newDay == 28) && (newYear % 4 != 0)) {
                        newDay = 1;
                        newMonth++;
                    } else if ((newMonth == 1) && (newDay == 29) && (newYear % 4 == 0)) {
                        newDay = 1;
                        newMonth++;
                    } else {
                        newDay++;
                    }

                    //updating due date in database
                    MainActivity.noteDb.updateAlarmData(String.valueOf(
                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                            alarmHour, alarmMinute, alarmAmpm,
                            String.valueOf(newDay), String.valueOf(newMonth),
                            String.valueOf(newYear));

                    //cancelling any snooze data
                    MainActivity.noteDb.updateSnoozeData(String.valueOf(
                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                            "", "", "", "", "", "");

                }
            }
        }else if(dbRepeatInterval.equals("week") && !dbTimestamp.equals("")){
            if((nowness.getTimeInMillis() / 1000) >= (Integer.parseInt(dbTimestamp) + 604800)) {
                //setting new timestamp to a week from previous
                int newStamp = Integer.parseInt(dbTimestamp) + 604800;
                MainActivity.noteDb.updateTimestamp(MainActivity.sortedIDs
                        .get(position), String.valueOf(newStamp));

                if(!alarmDay.equals("")) {
                    int theDay = Integer.parseInt(alarmDay) + 7;
                    int theMonth = Integer.parseInt(alarmMonth);
                    int theYear = Integer.parseInt(alarmYear);
                    //Incrementing week
                    if(((theMonth == 0) || (theMonth == 2)
                            || (theMonth == 4) || (theMonth == 6)
                            || (theMonth == 7) || (theMonth == 9)) && (theDay >= 25)){
                        theDay -= 31;
                        theMonth++;
                    }else if(((theMonth == 3) || (theMonth == 5)
                            || (theMonth == 8)|| (theMonth == 10)) && (theDay >= 24)){
                        theDay -= 30;
                        theMonth++;
                    }else if((theMonth == 11) && (theDay >= 25)){
                        theDay -= 31;
                        theMonth++;
                        theYear++;
                    }else if((theMonth == 1) && (theDay >= 22) && (theYear % 4 != 0)){
                        theDay -= 28;
                        theMonth++;
                    }else if((theMonth == 1) && (theDay >= 22) && (theYear % 4 == 0)){
                        theDay -= 29;
                        theMonth++;
                    }

                    //updating due time in database
                    MainActivity.noteDb.updateAlarmData(String.valueOf(
                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                            alarmHour, alarmMinute, alarmAmpm,
                            String.valueOf(theDay), String.valueOf(theMonth),
                            String.valueOf(theYear));

                    //cancelling any snooze data
                    MainActivity.noteDb.updateSnoozeData(String.valueOf(
                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                            "", "", "", "", "", "");

                }
            }
        }else if(dbRepeatInterval.equals("month") && !dbTimestamp.equals("")){

            //Getting interval in seconds based on specific day and month
            int interval = 0;
            int theYear = Integer.parseInt(alarmYear);
            int theMonth = Integer.parseInt(alarmMonth);
            int theDay = Integer.parseInt(alarmDay);
            //Month January and day is 29 non leap year 2592000
            if((theMonth == 0) && (theDay == 29) && (theYear % 4 != 0)){
                interval = 2592000;
            //Month January and day is 30 non leap year 2505600
            }else if((theMonth == 0) && (theDay == 30) && (theYear % 4 != 0)){
                interval = 2505600;
            //Month January and day is 31 non leap year 2419200
            }else if((theMonth == 0) && (theDay == 31) && (theYear % 4 != 0)){
                interval = 2419200;
            //Month January and day is 30 leap year 2592000
            }else if((theMonth == 0) && (theDay == 30)  && (theYear % 4 == 0)){
                interval = 2592000;
            //Month January and day is 31 leap year 2505600
            }else if((theMonth == 0) && (theDay == 31) && (theYear % 4 == 0)){
                interval = 2505600;
            //Month March||May||August||October and day is 31 2592000
            }else if(((theMonth == 2) || (theMonth == 4) || (theMonth == 7)
                    || (theMonth == 9)) && (theDay == 31)){
                interval = 2592000;
            //Month January||March||May||July||August||October||December 2678400
            }else if((theMonth == 0) || (theMonth == 2) || (theMonth == 4)
                    || (theMonth == 6) || (theMonth == 7) || (theMonth == 9)
                    || (theMonth == 11)){
                interval = 2678400;
            //Month April||June||September||November 2592000
            }else if((theMonth == 3) || (theMonth == 5) || (theMonth == 8)
                    || (theMonth == 10)){
                interval = 2592000;
            //Month February non leap year 2419200
            }else if((theMonth == 1) && (theYear % 4 != 0)){
                interval = 2419200;
            //Month February leap year 2505600
            }else if((theMonth == 1) && (theYear % 4 == 0)){
                 interval = 2505600;
            }

            if((nowness.getTimeInMillis() / 1000) >= (Integer.parseInt(dbTimestamp) + interval)) {
                //setting new timestamp to a month from previous
                int newStamp = Integer.parseInt(dbTimestamp) + interval;
                MainActivity.noteDb.updateTimestamp(MainActivity.sortedIDs
                        .get(position), String.valueOf(newStamp));

                //setting the next alarm because monthly repeats cannot be done automatically
                MainActivity.pendIntent = PendingIntent.getBroadcast(
                        getContext(), Integer.parseInt(MainActivity
                                .sortedIDs.get(position)),
                        MainActivity.alertIntent, PendingIntent
                                .FLAG_UPDATE_CURRENT);

                MainActivity.alarmManager.set(AlarmManager.RTC,
                        newStamp, MainActivity.pendIntent);

                if(!alarmDay.equals("")) {
                    int newDay = Integer.parseInt(alarmDay);
                    int newMonth = Integer.parseInt(alarmMonth);
                    int newYear = Integer.parseInt(alarmYear);
                    //incrementing month
                    if (((newMonth == 2) || (newMonth == 4) || (newMonth == 7)
                            || (newMonth == 9)) && (newDay == 31)) {
                        newDay = 30;
                        newMonth++;
                    } else if ((newMonth == 11) && (newDay == 31)) {
                        newMonth = 0;
                        newYear++;
                    } else if ((newMonth == 1) && (newDay > 28) && (newYear % 4 != 0)) {
                        newDay = 28;
                        newMonth++;
                    } else if ((newMonth == 1) && (newDay > 29) && (newYear % 4 == 0)) {
                        newDay = 28;
                        newMonth++;
                    } else {
                        newMonth++;
                    }

                    //updating due date in database
                    MainActivity.noteDb.updateAlarmData(String.valueOf(
                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                            alarmHour, alarmMinute, alarmAmpm,
                            String.valueOf(newDay), String.valueOf(newMonth),
                            String.valueOf(newYear));

                    //cancelling any snooze data
                    MainActivity.noteDb.updateSnoozeData(String.valueOf(
                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                            "", "", "", "", "", "");

                }
            }
        }

        //actions to occur in regards to selected task
        if(MainActivity.taskPropertiesShowing && position == MainActivity.activeTask){

            //Determine whether to show datepicker
            if(MainActivity.datePickerShowing) {

                dateRow.setVisibility(View.VISIBLE);
                MainActivity.dateOrTime = true;

            //Show alarm options
            }else if(MainActivity.alarmOptionsShowing){

                alarmOptionsRow.setVisibility(View.VISIBLE);

                propertyRow.setVisibility(View.GONE);

                MainActivity.alarmOptionsShowing = true;

                //Actions to occur if user selects 'remove alarm'
                killAlarmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MainActivity.noteDb.updateDue(String.valueOf(MainActivity
                                .sortedIDs.get(MainActivity.activeTask)), false);

                        MainActivity.noteDb.updateRepeat(MainActivity.sortedIDs
                                .get(position), false);

                        MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
                                Integer.parseInt(MainActivity.sortedIDs.get(position)),
                                MainActivity.alertIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                        MainActivity.alarmManager.cancel(MainActivity.pendIntent);

                        MainActivity.noteDb.updateAlarmData(String.valueOf(MainActivity.activeTask),
                                "", "", "", "", "", "");

                        MainActivity.alarmOptionsShowing = false;

                        notifyDataSetChanged();

                    }
                });

                //Actions to occur if user selects 'change due date'
                resetAlarmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MainActivity.datePickerShowing = true;

                        MainActivity.dateRowShowing = true;

                        notifyDataSetChanged();

                    }
                });

                //Actions to occur if user selects 'repeat alarm'
                repeatAlarmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alarmOptionsRow.setVisibility(View.GONE);

                        repeatRow.setVisibility(View.VISIBLE);

                        MainActivity.alarmOptionsShowing = false;

                    }
                });

            //show the overdue properties
            }else if(dbOverdue && !dbSnooze){

                taskOverdueRow.setVisibility(View.VISIBLE);

                //Actions to occur if user selects 'snooze'
                snoozeTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        taskOverdueRow.setVisibility(View.GONE);

                        snoozeRow.setVisibility(View.VISIBLE);

                        //Actions to occur if user selects '1 hour'
                        oneHourBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                MainActivity.noteDb.updateInterval(String.valueOf(MainActivity
                                        .sortedIDs.get(position)), String.valueOf(1));

                                Calendar dateNow = new GregorianCalendar();

                                //checking if there is enough time before next
                                // repeat to snooze for an hour
                                boolean dontSnooze = false;
                                if(finalDbRepeat){
                                    if (finalDbRepeatInterval.equals("day")) {
                                        if((dateNow.getTimeInMillis() / 1000) >= (Integer
                                                .parseInt(finalDbTimestamp) + 82800)){
                                            dontSnooze = true;
                                        }
                                    } else if (finalDbRepeatInterval.equals("week")) {
                                        if ((dateNow.getTimeInMillis() / 1000) >= (Integer
                                                .parseInt(finalDbTimestamp) + 601200)) {
                                            dontSnooze = true;
                                        }
                                    } else if (finalDbRepeatInterval.equals("month")) {
                                        //need to get interval specifically regarding day and month
                                        int interval = 0;
                                        int theYear = Integer.parseInt(finalAlarmYear);
                                        int theMonth = Integer.parseInt(finalAlarmMonth);
                                        int theDay = Integer.parseInt(finalAlarmDay);
                                        //Month January and day is 29 non leap year 2592000
                                        if((theMonth == 0) && (theDay == 29) && (theYear % 4 != 0)){
                                            interval = 2592000;
                                            //Month January and day is 30 non leap year 2505600
                                        }else if((theMonth == 0) && (theDay == 30)
                                                && (theYear % 4 != 0)){
                                            interval = 2505600;
                                            //Month January and day is 31 non leap year 2419200
                                        }else if((theMonth == 0) && (theDay == 31)
                                                && (theYear % 4 != 0)){
                                            interval = 2419200;
                                            //Month January and day is 30 leap year 2592000
                                        }else if((theMonth == 0) && (theDay == 30)
                                                && (theYear % 4 == 0)){
                                            interval = 2592000;
                                            //Month January and day is 31 leap year 2505600
                                        }else if((theMonth == 0) && (theDay == 31)
                                                && (theYear % 4 == 0)){
                                            interval = 2505600;
                                            //Month March||May||August||October
                                            // and day is 31 2592000
                                        }else if(((theMonth == 2) || (theMonth == 4)
                                                || (theMonth == 7) || (theMonth == 9))
                                                && (theDay == 31)){
                                            interval = 2592000;
                                            //Month January||March||May||July||August
                                            // ||October||December 2678400
                                        }else if((theMonth == 0) || (theMonth == 2)
                                                || (theMonth == 4) || (theMonth == 6)
                                                || (theMonth == 7) || (theMonth == 9)
                                                || (theMonth == 11)){
                                            interval = 2678400;
                                            //Month April||June||September||November 2592000
                                        }else if((theMonth == 3) || (theMonth == 5)
                                                || (theMonth == 8) || (theMonth == 10)){
                                            interval = 2592000;
                                            //Month February non leap year 2419200
                                        }else if((theMonth == 1) && (theYear % 4 != 0)){
                                            interval = 2419200;
                                            //Month February leap year 2505600
                                        }else if((theMonth == 1) && (theYear % 4 == 0)){
                                            interval = 2505600;
                                        }
                                        if ((dateNow.getTimeInMillis() / 1000) >= (Integer
                                                .parseInt(finalDbTimestamp) + interval)) {
                                            dontSnooze = true;
                                        }
                                    }
                                }

                                if(dontSnooze){

                                    Toast.makeText(v.getContext(),
                                            "Task not snoozed because repeat alarm is due.",
                                            Toast.LENGTH_SHORT).show();

                                    int newDay = Integer.parseInt(finalAlarmDay);
                                    int newMonth = Integer.parseInt(finalAlarmMonth);
                                    int newYear = Integer.parseInt(finalAlarmYear);

                                    int adjustedStamp = 0;

                                    if(finalDbRepeatInterval.equals("day")){

                                        adjustedStamp = Integer.parseInt(finalDbTimestamp) + 86400;

                                        //Incrementing day
                                        if (((newMonth == 0)
                                                || (newMonth == 2) || (newMonth == 4)
                                                || (newMonth == 6) || (newMonth == 7)
                                                || (newMonth == 9)) && (newDay == 31)) {
                                            newDay = 1;
                                            newMonth++;
                                        } else if (((newMonth == 3) || (newMonth == 5)
                                                || (newMonth == 8) || (newMonth == 10))
                                                && (newDay == 30)) {
                                            newDay = 1;
                                            newMonth++;
                                        } else if ((newMonth == 11) && (newDay == 31)) {
                                            newDay = 1;
                                            newMonth = 0;
                                            newYear++;
                                        }else if(newMonth == 1
                                                && (newDay == 28) && (newYear % 4 != 0)) {
                                            newDay = 1;
                                            newMonth++;
                                        }else if(newMonth == 1
                                                && (newDay == 29) && (newYear % 4 == 0)){
                                            newDay = 1;
                                            newMonth++;
                                        } else {
                                            newDay++;
                                        }

                                    }else if(finalDbRepeatInterval.equals("week")){

                                        adjustedStamp = Integer.parseInt(finalDbTimestamp) + 604800;
                                        newDay += 7;

                                        //incrementing week
                                        if(((newMonth == 0) || (newMonth == 2) || (newMonth == 4)
                                                || (newMonth == 6) || (newMonth == 7)
                                                || (newMonth == 9)) && (newDay >= 25)){
                                            newDay -= 31;
                                            newMonth++;
                                        }else if(((newMonth == 3) || (newMonth == 5)
                                                || (newMonth == 8)|| (newMonth == 10))
                                                && (newDay >= 24)){
                                            newDay -= 30;
                                            newMonth++;
                                        }else if((newMonth == 11) && (newDay >= 25)){
                                            newDay -= 31;
                                            newMonth++;
                                            newYear++;
                                        }else if((newMonth == 1) && (newDay >= 22)
                                                && (newYear % 4 != 0)){
                                            newDay -= 28;
                                            newMonth++;
                                        }else if((newMonth == 1) && (newDay >= 22)
                                                && (newYear % 4 == 0)){
                                            newDay -= 29;
                                            newMonth++;
                                        }

                                    }else if(finalDbRepeatInterval.equals("month")){

                                        //getting interval based on specific day and month
                                        int interval = 0;
                                        int theYear = Integer.parseInt(finalAlarmYear);
                                        int theMonth = Integer.parseInt(finalAlarmMonth);
                                        int theDay = Integer.parseInt(finalAlarmDay);
                                        //Month January and day is 29 non leap year 2592000
                                        if((theMonth == 0) && (theDay == 29) && (theYear % 4 != 0)){
                                            interval = 2592000;
                                            //Month January and day is 30 non leap year 2505600
                                        }else if((theMonth == 0) && (theDay == 30)
                                                && (theYear % 4 != 0)){
                                            interval = 2505600;
                                            //Month January and day is 31 non leap year 2419200
                                        }else if((theMonth == 0) && (theDay == 31)
                                                && (theYear % 4 != 0)){
                                            interval = 2419200;
                                            //Month January and day is 30 leap year 2592000
                                        }else if((theMonth == 0) && (theDay == 30)
                                                && (theYear % 4 == 0)){
                                            interval = 2592000;
                                            //Month January and day is 31 leap year 2505600
                                        }else if((theMonth == 0) && (theDay == 31)
                                                && (theYear % 4 == 0)){
                                            interval = 2505600;
                                            //Month March||May||August||October
                                            // and day is 31 2592000
                                        }else if(((theMonth == 2) || (theMonth == 4)
                                                || (theMonth == 7) || (theMonth == 9))
                                                && (theDay == 31)){
                                            interval = 2592000;
                                            //Month January||March||May||July||August
                                            // ||October||December 2678400
                                        }else if((theMonth == 0) || (theMonth == 2)
                                                || (theMonth == 4) || (theMonth == 6)
                                                || (theMonth == 7) || (theMonth == 9)
                                                || (theMonth == 11)){
                                            interval = 2678400;
                                            //Month April||June||September||November 2592000
                                        }else if((theMonth == 3) || (theMonth == 5)
                                                || (theMonth == 8) || (theMonth == 10)){
                                            interval = 2592000;
                                            //Month February non leap year 2419200
                                        }else if((theMonth == 1) && (theYear % 4 != 0)){
                                            interval = 2419200;
                                            //Month February leap year 2505600
                                        }else if((theMonth == 1) && (theYear % 4 == 0)){
                                            interval = 2505600;
                                        }

                                        adjustedStamp = Integer.parseInt
                                                (finalDbTimestamp) + interval;

                                        //Setting next alarm because monthly repeats
                                        // cannot be done automatically
                                        MainActivity.pendIntent = PendingIntent.getBroadcast(
                                                getContext(), Integer.parseInt(MainActivity
                                                        .sortedIDs.get(position)),
                                                MainActivity.alertIntent, PendingIntent
                                                        .FLAG_UPDATE_CURRENT);

                                        MainActivity.alarmManager.set(AlarmManager.RTC,
                                                adjustedStamp, MainActivity.pendIntent);

                                        //incrementing month
                                        if (((newMonth == 2)
                                                || (newMonth == 4) || (newMonth == 7)
                                                || (newMonth == 9)) && (newDay == 31)) {
                                            newDay = 30;
                                            newMonth++;
                                        } else if ((newMonth == 11) && (newDay == 31)) {
                                            newMonth = 0;
                                            newYear++;
                                        } else if (newMonth == 1
                                                && (newDay > 28) && (newYear % 4 != 0)) {
                                            newDay = 28;
                                            newMonth++;
                                        } else if (newMonth == 1
                                                && (newDay > 29) && (newYear % 4 == 0)) {
                                            newDay = 28;
                                            newMonth++;
                                        }

                                    }

                                    //updating due date in database
                                    MainActivity.noteDb.updateAlarmData(String.valueOf(
                                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                            finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                                            String.valueOf(newDay),
                                            String.valueOf(newMonth),
                                            String.valueOf(newYear));

                                    //cancelling any snoozed alarm data
                                    MainActivity.noteDb.updateSnoozeData(String.valueOf(
                                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "");

                                    MainActivity.noteDb.updateTimestamp(String.valueOf(MainActivity
                                            .sortedIDs.get(position)),
                                            String.valueOf(adjustedStamp));

                                    MainActivity.noteDb.updateOverdue(String.valueOf(
                                            MainActivity.sortedIDs.get(position)), false);

                                    //set background to white
                                    MainActivity.activityRootView.setBackgroundColor(Color
                                            .parseColor("#FFFFFF"));

                                    MainActivity.taskPropertiesShowing = false;

                                    //Returns the 'add' button
                                    MainActivity.params.height = MainActivity.addHeight;

                                    MainActivity.add.setLayoutParams(MainActivity.params);

                                    MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                                }else {

                                    MainActivity.pendIntent = PendingIntent.getBroadcast(
                                            getContext(), Integer.parseInt(
                                                    MainActivity.sortedIDs.get(position) + 1000),
                                            MainActivity.alertIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT);

                                    MainActivity.alarmManager.cancel(MainActivity.pendIntent);

                                    Calendar currentDate = new GregorianCalendar();

                                    //intention to execute AlertReceiver
                                    MainActivity.alertIntent = new Intent(getContext(),
                                            AlertReceiver.class);

                                    int newAmpm = currentDate.get(Calendar.AM_PM);
                                    if(currentDate.get(Calendar.HOUR) == 11){
                                        if(currentDate.get(Calendar.AM_PM) == 0){
                                            newAmpm = 1;
                                        }else{
                                            newAmpm = 0;
                                        }
                                    }

                                    int newDay = currentDate.get(Calendar.DAY_OF_MONTH);
                                    int newMonth = currentDate.get(Calendar.MONTH);
                                    int newYear = currentDate.get(Calendar.YEAR);
                                    //incrementing day/month/year if current hour
                                    // is the last hour of said day/month/year
                                    if((newAmpm == 0) && (currentDate.get(Calendar.HOUR) == 11)){
                                        if(((currentDate.get(Calendar.MONTH)) == 0
                                                || (currentDate.get(Calendar.MONTH)) == 2
                                                || (currentDate.get(Calendar.MONTH)) == 4
                                                || (currentDate.get(Calendar.MONTH)) == 6
                                                || (currentDate.get(Calendar.MONTH)) == 7
                                                || (currentDate.get(Calendar.MONTH)) == 9 )
                                                && (newDay == 31)) {
                                            newDay = 1;
                                            newMonth++;
                                        }else if(((currentDate.get(Calendar.MONTH)) == 3
                                                || (currentDate.get(Calendar.MONTH)) == 5
                                                || (currentDate.get(Calendar.MONTH)) == 8
                                                || (currentDate.get(Calendar.MONTH)) == 10 )
                                                && (newDay == 30)) {
                                            newDay = 1;
                                            newMonth++;
                                        }else if((currentDate.get(Calendar.MONTH) == 11 )
                                                && (newDay == 31)) {
                                            newDay = 1;
                                            newMonth = 0;
                                            newYear++;
                                        }else if(currentDate.get(Calendar.MONTH) == 1
                                                && (newDay == 28) && (newYear % 4 != 0)) {
                                            newDay = 1;
                                            newMonth++;
                                        }else if(currentDate.get(Calendar.MONTH) == 1
                                                && (newDay == 29) && (newYear % 4 == 0)){
                                            newDay = 1;
                                            newMonth++;
                                        }else{
                                            newDay++;
                                        }
                                    }

                                    //incrementing hour
                                    int newHour = currentDate.get(Calendar.HOUR);
                                    newHour++;

                                    //updating snooze due time in database
                                    MainActivity.noteDb.updateSnoozeData(String.valueOf(
                                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                            String.valueOf(newHour),
                                            String.valueOf(currentDate.get(Calendar.MINUTE)),
                                            String.valueOf(newAmpm),
                                            String.valueOf(newDay),
                                            String.valueOf(newMonth),
                                            String.valueOf(newYear));

                                    //setting the name of the task for which the
                                    // notification is being set
                                    MainActivity.alertIntent.putExtra("ToDo", task);

                                    int newBroadcast = finalDbBroadcast + 1000;

                                    int interval = 0;
                                    //getting interval to add to timestamp
                                    //TODO is this stuff needed? shouldn't it be incremented by one hour regardless of repeat interval?
                                    if(finalDbRepeatInterval.equals("day")){
                                        interval = 86400;
                                    }else if(finalDbRepeatInterval.equals("week")){
                                        interval = 604800;
                                    }else if(finalDbRepeatInterval.equals("month")) {
                                        int theYear = Integer.parseInt(finalAlarmYear);
                                        int theMonth = Integer.parseInt(finalAlarmMonth);
                                        int theDay = Integer.parseInt(finalAlarmDay);
                                        //Month January and day is 29 non leap year 2592000
                                        if ((theMonth == 0) && (theDay == 29)
                                                && (theYear % 4 != 0)) {
                                            interval = 2592000;
                                            //Month January and day is 30 non leap year 2505600
                                        } else if ((theMonth == 0) && (theDay == 30)
                                                && (theYear % 4 != 0)) {
                                            interval = 2505600;
                                            //Month January and day is 31 non leap year 2419200
                                        } else if ((theMonth == 0) && (theDay == 31)
                                                && (theYear % 4 != 0)) {
                                            interval = 2419200;
                                            //Month January and day is 30 leap year 2592000
                                        } else if ((theMonth == 0) && (theDay == 30)
                                                && (theYear % 4 == 0)) {
                                            interval = 2592000;
                                            //Month January and day is 31 leap year 2505600
                                        } else if ((theMonth == 0) && (theDay == 31)
                                                && (theYear % 4 == 0)) {
                                            interval = 2505600;
                                            //Month March||May||August||October
                                            // and day is 31 2592000
                                        } else if (((theMonth == 2) || (theMonth == 4)
                                                || (theMonth == 7) || (theMonth == 9))
                                                && (theDay == 31)) {
                                            interval = 2592000;
                                            //Month January||March||May||July||August
                                            // ||October||December 2678400
                                        } else if ((theMonth == 0) || (theMonth == 2)
                                                || (theMonth == 4) || (theMonth == 6)
                                                || (theMonth == 7) || (theMonth == 9)
                                                || (theMonth == 11)) {
                                            interval = 2678400;
                                            //Month April||June||September||November 2592000
                                        } else if ((theMonth == 3) || (theMonth == 5)
                                                || (theMonth == 8) || (theMonth == 10)) {
                                            interval = 2592000;
                                            //Month February non leap year 2419200
                                        } else if ((theMonth == 1) && (theYear % 4 != 0)) {
                                            interval = 2419200;
                                            //Month February leap year 2505600
                                        } else if ((theMonth == 1) && (theYear % 4 == 0)) {
                                            interval = 2505600;
                                        }
                                    }

                                    //Setting alarm
                                    MainActivity.pendIntent = PendingIntent.getBroadcast(
                                            getContext(), newBroadcast, MainActivity.alertIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT);

                                    MainActivity.alarmManager.set(AlarmManager.RTC, (currentDate
                                                    .getTimeInMillis() + interval),
                                            MainActivity.pendIntent);

                                    MainActivity.noteDb.updateSnooze(MainActivity.sortedIDs
                                            .get(position), true);

                                    datePicker.setVisibility(View.VISIBLE);

                                    timePicker.setVisibility(View.GONE);

                                    MainActivity.dateOrTime = false;

                                    //set background to white
                                    MainActivity.activityRootView.setBackgroundColor(Color
                                            .parseColor("#FFFFFF"));

                                    MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                                    //Marks properties as not showing
                                    MainActivity.taskPropertiesShowing = false;

                                    //Returns the 'add' button
                                    MainActivity.params.height = MainActivity.addHeight;

                                    MainActivity.add.setLayoutParams(MainActivity.params);

                                    MainActivity.dateRowShowing = false;

                                    MainActivity.repeating = false;

                                    MainActivity.timePickerShowing = false;

                                    reorderList();

                                    notifyDataSetChanged();

                                }

                            }
                        });

                        //Actions to occur if user selects '4 hours'
                        fourHourBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                MainActivity.noteDb.updateInterval(String.valueOf(
                                        MainActivity.sortedIDs.get(position)), String.valueOf(4));

                                Calendar dateNow = new GregorianCalendar();

                                //checking if there is enough time before next
                                // repeat to snooze for a week
                                boolean dontSnooze = false;
                                if(finalDbRepeat) {
                                    if (finalDbRepeatInterval.equals("day")) {
                                        if ((dateNow.getTimeInMillis() / 1000) >= (Integer
                                                .parseInt(finalDbTimestamp) + 72000)) {
                                            dontSnooze = true;
                                        }
                                    } else if (finalDbRepeatInterval.equals("week")) {
                                        if ((dateNow.getTimeInMillis() / 1000) >= (Integer
                                                .parseInt(finalDbTimestamp) + 590400)) {
                                            dontSnooze = true;
                                        }
                                    } else if (finalDbRepeatInterval.equals("month")) {
                                        //need to get interval specifically
                                        // regarding day and month
                                        int interval = 0;
                                        int theYear = Integer.parseInt(finalAlarmYear);
                                        int theMonth = Integer.parseInt(finalAlarmMonth);
                                        int theDay = Integer.parseInt(finalAlarmDay);

                                        //Month January and day is 29 non leap year 2577600
                                        if((theMonth == 0) && (theDay == 29)
                                                && (theYear % 4 != 0)){
                                            interval = 2577600;
                                            //Month January and day is 30 non leap year 2491200
                                        }else if((theMonth == 0) && (theDay == 30)
                                                && (theYear % 4 != 0)){
                                            interval = 2491200;
                                            //Month January and day is 31 non leap year 2404800
                                        }else if((theMonth == 0) && (theDay == 31)
                                                && (theYear % 4 != 0)){
                                            interval = 2404800;
                                            //Month January and day is 30 leap year 2577600
                                        }else if((theMonth == 0) && (theDay == 30)
                                                && (theYear % 4 == 0)){
                                            interval = 2577600;
                                            //Month January and day is 31 leap year 2491200
                                        }else if((theMonth == 0) && (theDay == 31)
                                                && (theYear % 4 == 0)){
                                            interval = 2491200;
                                            //Month March||May||August||October
                                            // and day is 31 2577600
                                        }else if(((theMonth == 2) || (theMonth == 4)
                                                || (theMonth == 7) || (theMonth == 9))
                                                && (theDay == 31)){
                                            interval = 2577600;
                                            //Month January||March||May||July||August
                                            // ||October||December 2664000
                                        }else if((theMonth == 0) || (theMonth == 2)
                                                || (theMonth == 4) || (theMonth == 6)
                                                || (theMonth == 7) || (theMonth == 9)
                                                || (theMonth == 11)){
                                            interval = 2664000;
                                            //Month April||June||September||November 2577600
                                        }else if((theMonth == 3) || (theMonth == 5)
                                                || (theMonth == 8) || (theMonth == 10)){
                                            interval = 2577600;
                                            //Month February non leap year 2404800
                                        }else if((theMonth == 1) && (theYear % 4 != 0)){
                                            interval = 2404800;
                                            //Month February leap year 2491200
                                        }else if((theMonth == 1) && (theYear % 4 == 0)){
                                            interval = 2491200;
                                        }
                                        if ((dateNow.getTimeInMillis() / 1000) >= (Integer
                                                .parseInt(finalDbTimestamp) + interval)) {
                                            dontSnooze = true;
                                        }
                                    }
                                }

                                if(dontSnooze){

                                    Toast.makeText(v.getContext(),
                                            "Task not snoozed because repeat alarm is due.",
                                            Toast.LENGTH_SHORT).show();

                                    int newDay = Integer.parseInt(finalAlarmDay);
                                    int newMonth = Integer.parseInt(finalAlarmMonth);
                                    int newYear = Integer.parseInt(finalAlarmYear);

                                    int adjustedStamp = 0;

                                    if(finalDbRepeatInterval.equals("day")){

                                        adjustedStamp = Integer.parseInt(finalDbTimestamp) + 86400;

                                        //incrementing day
                                        if (((newMonth == 0) || (newMonth == 2)
                                                || (newMonth == 4) || (newMonth == 6)
                                                || (newMonth == 7) || (newMonth == 9))
                                                && (newDay == 31)) {
                                            newDay = 1;
                                            newMonth++;
                                        } else if (((newMonth == 3) || (newMonth == 5)
                                                || (newMonth == 8) || (newMonth == 10))
                                                && (newDay == 30)) {
                                            newDay = 1;
                                            newMonth++;
                                        } else if ((newMonth == 11) && (newDay == 31)) {
                                            newDay = 1;
                                            newMonth = 0;
                                            newYear++;
                                        }else if(newMonth == 1
                                                && (newDay == 28) && (newYear % 4 != 0)) {
                                            newDay = 1;
                                            newMonth++;
                                        }else if(newMonth == 1
                                                && (newDay == 29) && (newYear % 4 == 0)){
                                            newDay = 1;
                                            newMonth++;
                                        } else {
                                            newDay++;
                                        }

                                    }else if(finalDbRepeatInterval.equals("week")){

                                        adjustedStamp = Integer.parseInt(finalDbTimestamp) + 604800;
                                        newDay += 7;

                                        //incrementing week
                                        if(((newMonth == 0) || (newMonth == 2)
                                                || (newMonth == 4) || (newMonth == 6)
                                                || (newMonth == 7) || (newMonth == 9))
                                                && (newDay >= 25)){
                                            newDay -= 31;
                                            newMonth++;
                                        }else if(((newMonth == 3) || (newMonth == 5)
                                                || (newMonth == 8)|| (newMonth == 10))
                                                && (newDay >= 24)){
                                            newDay -= 30;
                                            newMonth++;
                                        }else if((newMonth == 11) && (newDay >= 25)){
                                            newDay -= 31;
                                            newMonth++;
                                            newYear++;
                                        }else if((newMonth == 1) && (newDay >= 22)
                                                && (newYear % 4 != 0)){
                                            newDay -= 28;
                                            newMonth++;
                                        }else if((newMonth == 1) && (newDay >= 22)
                                                && (newYear % 4 == 0)){
                                            newDay -= 29;
                                            newMonth++;
                                        }

                                    }else if(finalDbRepeatInterval.equals("month")){

                                        //getting interval based on current day and month
                                        int interval = 0;
                                        int theYear = Integer.parseInt(finalAlarmYear);
                                        int theMonth = Integer.parseInt(finalAlarmMonth);
                                        int theDay = Integer.parseInt(finalAlarmDay);
                                        //Month January and day is 29 non leap year 2592000
                                        if((theMonth == 0) && (theDay == 29) && (theYear % 4 != 0)){
                                            interval = 2592000;
                                            //Month January and day is 30 non leap year 2505600
                                        }else if((theMonth == 0) && (theDay == 30)
                                                && (theYear % 4 != 0)){
                                            interval = 2505600;
                                            //Month January and day is 31 non leap year 2419200
                                        }else if((theMonth == 0) && (theDay == 31)
                                                && (theYear % 4 != 0)){
                                            interval = 2419200;
                                            //Month January and day is 30 leap year 2592000
                                        }else if((theMonth == 0) && (theDay == 30)
                                                && (theYear % 4 == 0)){
                                            interval = 2592000;
                                            //Month January and day is 31 leap year 2505600
                                        }else if((theMonth == 0) && (theDay == 31)
                                                && (theYear % 4 == 0)){
                                            interval = 2505600;
                                            //Month March||May||August||October
                                            // and day is 31 2592000
                                        }else if(((theMonth == 2) || (theMonth == 4)
                                                || (theMonth == 7) || (theMonth == 9))
                                                && (theDay == 31)){
                                            interval = 2592000;
                                            //Month January||March||May||July||August
                                            // ||October||December 2678400
                                        }else if((theMonth == 0) || (theMonth == 2)
                                                || (theMonth == 4) || (theMonth == 6)
                                                || (theMonth == 7) || (theMonth == 9)
                                                || (theMonth == 11)){
                                            interval = 2678400;
                                            //Month April||June||September||November 2592000
                                        }else if((theMonth == 3) || (theMonth == 5)
                                                || (theMonth == 8) || (theMonth == 10)){
                                            interval = 2592000;
                                            //Month February non leap year 2419200
                                        }else if((theMonth == 1) && (theYear % 4 != 0)){
                                            interval = 2419200;
                                            //Month February leap year 2505600
                                        }else if((theMonth == 1) && (theYear % 4 == 0)){
                                            interval = 2505600;
                                        }

                                        adjustedStamp = Integer.parseInt
                                                (finalDbTimestamp) + interval;

                                        //setting next alarm because monthly
                                        // repeats cannot be done automatically
                                        MainActivity.pendIntent = PendingIntent.getBroadcast(
                                                getContext(), Integer.parseInt(MainActivity
                                                        .sortedIDs.get(position)),
                                                MainActivity.alertIntent, PendingIntent
                                                        .FLAG_UPDATE_CURRENT);

                                        MainActivity.alarmManager.set(AlarmManager.RTC,
                                                adjustedStamp, MainActivity.pendIntent);

                                        //incrementing month
                                        if (((newMonth == 2) || (newMonth == 4)
                                                || (newMonth == 7) || (newMonth == 9))
                                                && (newDay == 31)) {
                                            newDay = 30;
                                            newMonth++;
                                        } else if ((newMonth == 11) && (newDay == 31)) {
                                            newMonth = 0;
                                            newYear++;
                                        } else if (newMonth == 1
                                                && (newDay > 28) && (newYear % 4 != 0)) {
                                            newDay = 28;
                                            newMonth++;
                                        } else if (newMonth == 1
                                                && (newDay > 29) && (newYear % 4 == 0)) {
                                            newDay = 28;
                                            newMonth++;
                                        }

                                    }

                                    //updating due date in database
                                    MainActivity.noteDb.updateAlarmData(String.valueOf(
                                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                            finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                                            String.valueOf(newDay),
                                            String.valueOf(newMonth),
                                            String.valueOf(newYear));

                                    //cancelling any snooze data
                                    MainActivity.noteDb.updateSnoozeData(String.valueOf(
                                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "");

                                    MainActivity.noteDb.updateTimestamp(String.valueOf(MainActivity
                                                    .sortedIDs.get(position)),
                                            String.valueOf(adjustedStamp));

                                    MainActivity.noteDb.updateOverdue(String.valueOf(
                                            MainActivity.sortedIDs.get(position)), false);

                                    //set background to white
                                    MainActivity.activityRootView.setBackgroundColor(Color
                                            .parseColor("#FFFFFF"));

                                    MainActivity.taskPropertiesShowing = false;

                                    //Returns the 'add' button
                                    MainActivity.params.height = MainActivity.addHeight;

                                    MainActivity.add.setLayoutParams(MainActivity.params);

                                    MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                                }else {

                                    MainActivity.pendIntent = PendingIntent.getBroadcast(
                                            getContext(), Integer.parseInt(
                                                    MainActivity.sortedIDs.get(position) + 1000),
                                            MainActivity.alertIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT);

                                    MainActivity.alarmManager.cancel(MainActivity.pendIntent);

                                    Calendar currentDate = new GregorianCalendar();

                                    //intention to execute AlertReceiver
                                    MainActivity.alertIntent = new Intent(getContext(),
                                            AlertReceiver.class);

                                    int newAmpm = currentDate.get(Calendar.AM_PM);
                                    if (currentDate.get(Calendar.HOUR) >= 8) {
                                        if (currentDate.get(Calendar.AM_PM) == 0) {
                                            newAmpm = 1;
                                        } else {
                                            newAmpm = 0;
                                        }
                                    }

                                    int newDay = currentDate.get(Calendar.DAY_OF_MONTH);
                                    int newMonth = currentDate.get(Calendar.MONTH);
                                    int newYear = currentDate.get(Calendar.YEAR);
                                    //incrementing day/month/year if time is within
                                    // last four hours of said day/month/year
                                    if ((newAmpm == 0) && (currentDate.get(Calendar.HOUR) >= 8)) {
                                        if (((currentDate.get(Calendar.MONTH)) == 0
                                                || (currentDate.get(Calendar.MONTH)) == 2
                                                || (currentDate.get(Calendar.MONTH)) == 4
                                                || (currentDate.get(Calendar.MONTH)) == 6
                                                || (currentDate.get(Calendar.MONTH)) == 7
                                                || (currentDate.get(Calendar.MONTH)) == 9)
                                                && (newDay == 31)) {
                                            newDay = 1;
                                            newMonth++;
                                        } else if (((currentDate.get(Calendar.MONTH)) == 3
                                                || (currentDate.get(Calendar.MONTH)) == 5
                                                || (currentDate.get(Calendar.MONTH)) == 8
                                                || (currentDate.get(Calendar.MONTH)) == 10)
                                                && (newDay == 30)) {
                                            newDay = 1;
                                            newMonth++;
                                        } else if ((currentDate.get(Calendar.MONTH) == 11)
                                                && (newDay == 31)) {
                                            newDay = 1;
                                            newMonth = 0;
                                            newYear++;
                                        }else if(currentDate.get(Calendar.MONTH) == 1
                                                && (newDay == 28) && (newYear % 4 != 0)) {
                                            newDay = 1;
                                            newMonth++;
                                        }else if(currentDate.get(Calendar.MONTH) == 1
                                                && (newDay == 29) && (newYear % 4 == 0)){
                                            newDay = 1;
                                            newMonth++;
                                        } else {
                                            newDay++;
                                        }
                                    }

                                    //adding four hours to due time
                                    int newHour = currentDate.get(Calendar.HOUR);
                                    newHour += 4;
                                    if (newHour > 12) {
                                        newHour -= 12;
                                    }

                                    MainActivity.noteDb.updateSnoozeData(String.valueOf(
                                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                            String.valueOf(newHour),
                                            String.valueOf(currentDate.get(Calendar.MINUTE)),
                                            String.valueOf(newAmpm),
                                            String.valueOf(newDay),
                                            String.valueOf(newMonth),
                                            String.valueOf(newYear));

                                    //setting the name of the task for which
                                    // the notification is being set
                                    MainActivity.alertIntent.putExtra("ToDo", task);

                                    int newBroadcast = finalDbBroadcast + 1000;

                                    int interval = 0;
                                    //getting interval to add to timestamp
                                    //TODO is this stuff needed? shouldn't it be incremented by four hours regardless of repeat interval?
                                    if(finalDbRepeatInterval.equals("day")){
                                        interval = 86400;
                                    }else if(finalDbRepeatInterval.equals("week")){
                                        interval = 604800;
                                    }else if(finalDbRepeatInterval.equals("month")) {
                                        int theYear = Integer.parseInt(finalAlarmYear);
                                        int theMonth = Integer.parseInt(finalAlarmMonth);
                                        int theDay = Integer.parseInt(finalAlarmDay);
                                        //Month January and day is 29 non leap year 2592000
                                        if ((theMonth == 0) && (theDay == 29)
                                                && (theYear % 4 != 0)) {
                                            interval = 2592000;
                                            //Month January and day is 30 non leap year 2505600
                                        } else if ((theMonth == 0) && (theDay == 30)
                                                && (theYear % 4 != 0)) {
                                            interval = 2505600;
                                            //Month January and day is 31 non leap year 2419200
                                        } else if ((theMonth == 0) && (theDay == 31)
                                                && (theYear % 4 != 0)) {
                                            interval = 2419200;
                                            //Month January and day is 30 leap year 2592000
                                        } else if ((theMonth == 0) && (theDay == 30)
                                                && (theYear % 4 == 0)) {
                                            interval = 2592000;
                                            //Month January and day is 31 leap year 2505600
                                        } else if ((theMonth == 0) && (theDay == 31)
                                                && (theYear % 4 == 0)) {
                                            interval = 2505600;
                                            //Month March||May||August||October
                                            // and day is 31 2592000
                                        } else if (((theMonth == 2) || (theMonth == 4)
                                                || (theMonth == 7) || (theMonth == 9))
                                                && (theDay == 31)) {
                                            interval = 2592000;
                                            //Month January||March||May||July||August
                                            // ||October||December 2678400
                                        } else if ((theMonth == 0) || (theMonth == 2)
                                                || (theMonth == 4) || (theMonth == 6)
                                                || (theMonth == 7) || (theMonth == 9)
                                                || (theMonth == 11)) {
                                            interval = 2678400;
                                            //Month April||June||September||November 2592000
                                        } else if ((theMonth == 3) || (theMonth == 5)
                                                || (theMonth == 8) || (theMonth == 10)) {
                                            interval = 2592000;
                                            //Month February non leap year 2419200
                                        } else if ((theMonth == 1) && (theYear % 4 != 0)) {
                                            interval = 2419200;
                                            //Month February leap year 2505600
                                        } else if ((theMonth == 1) && (theYear % 4 == 0)) {
                                            interval = 2505600;
                                        }
                                    }

                                    //setting new alarm
                                    MainActivity.pendIntent = PendingIntent.getBroadcast(
                                            getContext(), newBroadcast, MainActivity.alertIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT);

                                    MainActivity.alarmManager.set(AlarmManager.RTC, (currentDate
                                                    .getTimeInMillis() + interval),
                                            MainActivity.pendIntent);

                                    MainActivity.noteDb.updateSnooze(MainActivity.sortedIDs
                                            .get(position), true);

                                    datePicker.setVisibility(View.VISIBLE);

                                    timePicker.setVisibility(View.GONE);

                                    MainActivity.dateOrTime = false;

                                    //set background to white
                                    MainActivity.activityRootView.setBackgroundColor(
                                            Color.parseColor("#FFFFFF"));

                                    MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                                    //Marks properties as not showing
                                    MainActivity.taskPropertiesShowing = false;

                                    //Returns the 'add' button
                                    MainActivity.params.height = MainActivity.addHeight;

                                    MainActivity.add.setLayoutParams(MainActivity.params);

                                    MainActivity.dateRowShowing = false;

                                    MainActivity.repeating = false;

                                    MainActivity.timePickerShowing = false;

                                    reorderList();

                                    notifyDataSetChanged();

                                }
                            }
                        });

                        //Actions to occur if user selects 'tomorrow'
                        tomorrowBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                MainActivity.noteDb.updateInterval(String.valueOf(
                                        MainActivity.sortedIDs.get(position)), String.valueOf(24));

                                Calendar dateNow = new GregorianCalendar();

                                //checking if new repeat is less than a day
                                boolean dontSnooze = false;
                                if(finalDbRepeat) {
                                    if(finalDbRepeatInterval.equals("day")){
                                        if((dateNow.getTimeInMillis() / 1000) >= (Integer
                                                .parseInt(finalDbTimestamp))) {
                                            dontSnooze = true;
                                        }
                                    }else if(finalDbRepeatInterval.equals("week")){
                                        if((dateNow.getTimeInMillis() / 1000) >= (Integer
                                                .parseInt(finalDbTimestamp) + 518400)) {
                                            dontSnooze = true;
                                        }
                                    }else if(finalDbRepeatInterval.equals("month")){

                                        //getting the interval based on current day and month
                                        int interval = 0;
                                        int theYear = Integer.parseInt(finalAlarmYear);
                                        int theMonth = Integer.parseInt(finalAlarmMonth);
                                        int theDay = Integer.parseInt(finalAlarmDay);
                                        //Month January and day is 29 non leap year 2505600
                                        if((theMonth == 0) && (theDay == 29) && (theYear % 4 != 0)){
                                            interval = 2505600;
                                            //Month January and day is 30 non leap year 2419200
                                        }else if((theMonth == 0) && (theDay == 30)
                                                && (theYear % 4 != 0)){
                                            interval = 2419200;
                                            //Month January and day is 31 non leap year 2332800
                                        }else if((theMonth == 0) && (theDay == 31)
                                                && (theYear % 4 != 0)){
                                            interval = 2332800;
                                            //Month January and day is 30 leap year 2505600
                                        }else if((theMonth == 0) && (theDay == 30)
                                                && (theYear % 4 == 0)){
                                            interval = 2505600;
                                            //Month January and day is 31 leap year 2419200
                                        }else if((theMonth == 0) && (theDay == 31)
                                                && (theYear % 4 == 0)){
                                            interval = 2419200;
                                            //Month March||May||August||October
                                            // and day is 31 2505600
                                        }else if(((theMonth == 2) || (theMonth == 4)
                                                || (theMonth == 7) || (theMonth == 9))
                                                && (theDay == 31)){
                                            interval = 2505600;
                                            //Month January||March||May||July||August
                                            // ||October||December 2592000
                                        }else if((theMonth == 0) || (theMonth == 2)
                                                || (theMonth == 4) || (theMonth == 6)
                                                || (theMonth == 7) || (theMonth == 9)
                                                || (theMonth == 11)){
                                            interval = 2592000;
                                            //Month April||June||September||November 2505600
                                        }else if((theMonth == 3) || (theMonth == 5)
                                                || (theMonth == 8) || (theMonth == 10)){
                                            interval = 2505600;
                                            //Month February non leap year 2332800
                                        }else if((theMonth == 1) && (theYear % 4 != 0)){
                                            interval = 2332800;
                                            //Month February leap year 2419200
                                        }else if((theMonth == 1) && (theYear % 4 == 0)){
                                            interval = 2419200;
                                        }
                                        if ((dateNow.getTimeInMillis() / 1000) >= (Integer
                                                .parseInt(finalDbTimestamp) + interval)) {
                                            dontSnooze = true;
                                        }

                                    }
                                }

                                if (dontSnooze) {

                                    Toast.makeText(v.getContext(),
                                            "Task not snoozed because repeat alarm is due.",
                                            Toast.LENGTH_SHORT).show();

                                    int newDay = Integer.parseInt(finalAlarmDay);
                                    int newMonth = Integer.parseInt(finalAlarmMonth);
                                    int newYear = Integer.parseInt(finalAlarmYear);

                                    int adjustedStamp = 0;

                                    if(finalDbRepeatInterval.equals("day")){

                                        adjustedStamp = Integer.parseInt(finalDbTimestamp) + 86400;

                                        //incrementing day
                                        if (((newMonth == 0)
                                                || (newMonth == 2) || (newMonth == 4)
                                                || (newMonth == 6) || (newMonth == 7)
                                                || (newMonth == 9)) && (newDay == 31)) {
                                            newDay = 1;
                                            newMonth++;
                                        } else if (((newMonth == 3) || (newMonth == 5)
                                                || (newMonth == 8) || (newMonth == 10))
                                                && (newDay == 30)) {
                                            newDay = 1;
                                            newMonth++;
                                        } else if ((newMonth == 11) && (newDay == 31)) {
                                            newDay = 1;
                                            newMonth = 0;
                                            newYear++;
                                        }else if(newMonth == 1
                                                && (newDay == 28) && (newYear % 4 != 0)) {
                                            newDay = 1;
                                            newMonth++;
                                        }else if(newMonth == 1
                                                && (newDay == 29) && (newYear % 4 == 0)){
                                            newDay = 1;
                                            newMonth++;
                                        } else {
                                            newDay++;
                                        }

                                    }else if(finalDbRepeatInterval.equals("week")){

                                        adjustedStamp = Integer.parseInt(finalDbTimestamp) + 604800;
                                        newDay += 7;

                                        //incrementing week
                                        if(((newMonth == 0) || (newMonth == 2)
                                                || (newMonth == 4) || (newMonth == 6)
                                                || (newMonth == 7) || (newMonth == 9))
                                                && (newDay >= 25)){
                                            newDay -= 31;
                                            newMonth++;
                                        }else if(((newMonth == 3) || (newMonth == 5)
                                                || (newMonth == 8)|| (newMonth == 10))
                                                && (newDay >= 24)){
                                            newDay -= 30;
                                            newMonth++;
                                        }else if((newMonth == 11) && (newDay >= 25)){
                                            newDay -= 31;
                                            newMonth++;
                                            newYear++;
                                        }else if((newMonth == 1) && (newDay >= 22)
                                                && (newYear % 4 != 0)){
                                            newDay -= 28;
                                            newMonth++;
                                        }else if((newMonth == 1) && (newDay >= 22)
                                                && (newYear % 4 == 0)){
                                            newDay -= 29;
                                            newMonth++;
                                        }

                                    }else if(finalDbRepeatInterval.equals("month")){

                                        //getting interval based on current day and month
                                        int interval = 0;
                                        int theYear = Integer.parseInt(finalAlarmYear);
                                        int theMonth = Integer.parseInt(finalAlarmMonth);
                                        int theDay = Integer.parseInt(finalAlarmDay);
                                        //Month January and day is 29 non leap year 2592000
                                        if((theMonth == 0) && (theDay == 29) && (theYear % 4 != 0)){
                                            interval = 2592000;
                                            //Month January and day is 30 non leap year 2505600
                                        }else if((theMonth == 0) && (theDay == 30)
                                                && (theYear % 4 != 0)){
                                            interval = 2505600;
                                            //Month January and day is 31 non leap year 2419200
                                        }else if((theMonth == 0) && (theDay == 31)
                                                && (theYear % 4 != 0)){
                                            interval = 2419200;
                                            //Month January and day is 30 leap year 2592000
                                        }else if((theMonth == 0) && (theDay == 30)
                                                && (theYear % 4 == 0)){
                                            interval = 2592000;
                                            //Month January and day is 31 leap year 2505600
                                        }else if((theMonth == 0) && (theDay == 31)
                                                && (theYear % 4 == 0)){
                                            interval = 2505600;
                                            //Month March||May||August||October
                                            // and day is 31 2592000
                                        }else if(((theMonth == 2) || (theMonth == 4)
                                                || (theMonth == 7) || (theMonth == 9))
                                                && (theDay == 31)){
                                            interval = 2592000;
                                            //Month January||March||May||July||August
                                            // ||October||December 2678400
                                        }else if((theMonth == 0) || (theMonth == 2)
                                                || (theMonth == 4) || (theMonth == 6)
                                                || (theMonth == 7) || (theMonth == 9)
                                                || (theMonth == 11)){
                                            interval = 2678400;
                                            //Month April||June||September||November 2592000
                                        }else if((theMonth == 3) || (theMonth == 5)
                                                || (theMonth == 8) || (theMonth == 10)){
                                            interval = 2592000;
                                            //Month February non leap year 2419200
                                        }else if((theMonth == 1) && (theYear % 4 != 0)){
                                            interval = 2419200;
                                            //Month February leap year 2505600
                                        }else if((theMonth == 1) && (theYear % 4 == 0)){
                                            interval = 2505600;
                                        }

                                        adjustedStamp = Integer.parseInt
                                                (finalDbTimestamp) + interval;

                                        //setting next alarm because monthly
                                        // repeats cannot be done automatically
                                        MainActivity.pendIntent = PendingIntent.getBroadcast(
                                                getContext(), Integer.parseInt(MainActivity
                                                        .sortedIDs.get(position)),
                                                MainActivity.alertIntent, PendingIntent
                                                        .FLAG_UPDATE_CURRENT);

                                        MainActivity.alarmManager.set(AlarmManager.RTC,
                                                adjustedStamp, MainActivity.pendIntent);

                                        //incrementing month
                                        if (((newMonth == 2) || (newMonth == 4)
                                                || (newMonth == 7) || (newMonth == 9))
                                                && (newDay == 31)) {
                                            newDay = 30;
                                            newMonth++;
                                        } else if ((newMonth == 11) && (newDay == 31)) {
                                            newMonth = 0;
                                            newYear++;
                                        } else if (newMonth == 1 && (newDay > 28)
                                                && (newYear % 4 != 0)) {
                                            newDay = 28;
                                            newMonth++;
                                        } else if ((newMonth == 1) && (newDay > 29)
                                                && (newYear % 4 == 0)) {
                                            newDay = 28;
                                            newMonth++;
                                        }

                                    }

                                    //updating due time in database
                                    MainActivity.noteDb.updateAlarmData(String.valueOf(
                                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                            finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                                            String.valueOf(newDay),
                                            String.valueOf(newMonth),
                                            String.valueOf(newYear));

                                    //cancelling any snooze data
                                    MainActivity.noteDb.updateSnoozeData(String.valueOf(
                                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "");

                                    MainActivity.noteDb.updateTimestamp(String.valueOf(MainActivity
                                                    .sortedIDs.get(position)),
                                            String.valueOf(adjustedStamp));

                                    MainActivity.noteDb.updateOverdue(String.valueOf(
                                            MainActivity.sortedIDs.get(position)), false);

                                    //set background to white
                                    MainActivity.activityRootView.setBackgroundColor(Color
                                            .parseColor("#FFFFFF"));

                                    MainActivity.taskPropertiesShowing = false;

                                    //Returns the 'add' button
                                    MainActivity.params.height = MainActivity.addHeight;

                                    MainActivity.add.setLayoutParams(MainActivity.params);

                                    MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                                } else {

                                    MainActivity.pendIntent = PendingIntent.getBroadcast(
                                            getContext(), Integer.parseInt(
                                                    MainActivity.sortedIDs.get(position) + 1000),
                                            MainActivity.alertIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT);

                                    MainActivity.alarmManager.cancel(MainActivity.pendIntent);

                                    Calendar currentDate = new GregorianCalendar();

                                    //intention to execute AlertReceiver
                                    MainActivity.alertIntent = new Intent(getContext(),
                                            AlertReceiver.class);

                                    int newDay = currentDate.get(Calendar.DAY_OF_MONTH);
                                    int newMonth = currentDate.get(Calendar.MONTH);
                                    int newYear = currentDate.get(Calendar.YEAR);
                                    //incrementing day
                                    if (((currentDate.get(Calendar.MONTH)) == 0
                                            || (currentDate.get(Calendar.MONTH)) == 2
                                            || (currentDate.get(Calendar.MONTH)) == 4
                                            || (currentDate.get(Calendar.MONTH)) == 6
                                            || (currentDate.get(Calendar.MONTH)) == 7
                                            || (currentDate.get(Calendar.MONTH)) == 9)
                                            && (newDay == 31)) {
                                        newDay = 1;
                                        newMonth++;
                                    } else if (((currentDate.get(Calendar.MONTH)) == 3
                                            || (currentDate.get(Calendar.MONTH)) == 5
                                            || (currentDate.get(Calendar.MONTH)) == 8
                                            || (currentDate.get(Calendar.MONTH)) == 10)
                                            && (newDay == 30)) {
                                        newDay = 1;
                                        newMonth++;
                                    } else if ((currentDate.get(Calendar.MONTH) == 11)
                                            && (newDay == 31)) {
                                        newDay = 1;
                                        newMonth = 0;
                                        newYear++;
                                    }else if(currentDate.get(Calendar.MONTH) == 1
                                            && (newDay == 28) && (newYear % 4 != 0)) {
                                        newDay = 1;
                                        newMonth++;
                                    }else if(currentDate.get(Calendar.MONTH) == 1
                                            && (newDay == 29) && (newYear % 4 == 0)){
                                        newDay = 1;
                                        newMonth++;
                                    } else {
                                        newDay++;
                                    }

                                    //updating snooze data
                                    MainActivity.noteDb.updateSnoozeData(String.valueOf(
                                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                            String.valueOf(currentDate.get(Calendar.HOUR)),
                                            String.valueOf(currentDate.get(Calendar.MINUTE)),
                                            String.valueOf(currentDate.get(Calendar.AM_PM)),
                                            String.valueOf(newDay),
                                            String.valueOf(newMonth),
                                            String.valueOf(newYear));

                                    //setting the name of the task for which
                                    // the notification is being set
                                    MainActivity.alertIntent.putExtra("ToDo", task);

                                    int newBroadcast = finalDbBroadcast + 1000;

                                    int interval = 0;
                                    //TODO is this stuff needed? shouldn't it be incremented by one day regardless of repeat interval?
                                    if(finalDbRepeatInterval.equals("day")){
                                        interval = 86400;
                                    }else if(finalDbRepeatInterval.equals("week")){
                                        interval = 604800;
                                    }else if(finalDbRepeatInterval.equals("month")) {
                                        int theYear = Integer.parseInt(finalAlarmYear);
                                        int theMonth = Integer.parseInt(finalAlarmMonth);
                                        int theDay = Integer.parseInt(finalAlarmDay);
                                        //Month January and day is 29 non leap year 2592000
                                        if ((theMonth == 0) && (theDay == 29)
                                                && (theYear % 4 != 0)) {
                                            interval = 2592000;
                                            //Month January and day is 30 non leap year 2505600
                                        } else if ((theMonth == 0) && (theDay == 30)
                                                && (theYear % 4 != 0)) {
                                            interval = 2505600;
                                            //Month January and day is 31 non leap year 2419200
                                        } else if ((theMonth == 0) && (theDay == 31)
                                                && (theYear % 4 != 0)) {
                                            interval = 2419200;
                                            //Month January and day is 30 leap year 2592000
                                        } else if ((theMonth == 0) && (theDay == 30)
                                                && (theYear % 4 == 0)) {
                                            interval = 2592000;
                                            //Month January and day is 31 leap year 2505600
                                        } else if ((theMonth == 0) && (theDay == 31)
                                                && (theYear % 4 == 0)) {
                                            interval = 2505600;
                                            //Month March||May||August||October
                                            // and day is 31 2592000
                                        } else if (((theMonth == 2) || (theMonth == 4)
                                                || (theMonth == 7) || (theMonth == 9))
                                                && (theDay == 31)) {
                                            interval = 2592000;
                                            //Month January||March||May||July||August
                                            // ||October||December 2678400
                                        } else if ((theMonth == 0) || (theMonth == 2)
                                                || (theMonth == 4) || (theMonth == 6)
                                                || (theMonth == 7) || (theMonth == 9)
                                                || (theMonth == 11)) {
                                            interval = 2678400;
                                            //Month April||June||September||November 2592000
                                        } else if ((theMonth == 3) || (theMonth == 5)
                                                || (theMonth == 8) || (theMonth == 10)) {
                                            interval = 2592000;
                                            //Month February non leap year 2419200
                                        } else if ((theMonth == 1) && (theYear % 4 != 0)) {
                                            interval = 2419200;
                                            //Month February leap year 2505600
                                        } else if ((theMonth == 1) && (theYear % 4 == 0)) {
                                            interval = 2505600;
                                        }
                                    }

                                    //setting alarm
                                    MainActivity.pendIntent = PendingIntent.getBroadcast(
                                            getContext(), newBroadcast, MainActivity.alertIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT);

                                    MainActivity.alarmManager.set(AlarmManager.RTC, (currentDate
                                                    .getTimeInMillis() + interval),
                                            MainActivity.pendIntent);

                                    MainActivity.noteDb.updateSnooze(MainActivity
                                            .sortedIDs.get(position), true);

                                    datePicker.setVisibility(View.VISIBLE);

                                    timePicker.setVisibility(View.GONE);

                                    MainActivity.dateOrTime = false;

                                    //set background to white
                                    MainActivity.activityRootView.setBackgroundColor(
                                            Color.parseColor("#FFFFFF"));

                                    MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                                    //Marks properties as not showing
                                    MainActivity.taskPropertiesShowing = false;

                                    //Returns the 'add' button
                                    MainActivity.params.height = MainActivity.addHeight;

                                    MainActivity.add.setLayoutParams(MainActivity.params);

                                    MainActivity.dateRowShowing = false;

                                    MainActivity.repeating = false;

                                    MainActivity.timePickerShowing = false;

                                    reorderList();

                                    notifyDataSetChanged();

                                }
                            }
                        });

                    }
                });

                //Actions to occur if user selects 'Done'
                taskDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //kill task if not repeating
                        if(!finalDbRepeat) {
                            taskOverdueRow.setVisibility(View.GONE);

                            MainActivity.noteDb.updateOverdue(String.valueOf(
                                    MainActivity.sortedIDs.get(position)), false);

                            MainActivity.noteDb.updateIgnored(MainActivity.sortedIDs
                                    .get(position), false);

                            //set background white
                            MainActivity.activityRootView.setBackgroundColor(Color
                                    .parseColor("#FFFFFF"));

                            notifyDataSetChanged();

                            MainActivity.taskPropertiesShowing = false;

                            MainActivity.noteDb.updateKilled(String.valueOf(
                                    MainActivity.sortedIDs.get(
                                            MainActivity.activeTask)), true);

                            Toast.makeText(v.getContext(), "You killed this task!",
                                    Toast.LENGTH_SHORT).show();

                            MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
                                    Integer.parseInt(
                                            MainActivity.sortedIDs.get(position) + 1000),
                                    MainActivity.alertIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);

                            MainActivity.alarmManager.cancel(MainActivity.pendIntent);

                            MainActivity.add.setVisibility(View.VISIBLE);

                            MainActivity.vibrate.vibrate(50);

                            MainActivity.params.height = MainActivity.addHeight;

                            v.setLayoutParams(MainActivity.params);

                        //update repeating task to be due at next available date
                        }else {

                            //cancelling any snooze data
                            MainActivity.noteDb.updateSnoozeData(String.valueOf(
                                    MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                    "", "", "", "", "", "");

                            Calendar currentDate = new GregorianCalendar();

                            if(finalDbRepeatInterval.equals("day")) {

                                //adding one day to timestamp
                                int adjustedStamp = Integer.parseInt(finalDbTimestamp) + 86400;
                                MainActivity.noteDb.updateTimestamp(String.valueOf(MainActivity
                                        .sortedIDs.get(position)), String.valueOf(adjustedStamp));

                                int newDay = Integer.parseInt(finalAlarmDay);
                                int newMonth = Integer.parseInt(finalAlarmMonth);
                                int newYear = Integer.parseInt(finalAlarmYear);
                                //incrementing day
                                if (((Integer.parseInt(finalAlarmMonth) == 0)
                                        || (Integer.parseInt(finalAlarmMonth) == 2)
                                        || (Integer.parseInt(finalAlarmMonth) == 4)
                                        || (Integer.parseInt(finalAlarmMonth) == 6)
                                        || (Integer.parseInt(finalAlarmMonth) == 7)
                                        || (Integer.parseInt(finalAlarmMonth) == 9))
                                        && (newDay == 31)) {
                                    newDay = 1;
                                    newMonth++;
                                } else if (((Integer.parseInt(finalAlarmMonth) == 1)
                                        || (Integer.parseInt(finalAlarmMonth) == 3)
                                        || (Integer.parseInt(finalAlarmMonth) == 5)
                                        || (Integer.parseInt(finalAlarmMonth) == 8)
                                        || (Integer.parseInt(finalAlarmMonth) == 10))
                                        && (newDay == 30)) {
                                    newDay = 1;
                                    newMonth++;
                                } else if ((Integer.parseInt(finalAlarmMonth) == 11)
                                        && (newDay == 31)) {
                                    newDay = 1;
                                    newMonth = 0;
                                    newYear++;
                                }else if((Integer.parseInt(finalAlarmMonth) == 1)
                                        && (newDay == 28) && (newYear % 4 != 0)) {
                                    newDay = 1;
                                    newMonth++;
                                }else if((Integer.parseInt(finalAlarmMonth) == 1)
                                        && (newDay == 29) && (newYear % 4 == 0)){
                                    newDay = 1;
                                    newMonth++;
                                } else {
                                    newDay++;
                                }

                                MainActivity.noteDb.updateAlarmData(String.valueOf(
                                        MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                        finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                                        String.valueOf(newDay), String.valueOf(newMonth),
                                        String.valueOf(newYear));

                            }else if(finalDbRepeatInterval.equals("week")) {

                                //adding one week to timestamp
                                int adjustedStamp = Integer.parseInt(finalDbTimestamp) + 604800;
                                MainActivity.noteDb.updateTimestamp(String.valueOf(MainActivity
                                        .sortedIDs.get(position)), String.valueOf(adjustedStamp));

                                int newDay = currentDate.get(Calendar.DAY_OF_MONTH) + 7;
                                int newMonth = currentDate.get(Calendar.MONTH);
                                int newYear = currentDate.get(Calendar.YEAR);
                                //incrementing week
                                if (((currentDate.get(Calendar.MONTH)) == 0
                                        || (currentDate.get(Calendar.MONTH)) == 2
                                        || (currentDate.get(Calendar.MONTH)) == 4
                                        || (currentDate.get(Calendar.MONTH)) == 6
                                        || (currentDate.get(Calendar.MONTH)) == 7
                                        || (currentDate.get(Calendar.MONTH)) == 9)
                                        && (newDay > 31)) {
                                    newDay -= 31;
                                    newMonth++;
                                } else if (((currentDate.get(Calendar.MONTH)) == 1
                                        || (currentDate.get(Calendar.MONTH)) == 3
                                        || (currentDate.get(Calendar.MONTH)) == 5
                                        || (currentDate.get(Calendar.MONTH)) == 8
                                        || (currentDate.get(Calendar.MONTH)) == 10)
                                        && (newDay > 30)) {
                                    newDay -= 30;
                                    newMonth++;
                                } else if ((currentDate.get(Calendar.MONTH) == 11)
                                        && (newDay == 31)) {
                                    newDay -= 31;
                                    newMonth = 0;
                                    newYear++;
                                }else if(currentDate.get(Calendar.MONTH) == 1
                                        && (newDay == 28) && (newYear % 4 != 0)) {
                                    newDay = 1;
                                    newMonth++;
                                }else if(currentDate.get(Calendar.MONTH) == 1
                                        && (newDay == 29) && (newYear % 4 == 0)){
                                    newDay = 1;
                                    newMonth++;
                                }

                                MainActivity.noteDb.updateAlarmData(String.valueOf(
                                        MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                        finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                                        String.valueOf(newDay), String.valueOf(newMonth),
                                        String.valueOf(newYear));

                            }else if(finalDbRepeatInterval.equals("month")) {

                                //getting interval based on current day and month
                                int interval = 0;
                                int theYear = Integer.parseInt(finalAlarmYear);
                                int theMonth = Integer.parseInt(finalAlarmMonth);
                                int theDay = Integer.parseInt(finalAlarmDay);
                                //Month January and day is 29 non leap year 2592000
                                if((theMonth == 0) && (theDay == 29) && (theYear % 4 != 0)){
                                    interval = 2592000;
                                    //Month January and day is 30 non leap year 2505600
                                }else if((theMonth == 0) && (theDay == 30) && (theYear % 4 != 0)){
                                    interval = 2505600;
                                    //Month January and day is 31 non leap year 2419200
                                }else if((theMonth == 0) && (theDay == 31) && (theYear % 4 != 0)){
                                    interval = 2419200;
                                    //Month January and day is 30 leap year 2592000
                                }else if((theMonth == 0) && (theDay == 30)  && (theYear % 4 == 0)){
                                    interval = 2592000;
                                    //Month January and day is 31 leap year 2505600
                                }else if((theMonth == 0) && (theDay == 31) && (theYear % 4 == 0)){
                                    interval = 2505600;
                                    //Month March||May||August||October and day is 31 2592000
                                }else if(((theMonth == 2) || (theMonth == 4) || (theMonth == 7)
                                        || (theMonth == 9)) && (theDay == 31)){
                                    interval = 2592000;
                                    //Month January||March||May||July||August
                                    // ||October||December 2678400
                                }else if((theMonth == 0) || (theMonth == 2) || (theMonth == 4)
                                        || (theMonth == 6) || (theMonth == 7) || (theMonth == 9)
                                        || (theMonth == 11)){
                                    interval = 2678400;
                                    //Month April||June||September||November 2592000
                                }else if((theMonth == 3) || (theMonth == 5) || (theMonth == 8)
                                        || (theMonth == 10)){
                                    interval = 2592000;
                                    //Month February non leap year 2419200
                                }else if((theMonth == 1) && (theYear % 4 != 0)){
                                    interval = 2419200;
                                    //Month February leap year 2505600
                                }else if((theMonth == 1) && (theYear % 4 == 0)){
                                    interval = 2505600;
                                }

                                //adding one month to timestamp
                                int adjustedStamp = Integer.parseInt(finalDbTimestamp) + interval;
                                MainActivity.noteDb.updateTimestamp(String.valueOf(MainActivity
                                                .sortedIDs.get(position)),
                                        String.valueOf(adjustedStamp));

                                //setting next alarm
                                MainActivity.pendIntent = PendingIntent.getBroadcast(
                                        getContext(), Integer.parseInt(MainActivity
                                                .sortedIDs.get(position)),
                                        MainActivity.alertIntent, PendingIntent
                                                .FLAG_UPDATE_CURRENT);

                                MainActivity.alarmManager.set(AlarmManager.RTC,
                                        adjustedStamp, MainActivity.pendIntent);

                                int newDay = Integer.parseInt(finalAlarmDay);
                                int newMonth = Integer.parseInt(finalAlarmMonth);
                                int newYear = Integer.parseInt(finalAlarmYear);
                                if (((newMonth == 2) || (newMonth == 4)
                                        || (newMonth == 7) || (newMonth == 9))
                                        && (newDay == 31)) {
                                    newDay = 30;
                                    newMonth++;
                                } else if ((newMonth == 11) && (newDay == 31)) {
                                    newMonth = 0;
                                    newYear++;
                                } else if ((newMonth == 1) && (newDay > 28) && (newYear % 4 != 0)) {
                                    newDay = 28;
                                    newMonth++;
                                } else if (newMonth == 1
                                        && (newDay > 29) && (newYear % 4 == 0)) {
                                    newDay = 28;
                                    newMonth++;
                                }else{
                                    newMonth++;
                                }

                                //setting new due time in database
                                MainActivity.noteDb.updateAlarmData(String.valueOf(
                                    MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                    finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                                    String.valueOf(newDay),
                                    String.valueOf(newMonth),
                                    String.valueOf(newYear));

                            }

                            //set background to white
                            MainActivity.activityRootView.setBackgroundColor(
                                    Color.parseColor("#FFFFFF"));

                            taskOverdueRow.setVisibility(View.GONE);

                            MainActivity.taskPropertiesShowing = false;

                            MainActivity.noteDb.updateOverdue(
                                    MainActivity.sortedIDs.get(position), false);

                            MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                        }

                    }
                });

                //Actions to occur if user selects 'ignore'
                taskIgnore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        taskOverdueRow.setVisibility(View.GONE);

                        MainActivity.noteDb.updateOverdue(
                                MainActivity.sortedIDs.get(position), false);

                        MainActivity.noteDb.updateShowOnce(
                                MainActivity.sortedIDs.get(position), false);

                        MainActivity.noteDb.updateIgnored(String.valueOf(
                                MainActivity.sortedIDs.get(position)), true);

                        //cancelling any snooze data
                        MainActivity.noteDb.updateSnoozeData(String.valueOf(
                                MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                "",
                                "",
                                "",
                                "",
                                "",
                                "");

                        //set background to white
                        MainActivity.activityRootView.setBackgroundColor(Color
                                .parseColor("#FFFFFF"));

                        MainActivity.taskPropertiesShowing = false;

                        //Returns the 'add' button
                        MainActivity.params.height = MainActivity.addHeight;

                        MainActivity.add.setLayoutParams(MainActivity.params);

                        //Updates the view
                        MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                    }
                });

            //show tasks properties
            }else{

                propertyRow.setVisibility(View.VISIBLE);

            }

            //Making extra row visible removes clickability. Clickability needs to be reinstated.
            taskView.findViewById(R.id.taskName).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {

                    //set background to white
                    MainActivity.activityRootView.setBackgroundColor(Color
                            .parseColor("#FFFFFF"));

                    //Updates the view
                    MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                    //Marks properties as not showing
                    MainActivity.taskPropertiesShowing = false;

                    //Returns the 'add' button
                    MainActivity.params.height = MainActivity.addHeight;

                    MainActivity.add.setLayoutParams(MainActivity.params);

                }
            });

            //centering the selected item in the view
            MainActivity.listViewHeight = MainActivity.theListView.getMeasuredHeight();
            MainActivity.theListView.smoothScrollToPositionFromTop(position,
                    (MainActivity.listViewHeight / 2));

            //put data in text view
            theTextView.setText(task);

            //"set due date" button becomes "remove due date" button if due date already set
            if (dbDue && dbSnooze){

                alarm.setText("Cancel snooze");

            }else if(dbDue){

                alarm.setText("Alarm Options");

            }

            //if alarm is ignored user has option to turn alarm off
            if(dbIgnored){
                alarm.setText("Turn Off Alarm");
            }

            //Actions to occur if user selects 'complete'
            complete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //task is killed if not repeating
                    if(!finalDbRepeat) {

                        //set background white
                        MainActivity.activityRootView.setBackgroundColor(Color
                                .parseColor("#FFFFFF"));

                        notifyDataSetChanged();

                        MainActivity.taskPropertiesShowing = false;

                        MainActivity.noteDb.updateKilled(String.valueOf(
                                MainActivity.sortedIDs.get(MainActivity.activeTask)), true);

                        MainActivity.noteDb.updateIgnored(MainActivity.sortedIDs
                                .get(position), false);

                        Toast.makeText(v.getContext(), "You killed this task!",
                                Toast.LENGTH_SHORT).show();

                        //need to kill the right alarm. Need to know if
                        // killing initial alarm or a snoozed alarm
                        if (!finalDbSnooze) {
                            MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
                                    Integer.parseInt(MainActivity.sortedIDs.get(position)),
                                    MainActivity.alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        } else {
                            MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
                                    Integer.parseInt(MainActivity.sortedIDs.get(position) + 1000),
                                    MainActivity.alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        }

                        MainActivity.alarmManager.cancel(MainActivity.pendIntent);

                        MainActivity.add.setVisibility(View.VISIBLE);

                        MainActivity.vibrate.vibrate(50);

                        MainActivity.params.height = MainActivity.addHeight;

                        v.setLayoutParams(MainActivity.params);

                        reorderList();

                    //task is updated to be due at next repeat
                    }else{

                        int interval = 0;
                        int newDay = Integer.parseInt(finalAlarmDay);
                        int newMonth = Integer.parseInt(finalAlarmMonth);
                        int newYear = Integer.parseInt(finalAlarmYear);

                        if(finalDbRepeatInterval.equals("day")){

                            interval = 86400;

                            //incrementing day
                            if (((Integer.parseInt(finalAlarmMonth) == 0)
                                    || (Integer.parseInt(finalAlarmMonth) == 2)
                                    || (Integer.parseInt(finalAlarmMonth) == 4)
                                    || (Integer.parseInt(finalAlarmMonth) == 6)
                                    || (Integer.parseInt(finalAlarmMonth) == 7)
                                    || (Integer.parseInt(finalAlarmMonth) == 9))
                                    && (newDay == 31)) {
                                newDay = 1;
                                newMonth++;
                            } else if (((Integer.parseInt(finalAlarmMonth) == 1)
                                    || (Integer.parseInt(finalAlarmMonth) == 3)
                                    || (Integer.parseInt(finalAlarmMonth) == 5)
                                    || (Integer.parseInt(finalAlarmMonth) == 8)
                                    || (Integer.parseInt(finalAlarmMonth) == 10))
                                    && (newDay == 30)) {
                                newDay = 1;
                                newMonth++;
                            } else if ((Integer.parseInt(finalAlarmMonth) == 11)
                                    && (newDay == 31)) {
                                newDay = 1;
                                newMonth = 0;
                                newYear++;
                            }else if((Integer.parseInt(finalAlarmMonth) == 1)
                                    && (newDay == 28) && (newYear % 4 != 0)) {
                                newDay = 1;
                                newMonth++;
                            }else if((Integer.parseInt(finalAlarmMonth) == 1)
                                    && (newDay == 29) && (newYear % 4 == 0)){
                                newDay = 1;
                                newMonth++;
                            } else {
                                newDay++;
                            }

                        }else if(finalDbRepeatInterval.equals("week")){

                            interval = 604800;
                            newDay += 7;

                            //incrementing week
                            if(((newMonth == 0) || (newMonth == 2)
                                    || (newMonth == 4) || (newMonth == 6)
                                    || (newMonth == 7) || (newMonth == 9)) && (newDay >= 25)){
                                newDay -= 31;
                                newMonth++;
                            }else if(((newMonth == 3) || (newMonth == 5)
                                    || (newMonth == 8)|| (newMonth == 10)) && (newDay >= 24)){
                                newDay -= 30;
                                newMonth++;
                            }else if((newMonth == 11) && (newDay >= 25)){
                                newDay -= 31;
                                newMonth++;
                                newYear++;
                            }else if((newMonth == 1) && (newDay >= 22) && (newYear % 4 != 0)){
                                newDay -= 28;
                                newMonth++;
                            }else if((newMonth == 1) && (newDay >= 22) && (newYear % 4 == 0)){
                                newDay -= 29;
                                newMonth++;
                            }

                        }else if(finalDbRepeatInterval.equals("month")){

                            //getting interval based on current day and month
                            interval = 0;
                            int theYear = Integer.parseInt(finalAlarmYear);
                            int theMonth = Integer.parseInt(finalAlarmMonth);
                            int theDay = Integer.parseInt(finalAlarmDay);
                            //Month January and day is 29 non leap year 2592000
                            if((theMonth == 0) && (theDay == 29) && (theYear % 4 != 0)){
                                interval = 2592000;
                                //Month January and day is 30 non leap year 2505600
                            }else if((theMonth == 0) && (theDay == 30) && (theYear % 4 != 0)){
                                interval = 2505600;
                                //Month January and day is 31 non leap year 2419200
                            }else if((theMonth == 0) && (theDay == 31) && (theYear % 4 != 0)){
                                interval = 2419200;
                                //Month January and day is 30 leap year 2592000
                            }else if((theMonth == 0) && (theDay == 30)  && (theYear % 4 == 0)){
                                interval = 2592000;
                                //Month January and day is 31 leap year 2505600
                            }else if((theMonth == 0) && (theDay == 31) && (theYear % 4 == 0)){
                                interval = 2505600;
                                //Month March||May||August||October and day is 31 2592000
                            }else if(((theMonth == 2) || (theMonth == 4) || (theMonth == 7)
                                    || (theMonth == 9)) && (theDay == 31)){
                                interval = 2592000;
                                //Month January||March||May||July||August||October||December 2678400
                            }else if((theMonth == 0) || (theMonth == 2) || (theMonth == 4)
                                    || (theMonth == 6) || (theMonth == 7) || (theMonth == 9)
                                    || (theMonth == 11)){
                                interval = 2678400;
                                //Month April||June||September||November 2592000
                            }else if((theMonth == 3) || (theMonth == 5) || (theMonth == 8)
                                    || (theMonth == 10)){
                                interval = 2592000;
                                //Month February non leap year 2419200
                            }else if((theMonth == 1) && (theYear % 4 != 0)){
                                interval = 2419200;
                                //Month February leap year 2505600
                            }else if((theMonth == 1) && (theYear % 4 == 0)){
                                interval = 2505600;
                            }

                            newDay = Integer.parseInt(finalAlarmDay);
                            newMonth = Integer.parseInt(finalAlarmMonth);
                            newYear = Integer.parseInt(finalAlarmYear);
                            //incrementing month
                            if (((newMonth == 2) || (newMonth == 4) || (newMonth == 7)
                                    || (newMonth == 9)) && (newDay == 31)) {
                                newDay = 30;
                                newMonth++;
                            } else if ((newMonth == 11) && (newDay == 31)) {
                                newMonth = 0;
                                newYear++;
                            } else if (newMonth == 1
                                    && (newDay > 28) && (newYear % 4 != 0)) {
                                newDay = 28;
                                newMonth++;
                            } else if (newMonth == 1
                                    && (newDay > 29) && (newYear % 4 == 0)) {
                                newDay = 28;
                                newMonth++;
                            }else{
                                newMonth++;
                            }

                            int newStamp = Integer.parseInt(finalDbTimestamp) + interval;

                            //setting alarm
                            MainActivity.pendIntent = PendingIntent.getBroadcast(
                                    getContext(), Integer.parseInt(MainActivity
                                            .sortedIDs.get(position)),
                                    MainActivity.alertIntent, PendingIntent
                                            .FLAG_UPDATE_CURRENT);

                            MainActivity.alarmManager.set(AlarmManager.RTC,
                                    newStamp, MainActivity.pendIntent);

                        }

                        //updating timestamp
                        int adjustedStamp = Integer.parseInt(finalDbTimestamp) + interval;
                        MainActivity.noteDb.updateTimestamp(String.valueOf(MainActivity
                                .sortedIDs.get(position)), String.valueOf(adjustedStamp));

                        //updating due time in database
                        MainActivity.noteDb.updateAlarmData(String.valueOf(
                                MainActivity.sortedIDs.get(position)),
                                finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                                String.valueOf(newDay), String.valueOf(newMonth),
                                String.valueOf(newYear));

                        MainActivity.noteDb.updateShowOnce(
                                MainActivity.sortedIDs.get(MainActivity.activeTask), true);

                        //TODO Show this only when necessary
                        Toast.makeText(v.getContext(), "HINT: You can cancel " +
                                "repeat in alarm options.", Toast.LENGTH_LONG).show();

                        propertyRow.setVisibility(View.GONE);

                        MainActivity.activityRootView
                                .setBackgroundColor(Color.parseColor("#FFFFFF"));

                        MainActivity.taskPropertiesShowing = false;

                        MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                    }

                }

            });

            //Actions to occur if user selects 'set due date'
            alarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //clear out all data related to alarm
                    if(finalDbIgnored){

                        MainActivity.noteDb.updateDue(String.valueOf(MainActivity
                                .sortedIDs.get(MainActivity.activeTask)), false);

                        MainActivity.noteDb.removeTimestamp(String.valueOf(MainActivity
                                .sortedIDs.get(MainActivity.activeTask)));

                        MainActivity.noteDb.updateRepeat(MainActivity.sortedIDs
                                .get(position), false);

                        MainActivity.noteDb.updateIgnored(MainActivity.sortedIDs
                                .get(position), false);

                        MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
                                Integer.parseInt(MainActivity.sortedIDs.get(position)),
                                MainActivity.alertIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                        MainActivity.alarmManager.cancel(MainActivity.pendIntent);

                        MainActivity.noteDb.updateAlarmData
                                (String.valueOf(MainActivity.sortedIDs.get(position)),
                                        "", "", "",
                                        "", "", "");

                        MainActivity.alarmOptionsShowing = false;

                        reorderList();

                        MainActivity.taskPropertiesShowing = false;

                        MainActivity.activityRootView
                                .setBackgroundColor(Color.parseColor("#FFFFFF"));

                        MainActivity.add.setVisibility(View.VISIBLE);

                        MainActivity.vibrate.vibrate(50);

                        alarm.setText("Set Due Date");

                        MainActivity.params.height = MainActivity.addHeight;

                        v.setLayoutParams(MainActivity.params);

                        notifyDataSetChanged();

                    }else {
                        //TODO reword this
//                    Toast.makeText(v.getContext(), "Upgrade to the Pro version to" +
//                                    " get this feature", Toast.LENGTH_SHORT).show();

                        //actions to occur if alarm not already set
                        if (!finalDbDue) {

                            MainActivity.dateRowShowing = true;

                            MainActivity.datePickerShowing = true;

                            notifyDataSetChanged();

                        //actions to occur when cancelling snooze
                        } else if (finalDbSnooze) {

                            //remove any associated snooze
                            MainActivity.noteDb.updateSnooze(String.valueOf(MainActivity.sortedIDs
                                    .get(position)), false);

                            //marks showonce as false
                            MainActivity.noteDb.updateShowOnce(String.valueOf(MainActivity
                                    .sortedIDs.get(position)), false);

                            //remove snooze time data
                            MainActivity.noteDb.updateSnoozeData
                                    (String.valueOf(MainActivity.sortedIDs.get(position)),
                                            "", "", "",
                                            "", "", "");

                            MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
                                    Integer.parseInt(
                                            MainActivity.sortedIDs.get(position) + 1000),
                                    MainActivity.alertIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);

                            MainActivity.alarmManager.cancel(MainActivity.pendIntent);

                            alarm.setText("Alarm Options");
                            MainActivity.taskPropertiesShowing = false;
                            MainActivity.activityRootView
                                    .setBackgroundColor(Color.parseColor("#FFFFFF"));

                            MainActivity.params.height = MainActivity.addHeight;

                            v.setLayoutParams(MainActivity.params);

                            MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                        //actions to occur when viewing alarm properties
                        } else {

                            alarmOptionsRow.setVisibility(View.VISIBLE);

                            propertyRow.setVisibility(View.GONE);

                            MainActivity.alarmOptionsShowing = true;

                            if (finalDbRepeat) {

                                repeatAlarmBtn.setText("Cancel Repeat");

                            }

                            //Actions to occur if user selects 'remove alarm'
                            killAlarmBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    MainActivity.noteDb.updateDue(String.valueOf(MainActivity
                                            .sortedIDs.get(MainActivity.activeTask)), false);
                                    MainActivity.noteDb.removeTimestamp(String.valueOf(MainActivity
                                            .sortedIDs.get(MainActivity.activeTask)));

                                    MainActivity.noteDb.updateRepeat(MainActivity.sortedIDs
                                            .get(position), false);

                                    MainActivity.pendIntent = PendingIntent.getBroadcast
                                            (getContext(), Integer.parseInt(MainActivity
                                                            .sortedIDs.get(position)),
                                            MainActivity.alertIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT);

                                    MainActivity.alarmManager.cancel(MainActivity.pendIntent);

                                    MainActivity.noteDb.updateAlarmData
                                            (String.valueOf(MainActivity.sortedIDs.get(position)),
                                                    "", "", "",
                                                    "", "", "");

                                    MainActivity.alarmOptionsShowing = false;

                                    reorderList();

                                    MainActivity.taskPropertiesShowing = false;

                                    MainActivity.activityRootView
                                            .setBackgroundColor(Color
                                                    .parseColor("#FFFFFF"));

                                    MainActivity.add.setVisibility(View.VISIBLE);

                                    MainActivity.vibrate.vibrate(50);

                                    MainActivity.params.height = MainActivity.addHeight;

                                    v.setLayoutParams(MainActivity.params);

                                    notifyDataSetChanged();

                                }
                            });

                            //Actions to occur if user selects 'change due date'
                            resetAlarmBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    MainActivity.datePickerShowing = true;

                                    MainActivity.dateRowShowing = true;

                                    notifyDataSetChanged();

                                }
                            });

                            //Actions to occur if user selects 'repeat alarm'
                            repeatAlarmBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (finalDbRepeat) {

                                        MainActivity.noteDb.updateRepeat(MainActivity.sortedIDs
                                                .get(MainActivity.activeTask), false);

                                        MainActivity.pendIntent = PendingIntent.getBroadcast(
                                                getContext(), Integer.parseInt(MainActivity
                                                        .sortedIDs.get(position) + 1000),
                                                MainActivity.alertIntent, PendingIntent
                                                        .FLAG_UPDATE_CURRENT);

                                        MainActivity.alarmManager.cancel(MainActivity.pendIntent);

                                        Calendar prevCalendar = new GregorianCalendar();
                                        String newHour = "";
                                        if (finalAlarmAmpm.equals("1")) {
                                            int tempHour = Integer.parseInt(finalAlarmHour) + 12;
                                            newHour = String.valueOf(tempHour);
                                        }
                                        if (!finalAlarmHour.equals("")) {
                                            prevCalendar.set(Integer.parseInt(finalAlarmYear),
                                                    Integer.parseInt(finalAlarmMonth),
                                                    Integer.parseInt(finalAlarmDay),
                                                    Integer.parseInt(newHour),
                                                    Integer.parseInt(finalAlarmMinute));
                                        }

                                        MainActivity.alarmManager.set(AlarmManager.RTC,
                                                prevCalendar.getTimeInMillis(),
                                                MainActivity.pendIntent);

                                        //set background to white
                                        MainActivity.activityRootView.setBackgroundColor(
                                                Color.parseColor("#FFFFFF"));

                                        alarmOptionsRow.setVisibility(View.GONE);

                                        MainActivity.repeatShowing = false;
                                        MainActivity.repeating = false;
                                        MainActivity.alarmOptionsShowing = false;
                                        MainActivity.taskPropertiesShowing = false;

                                        MainActivity.theListView.setAdapter
                                                (MainActivity.theAdapter[0]);

                                        //Returns the 'add' button
                                        MainActivity.params.height = MainActivity.addHeight;

                                        MainActivity.add.setLayoutParams(MainActivity.params);

                                    //show repeat row
                                    } else {

                                        alarmOptionsRow.setVisibility(View.GONE);

                                        repeatRow.setVisibility(View.VISIBLE);

                                        MainActivity.repeatShowing = true;

                                    }

                                }
                            });

                        }
                    }

                }
            });

            //Actions to occur if user selects 'more'
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    propertyRow.setVisibility(View.GONE);

                    optionsRow.setVisibility(View.VISIBLE);

                    MainActivity.taskOptionsShowing = true;

                }
            });

            //Actions to occur if user selects 'rename' or 'reinstate'
            rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Cannot update the list until after the task has been updated.
                    MainActivity.goToMyAdapter = false;

                    //Indicates that a task is being edited
                    MainActivity.taskBeingEdited = true;

                    MainActivity.activeTask = position;

                    MainActivity.tasksAreClickable = false;

                    MainActivity.fadeTasks = true;

                    MainActivity.taskPropertiesShowing = false;

                    MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                }
            });

            //Actions to occur if user selects 'Sub-Tasks'
            subTasks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.checklistShowing = true;

                    MainActivity.vibrate.vibrate(50);

                    getContext().startActivity(intent);

                }
            });

            //Actions to occur if user selects 'Add Note'
            note.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.vibrate.vibrate(50);

                    getContext().startActivity(noteIntent);

                }
            });

            //Actions to occur if user selects 'Set Time'
            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dateButton.setText("Set Time");

                    setAlarm(dateRow, datePicker, timePicker);

                }
            });

            //Actions to occur if user selects to repeat daily
            daily.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.dateRowShowing = true;

                    MainActivity.repeatInterval = AlarmManager.INTERVAL_DAY;

                    MainActivity.noteDb.updateRepeatInterval(String.valueOf(
                            MainActivity.sortedIDs.get(position)), "day");

                    MainActivity.repeating = true;

                    MainActivity.taskPropertiesShowing = false;

                    setAlarm(dateRow, datePicker, timePicker);

                    //Returns the 'add' button
                    MainActivity.params.height = MainActivity.addHeight;

                    MainActivity.add.setLayoutParams(MainActivity.params);

                    notifyDataSetChanged();

                }
            });

            //Actions to occur if user selects to repeat weekly
            weekly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.dateRowShowing = true;

                    MainActivity.repeatInterval = (AlarmManager.INTERVAL_DAY * 7);

                    MainActivity.noteDb.updateRepeatInterval(String.valueOf(
                            MainActivity.sortedIDs.get(position)), "week");

                    MainActivity.repeating = true;

                    MainActivity.taskPropertiesShowing = false;

                    setAlarm(dateRow, datePicker, timePicker);

                    //Returns the 'add' button
                    MainActivity.params.height = MainActivity.addHeight;

                    MainActivity.add.setLayoutParams(MainActivity.params);

                    notifyDataSetChanged();

                }
            });

            //Actions to occur if user selects to repeat monthly
            monthly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.dateRowShowing = true;

                    MainActivity.noteDb.updateRepeatInterval(String.valueOf(
                            MainActivity.sortedIDs.get(position)), "month");

                    MainActivity.taskPropertiesShowing = false;

                    setAlarm(dateRow, datePicker, timePicker);

                    //Returns the 'add' button
                    MainActivity.params.height = MainActivity.addHeight;

                    MainActivity.add.setLayoutParams(MainActivity.params);

                    notifyDataSetChanged();

                }
            });

        }

        if (MainActivity.fadeTasks) {

            //fade tasks when keyboard is up
            taskView.setBackgroundColor(Color.parseColor("#888888"));

        }else{

            //tasks are white when keyboard is down
            taskView.setBackgroundColor(Color.parseColor("#FFFFFF"));

        }

        //put data in text view
        theTextView.setText(task);

        //crossing out completed tasks

        //check if task has to be crossed out
        if (dbKilled) {

            theTextView.setPaintFlags(theTextView.getPaintFlags() |
                    Paint.STRIKE_THRU_TEXT_FLAG);

        }

        //Show due icon and due date if required
        if (dbDue) {

            Calendar currentDate = new GregorianCalendar();

            //Getting time data
            Cursor result = MainActivity.noteDb.getSnoozeData(Integer.parseInt(
                    MainActivity.sortedIDs.get(position)));;
            String hour = "";
            String minute = "";
            String ampm = "";
            String day = "";
            String month = "";
            String year = "";
            while(result.moveToNext()){
                hour = result.getString(1);
                minute = result.getString(2);
                ampm = result.getString(3);
                day = result.getString(4);
                month = result.getString(5);
                year = result.getString(6);
            }

            //If there is no snoozed alarm then get initial alarm
            if(hour.equals("")){
                //getting alarm data
                result = MainActivity.noteDb.getAlarmData(
                        Integer.parseInt(MainActivity.sortedIDs.get(position)));
                while(result.moveToNext()){
                    hour = result.getString(1);
                    minute = result.getString(2);
                    ampm = result.getString(3);
                    day = result.getString(4);
                    month = result.getString(5);
                    year = result.getString(6);
                }
            }

            //Checking for overdue tasks
            String formattedTime;
            Boolean sameDay = false;
            Boolean markAsOverdue = false;
            if(!dbKilled) {
                //Overdue
                if (currentDate.get(Calendar.YEAR) > Integer.valueOf(year)) {
                    dueGrey.setVisibility(View.GONE);
                    overdue.setVisibility(View.VISIBLE);
                    dueTextView.setTextColor(Color.parseColor("#FF0000"));
                    markAsOverdue = true;
                //Overdue
                } else if (currentDate.get(Calendar.YEAR) == Integer.valueOf(year)
                        && currentDate.get(Calendar.MONTH) > Integer.valueOf(month)) {
                    dueGrey.setVisibility(View.GONE);
                    overdue.setVisibility(View.VISIBLE);
                    dueTextView.setTextColor(Color.parseColor("#FF0000"));
                    markAsOverdue = true;
                //Overdue
                } else if (currentDate.get(Calendar.YEAR) == Integer.valueOf(year)
                        && currentDate.get(Calendar.MONTH) == Integer.valueOf(month)
                        && currentDate.get(Calendar.DAY_OF_MONTH) > Integer.valueOf(day)) {
                    dueGrey.setVisibility(View.GONE);
                    overdue.setVisibility(View.VISIBLE);
                    dueTextView.setTextColor(Color.parseColor("#FF0000"));
                    markAsOverdue = true;
                } else if (currentDate.get(Calendar.YEAR) == Integer.valueOf(year)
                        && currentDate.get(Calendar.MONTH) == Integer.valueOf(month)
                        && currentDate.get(Calendar.DAY_OF_MONTH) == Integer.valueOf(day)) {
                    sameDay = true;
                    //Saved hours are in 12 hour time. Accounting for am/pm.
                    int adjustedHour;
                    if (Integer.valueOf(ampm) == 1) {
                        adjustedHour = Integer.valueOf(hour) + 12;
                    } else {
                        adjustedHour = Integer.valueOf(hour);
                    }
                    //Overdue
                    if (currentDate.get(Calendar.HOUR_OF_DAY) > adjustedHour) {
                        dueGrey.setVisibility(View.GONE);
                        overdue.setVisibility(View.VISIBLE);
                        dueTextView.setTextColor(Color.parseColor("#FF0000"));
                        markAsOverdue = true;
                    //Overdue
                    } else if (currentDate.get(Calendar.HOUR_OF_DAY) == adjustedHour
                            && currentDate.get(Calendar.MINUTE) >= Integer.valueOf(minute)) {
                        dueGrey.setVisibility(View.GONE);
                        overdue.setVisibility(View.VISIBLE);
                        dueTextView.setTextColor(Color.parseColor("#FF0000"));
                        markAsOverdue = true;
                    //Not overdue
                    } else {
                        dueGrey.setVisibility(View.GONE);
                        due.setVisibility(View.VISIBLE);
                    }
                //Not overdue
                } else {
                    dueGrey.setVisibility(View.GONE);
                    due.setVisibility(View.VISIBLE);
                }

                }

                //determine if snoozed alarm is overdue or not
                if(dbSnooze) {

                    //Show overdue icon
                    if (markAsOverdue) {
                        snoozed.setVisibility(View.GONE);
                        overdue.setVisibility(View.VISIBLE);
                        MainActivity.noteDb.updateSnooze(
                                MainActivity.sortedIDs.get(position), false);
                    //show snooze icon
                    } else {
                        dueGrey.setVisibility(View.GONE);
                        snoozed.setVisibility(View.VISIBLE);
                        overdue.setVisibility(View.GONE);
                        due.setVisibility(View.GONE);
                    }

                //Show the once off overdue options
                }else if(markAsOverdue && dbShowOnce){

                    MainActivity.noteDb.updateOverdue(
                            MainActivity.sortedIDs.get(position), true);

                    MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                }else if(markAsOverdue){

                    //if ignored task is ignored beyond the next repeat then
                    // the due time is updated to that new repeat time
                    if((currentDate.get(Calendar.MINUTE) >= (Integer
                            .parseInt(hour) + dbInterval)) && !dbRepeat){

                        alarmHour = String.valueOf(Integer.parseInt(alarmHour) + dbInterval);

                        MainActivity.noteDb.updateAlarmData(String.valueOf(
                                MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                alarmHour, alarmMinute, alarmAmpm, alarmDay, alarmMonth, alarmYear);

                    }

                }


            //If task due on same day show the due date
            if(!sameDay){
                //TODO account for MM/DD/YYYY https://en.wikipedia.org/wiki/Date_format_by_country
                //Formatting date
                String formattedMonth = "";
                String formattedDate;

                int intMonth = Integer.valueOf(month) + 1;
                if(intMonth == 1){
                    formattedMonth = "Jan";
                }else if(intMonth == 2){
                    formattedMonth = "Feb";
                }else if(intMonth == 3){
                    formattedMonth = "Mar";
                }else if(intMonth == 4){
                    formattedMonth = "Apr";
                }else if(intMonth == 5){
                    formattedMonth = "May";
                }else if(intMonth == 6){
                    formattedMonth = "Jun";
                }else if(intMonth == 7){
                    formattedMonth = "Jul";
                }else if(intMonth == 8){
                    formattedMonth = "Aug";
                }else if(intMonth == 9){
                    formattedMonth = "Sep";
                }else if(intMonth == 10){
                    formattedMonth = "Oct";
                }else if(intMonth == 11){
                    formattedMonth = "Nov";
                }else if(intMonth == 12){
                    formattedMonth = "Dec";
                }

                formattedDate = day + " " + formattedMonth;

                dueTextView.setText(formattedDate);
            //If task due on different day show the due time
            }else{

                if(Integer.valueOf(hour) == 0){
                    hour = "12";
                }
                if(Integer.valueOf(minute) < 10){
                    if(Integer.valueOf(ampm) == 0) {
                        formattedTime = hour + ":0" + minute + "am";
                    }else{
                        formattedTime = hour + ":0" + minute + "pm";
                    }
                }else{
                    if(Integer.valueOf(ampm) == 0) {
                        formattedTime = hour + ":" + minute + "am";
                    }else{
                        formattedTime = hour + ":" + minute + "pm";
                    }
                }

                dueTextView.setText(formattedTime);
            }

        }

        //show repeat icon if required
        if(dbRepeat && !dbKilled){

            repeatGrey.setVisibility(View.GONE);
            if(dbRepeatInterval.equals("day")){
                repeatDay.setVisibility(View.VISIBLE);
            }else if(dbRepeatInterval.equals("week")){
                repeatWeek.setVisibility(View.VISIBLE);
            }else if(dbRepeatInterval.equals("month")){
                repeatMonth.setVisibility(View.VISIBLE);
            }

        }

        //Show checklist/note icon if required
        if(dbChecklist){
            checklistGrey.setVisibility(View.GONE);
            checklistImg.setVisibility(View.VISIBLE);
        }
        if(!dbNote.equals("")){
            noteGrey.setVisibility(View.GONE);
            noteImg.setVisibility(View.VISIBLE);
        }

        //greying out unselected tasks
        if (MainActivity.taskPropertiesShowing && (position != MainActivity.activeTask)) {

            //fade out inactive tasks
            taskView.setBackgroundColor(Color.parseColor("#888888"));

        }

        //Actions to take when editing task
        if (MainActivity.taskBeingEdited && (position == MainActivity.activeTask) &&
                !MainActivity.goToMyAdapter) {

            MainActivity.keyboard.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

            String oldTaskString = theTextView.getText().toString();

            theTextView.setVisibility(View.GONE);

            MainActivity.taskNameEditText.setText(oldTaskString);

            MainActivity.taskNameEditText.setVisibility(View.VISIBLE);

            //Keyboard is inactive without this line
            MainActivity.taskNameEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI |
                    EditorInfo.IME_ACTION_DONE);

            MainActivity.taskNameEditText.setFocusable(true);

            MainActivity.taskNameEditText.requestFocus();

            MainActivity.taskNameEditText.setSelection(MainActivity.taskNameEditText
                    .getText().length());

        }

        return taskView;

    }

    //set notification alarm for selected task
    private void setAlarm(TableRow dateRow, DatePicker datePicker, TimePicker timePicker){

        //getting task data
        String dbTask = "";
        Integer dbBroadcast = 0;
        Boolean dbSnooze = false;
        String dbRepeatInterval = "";
        Cursor dbResult = MainActivity.noteDb.getData(Integer.parseInt(
                MainActivity.sortedIDs.get(MainActivity.activeTask)));
        while (dbResult.moveToNext()) {
            dbTask = dbResult.getString(4);
            dbBroadcast = dbResult.getInt(7);
            dbSnooze = dbResult.getInt(10) > 0;
            dbRepeatInterval = dbResult.getString(13);
        }

        //getting alarm data
        Cursor alarmResult = MainActivity.noteDb.getAlarmData(
                Integer.parseInt(MainActivity.sortedIDs.get(MainActivity.activeTask)));
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

        //Show time picker
        if(MainActivity.dateOrTime) {

            dateRow.setVisibility(View.VISIBLE);
            datePicker.setVisibility(View.GONE);
            timePicker.setVisibility(View.VISIBLE);
            MainActivity.dateOrTime = false;
            MainActivity.datePickerShowing = false;
            MainActivity.timePickerShowing = true;

        //actions to occur when setting a repeating task
        }else if(MainActivity.repeating){

            Calendar prevCalendar = new GregorianCalendar();
            if(alarmAmpm.equals("1")){
                int tempHour = Integer.parseInt(alarmHour) + 12;
                alarmHour = String.valueOf(tempHour);
            }
            if(!alarmHour.equals("")) {
                prevCalendar.set(Integer.parseInt(alarmYear), Integer.parseInt(alarmMonth),
                        Integer.parseInt(alarmDay), Integer.parseInt(alarmHour),
                        Integer.parseInt(alarmMinute));
            }

            MainActivity.alarmManager.setInexactRepeating(AlarmManager.RTC,
                    prevCalendar.getTimeInMillis(),
                    MainActivity.repeatInterval, MainActivity.pendIntent);

            MainActivity.noteDb.updateRepeat(MainActivity.sortedIDs
                    .get(MainActivity.activeTask), true);

            MainActivity.repeatShowing = false;
            MainActivity.repeating = false;

            //set background to white
            MainActivity.activityRootView.setBackgroundColor(Color.parseColor("#FFFFFF"));

        //actions to occur when setting a normal alarm
        }else{

            if (!dbSnooze) {
                MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
                        Integer.parseInt(MainActivity.sortedIDs.get(MainActivity.activeTask)),
                        MainActivity.alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
            } else {
                MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
                        Integer.parseInt(
                                MainActivity.sortedIDs.get(MainActivity.activeTask) + 1000),
                        MainActivity.alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
            }

            //actions specific to monthly repeating task
            if(dbRepeatInterval.equals("month")){

                MainActivity.noteDb.updateRepeat(MainActivity.sortedIDs
                        .get(MainActivity.activeTask), true);

                //set background to white
                MainActivity.activityRootView.setBackgroundColor
                        (Color.parseColor("#FFFFFF"));

                MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

            }else {

                MainActivity.alarmManager.cancel(MainActivity.pendIntent);

                Calendar calendar = Calendar.getInstance();

                //setting alarm
                calendar.set(Calendar.YEAR, datePicker.getYear());
                calendar.set(Calendar.MONTH, datePicker.getMonth());
                calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());

                Calendar currentDate = new GregorianCalendar();

                //Checking that task due date is in the future
                if (currentDate.get(Calendar.YEAR) > datePicker.getYear()) {
                    Toast.makeText(getContext(), "Cannot set task to be completed in the past",
                            Toast.LENGTH_SHORT).show();
                } else if (currentDate.get(Calendar.YEAR) == datePicker.getYear()
                        && currentDate.get(Calendar.MONTH) > datePicker.getMonth()) {
                    Toast.makeText(getContext(), "Cannot set task to be completed in the past",
                            Toast.LENGTH_SHORT).show();
                } else if (currentDate.get(Calendar.YEAR) == datePicker.getYear()
                        && currentDate.get(Calendar.MONTH) == datePicker.getMonth()
                        && currentDate.get(Calendar.DAY_OF_MONTH) >
                        datePicker.getDayOfMonth()) {
                    Toast.makeText(getContext(), "Cannot set task to be completed in the past",
                            Toast.LENGTH_SHORT).show();
                } else if (currentDate.get(Calendar.YEAR) == datePicker.getYear()
                        && currentDate.get(Calendar.MONTH) == datePicker.getMonth()
                        && currentDate.get(Calendar.DAY_OF_MONTH) ==
                        datePicker.getDayOfMonth()
                        && currentDate.get(Calendar.HOUR_OF_DAY) >
                        timePicker.getHour()) {
                    Toast.makeText(getContext(), "Cannot set task to be completed in the past",
                            Toast.LENGTH_SHORT).show();
                } else if (currentDate.get(Calendar.YEAR) == datePicker.getYear()
                        && currentDate.get(Calendar.MONTH) == datePicker.getMonth()
                        && currentDate.get(Calendar.DAY_OF_MONTH) ==
                        datePicker.getDayOfMonth()
                        && currentDate.get(Calendar.HOUR_OF_DAY) ==
                        timePicker.getHour()
                        && currentDate.get(Calendar.MINUTE) > timePicker.getMinute()) {
                    Toast.makeText(getContext(), "Cannot set task to be completed in the past",
                            Toast.LENGTH_SHORT).show();
                } else {

                    Calendar futureDate = new GregorianCalendar(datePicker.getYear(),
                            datePicker.getMonth(), datePicker.getDayOfMonth(),
                            timePicker.getHour(), timePicker.getMinute());

                    //updating timestamp
                    MainActivity.noteDb.updateTimestamp(String.valueOf(
                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                            String.valueOf(futureDate.getTimeInMillis() / 1000));

                    //intention to execute AlertReceiver
                    MainActivity.alertIntent = new Intent(getContext(), AlertReceiver.class);

                    //updating due time in database
                    MainActivity.noteDb.updateAlarmData(String.valueOf(
                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                            String.valueOf(calendar.get(calendar.HOUR)),
                            String.valueOf(calendar.get(calendar.MINUTE)),
                            String.valueOf(calendar.get(calendar.AM_PM)),
                            String.valueOf(calendar.get(calendar.DAY_OF_MONTH)),
                            String.valueOf(calendar.get(calendar.MONTH)),
                            String.valueOf(calendar.get(calendar.YEAR)));

                    //setting the name of the task for which the notification is being set
                    MainActivity.alertIntent.putExtra("ToDo", dbTask);

                    MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(), dbBroadcast,
                            MainActivity.alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    if (!dbSnooze) {
                        MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
                                Integer.parseInt(MainActivity.sortedIDs
                                        .get(MainActivity.activeTask)), MainActivity.alertIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
                                Integer.parseInt(
                                        MainActivity.sortedIDs.get
                                                (MainActivity.activeTask) + 1000),
                                MainActivity.alertIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    MainActivity.alarmManager.cancel(MainActivity.pendIntent);

                    MainActivity.alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(),
                            MainActivity.pendIntent);

                    MainActivity.noteDb.updateDue(
                            MainActivity.sortedIDs.get(MainActivity.activeTask), true);

                    MainActivity.noteDb.updateShowOnce(
                            MainActivity.sortedIDs.get(MainActivity.activeTask), true);

                }

                datePicker.setVisibility(View.VISIBLE);

                timePicker.setVisibility(View.GONE);

                MainActivity.dateOrTime = false;

                //set background to white
                MainActivity.activityRootView.setBackgroundColor
                        (Color.parseColor("#FFFFFF"));

                MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                //Marks properties as not showing
                MainActivity.taskPropertiesShowing = false;

                //Returns the 'add' button
                MainActivity.params.height = MainActivity.addHeight;

                MainActivity.add.setLayoutParams(MainActivity.params);

                MainActivity.dateRowShowing = false;

                MainActivity.repeating = false;

                MainActivity.timePickerShowing = false;

                reorderList();

                notifyDataSetChanged();

            }

        }

    }

    //Reordering tasks by due date
    public void reorderList(){

        ArrayList<Integer> tempList = new ArrayList<>();

        //Saving timestamps into a temporary array
        for(int i = 0; i < MainActivity.taskListSize; i++){

            //getting timestamp
            String dbTimestamp = "";
            Cursor dbResult = MainActivity.noteDb.getData(Integer.parseInt(
                    MainActivity.sortedIDs.get(i)));
            while (dbResult.moveToNext()) {
                dbTimestamp = dbResult.getString(3);
            }

            tempList.add(i, Integer.valueOf(dbTimestamp));

        }

        ArrayList<String> idsList = new ArrayList<>();
        ArrayList<String> tempTaskList = new ArrayList<>();

        //getting tasks which have no due date
        for(int i = 0; i < MainActivity.taskListSize; i++){

            //getting task data
            int dbId = 0;
            String dbTimestamp = "";
            String dbTask = "";
            Cursor dbResult = MainActivity.noteDb.getData(Integer.parseInt(
                    MainActivity.sortedIDs.get(i)));
            while (dbResult.moveToNext()) {
                dbId = dbResult.getInt(0);
                dbTimestamp = dbResult.getString(3);
                dbTask = dbResult.getString(4);
            }

            if((tempList.get(i) == 0) && (Integer.parseInt(dbTimestamp) == 0)){
                idsList.add(String.valueOf(dbId));
                tempTaskList.add(dbTask);
            }

        }

        while(tempList.size() > 0) {
            int minValue = Collections.min(tempList);
            if(minValue != 0) {
                for (int i = 0; i < MainActivity.taskListSize; i++) {

                    //getting task data
                    int dbId = 0;
                    String dbTimestamp = "";
                    String dbTask = "";
                    Cursor dbResult = MainActivity.noteDb.getData(Integer.parseInt(
                            MainActivity.sortedIDs.get(i)));
                    while (dbResult.moveToNext()) {
                        dbId = dbResult.getInt(0);
                        dbTimestamp = dbResult.getString(3);
                        dbTask = dbResult.getString(4);
                    }

                    if (minValue == Integer.parseInt(dbTimestamp)) {
                        idsList.add(String.valueOf(dbId));
                        tempTaskList.add(dbTask);
                        tempList.remove(Collections.min(tempList));
                    }
                }
            }else{
                tempList.remove(Collections.min(tempList));
            }
        }

        //moving killed tasks to the end of the list
        for (int i = 0; i < MainActivity.taskListSize; i++) {

            //getting task data
            int dbId = 0;
            String dbTask = "";
            Boolean dbKilled = false;
            Cursor dbResult = MainActivity.noteDb.getData(Integer.parseInt(
                    MainActivity.sortedIDs.get(Integer.parseInt(idsList.get(i)))));
            while (dbResult.moveToNext()) {
                dbId = dbResult.getInt(0);
                dbTask = dbResult.getString(4);
                dbKilled = dbResult.getInt(6) > 0;
            }

            if(dbKilled){
                idsList.add(String.valueOf(dbId));
                tempTaskList.add(dbTask);
                idsList.remove(i);
                tempTaskList.remove(i);
            }

        }

        MainActivity.sortedIDs = idsList;
        MainActivity.taskList = tempTaskList;

        //Updating the view with the new order
        MainActivity.theAdapter = new ListAdapter[]{new MyAdapter(
                getContext(), MainActivity.taskList)};
        MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

    }

}