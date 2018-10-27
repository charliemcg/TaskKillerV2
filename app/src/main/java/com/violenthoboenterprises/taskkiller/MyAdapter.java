package com.violenthoboenterprises.taskkiller;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.os.Handler;

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
        //Where the task text is displayed
        final TextView theTextView = taskView.findViewById(R.id.textView);
        final Intent intent = new Intent(getContext(), Checklist.class);
        final Intent noteIntent = new Intent(getContext(), Note.class);
        final Intent dueIntent = new Intent(getContext(), SetDue.class);
        //This row changes content depending on what needs to be displayed
        final TableRow propertyRow = taskView.findViewById(R.id.properties);
        //Part of the task view which displays the task name
        LinearLayout taskNameRow = taskView.findViewById(R.id.taskName);
        //For displaying the ad
        final RelativeLayout adRow = taskView.findViewById(R.id.adRow);
        //For displaying the snooze options
        final TableRow snoozeRow = taskView.findViewById(R.id.snoozeRow);
        //For displaying the overdue options
        final TableRow taskOverdueRow = taskView.findViewById(R.id.taskIsOverdue);
        //Displays the tasks due date
        TextView dueTextView = taskView.findViewById(R.id.dueTextView);
        //Gives user ability to set alarm on click
        final LinearLayout alarm = taskView.findViewById(R.id.alarm);
        //The text on this button needs to change depending on the state of the alarm
        final TextView alarmBtnText = taskView.findViewById(R.id.alarmBtnText);
        //Need the following texts for color changing
        final TextView subtasksBtnText = taskView.findViewById(R.id.subtasksBtnText);
        //Need the following texts for color changing
        final TextView noteBtnText = taskView.findViewById(R.id.noteBtnText);
        final TextView oneHourBtnText = taskView.findViewById(R.id.oneHourBtnText);
        final TextView fourHoursBtnText = taskView.findViewById(R.id.fourHoursBtnText);
        final TextView tomorrowBtnText = taskView.findViewById(R.id.tomorrowBtnText);
        final TextView snoozeTaskBtnText = taskView.findViewById(R.id.snoozeTaskBtnText);
        final TextView taskDoneBtnText = taskView.findViewById(R.id.taskDoneBtnText);
        final TextView taskIgnoreBtnText = taskView.findViewById(R.id.taskIgnoreBtnText);
        //Takes user to sub task activity
        final LinearLayout subTasks = taskView.findViewById(R.id.subTasks);
        //Takes user to note activity
        final LinearLayout note = taskView.findViewById(R.id.note);
        //The display of status icons
        final LinearLayout statusLayout = taskView.findViewById(R.id.statusLayout);
        //Task status icons are transparent. This is so the background colour can be
        // changed giving the illusion that the icon image color has changed.
        // Each icon has it's own relative layout.
        RelativeLayout dueLayout = taskView.findViewById(R.id.dueLayout);
        RelativeLayout overdueLayout = taskView.findViewById(R.id.overdueLayout);
        RelativeLayout snoozeLayout = taskView.findViewById(R.id.snoozeLayout);
        //Displays the snooze options on click
        LinearLayout snoozeTask = taskView.findViewById(R.id.snoozeTask);
        //Marks overdue task as done
        LinearLayout taskDone = taskView.findViewById(R.id.taskDone);
        //Ignores overdue task so that regular task properties can be accessed
        LinearLayout taskIgnore = taskView.findViewById(R.id.taskIgnore);
        //Buttons which set the snooze interval
        final LinearLayout oneHourBtn = taskView.findViewById(R.id.oneHour);
        final LinearLayout fourHourBtn = taskView.findViewById(R.id.fourHours);
        final LinearLayout tomorrowBtn = taskView.findViewById(R.id.tomorrow);

        ImageView repeatClear = taskView.findViewById(R.id.repeatClear);
        ImageView repeatClearWhite = taskView.findViewById(R.id.repeatClearWhite);
        ImageView noteClear = taskView.findViewById(R.id.noteClear);
        ImageView noteClearWhite = taskView.findViewById(R.id.noteClearWhite);
        ImageView checklistClear = taskView.findViewById(R.id.checklistClear);
        ImageView checklistClearWhite = taskView.findViewById(R.id.checklistClearWhite);
        ImageView overdueClear = taskView.findViewById(R.id.overdueClear);
        ImageView overdueClearWhite = taskView.findViewById(R.id.overdueClearWhite);
        final ImageView snoozeClear = taskView.findViewById(R.id.snoozeClear);
        final ImageView snoozeClearWhite = taskView.findViewById(R.id.snoozeClearWhite);
        final ImageView dueClear = taskView.findViewById(R.id.dueClear);
        final ImageView dueClearWhite = taskView.findViewById(R.id.dueClearWhite);
        //Icon needs to changed based on light/dark mode
        ImageView noteBtnIcon = taskView.findViewById(R.id.noteBtnIcon);
        ImageView noteBtnIconWhite = taskView.findViewById(R.id.noteBtnIconWhite);
        //Icon needs to changed based on light/dark mode
        ImageView subTasksBtnIcon = taskView.findViewById(R.id.subTasksBtnIcon);
        ImageView subTasksBtnIconWhite = taskView.findViewById(R.id.subTasksBtnIconWhite);
        //Icon needs to changed based on light/dark mode
        ImageView alarmBtnIcon = taskView.findViewById(R.id.alarmBtnIcon);
        ImageView alarmBtnIconWhite = taskView.findViewById(R.id.alarmBtnIconWhite);
        //Button used for marking task as complete
        ImageView complete = taskView.findViewById(R.id.complete);
        ImageView completeWhite = taskView.findViewById(R.id.completeWhite);
        //Graphically depicts a task as being complete
        ImageView completed = taskView.findViewById(R.id.completed);
        ImageView completedWhite = taskView.findViewById(R.id.completedWhite);

        //Exit animations for when properties are removed due to user clicking on the list item //TODO complete this and make it less buggy
//        if((position == MainActivity.activeTask) && !MainActivity.taskPropertiesShowing && MainActivity.timePickerShowing) {
//            datePicker.setVisibility(View.GONE);
//            timePicker.setVisibility(View.VISIBLE);
//            dateRow.setVisibility(View.VISIBLE);
//            dateRow.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.exit_out_left));
//
//            final Handler handler = new Handler();
//
//            final Runnable runnable = new Runnable() {
//                public void run() {
//                    dateRow.setVisibility(View.GONE);
//                }
//            };
//
//            handler.postDelayed(runnable, 400);
//        }else if((position == MainActivity.activeTask) && !MainActivity.taskPropertiesShowing && MainActivity.datePickerShowing) {
//            dateRow.setVisibility(View.VISIBLE);
//            dateRow.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.exit_out_left));
//
//            final Handler handler = new Handler();
//
//            final Runnable runnable = new Runnable() {
//                public void run() {
//                    dateRow.setVisibility(View.GONE);
//                }
//            };
//
//            handler.postDelayed(runnable, 400);
//        }else if((position == MainActivity.activeTask) && !MainActivity.taskPropertiesShowing && MainActivity.alarmOptionsShowing) {
//            alarmOptionsRow.setVisibility(View.VISIBLE);
//            alarmOptionsRow.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.exit_out_left));
//
//            final Handler handler = new Handler();
//
//            final Runnable runnable = new Runnable() {
//                public void run() {
//                    alarmOptionsRow.setVisibility(View.GONE);
//                }
//            };
//
//            handler.postDelayed(runnable, 400);
//        }else if((position == MainActivity.activeTask) && !MainActivity.taskPropertiesShowing) {
//            propertyRow.setVisibility(View.VISIBLE);
//            propertyRow.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.exit_out_left));
//
//            final Handler handler = new Handler();
//
//            final Runnable runnable = new Runnable() {
//                public void run() {
//                    propertyRow.setVisibility(View.GONE);
//                }
//            };
//
//            handler.postDelayed(runnable, 400);
//        }

        //getting task data
        int dbID = 0;
        String dbNote = "";
        String dbTimestamp = "";
        Boolean dbDue = false;
        Boolean dbKilled = false;
        int dbBroadcast = 0;
        Boolean dbRepeat = false;
        Boolean dbOverdue = false;
        Boolean dbSnooze = false;
        String dbRepeatInterval = "";
        Boolean dbIgnored = false;
        int dbChecklistSize = 0;
        String dbSnoozedStamp = "";
        Cursor dbResult = MainActivity.db.getData(Integer.parseInt(
                MainActivity.sortedIDs.get(position)));
        while (dbResult.moveToNext()) {
            dbID = dbResult.getInt(0);
            dbNote = dbResult.getString(1);
            dbTimestamp = dbResult.getString(3);
            dbDue = dbResult.getInt(5) > 0;
            dbKilled = dbResult.getInt(6) > 0;
            dbBroadcast = dbResult.getInt(7);
            dbRepeat = dbResult.getInt(8) > 0;
            dbOverdue = dbResult.getInt(9) > 0;
            dbSnooze = dbResult.getInt(10) > 0;
            dbRepeatInterval = dbResult.getString(13);
            dbIgnored = dbResult.getInt(14) > 0;
            dbChecklistSize = dbResult.getInt(17);
            dbSnoozedStamp = dbResult.getString(21);
        }
        dbResult.close();

        //getting alarm data
        Cursor alarmResult = MainActivity.db.getAlarmData(
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
        alarmResult.close();

        //getting universal data
        Cursor uniResult = MainActivity.db.getUniversalData();
        Boolean uniSetAlarm = false;
        int uniYear = 0;
        int uniMonth = 0;
        int uniDay = 0;
        int uniHour = 0;
        int uniMinute = 0;
        int uniAmPm = 0;
        String uniInterval = "0";
        String uniOriginalDayTemp = "";
        while(uniResult.moveToNext()){
            uniSetAlarm = uniResult.getInt(10) > 0;
            uniYear = uniResult.getInt(11);
            uniMonth = uniResult.getInt(12);
            uniDay = uniResult.getInt(13);
            uniHour = uniResult.getInt(14);
            uniMinute = uniResult.getInt(15);
            uniAmPm = uniResult.getInt(17);
            uniInterval = uniResult.getString(30);
            uniOriginalDayTemp = uniResult.getString(31);
        }
        uniResult.close();

        final int finalDbID = dbID;
        final String finalDbTimestamp = dbTimestamp;
        final Boolean finalDbDue = dbDue;
        final int finalDbBroadcast = dbBroadcast;
        final Boolean finalDbRepeat = dbRepeat;
        final boolean finalDbOverdue = dbOverdue;
        final Boolean finalDbSnooze = dbSnooze;
        final String finalDbRepeatInterval = dbRepeatInterval;
        final String finalDbTimesSnoozed = dbSnoozedStamp;

        final String finalAlarmHour = alarmHour;
        final String finalAlarmMinute = alarmMinute;
        final String finalAlarmAmpm = alarmAmpm;
        final String finalAlarmDay = alarmDay;
        final String finalAlarmMonth = alarmMonth;
        final String finalAlarmYear = alarmYear;

        if(!MainActivity.lightDark){
            taskView.setBackgroundColor(Color.parseColor("#333333"));
            propertyRow.setBackgroundColor(Color.parseColor("#333333"));
            dueTextView.setBackgroundColor(Color.parseColor("#333333"));
            statusLayout.setBackgroundColor(Color.parseColor("#333333"));
            theTextView.setBackgroundColor(Color.parseColor("#333333"));
            snoozeRow.setBackgroundColor(Color.parseColor("#333333"));
            taskOverdueRow.setBackgroundColor(Color.parseColor("#333333"));
            theTextView.setTextColor(Color.parseColor("#AAAAAA"));
            dueTextView.setTextColor(Color.parseColor("#AAAAAA"));
            alarmBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            subtasksBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            noteBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            oneHourBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            fourHoursBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            tomorrowBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            snoozeTaskBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            taskDoneBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            taskIgnoreBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            checklistClear.setVisibility(View.VISIBLE);
            noteClear.setVisibility(View.VISIBLE);
            repeatClear.setVisibility(View.VISIBLE);
            dueClear.setVisibility(View.VISIBLE);
            overdueClear.setVisibility(View.VISIBLE);
            snoozeClear.setVisibility(View.VISIBLE);
            checklistClearWhite.setVisibility(View.GONE);
            noteClearWhite.setVisibility(View.GONE);
            repeatClearWhite.setVisibility(View.GONE);
            dueClearWhite.setVisibility(View.GONE);
            overdueClearWhite.setVisibility(View.GONE);
            snoozeClearWhite.setVisibility(View.GONE);
            complete.setVisibility(View.VISIBLE);
            completeWhite.setVisibility(View.GONE);
            alarmBtnIcon.setVisibility(View.VISIBLE);
            subTasksBtnIcon.setVisibility(View.VISIBLE);
            noteBtnIcon.setVisibility(View.VISIBLE);
            alarmBtnIconWhite.setVisibility(View.GONE);
            subTasksBtnIconWhite.setVisibility(View.GONE);
            noteBtnIconWhite.setVisibility(View.GONE);
        }else{
            taskView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            propertyRow.setBackgroundColor(Color.parseColor("#FFFFFF"));
            dueTextView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            statusLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            theTextView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            snoozeRow.setBackgroundColor(Color.parseColor("#FFFFFF"));
            taskOverdueRow.setBackgroundColor(Color.parseColor("#FFFFFF"));
            theTextView.setTextColor(Color.parseColor("#000000"));
            dueTextView.setTextColor(Color.parseColor("#000000"));
            alarmBtnText.setTextColor(Color.parseColor("#000000"));
            subtasksBtnText.setTextColor(Color.parseColor("#000000"));
            noteBtnText.setTextColor(Color.parseColor("#000000"));
            oneHourBtnText.setTextColor(Color.parseColor("#000000"));
            fourHoursBtnText.setTextColor(Color.parseColor("#000000"));
            tomorrowBtnText.setTextColor(Color.parseColor("#000000"));
            snoozeTaskBtnText.setTextColor(Color.parseColor("#000000"));
            taskDoneBtnText.setTextColor(Color.parseColor("#000000"));
            taskIgnoreBtnText.setTextColor(Color.parseColor("#000000"));
            checklistClear.setVisibility(View.GONE);
            noteClear.setVisibility(View.GONE);
            repeatClear.setVisibility(View.GONE);
            dueClear.setVisibility(View.GONE);
            overdueClear.setVisibility(View.GONE);
            snoozeClear.setVisibility(View.GONE);
            checklistClearWhite.setVisibility(View.VISIBLE);
            noteClearWhite.setVisibility(View.VISIBLE);
            repeatClearWhite.setVisibility(View.VISIBLE);
            dueClearWhite.setVisibility(View.VISIBLE);
            overdueClearWhite.setVisibility(View.VISIBLE);
            snoozeClearWhite.setVisibility(View.VISIBLE);
            complete.setVisibility(View.GONE);
            completeWhite.setVisibility(View.VISIBLE);
            alarmBtnIcon.setVisibility(View.GONE);
            subTasksBtnIcon.setVisibility(View.GONE);
            noteBtnIcon.setVisibility(View.GONE);
            alarmBtnIconWhite.setVisibility(View.VISIBLE);
            subTasksBtnIconWhite.setVisibility(View.VISIBLE);
            noteBtnIconWhite.setVisibility(View.VISIBLE);
            alarm.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),
                    R.drawable.layout_border_white));
            subTasks.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),
                    R.drawable.layout_border_white));
            note.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),
                    R.drawable.layout_border_white));
        }

        //TODO see if it is possible to make these animations run smooth
        //Animating a killed task moving down through the list view
//        if(MainActivity.killedAnimation) {
//            if(position == (MainActivity.taskList.size() - 1)){
//                MainActivity.theListView.setSelection(MainActivity.animatePosition);
//                //TODO make sure to get correct resting position
//                if(MainActivity.animatePosition == (MainActivity.taskList.size() - 1)){
//                    MainActivity.killedAnimation = false;
//                }else{
//                    final Handler handler = new Handler();
//
//                    final Runnable runnable = new Runnable() {
//                        public void run() {
//                            reorderList();
//                        }
//                    };
//
//                    handler.postDelayed(runnable, 5);
//                    MainActivity.animatePosition++;
//                }
//            }
//        }
//
//        //Animating a reinstated task moving up through the list view
//        if(MainActivity.reinstateAnimation) {
//            if(position == (MainActivity.taskList.size() - 1)){
//                MainActivity.theListView.setSelection(MainActivity.animatePosition);
//                //TODO make sure to get correct resting position
//                if(MainActivity.animatePosition == 0){
//                    MainActivity.reinstateAnimation = false;
//                }else{
//                    final Handler handler = new Handler();
//
//                    final Runnable runnable = new Runnable() {
//                        public void run() {
//                            reorderList();
//                        }
//                    };
//
//                    handler.postDelayed(runnable, 50);
//                    MainActivity.animatePosition--;
//                }
//            }
//        }
//
//        //Animating a task with an alarm moving down through the list view
//        if(MainActivity.alarmAnimation) {
//            if(position == (MainActivity.taskList.size() - 1)){
//                MainActivity.theListView.setSelection(MainActivity.animatePosition);
//                //TODO make sure to get correct resting position
//                if(MainActivity.animatePosition == (MainActivity.taskList.size() - 1)){
//                    MainActivity.alarmAnimation = false;
//                }else{
//                    final Handler handler = new Handler();
//
//                    final Runnable runnable = new Runnable() {
//                        public void run() {
//                            reorderList();
//                        }
//                    };
//
//                    handler.postDelayed(runnable, 50);
//                    //TODO account for correct direction of movement
//                    MainActivity.animatePosition++;
//                }
//            }
//        }

        if(!MainActivity.adsRemoved) {
            //Displaying ad if there are five or more tasks
            if (position == 0 && MainActivity.taskList.size() > 4) {
                adRow.setVisibility(View.VISIBLE);
                boolean networkAvailable = false;
//                ConnectivityManager connectivityManager = (ConnectivityManager)
//                        getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//                if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
//                    networkAvailable = true;
//                }

                //Initialising banner ad
                final AdView adView = taskView.findViewById(R.id.adView);
                ImageView banner = taskView.findViewById(R.id.banner);

                if (networkAvailable) {
                    adView.setVisibility(View.VISIBLE);
                    final AdRequest banRequest = new AdRequest.Builder()/*.build();*/
                            //TODO probably need a new ID
                            //TODO find out if id should go into strings.xml
                            /*.addTestDevice("7A57C74D0EDE338C302869CB538CD3AC")*/.addTestDevice
                    (AdRequest.DEVICE_ID_EMULATOR).build();//TODO remove .addTestDevice()
                    adView.loadAd(banRequest);
                } else {
                    banner.setVisibility(View.VISIBLE);
                }
            }
        }

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
            new Reorder();
            //Updating the view with the new order
            MainActivity.theAdapter = new ListAdapter[]{new MyAdapter(
                    getContext(), MainActivity.taskList)};
            MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);
            MainActivity.reorderList = false;
        }

        //implementing exit animations if required
        if(MainActivity.exitTaskProperties && (position == MainActivity.activeTask)){
            if(!dbOverdue){

                ViewGroup.LayoutParams params = alarm.getLayoutParams();
                params.width = MainActivity.deviceWidth / 3;
                alarm.setLayoutParams(params);
                subTasks.setLayoutParams(params);
                note.setLayoutParams(params);

                propertyRow.setVisibility(View.VISIBLE);
                propertyRow.startAnimation(AnimationUtils.loadAnimation
                        (getContext(), R.anim.exit_out_left));

                final Handler handler = new Handler();

                final Runnable runnable = new Runnable() {
                    public void run() {
                        propertyRow.setVisibility(View.GONE);
                    }
                };

                handler.postDelayed(runnable, 400);
            }else if(MainActivity.snoozeRowShowing) {
                snoozeRow.setVisibility(View.VISIBLE);
                snoozeRow.startAnimation(AnimationUtils.loadAnimation
                        (getContext(), R.anim.exit_out_left));

                final Handler handler = new Handler();

                final Runnable runnable = new Runnable() {
                    public void run() {
                        snoozeRow.setVisibility(View.GONE);
                        taskOverdueRow.startAnimation(AnimationUtils.loadAnimation
                                (getContext(), R.anim.enter_from_right));
                        taskOverdueRow.setVisibility(View.VISIBLE);
                    }
                };

                handler.postDelayed(runnable, 400);
                MainActivity.snoozeRowShowing = false;
                MainActivity.taskPropertiesShowing = true;
            }else{
                taskOverdueRow.setVisibility(View.VISIBLE);
                taskOverdueRow.startAnimation(AnimationUtils.loadAnimation
                        (getContext(), R.anim.exit_out_left));

                final Handler handler = new Handler();

                final Runnable runnable = new Runnable() {
                    public void run() {
                        taskOverdueRow.setVisibility(View.GONE);
                    }
                };

                handler.postDelayed(runnable, 400);
            }
            MainActivity.exitTaskProperties = false;
        }

        if(MainActivity.reinstateAlarm){

            if(!dbOverdue){

                //setting the name of the task for which the
                // notification is being set
                MainActivity.alertIntent.putExtra("ToDo", task);
                MainActivity.alertIntent.putExtra
                        ("broadId", finalDbBroadcast);

                //Setting alarm
                MainActivity.pendIntent = PendingIntent.getBroadcast(
                        getContext(), finalDbBroadcast, MainActivity.alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                MainActivity.alarmManager.set(AlarmManager.RTC,
                        (Calendar.getInstance().getTimeInMillis()
                                + AlarmManager.INTERVAL_HOUR),
                        MainActivity.pendIntent);
            }

            MainActivity.reinstateAlarm = false;

        }

        //Determining if ignored repeating task has reached next repeat time
        Calendar nowness = new GregorianCalendar();
        if(dbRepeatInterval.equals("day") && (!dbTimestamp.equals("") || !dbTimestamp.equals("0"))){
            if((nowness.getTimeInMillis() / 1000) >= (Integer.parseInt(dbTimestamp) + 86400)) {

                long daysOut = (nowness.getTimeInMillis() / 1000) - Long.parseLong(dbTimestamp);
                daysOut = daysOut / 86400;
                //App crashes if exact duplicate of timestamp is saved in database. Attempting to
                // detect duplicates and then adjusting the timestamp on the second level
                long futureStamp = Integer.parseInt(dbTimestamp) + (86400 * daysOut);
                String tempTimestamp = "";
                for(int i = 0; i < MainActivity.taskList.size(); i++) {
                    Cursor tempResult = MainActivity.db.getData(Integer.parseInt(
                            MainActivity.sortedIDs.get(i)));
                    while (tempResult.moveToNext()) {
                        tempTimestamp = tempResult.getString(3);
                    }
                    tempResult.close();
                    if(futureStamp == Long.parseLong(tempTimestamp)){
                        futureStamp++;
                        i = 0;
                    }

                }

                //updating timestamp
                MainActivity.db.updateTimestamp(String.valueOf(
                        MainActivity.sortedIDs.get(position)),
                        String.valueOf(futureStamp));

                if(!alarmDay.equals("")) {

                    Calendar adjustedCalendar = Calendar.getInstance();
                    adjustedCalendar.setTimeInMillis(futureStamp * 1000);
                    int newDay = adjustedCalendar.get(Calendar.DAY_OF_MONTH);
                    int newMonth = adjustedCalendar.get(Calendar.MONTH);
                    int newYear = adjustedCalendar.get(Calendar.YEAR);

                    MainActivity.db.updateAlarmData(String.valueOf(MainActivity.sortedIDs
                            .get(position)), alarmHour, alarmMinute, alarmAmpm,
                            String.valueOf(newDay), String.valueOf(newMonth),
                            String.valueOf(newYear));

                    //cancelling any snooze data
                    MainActivity.db.updateSnoozeData(String.valueOf(
                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                            "", "", "", "", "", "");

                }
            }
        }else if(dbRepeatInterval.equals("week") && !dbTimestamp.equals("")){
            if((nowness.getTimeInMillis() / 1000) >= (Integer.parseInt(dbTimestamp) + 604800)) {

                long daysOut = (nowness.getTimeInMillis() / 1000) - Long.parseLong(dbTimestamp);
                daysOut = daysOut / 604800;
                //App crashes if exact duplicate of timestamp is saved in database. Attempting to
                // detect duplicates and then adjusting the timestamp on the second level
                long futureStamp = Integer.parseInt(dbTimestamp) + (604800 * daysOut);
                String tempTimestamp = "";
                for(int i = 0; i < MainActivity.taskList.size(); i++) {
                    Cursor tempResult = MainActivity.db.getData(Integer.parseInt(
                            MainActivity.sortedIDs.get(i)));
                    while (tempResult.moveToNext()) {
                        tempTimestamp = tempResult.getString(3);
                    }
                    tempResult.close();
                    if(futureStamp == Long.parseLong(tempTimestamp)){
                        futureStamp++;
                        i = 0;
                    }

                }

                //updating timestamp
                MainActivity.db.updateTimestamp(String.valueOf(
                        MainActivity.sortedIDs.get(position)),
                        String.valueOf(futureStamp));

                if(!alarmDay.equals("")) {
                    Calendar adjustedCalendar = Calendar.getInstance();
                    adjustedCalendar.setTimeInMillis(futureStamp * 1000);
                    int newDay = adjustedCalendar.get(Calendar.DAY_OF_MONTH);
                    int newMonth = adjustedCalendar.get(Calendar.MONTH);
                    int newYear = adjustedCalendar.get(Calendar.YEAR);

                    MainActivity.db.updateAlarmData(String.valueOf(MainActivity.sortedIDs
                                    .get(position)), alarmHour, alarmMinute, alarmAmpm,
                            String.valueOf(newDay), String.valueOf(newMonth),
                            String.valueOf(newYear));

                    //cancelling any snooze data
                    MainActivity.db.updateSnoozeData(String.valueOf(
                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                            "", "", "", "", "", "");
                }

            }
        }else if(dbRepeatInterval.equals("month") && !dbTimestamp.equals("")
                && !alarmYear.equals("")){

            //Getting interval in seconds based on specific day and month
            int interval;
            int theYear = Integer.parseInt(alarmYear);
            int theMonth = Integer.parseInt(alarmMonth);
            int theDay = Integer.parseInt(alarmDay);
            //Month February and due day 28 and previous day 31 non leap year
            if((theMonth == 1) && (theDay == 28) && (alarmDay.equals("31")) && (theYear % 4 != 0)){
                interval = 2419200;
            //Month February and due day 28 and previous day 30 non leap year
            }else if((theMonth == 1) && (theDay == 28) && (alarmDay.equals("30")) && (theYear % 4 != 0)) {
                interval = 2505600;
            //Month February and due day 28 and previous day 29 non leap year
            }else if((theMonth == 1) && (theDay == 28) && (alarmDay.equals("29")) && (theYear % 4 != 0)) {
                interval = 2592000;
            //Month February and due day 29 and previous day 31 leap year
            }else if((theMonth == 1) && (theDay == 29) && (alarmDay.equals("31")) && (theYear % 4 == 0)) {
                interval = 2505600;
            //Month February and due day 29 and previous day 30 leap year
            }else if((theMonth == 1) && (theDay == 29) && (alarmDay.equals("30")) && (theYear % 4 == 0)) {
                interval = 2592000;
            //Month February||April||June||September||November
            }else if(theMonth == 0 || theMonth == 1 || theMonth == 3 || theMonth == 5 || theMonth == 8 || theMonth == 10) {
                interval = 2678400;
            //Any other month
            }else{
                interval = 2592000;
            }

            if((nowness.getTimeInMillis() / 1000) >= (Integer.parseInt(dbTimestamp) + interval)) {

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

                long daysOut = (nowness.getTimeInMillis() / 1000) - Long.parseLong(dbTimestamp);
                daysOut = daysOut / interval;
                //App crashes if exact duplicate of timestamp is saved in database. Attempting to
                // detect duplicates and then adjusting the timestamp on the second level
                long futureStamp = Integer.parseInt(dbTimestamp) + (interval * daysOut);
                String tempTimestamp = "";
                for(int i = 0; i < MainActivity.taskList.size(); i++) {
                    Cursor tempResult = MainActivity.db.getData(Integer.parseInt(
                            MainActivity.sortedIDs.get(i)));
                    while (tempResult.moveToNext()) {
                        tempTimestamp = tempResult.getString(3);
                    }
                    tempResult.close();
                    if(futureStamp == Long.parseLong(tempTimestamp)){
                        futureStamp++;
                        i = 0;
                    }

                }

                //updating timestamp
                MainActivity.db.updateTimestamp(String.valueOf(
                        MainActivity.sortedIDs.get(position)),
                        String.valueOf(futureStamp));

                if(!alarmDay.equals("")) {
                    Calendar adjustedCalendar = Calendar.getInstance();
                    adjustedCalendar.setTimeInMillis(futureStamp * 1000);

                    //cancelling any snooze data
                    MainActivity.db.updateSnoozeData(String.valueOf(
                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                            "", "", "", "", "", "");
                }
            }
        }

        if(uniSetAlarm && (position == MainActivity.activeTask)){
            setAlarm(position, uniYear, uniMonth, uniDay, uniHour, uniMinute,
                    uniAmPm, uniInterval, uniOriginalDayTemp);
            MainActivity.db.updateSetAlarm(false);
            MainActivity.db.updateYear(0);
            MainActivity.db.updateMonth(0);
            MainActivity.db.updateDay(0);
            MainActivity.db.updateHour(0);
            MainActivity.db.updateMinute(0);
            MainActivity.db.updateAmPm(0);
            MainActivity.db.updateRepeatIntervalTemp("0");
            MainActivity.db.updateOriginalDayTemp("");
        }

        if(MainActivity.fadeTasks){
            //Fade out inactive taskviews
            taskView.startAnimation(AnimationUtils.loadAnimation
                    (getContext(), android.R.anim.fade_out));
            taskView.setVisibility(View.INVISIBLE);
        }

        if(MainActivity.longClicked) {
            complete.setVisibility(View.INVISIBLE);
            completeWhite.setVisibility(View.INVISIBLE);
            MainActivity.longClicked = false;
        }

        if(MainActivity.colorPickerShowing || MainActivity.purchasesShowing){
            complete.setClickable(false);
            completed.setClickable(false);
            completeWhite.setClickable(false);
            completedWhite.setClickable(false);
        }

        if(dbSnooze){

            dueLayout.setVisibility(View.GONE);

            snoozeClear.setBackgroundColor(Color.parseColor(MainActivity.highlight));
            snoozeClearWhite.setBackgroundColor(Color.parseColor(MainActivity.highlight));
            snoozeClearWhite.setBackgroundColor(Color.parseColor(MainActivity.highlight));

        }

        if(dbID == (MainActivity.sortedIDs.size() - 1)){
            MainActivity.showDueDates = true;
        }

        //Show due icon and due date if required
        if (dbDue && MainActivity.showDueDates) {

            Calendar currentDate = new GregorianCalendar();

            //Getting time data
            Cursor result = MainActivity.db.getSnoozeData(Integer.parseInt(
                    MainActivity.sortedIDs.get(position)));
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
            result.close();

            //If there is no snoozed alarm then get initial alarm
            if(hour.equals("")){
                //getting alarm data
                result = MainActivity.db.getAlarmData(
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

            int currentYear = currentDate.get(Calendar.YEAR);
            int currentMonth = currentDate.get(Calendar.MONTH);
            int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
            int currentHour = currentDate.get(Calendar.HOUR_OF_DAY);
            int currentMinute = currentDate.get(Calendar.MINUTE);
            int currentAmPm = currentDate.get(Calendar.AM_PM);

            //Checking for overdue tasks
            String formattedTime;
            Boolean sameDay = false;
            Boolean tomorrow = false;
            Boolean markAsOverdue = false;
            MainActivity.db.updateOverdue(MainActivity.sortedIDs.get(position), false);

                //Overdue
            if (currentYear > Integer.valueOf(year)) {
                dueClear.setVisibility(View.GONE);
                dueLayout.setVisibility(View.GONE);
                overdueClear.setVisibility(View.VISIBLE);
                overdueLayout.setVisibility(View.VISIBLE);
                if(!dbKilled) {
                    overdueClear.setBackgroundColor(ContextCompat
                            .getColor(getContext(), R.color.darkRed));
                    overdueClearWhite.setBackgroundColor(ContextCompat
                            .getColor(getContext(), R.color.darkRed));
                    dueTextView.setTextColor(ContextCompat.getColor
                            (getContext(), R.color.darkRed));
                }else if(!MainActivity.lightDark){
                    dueTextView.setTextColor(ContextCompat.getColor
                            (getContext(), R.color.gray));
                    overdueLayout.setVisibility(View.GONE);
                    dueLayout.setVisibility(View.VISIBLE);
                    dueClear.setVisibility(View.VISIBLE);
                    dueClear.setBackgroundColor(ContextCompat
                            .getColor(getContext(), R.color.gray));
                }else{
                    dueTextView.setTextColor(ContextCompat.getColor
                            (getContext(), R.color.black));
                    overdueLayout.setVisibility(View.GONE);
                    dueLayout.setVisibility(View.VISIBLE);
                    dueClearWhite.setVisibility(View.VISIBLE);
                    dueClearWhite.setBackgroundColor(ContextCompat
                            .getColor(getContext(), R.color.black));
                }
                markAsOverdue = true;
            //Overdue
            } else if (currentYear == Integer.valueOf(year)
                    && currentMonth > Integer.valueOf(month)) {
                dueClear.setVisibility(View.GONE);
                dueLayout.setVisibility(View.GONE);
                overdueClear.setVisibility(View.VISIBLE);
                overdueLayout.setVisibility(View.VISIBLE);
                dueTextView.setTextColor(Color.parseColor("#FF0000"));
                if(!dbKilled) {
                    overdueClear.setBackgroundColor(ContextCompat
                            .getColor(getContext(), R.color.darkRed));
                    overdueClearWhite.setBackgroundColor(ContextCompat
                            .getColor(getContext(), R.color.darkRed));
                    dueTextView.setTextColor(ContextCompat.getColor
                            (getContext(), R.color.darkRed));
                }else if(!MainActivity.lightDark){
                    dueTextView.setTextColor(ContextCompat.getColor
                            (getContext(), R.color.gray));
                    overdueLayout.setVisibility(View.GONE);
                    dueLayout.setVisibility(View.VISIBLE);
                    dueClear.setVisibility(View.VISIBLE);
                    dueClear.setBackgroundColor(ContextCompat
                            .getColor(getContext(), R.color.gray));
                }else{
                    dueTextView.setTextColor(ContextCompat.getColor
                            (getContext(), R.color.black));
                    overdueLayout.setVisibility(View.GONE);
                    dueLayout.setVisibility(View.VISIBLE);
                    dueClearWhite.setVisibility(View.VISIBLE);
                    dueClearWhite.setBackgroundColor(ContextCompat
                            .getColor(getContext(), R.color.black));
                }
                markAsOverdue = true;
            //Overdue
            } else if (currentYear == Integer.valueOf(year)
                    && currentMonth == Integer.valueOf(month)
                    && currentDay > Integer.valueOf(day)) {
                dueClear.setVisibility(View.GONE);
                dueLayout.setVisibility(View.GONE);
                overdueClear.setVisibility(View.VISIBLE);
                overdueLayout.setVisibility(View.VISIBLE);
                dueTextView.setTextColor(Color.parseColor("#FF0000"));
                if(!dbKilled) {
                    overdueClear.setBackgroundColor(ContextCompat
                            .getColor(getContext(), R.color.darkRed));
                    overdueClearWhite.setBackgroundColor(ContextCompat
                            .getColor(getContext(), R.color.darkRed));
                    dueTextView.setTextColor(ContextCompat.getColor
                            (getContext(), R.color.darkRed));
                }else if(!MainActivity.lightDark){
                    dueTextView.setTextColor(ContextCompat.getColor
                            (getContext(), R.color.gray));
                    overdueLayout.setVisibility(View.GONE);
                    dueLayout.setVisibility(View.VISIBLE);
                    dueClear.setVisibility(View.VISIBLE);
                    dueClear.setBackgroundColor(ContextCompat
                            .getColor(getContext(), R.color.gray));
                }else{
                    dueTextView.setTextColor(ContextCompat.getColor
                            (getContext(), R.color.black));
                    overdueLayout.setVisibility(View.GONE);
                    dueLayout.setVisibility(View.VISIBLE);
                    dueClearWhite.setVisibility(View.VISIBLE);
                    dueClearWhite.setBackgroundColor(ContextCompat
                            .getColor(getContext(), R.color.black));
                }
                markAsOverdue = true;
            } else if (currentYear == Integer.valueOf(year)
                    && currentMonth == Integer.valueOf(month)
                    && currentDay == Integer.valueOf(day)) {
                sameDay = true;
                //Saved hours are in 12 hour time. Accounting for am/pm.
                int adjustedHour;
                if (Integer.valueOf(ampm) == 1) {
                    adjustedHour = Integer.valueOf(hour) + 12;
                } else {
                    adjustedHour = Integer.valueOf(hour);
                }
                //Overdue
                if (currentHour > adjustedHour) {
                    dueClear.setVisibility(View.GONE);
                    dueLayout.setVisibility(View.GONE);
                    overdueClear.setVisibility(View.VISIBLE);
                    overdueLayout.setVisibility(View.VISIBLE);
                    dueTextView.setTextColor(Color.parseColor("#FF0000"));
                    if(!dbKilled) {
                        overdueClear.setBackgroundColor(ContextCompat
                                .getColor(getContext(), R.color.darkRed));
                        overdueClearWhite.setBackgroundColor(ContextCompat
                                .getColor(getContext(), R.color.darkRed));
                        dueTextView.setTextColor(ContextCompat.getColor
                                (getContext(), R.color.darkRed));
                    }else if(!MainActivity.lightDark){
                        dueTextView.setTextColor(ContextCompat.getColor
                                (getContext(), R.color.gray));
                        overdueLayout.setVisibility(View.GONE);
                        dueLayout.setVisibility(View.VISIBLE);
                        dueClear.setVisibility(View.VISIBLE);
                        dueClear.setBackgroundColor(ContextCompat
                                .getColor(getContext(), R.color.gray));
                    }else{
                        dueTextView.setTextColor(ContextCompat.getColor
                                (getContext(), R.color.black));
                        overdueLayout.setVisibility(View.GONE);
                        dueLayout.setVisibility(View.VISIBLE);
                        dueClearWhite.setVisibility(View.VISIBLE);
                        dueClearWhite.setBackgroundColor(ContextCompat
                                .getColor(getContext(), R.color.black));
                    }
                    markAsOverdue = true;
                //Overdue
                } else if ((currentHour == adjustedHour ||
                        (currentHour == 0 && Integer.valueOf(hour) == 12 &&
                                Integer.valueOf(ampm) == 0) ||
                        (currentHour == 12 && Integer.parseInt(hour) == 12))
                        && currentMinute >= Integer.valueOf(minute)) {
                    dueClear.setVisibility(View.GONE);
                    dueLayout.setVisibility(View.GONE);
                    overdueClear.setVisibility(View.VISIBLE);
                    overdueLayout.setVisibility(View.VISIBLE);
                    dueTextView.setTextColor(Color.parseColor("#FF0000"));
                    if(!dbKilled) {
                        overdueClear.setBackgroundColor(ContextCompat
                                .getColor(getContext(), R.color.darkRed));
                        overdueClearWhite.setBackgroundColor(ContextCompat
                                .getColor(getContext(), R.color.darkRed));
                        dueTextView.setTextColor(ContextCompat.getColor
                                (getContext(), R.color.darkRed));
                    }else if(!MainActivity.lightDark){
                        dueTextView.setTextColor(ContextCompat.getColor
                                (getContext(), R.color.gray));
                        overdueLayout.setVisibility(View.GONE);
                        dueLayout.setVisibility(View.VISIBLE);
                        dueClear.setVisibility(View.VISIBLE);
                        dueClear.setBackgroundColor(ContextCompat
                                .getColor(getContext(), R.color.gray));
                    }else{
                        dueTextView.setTextColor(ContextCompat.getColor
                                (getContext(), R.color.black));
                        overdueLayout.setVisibility(View.GONE);
                        dueLayout.setVisibility(View.VISIBLE);
                        dueClearWhite.setVisibility(View.VISIBLE);
                        dueClearWhite.setBackgroundColor(ContextCompat
                               .getColor(getContext(), R.color.black));
                    }
                    markAsOverdue = true;
                //Not overdue
                } else {
                    if(!dbKilled) {
                        dueClear.setBackgroundColor(Color.parseColor(MainActivity.highlight));
                        dueClearWhite.setBackgroundColor(Color.parseColor
                                (MainActivity.highlight));
                    }else{
                        if(MainActivity.lightDark) {
                            dueClearWhite.setBackgroundColor(ContextCompat
                                    .getColor(getContext(), R.color.black));
                        }else {
                            dueClear.setBackgroundColor(ContextCompat
                                    .getColor(getContext(), R.color.gray));
                        }
                    }
                }
            //Not overdue
            } else {
                //Checking if date due tomorrow
                //Incrementing day
                if(currentYear == Integer.parseInt(year)) {
                    if (((currentMonth == 0) || (currentMonth == 2)
                            || (currentMonth == 4) || (currentMonth == 6)
                            || (currentMonth == 7) || (currentMonth == 9))
                            && (currentDay == 31) && (Integer.valueOf(day) == 1)
                            && (currentMonth == (Integer.valueOf(month) - 1))) {
                        tomorrow = true;
                    } else if (((currentMonth == 1) || (currentMonth == 3)
                            || (currentMonth == 5) || (currentMonth == 8)
                            || (currentMonth == 10)) && (currentDay == 30)
                            && (Integer.valueOf(day) == 1)
                            && (currentMonth == (Integer.valueOf(month) - 1))) {
                        tomorrow = true;
                    } else if ((currentMonth == 11) && (currentDay == 31)
                            && (Integer.valueOf(day) == 1)
                            && (currentMonth == (Integer.valueOf(month) - 1))) {
                        tomorrow = true;
                    } else if ((currentMonth == 1) && (currentDay == 28)
                            && (currentYear % 4 != 0) && (Integer.valueOf(day) == 1)
                            && (currentMonth == (Integer.valueOf(month) - 1))) {
                        tomorrow = true;
                    } else if ((currentMonth == 1) && (currentDay == 29)
                            && (currentYear % 4 == 0) && (Integer.valueOf(day) == 1)
                            && (currentMonth == (Integer.valueOf(month) - 1))) {
                        tomorrow = true;
                    } else if (currentDay == (Integer.valueOf(day) - 1) &&
                            currentMonth == Integer.valueOf(month)) {
                        tomorrow = true;
                    }
                }
                if(!dbKilled) {
                    dueClear.setBackgroundColor(Color.parseColor(MainActivity.highlight));
                    dueClearWhite.setBackgroundColor(Color.parseColor(MainActivity.highlight));
                }else{
                    dueClear.setBackgroundColor(ContextCompat
                            .getColor(getContext(), R.color.gray));
                    dueClearWhite.setBackgroundColor(Color.parseColor(MainActivity.highlight));
                }
            }

            //determine if snoozed alarm is overdue or not
            if(dbSnooze) {

                //Show overdue icon
                if (markAsOverdue) {
                    snoozeClear.setVisibility(View.GONE);
                    snoozeLayout.setVisibility(View.GONE);
                    overdueClear.setVisibility(View.VISIBLE);
                    overdueLayout.setVisibility(View.VISIBLE);
                    MainActivity.db.updateSnooze(
                            MainActivity.sortedIDs.get(position), false);
                    MainActivity.db.updateSnoozedTimestamp(MainActivity.sortedIDs.get(position),
                            "0");
                    //show snooze icon
                } else if(!dbKilled){
                    snoozeClear.setVisibility(View.VISIBLE);
                    snoozeLayout.setVisibility(View.VISIBLE);
                    overdueClear.setVisibility(View.GONE);
                    overdueLayout.setVisibility(View.GONE);
                }else{
                    dueClear.setVisibility(View.VISIBLE);
                    dueLayout.setVisibility(View.VISIBLE);
                }

            }else if(markAsOverdue){

                MainActivity.db.updateOverdue(String.valueOf(
                        MainActivity.sortedIDs.get(position)), true);
                dbOverdue = true;

            }

            //If task due on following day say "Tomorrow"
            if (tomorrow){

                dueTextView.setText(R.string.tomorrow);

            //If task due on same day show the due date
            } else if(!sameDay){
                //Formatting date
                String formattedMonth = "";
                String formattedDate;

                int intMonth = Integer.valueOf(month) + 1;
                if(intMonth == 1){
                    formattedMonth = getContext().getString(R.string.jan);
                }else if(intMonth == 2){
                    formattedMonth = getContext().getString(R.string.feb);
                }else if(intMonth == 3){
                    formattedMonth = getContext().getString(R.string.mar);
                }else if(intMonth == 4){
                    formattedMonth = getContext().getString(R.string.apr);
                }else if(intMonth == 5){
                    formattedMonth = getContext().getString(R.string.may);
                }else if(intMonth == 6){
                    formattedMonth = getContext().getString(R.string.jun);
                }else if(intMonth == 7){
                    formattedMonth = getContext().getString(R.string.jul);
                }else if(intMonth == 8){
                    formattedMonth = getContext().getString(R.string.aug);
                }else if(intMonth == 9){
                    formattedMonth = getContext().getString(R.string.sep);
                }else if(intMonth == 10){
                    formattedMonth = getContext().getString(R.string.oct);
                }else if(intMonth == 11){
                    formattedMonth = getContext().getString(R.string.nov);
                }else if(intMonth == 12){
                    formattedMonth = getContext().getString(R.string.dec);
                }

                String lang = String.valueOf(Locale.getDefault());
                if(lang.equals("en_AS") || lang.equals("en_BM")
                        || lang.equals("en_CA") || lang.equals("en_GU")
                        || lang.equals("en_PH")
                        || lang.equals("en_PR") || lang.equals("en_UM")
                        || lang.equals("en_US") || lang.equals("en_VI")) {
                    formattedDate = formattedMonth + " " + day;
                }else{
                    formattedDate = day + " " + formattedMonth;
                }

                dueTextView.setText(formattedDate);

            //If task due on different day show the due time
            }else{

                if(Integer.valueOf(hour) == 0){
                    hour = getContext().getString(R.string.twelveNumerals);
                }else if(Integer.valueOf(hour) > 12){
                    hour = String.valueOf(Integer.parseInt(hour) - 12);
                }
                if(Integer.valueOf(minute) < 10){
                    if(Integer.valueOf(ampm) == 0) {
                        formattedTime = hour + getContext().getString(R.string.colonZero)
                                + minute + getContext().getString(R.string.am);
                    }else{
                        formattedTime = hour + getContext().getString(R.string.colonZero)
                                + minute + getContext().getString(R.string.pm);
                    }
                }else{
                    if(Integer.valueOf(ampm) == 0) {
                        formattedTime = hour + ":" + minute + getContext().getString(R.string.am);
                    }else{
                        formattedTime = hour + ":" + minute + getContext().getString(R.string.pm);
                    }
                }

                dueTextView.setText(formattedTime);
            }

        }else if(MainActivity.lightDark){
            dueClearWhite.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }

        //actions to occur in regards to selected task
        if((MainActivity.completeTask) && (MainActivity.thePosition == position)){

            MainActivity.vibrate.vibrate(100);

            //task is killed if not repeating
            if(!finalDbRepeat) {

                notifyDataSetChanged();

                MainActivity.taskPropertiesShowing = false;

                MainActivity.db.updateKilled(String.valueOf(
                        MainActivity.sortedIDs.get(position)), true);

                MainActivity.db.updateSnoozedTimestamp(MainActivity.sortedIDs.get(position),
                        "0");

                MainActivity.db.updateIgnored(MainActivity.sortedIDs
                        .get(position), false);

                MainActivity.db.updateManualKill(String.valueOf(
                        MainActivity.sortedIDs.get(position)), true);

                MainActivity.db.updateOverdue(String.valueOf(MainActivity.sortedIDs
                        .get(position)), false);

                //cancelling any snooze data
                MainActivity.db.updateSnoozeData(String.valueOf(
                        MainActivity.sortedIDs.get(position)),
                        "", "", "", "", "", "");
                MainActivity.db.updateSnooze(String.valueOf(MainActivity.sortedIDs
                        .get(position)), false);

                if(MainActivity.reinstateHint <= 2) {
                    if(MainActivity.reinstateHint == 2) {
                        MainActivity.toast.setText
                                (R.string.longClickToReinstate);
                        final Handler handler = new Handler();

                        final Runnable runnable = new Runnable() {
                            public void run() {
                                MainActivity.hint.start();
                                MainActivity.toastView.startAnimation(AnimationUtils.loadAnimation
                                        (getContext(), R.anim.enter_from_right_fast));
                                MainActivity.toastView.setVisibility(View.VISIBLE);
                                final Handler handler2 = new Handler();
                                final Runnable runnable2 = new Runnable() {
                                    public void run() {
                                        MainActivity.toastView.startAnimation
                                                (AnimationUtils.loadAnimation
                                                (getContext(), android.R.anim.fade_out));
                                        MainActivity.toastView.setVisibility(View.GONE);
                                    }
                                };
                                handler2.postDelayed(runnable2, 2500);
                            }
                        };

                        handler.postDelayed(runnable, 500);
                    }else{
                        if(MainActivity.showMotivation) {
                            MainActivity.toast.setText(R.string.youKilledThisTask);
                            final Handler handler = new Handler();

                            final Runnable runnable = new Runnable() {
                                public void run() {
                                    if (!MainActivity.mute) {
                                        MainActivity.sweep.start();
                                    }
                                    MainActivity.toastView.startAnimation
                                            (AnimationUtils.loadAnimation
                                            (getContext(), R.anim.enter_from_right_fast));
                                    MainActivity.toastView.setVisibility(View.VISIBLE);
                                    final Handler handler2 = new Handler();
                                    final Runnable runnable2 = new Runnable() {
                                        public void run() {
                                            MainActivity.toastView.startAnimation(AnimationUtils
                                                    .loadAnimation(getContext(),
                                                            android.R.anim.fade_out));
                                            MainActivity.toastView.setVisibility(View.GONE);
                                        }
                                    };
                                    handler2.postDelayed(runnable2, 1500);
                                }
                            };

                            handler.postDelayed(runnable, 500);
                        }
                    }
                    MainActivity.reinstateHint++;
                    MainActivity.db.updateReinstateHint(MainActivity.reinstateHint);
                }else{
                    if(MainActivity.showMotivation) {
                        MainActivity.blockSoundAndAnimate = true;
                        MainActivity.toast.setText(R.string.youKilledThisTask);
                        final Handler handler = new Handler();

                        final Runnable runnable = new Runnable() {
                            public void run() {
                                MainActivity.blockSoundAndAnimate = false;
                                if (!MainActivity.mute) {
                                    MainActivity.sweep.start();
                                }
                                MainActivity.toastView.startAnimation(AnimationUtils.loadAnimation
                                        (getContext(), R.anim.enter_from_right_fast));
                                MainActivity.toastView.setVisibility(View.VISIBLE);
                                final Handler handler2 = new Handler();
                                final Runnable runnable2 = new Runnable() {
                                    public void run() {
                                        MainActivity.toastView.startAnimation(AnimationUtils
                                                .loadAnimation(getContext(),
                                                        android.R.anim.fade_out));
                                        MainActivity.toastView.setVisibility(View.GONE);
                                    }
                                };
                                handler2.postDelayed(runnable2, 1500);
                            }
                        };

                        handler.postDelayed(runnable, 500);
                    }
                }

                if(!MainActivity.remindersAvailable && !dbTimestamp.equals("0")) {
                    MainActivity.duesSet--;
                    MainActivity.db.updateDuesSet(MainActivity.duesSet);
                }

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
                MainActivity.addIcon.setVisibility(View.VISIBLE);

                MainActivity.vibrate.vibrate(50);

                MainActivity.params.height = MainActivity.addHeight;
                MainActivity.iconParams.height = MainActivity.addIconHeight;

                taskView.setLayoutParams(MainActivity.params);
                taskView.setLayoutParams(MainActivity.iconParams);

                new Reorder();
                //Updating the view with the new order
                MainActivity.theAdapter = new ListAdapter[]{new MyAdapter(
                        getContext(), MainActivity.taskList)};
                MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

            //task is updated to be due at next repeat
            }else {

                int interval = 0;
                int newDay;
                int newMonth;
                int newYear;
                long futureStamp = 0;
                int daysOut = 0;

                if (finalDbRepeatInterval.equals("day")) {

                    //App crashes if exact duplicate of timestamp is saved in database. Attempting to
                    // detect duplicates and then adjusting the timestamp on the millisecond level
                    futureStamp = Long.parseLong(dbTimestamp) + (AlarmManager.INTERVAL_DAY / 1000);
                    String tempTimestamp = "";
                    for (int i = 0; i < MainActivity.taskList.size(); i++) {
                        Cursor tempResult = MainActivity.db.getData(Integer.parseInt(
                                MainActivity.sortedIDs.get(i)));
                        while (tempResult.moveToNext()) {
                            tempTimestamp = tempResult.getString(3);
                        }
                        tempResult.close();
                        if (futureStamp == Long.parseLong(tempTimestamp)) {
                            futureStamp++;
                            i = 0;
                        }
                    }

                    futureStamp = futureStamp * 1000;

                } else if (finalDbRepeatInterval.equals("week")) {

                    //App crashes if exact duplicate of timestamp is saved in database. Attempting to
                    // detect duplicates and then adjusting the timestamp on the millisecond level
                    futureStamp = Long.parseLong(dbTimestamp) +
                            ((AlarmManager.INTERVAL_DAY * 7) / 1000);
                    String tempTimestamp = "";
                    for (int i = 0; i < MainActivity.taskList.size(); i++) {
                        Cursor tempResult = MainActivity.db.getData(Integer.parseInt(
                                MainActivity.sortedIDs.get(i)));
                        while (tempResult.moveToNext()) {
                            tempTimestamp = tempResult.getString(3);
                        }
                        tempResult.close();
                        if (futureStamp == Long.parseLong(tempTimestamp)) {
                            futureStamp++;
                            i = 0;
                        }

                    }

                    futureStamp = futureStamp * 1000;

                } else if (finalDbRepeatInterval.equals("month")) {

                    //getting interval based on current day and month
                    int theYear = Integer.parseInt(finalAlarmYear);
                    int theMonth = Integer.parseInt(finalAlarmMonth);
                    int theDay = Integer.parseInt(finalAlarmDay);
                    //Month January and day is 29 non leap year 2592000
                    if ((theMonth == 0) && (theDay == 29) && (theYear % 4 != 0)) {
                        interval = 2592000;
                        //Month January and day is 30 non leap year 2505600
                    } else if ((theMonth == 0) && (theDay == 30) && (theYear % 4 != 0)) {
                        interval = 2505600;
                        //Month January and day is 31 non leap year 2419200
                    } else if ((theMonth == 0) && (theDay == 31) && (theYear % 4 != 0)) {
                        interval = 2419200;
                        //Month January and day is 30 leap year 2592000
                    } else if ((theMonth == 0) && (theDay == 30) && (theYear % 4 == 0)) {
                        interval = 2592000;
                        //Month January and day is 31 leap year 2505600
                    } else if ((theMonth == 0) && (theDay == 31) && (theYear % 4 == 0)) {
                        interval = 2505600;
                        //Month March||May||August||October and day is 31 2592000
                    } else if (((theMonth == 2) || (theMonth == 4) || (theMonth == 7)
                            || (theMonth == 9)) && (theDay == 31)) {
                        interval = 2592000;
                        //Month January||March||May||July||August||October||December 2678400
                    } else if ((theMonth == 0) || (theMonth == 2) || (theMonth == 4)
                            || (theMonth == 6) || (theMonth == 7) || (theMonth == 9)
                            || (theMonth == 11)) {
                        interval = 2678400;
                        //Month April||June||September||November 2592000
                    } else if ((theMonth == 3) || (theMonth == 5) || (theMonth == 8)
                            || (theMonth == 10)) {
                        interval = 2592000;
                        //Month February non leap year 2419200
                    } else if ((theMonth == 1) && (theYear % 4 != 0)) {
                        interval = 2419200;
                        //Month February leap year 2505600
                    } else if ((theMonth == 1) && (theYear % 4 == 0)) {
                        interval = 2505600;
                    }

                    //App crashes if exact duplicate of timestamp is saved in database. Attempting to
                    // detect duplicates and then adjusting the timestamp on the millisecond level
                    futureStamp = (Long.parseLong(dbTimestamp) + interval/*monthInterval*/);
                    String tempTimestamp = "";
                    for (int i = 0; i < MainActivity.taskList.size(); i++) {
                        Cursor tempResult = MainActivity.db.getData(Integer.parseInt(
                                MainActivity.sortedIDs.get(i)));
                        while (tempResult.moveToNext()) {
                            tempTimestamp = tempResult.getString(3);
                        }
                        tempResult.close();
                        if (futureStamp == Long.parseLong(tempTimestamp)) {
                            futureStamp++;
                            i = 0;
                        }

                    }

                    futureStamp = Long.parseLong(String.valueOf(futureStamp) + "000");
                    Cursor originalResult = MainActivity.db.getData(Integer.parseInt(
                            MainActivity.sortedIDs.get(position)));
                    String origDay = "";
                    while (originalResult.moveToNext()) {
                        origDay = originalResult.getString(20);
                    }
                    originalResult.close();
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(futureStamp);
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    int month = cal.get(Calendar.MONTH);

                    if (day != Integer.parseInt(origDay)) {
                        if (month == 0 && (day == 28 || day == 29 || day == 30)) {
                            daysOut = Integer.parseInt(origDay) - day;
                            futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                        } else if (month == 2 && (day == 28 || day == 29 || day == 30)) {
                            daysOut = Integer.parseInt(origDay) - day;
                            futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                        } else if (month == 3 && (day == 28 || day == 29)) {
                            daysOut = Integer.parseInt(origDay) - day;
                            futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                        } else if (month == 4 && (day == 28 || day == 29 || day == 30)) {
                            daysOut = Integer.parseInt(origDay) - day;
                            futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                        } else if (month == 5 && (day == 28 || day == 29)) {
                            daysOut = Integer.parseInt(origDay) - day;
                            futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                        } else if (month == 6 && (day == 28 || day == 29 || day == 30)) {
                            daysOut = Integer.parseInt(origDay) - day;
                            futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                        } else if (month == 7 && (day == 28 || day == 29 || day == 30)) {
                            daysOut = Integer.parseInt(origDay) - day;
                            futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                        } else if (month == 8 && (day == 28 || day == 29)) {
                            daysOut = Integer.parseInt(origDay) - day;
                            futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                        } else if (month == 9 && (day == 28 || day == 29 || day == 30)) {
                            daysOut = Integer.parseInt(origDay) - day;
                            futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                        } else if (month == 10 && (day == 28 || day == 29)) {
                            daysOut = Integer.parseInt(origDay) - day;
                            futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                        } else if (month == 11 && (day == 28 || day == 29 || day == 30)) {
                            daysOut = Integer.parseInt(origDay) - day;
                            futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                        }
                    }
                }

                futureStamp = futureStamp / 1000;

                Calendar currentCal = Calendar.getInstance();
                Calendar futureCal = Calendar.getInstance();
                futureCal.setTimeInMillis(futureStamp * 1000);
                Calendar timestampCal = Calendar.getInstance();
                timestampCal.setTimeInMillis(Long.parseLong(dbTimestamp) * 1000);
                Long diff = (Long.parseLong(dbTimestamp) * 1000) - currentCal.getTimeInMillis();
                diff = diff / 1000;

                int repeatInterval = 0;
                if(finalDbRepeatInterval.equals("day")){
                    repeatInterval = 86400;
                }else if(finalDbRepeatInterval.equals("week")){
                    repeatInterval = (86400 * 7);
                }else if(finalDbRepeatInterval.equals("month")){
                    String bigInterval = String.valueOf(interval + ((AlarmManager.INTERVAL_DAY * daysOut) / 1000));
                    repeatInterval = Integer.parseInt(bigInterval);
                }

                Calendar snoozeCal = Calendar.getInstance();
                //updating timestamp
                if(!finalDbRepeatInterval.equals("month")) {
                    if (!dbOverdue && (Integer.parseInt(alarmDay)
                            != currentCal.get(Calendar.DAY_OF_MONTH))) {
                        MainActivity.db.updateTimestamp(String.valueOf(
                                MainActivity.sortedIDs.get(position)),
                                String.valueOf(futureStamp));
                        snoozeCal.setTimeInMillis(futureStamp * 1000);
                    } else if (!dbOverdue && diff < repeatInterval && (Integer.parseInt(alarmDay)
                            == currentCal.get(Calendar.DAY_OF_MONTH))) {
                        Long value = Long.parseLong(finalDbTimestamp) + repeatInterval;
                        MainActivity.db.updateTimestamp(String.valueOf(
                                MainActivity.sortedIDs.get(position)),
                                String.valueOf(value));
                        snoozeCal.setTimeInMillis(value * 1000);
                    } else if (dbOverdue && diff < repeatInterval && (Integer.parseInt(alarmDay)
                            == currentCal.get(Calendar.DAY_OF_MONTH))) {
                        Long value = Long.parseLong(finalDbTimestamp);
                        MainActivity.db.updateTimestamp(String.valueOf(
                                MainActivity.sortedIDs.get(position)),
                                String.valueOf(value));
                        snoozeCal.setTimeInMillis(value * 1000);
                    } else if (diff < repeatInterval) {
                        MainActivity.db.updateTimestamp(String.valueOf(
                                MainActivity.sortedIDs.get(position)),
                                String.valueOf(finalDbTimestamp));
                        snoozeCal.setTimeInMillis(Long.parseLong(finalDbTimestamp) * 1000);
                    } else if (!dbOverdue) {
                        int daysWrong = (int) (diff / repeatInterval);
                        MainActivity.db.updateTimestamp(String.valueOf(
                                MainActivity.sortedIDs.get(position)),
                                String.valueOf((Integer.parseInt(finalDbTimestamp)
                                        - (repeatInterval * daysWrong)) + repeatInterval));
                        snoozeCal.setTimeInMillis(((Long.parseLong(finalDbTimestamp)
                                - (repeatInterval * daysWrong)) + repeatInterval) * 1000);
                    } else {
                        int daysWrong = (int) (diff / repeatInterval);
                        MainActivity.db.updateTimestamp(String.valueOf(
                                MainActivity.sortedIDs.get(position)),
                                String.valueOf(Integer.parseInt(finalDbTimestamp)
                                        - (repeatInterval * daysWrong)));
                        snoozeCal.setTimeInMillis((Long.parseLong(finalDbTimestamp)
                                - (repeatInterval * daysWrong)) * 1000);
                    }
                }else{
                    if (!dbOverdue && (Integer.parseInt(alarmMonth)
                            != currentCal.get(Calendar.MONTH))) {
                        MainActivity.db.updateTimestamp(String.valueOf(
                                MainActivity.sortedIDs.get(position)),
                                String.valueOf(futureStamp));
                        snoozeCal.setTimeInMillis(futureStamp * 1000);
                    } else if (!dbOverdue && diff < repeatInterval && (Integer.parseInt(alarmMonth)
                            == currentCal.get(Calendar.MONTH))) {
                        Long value = Long.parseLong(finalDbTimestamp) + repeatInterval;
                        MainActivity.db.updateTimestamp(String.valueOf(
                                MainActivity.sortedIDs.get(position)),
                                String.valueOf(value));
                        snoozeCal.setTimeInMillis(value * 1000);
                    } else if (dbOverdue && diff < repeatInterval && (Integer.parseInt(alarmMonth)
                            == currentCal.get(Calendar.MONTH))) {
                        Long value = Long.parseLong(finalDbTimestamp);
                        MainActivity.db.updateTimestamp(String.valueOf(
                                MainActivity.sortedIDs.get(position)),
                                String.valueOf(value));
                        snoozeCal.setTimeInMillis(value * 1000);
                    } else if (diff < repeatInterval) {
                        MainActivity.db.updateTimestamp(String.valueOf(
                                MainActivity.sortedIDs.get(position)),
                                String.valueOf(finalDbTimestamp));
                        snoozeCal.setTimeInMillis(Long.parseLong(finalDbTimestamp) * 1000);
                    } else if (!dbOverdue) {
                        int daysWrong = (int) (diff / repeatInterval);
                        MainActivity.db.updateTimestamp(String.valueOf(
                                MainActivity.sortedIDs.get(position)),
                                String.valueOf((Integer.parseInt(finalDbTimestamp)
                                        - (repeatInterval * daysWrong)) + repeatInterval));
                        snoozeCal.setTimeInMillis(((Long.parseLong(finalDbTimestamp)
                                - (repeatInterval * daysWrong)) + repeatInterval) * 1000);
                    } else {
                        Long value = Long.parseLong(finalDbTimestamp);
                        MainActivity.db.updateTimestamp(String.valueOf(
                                MainActivity.sortedIDs.get(position)),
                                String.valueOf(value));
                        snoozeCal.setTimeInMillis(value * 1000);
                    }
                }

                if(dbSnooze) {
                    diff = snoozeCal.getTimeInMillis() - currentCal.getTimeInMillis();
                    diff = diff / 1000;
                    long daysWrong = diff / repeatInterval;
                    long snoozeMillisForCalculation = snoozeCal.getTimeInMillis();
                    long intervalForCalculation = repeatInterval * daysWrong;
                    snoozeCal.setTimeInMillis(snoozeMillisForCalculation - (intervalForCalculation * 1000));
                }else if((timestampCal.get(Calendar.DAY_OF_MONTH) != Integer.parseInt(alarmDay))
                        || (finalDbRepeatInterval.equals("month") && (timestampCal.get(Calendar.MONTH)
                        != Integer.parseInt(alarmMonth)))){
                    diff = snoozeCal.getTimeInMillis() - timestampCal.getTimeInMillis();
                    diff = diff / 1000;
                    int daysWrong = (int) (diff / repeatInterval);
                    if(daysWrong >=0) {
                        snoozeCal.setTimeInMillis((snoozeCal.getTimeInMillis()
                                - ((repeatInterval * daysWrong) * 1000)));
                    }
                }else if(dbOverdue){
                    snoozeCal.setTimeInMillis((snoozeCal.getTimeInMillis()
                            + (repeatInterval * 1000)));
                }

                newYear = snoozeCal.get(Calendar.YEAR);
                newMonth = snoozeCal.get(Calendar.MONTH);
                newDay = snoozeCal.get(Calendar.DAY_OF_MONTH);

                //updating due time in database
                MainActivity.db.updateAlarmData(String.valueOf(
                        MainActivity.sortedIDs.get(position)),
                        finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                        String.valueOf(newDay), String.valueOf(newMonth),
                        String.valueOf(newYear));

                //cancelling any snoozed alarm data
                MainActivity.db.updateSnoozeData(String.valueOf(
                        MainActivity.sortedIDs.get(position)), "", "", "",
                        "", "", "");

                MainActivity.db.updateSnoozedTimestamp(String.valueOf(MainActivity
                        .sortedIDs.get(position)), "0");

                MainActivity.db.updateSnooze(String.valueOf(MainActivity.sortedIDs
                        .get(position)), false);

                MainActivity.db.updateManualKill(String.valueOf(
                        MainActivity.sortedIDs.get(position)), true);

                if (!dbOverdue) {
                    MainActivity.db.updateKilledEarly(String.valueOf(
                            MainActivity.sortedIDs.get(position)), true);
                }

                MainActivity.db.updateShowOnce(
                        MainActivity.sortedIDs.get(position), true);

                if(MainActivity.repeatHint <= 10) {
                    if((MainActivity.repeatHint == 1) || (MainActivity.repeatHint == 10)) {
                        MainActivity.toast.setText(R.string.youCanCancelRepeat);
                        final Handler handler = new Handler();

                        final Runnable runnable = new Runnable() {
                            public void run() {
                                MainActivity.hint.start();
                                MainActivity.toastView.startAnimation(AnimationUtils.loadAnimation
                                        (getContext(), R.anim.enter_from_right_fast));
                                MainActivity.toastView.setVisibility(View.VISIBLE);
                                final Handler handler2 = new Handler();
                                final Runnable runnable2 = new Runnable() {
                                    public void run() {
                                        MainActivity.toastView.startAnimation
                                                (AnimationUtils.loadAnimation
                                                (getContext(), android.R.anim.fade_out));
                                        MainActivity.toastView.setVisibility(View.GONE);
                                    }
                                };
                                handler2.postDelayed(runnable2, 2500);
                            }
                        };

                        handler.postDelayed(runnable, 500);
                    }
                    MainActivity.repeatHint++;
                    MainActivity.db.updateRepeatHint(MainActivity.repeatHint);
                }

                propertyRow.setVisibility(View.GONE);

                MainActivity.taskPropertiesShowing = false;

                new Reorder();

                MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

            }

        }else if(MainActivity.taskPropertiesShowing && position == MainActivity.activeTask){

            //show the overdue properties
            if(dbOverdue && !dbSnooze && !dbIgnored){

                ViewGroup.LayoutParams params = snoozeTask.getLayoutParams();
                params.width = MainActivity.deviceWidth / 3;
                snoozeTask.setLayoutParams(params);
                taskDone.setLayoutParams(params);
                taskIgnore.setLayoutParams(params);

                taskOverdueRow.startAnimation(AnimationUtils.loadAnimation
                        (getContext(), android.R.anim.slide_in_left));
                taskOverdueRow.setVisibility(View.VISIBLE);

                //Actions to occur if user selects 'snooze'
                snoozeTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MainActivity.vibrate.vibrate(50);

                        taskOverdueRow.startAnimation(AnimationUtils.loadAnimation
                                (getContext(), R.anim.exit_out_right));

                        ViewGroup.LayoutParams params = oneHourBtn.getLayoutParams();
                        params.width = MainActivity.deviceWidth / 3;
                        oneHourBtn.setLayoutParams(params);
                        fourHourBtn.setLayoutParams(params);
                        tomorrowBtn.setLayoutParams(params);

                        final Handler handler = new Handler();

                        final Runnable runnable = new Runnable() {
                            public void run() {
                                taskOverdueRow.setVisibility(View.GONE);
                                snoozeRow.startAnimation(AnimationUtils.loadAnimation
                                        (getContext(), R.anim.enter_from_right));
                                snoozeRow.setVisibility(View.VISIBLE);
                            }
                        };

                        handler.postDelayed(runnable, 600);

                        MainActivity.snoozeRowShowing = true;

                        //Actions to occur if user selects '1 hour'
                        oneHourBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {

                                MainActivity.vibrate.vibrate(50);

                                snoozeRow.startAnimation(AnimationUtils.loadAnimation
                                        (getContext(), R.anim.exit_out_right));

                                final Handler handler = new Handler();

                                final Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {

                                        MainActivity.db.updateInterval(String.valueOf(MainActivity
                                                .sortedIDs.get(position)), String.valueOf(1));

                                        Calendar dateNow = Calendar.getInstance();

                                        //checking if there is enough time before next
                                        // repeat to snooze for an hour
                                        boolean dontSnooze = false;
                                        if(finalDbRepeat){
                                            if (finalDbRepeatInterval.equals("day")) {
                                                if((dateNow.getTimeInMillis() / 1000) >= (Integer
                                                        .parseInt(finalDbTimestamp)
                                                        - (AlarmManager.INTERVAL_HOUR / 1000))){
                                                    dontSnooze = true;
                                                }
                                            } else if (finalDbRepeatInterval.equals("week")) {
                                                if ((dateNow.getTimeInMillis() / 1000) >= (Integer
                                                        .parseInt(finalDbTimestamp)
                                                        - (AlarmManager.INTERVAL_HOUR / 1000))) {
                                                    dontSnooze = true;
                                                }
                                            } else if (finalDbRepeatInterval.equals("month")) {
                                                if ((dateNow.getTimeInMillis() / 1000) >= (Integer
                                                        .parseInt(finalDbTimestamp)
                                                        - (AlarmManager.INTERVAL_HOUR / 1000))) {
                                                    dontSnooze = true;
                                                }
                                            }
                                        }

                                        if(dontSnooze){

                                            MainActivity.toast.setText
                                                    (R.string.taskNotSnoozedBecause);
                                            final Handler handler = new Handler();

                                            final Runnable runnable = new Runnable() {
                                                public void run() {
                                                    if(!MainActivity.mute) {
                                                        MainActivity.sweep.start();
                                                    }
                                                    MainActivity.toastView.startAnimation
                                                            (AnimationUtils.loadAnimation(getContext(),
                                                                    R.anim.enter_from_right_fast));
                                                    MainActivity.toastView.setVisibility(View.VISIBLE);
                                                    final Handler handler2 = new Handler();
                                                    final Runnable runnable2 = new Runnable(){
                                                        public void run(){
                                                            MainActivity.toastView.startAnimation
                                                                    (AnimationUtils.loadAnimation
                                                                            (getContext(),
                                                                                    android.R.anim
                                                                                            .fade_out));
                                                            MainActivity.toastView.setVisibility(View.GONE);
                                                        }
                                                    };
                                                    handler2.postDelayed(runnable2, 2500);
                                                }
                                            };

                                            handler.postDelayed(runnable, 500);

                                            int interval = 0;
                                            int newDay;
                                            int newMonth;
                                            int newYear;

                                            int adjustedStamp = 0;

                                            if(finalDbRepeatInterval.equals("day")){

                                                adjustedStamp = Integer.parseInt(finalDbTimestamp) + 86400;

                                            }else if(finalDbRepeatInterval.equals("week")){

                                                adjustedStamp = Integer.parseInt(finalDbTimestamp) + 604800;

                                            }else if(finalDbRepeatInterval.equals("month")){

                                                //getting interval based on specific day and month
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

                                            }

                                            //App crashes if exact duplicate of timestamp is saved in
                                            // database. Attempting to detect duplicates and then
                                            // adjusting the timestamp on the millisecond level
                                            long futureStamp = adjustedStamp;
                                            String tempTimestamp = "";
                                            for(int i = 0; i < MainActivity.taskList.size(); i++) {
                                                Cursor tempResult = MainActivity.db.getData
                                                        (Integer.parseInt(MainActivity.sortedIDs.get(i)));
                                                while (tempResult.moveToNext()) {
                                                    tempTimestamp = tempResult.getString(3);
                                                }
                                                tempResult.close();
                                                if(futureStamp == Long.parseLong(tempTimestamp)){
                                                    futureStamp++;
                                                    i = 0;
                                                }

                                            }

                                            int repeatInterval = 0;
                                            if(finalDbRepeatInterval.equals("day")){
                                                repeatInterval = 86400;
                                            }else if(finalDbRepeatInterval.equals("week")){
                                                repeatInterval = (86400 * 7);
                                            }else if(finalDbRepeatInterval.equals("month")){
                                                repeatInterval = interval;
                                            }

                                            //updating timestamp
                                            if(!finalDbOverdue) {
                                                MainActivity.db.updateTimestamp(String.valueOf(
                                                        MainActivity.sortedIDs.get(position)),
                                                        String.valueOf(futureStamp - repeatInterval));
                                            }else{
                                                MainActivity.db.updateTimestamp(String.valueOf(
                                                        MainActivity.sortedIDs.get(position)),
                                                        String.valueOf(finalDbTimestamp));
                                            }

                                            Calendar snoozeCal = Calendar.getInstance();
                                            if(!finalDbOverdue) {
                                                snoozeCal.setTimeInMillis((futureStamp - repeatInterval)
                                                        * 1000);
                                            }else{
                                                snoozeCal.setTimeInMillis(Long
                                                        .parseLong(finalDbTimestamp) * 1000);
                                            }
                                            newYear = snoozeCal.get(Calendar.YEAR);
                                            newMonth = snoozeCal.get(Calendar.MONTH);
                                            newDay = snoozeCal.get(Calendar.DAY_OF_MONTH);

                                            //updating due date in database
                                            MainActivity.db.updateAlarmData(String.valueOf(
                                                    MainActivity.sortedIDs.get(position)),
                                                    finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                                                    String.valueOf(newDay),
                                                    String.valueOf(newMonth),
                                                    String.valueOf(newYear));

                                            //cancelling any snoozed alarm data
                                            MainActivity.db.updateSnoozeData(String.valueOf(
                                                    MainActivity.sortedIDs.get(position)),
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    "");

                                            MainActivity.db.updateOverdue(String.valueOf(
                                                    MainActivity.sortedIDs.get(position)), false);

                                            MainActivity.db.updateSnoozedTimestamp(MainActivity
                                                            .sortedIDs.get(position), "0");

                                            MainActivity.db.updateManualKill
                                                    (MainActivity.sortedIDs.get(position), true);

                                            MainActivity.taskPropertiesShowing = false;

                                            //Returns the 'add' button
                                            MainActivity.params.height = MainActivity.addHeight;
                                            MainActivity.iconParams.height = MainActivity.addIconHeight;

                                            taskView.setLayoutParams(MainActivity.params);
                                            taskView.setLayoutParams(MainActivity.iconParams);

                                            MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                                        } else {

                                            Calendar currentDate = new GregorianCalendar();

                                            //intention to execute AlertReceiver
                                            MainActivity.alertIntent = new Intent(getContext(),
                                                    AlertReceiver.class);

                                            int newAmpm = currentDate.get(Calendar.AM_PM);
                                            if (currentDate.get(Calendar.HOUR) == 11) {
                                                if (currentDate.get(Calendar.AM_PM) == 0) {
                                                    newAmpm = 1;
                                                } else {
                                                    newAmpm = 0;
                                                }
                                            }

                                            int newDay = currentDate.get(Calendar.DAY_OF_MONTH);
                                            int newMonth = currentDate.get(Calendar.MONTH);
                                            int newYear = currentDate.get(Calendar.YEAR);
                                            //incrementing day/month/year if current hour
                                            // is the last hour of said day/month/year
                                            if ((newAmpm == 0) && (currentDate.get(Calendar.HOUR) == 11)) {
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
                                                } else if (currentDate.get(Calendar.MONTH) == 1
                                                        && (newDay == 28) && (newYear % 4 != 0)) {
                                                    newDay = 1;
                                                    newMonth++;
                                                } else if (currentDate.get(Calendar.MONTH) == 1
                                                        && (newDay == 29) && (newYear % 4 == 0)) {
                                                    newDay = 1;
                                                    newMonth++;
                                                } else {
                                                    newDay++;
                                                }
                                            }

                                            //incrementing hour
                                            int newHour = currentDate.get(Calendar.HOUR);
                                            newHour++;

                                            //updating snooze due time in database
                                            MainActivity.db.updateSnoozeData(String.valueOf(
                                                    MainActivity.sortedIDs.get(position)),
                                                    String.valueOf(newHour),
                                                    String.valueOf(currentDate.get(Calendar.MINUTE)),
                                                    String.valueOf(newAmpm),
                                                    String.valueOf(newDay),
                                                    String.valueOf(newMonth),
                                                    String.valueOf(newYear));

                                            //setting the name of the task for which the
                                            // notification is being set
                                            MainActivity.alertIntent.putExtra("ToDo", task);
                                            MainActivity.alertIntent.putExtra
                                                    ("broadId", finalDbBroadcast);
                                            MainActivity.alertIntent.putExtra
                                                    ("snoozeStatus", true);

                                            int newBroadcast = finalDbBroadcast + 1000;

                                            //Setting alarm
                                            MainActivity.pendIntent = PendingIntent.getBroadcast(
                                                    getContext(), newBroadcast, MainActivity.alertIntent,
                                                    PendingIntent.FLAG_UPDATE_CURRENT);

                                            MainActivity.alarmManager.set(AlarmManager.RTC,
                                                    (Calendar.getInstance().getTimeInMillis()
                                                            + AlarmManager.INTERVAL_HOUR),
                                                    MainActivity.pendIntent);

                                            MainActivity.db.updateSnoozedTimestamp(MainActivity.sortedIDs
                                                            .get(position), String.valueOf((Calendar
                                                    .getInstance().getTimeInMillis() / 1000)
                                                    + (AlarmManager.INTERVAL_HOUR) / 1000));

                                            MainActivity.db.updateSnooze(MainActivity.sortedIDs
                                                    .get(position), true);

                                            MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                                            //Marks properties as not showing
                                            MainActivity.taskPropertiesShowing = false;

                                            //Returns the 'add' button
                                            MainActivity.params.height = MainActivity.addHeight;
                                            MainActivity.iconParams.height = MainActivity.addIconHeight;

                                            taskView.setLayoutParams(MainActivity.params);
                                            taskView.setLayoutParams(MainActivity.iconParams);

                                            MainActivity.repeating = false;

                                            new Reorder();
                                            //Updating the view with the new order
                                            MainActivity.theAdapter = new ListAdapter[]{new MyAdapter(
                                                    getContext(), MainActivity.taskList)};
                                            MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                                            notifyDataSetChanged();
                                        }
                                    }
                                };

                                handler.postDelayed(runnable, 600);

                            }
                        });

                        //Actions to occur if user selects '4 hours'
                        fourHourBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {

                                MainActivity.vibrate.vibrate(50);

                                snoozeRow.startAnimation(AnimationUtils.loadAnimation
                                        (getContext(), R.anim.exit_out_right));

                                final Handler handler = new Handler();

                                final Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {

                                        MainActivity.db.updateInterval(String.valueOf(
                                                MainActivity.sortedIDs.get(position)),
                                                String.valueOf(4));

                                        Calendar dateNow = new GregorianCalendar();

                                        //checking if there is enough time before next
                                        // repeat to snooze for a week
                                        boolean dontSnooze = false;
                                        if (finalDbRepeat) {
                                            if (finalDbRepeatInterval.equals("day")) {
                                                if ((dateNow.getTimeInMillis() / 1000) >= (Integer
                                                        .parseInt(finalDbTimestamp)
                                                        - ((AlarmManager.INTERVAL_HOUR / 1000)
                                                        * 4))) {
                                                    dontSnooze = true;
                                                }
                                            } else if (finalDbRepeatInterval.equals("week")) {
                                                if ((dateNow.getTimeInMillis() / 1000) >= (Integer
                                                        .parseInt(finalDbTimestamp)
                                                        - ((AlarmManager.INTERVAL_HOUR / 1000)
                                                        * 4))) {
                                                    dontSnooze = true;
                                                }
                                            } else if (finalDbRepeatInterval.equals("month")) {

                                                if ((dateNow.getTimeInMillis() / 1000) >= (Integer
                                                        .parseInt(finalDbTimestamp)
                                                        - ((AlarmManager.INTERVAL_HOUR / 1000)
                                                        * 4))) {
                                                    dontSnooze = true;
                                                }
                                            }
                                        }

                                        if (dontSnooze) {

                                            MainActivity.toast.setText
                                                    (R.string
                                                    .taskNotSnoozedBecause);
                                            final Handler handler = new Handler();

                                            final Runnable runnable = new Runnable() {
                                                public void run() {
                                                    if(!MainActivity.mute) {
                                                        MainActivity.sweep.start();
                                                    }
                                                    MainActivity.toastView.startAnimation
                                                            (AnimationUtils.loadAnimation
                                                                    (getContext(), R.anim
                                                                            .enter_from_right_fast));
                                                    MainActivity.toastView.setVisibility(View.VISIBLE);
                                                    final Handler handler2 = new Handler();
                                                    final Runnable runnable2 = new Runnable() {
                                                        public void run() {
                                                            MainActivity.toastView.startAnimation
                                                                    (AnimationUtils.loadAnimation
                                                                            (getContext(),
                                                                                    android.R.anim
                                                                                            .fade_out));
                                                            MainActivity.toastView
                                                                    .setVisibility(View.GONE);
                                                        }
                                                    };
                                                    handler2.postDelayed(runnable2, 2500);
                                                }
                                            };

                                            handler.postDelayed(runnable, 500);

                                            int interval = 0;
                                            int newDay;
                                            int newMonth;
                                            int newYear;

                                            int adjustedStamp = 0;

                                            if (finalDbRepeatInterval.equals("day")) {

                                                adjustedStamp = Integer.parseInt
                                                        (finalDbTimestamp) + 86400;

                                            } else if (finalDbRepeatInterval.equals("week")) {

                                                adjustedStamp = Integer.parseInt
                                                        (finalDbTimestamp) + 604800;

                                            } else if (finalDbRepeatInterval.equals("month")) {

                                                //getting interval based on current day and month
                                                int theYear = Integer.parseInt(finalAlarmYear);
                                                int theMonth = Integer.parseInt(finalAlarmMonth);
                                                int theDay = Integer.parseInt(finalAlarmDay);
                                                //Month January and day is 29 non leap year 2592000
                                                if ((theMonth == 0) && (theDay == 29) &&
                                                        (theYear % 4 != 0)) {
                                                    interval = 2592000;
                                                    //Month January and day is 30
                                                    // non leap year 2505600
                                                } else if ((theMonth == 0) && (theDay == 30)
                                                        && (theYear % 4 != 0)) {
                                                    interval = 2505600;
                                                    //Month January and day is 31
                                                    // non leap year 2419200
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

                                                adjustedStamp = Integer.parseInt
                                                        (finalDbTimestamp) + interval;

                                            }

                                            //App crashes if exact duplicate of timestamp is
                                            // saved in database. Attempting to
                                            // detect duplicates and then adjusting the
                                            // timestamp on the millisecond level
                                            long futureStamp = adjustedStamp;
                                            String tempTimestamp = "";
                                            for(int i = 0; i < MainActivity.taskList.size(); i++) {
                                                Cursor tempResult = MainActivity.db.getData
                                                        (Integer.parseInt(
                                                        MainActivity.sortedIDs.get(i)));
                                                while (tempResult.moveToNext()) {
                                                    tempTimestamp = tempResult.getString(3);
                                                }
                                                tempResult.close();
                                                if(futureStamp == Long.parseLong(tempTimestamp)){
                                                    futureStamp++;
                                                    i = 0;
                                                }

                                            }

                                            int repeatInterval = 0;
                                            if(finalDbRepeatInterval.equals("day")){
                                                repeatInterval = 86400;
                                            }else if(finalDbRepeatInterval.equals("week")){
                                                repeatInterval = (86400 * 7);
                                            }else if(finalDbRepeatInterval.equals("month")){
                                                repeatInterval = interval;
                                            }

                                            //updating timestamp
                                            if(!finalDbOverdue) {
                                                MainActivity.db.updateTimestamp(String.valueOf(
                                                        MainActivity.sortedIDs.get(position)),
                                                        String.valueOf(futureStamp - repeatInterval/*(86400 * 4)*/));
                                            }else{
                                                MainActivity.db.updateTimestamp(String.valueOf(
                                                        MainActivity.sortedIDs.get(position)),
                                                        String.valueOf(finalDbTimestamp));
                                            }

                                            Calendar snoozeCal = Calendar.getInstance();
                                            if(!finalDbOverdue) {
                                                snoozeCal.setTimeInMillis((futureStamp - repeatInterval/*86400*/)
                                                        * 1000);
                                            }else{
                                                snoozeCal.setTimeInMillis(Long
                                                        .parseLong(finalDbTimestamp) * 1000);
                                            }

                                            newYear = snoozeCal.get(Calendar.YEAR);
                                            newMonth = snoozeCal.get(Calendar.MONTH);
                                            newDay = snoozeCal.get(Calendar.DAY_OF_MONTH);

                                            //updating due date in database
                                            MainActivity.db.updateAlarmData(String.valueOf(
                                                    MainActivity.sortedIDs.get(position)),
                                                    finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                                                    String.valueOf(newDay),
                                                    String.valueOf(newMonth),
                                                    String.valueOf(newYear));

                                            //cancelling any snoozed alarm data
                                            MainActivity.db.updateSnoozeData(String.valueOf(
                                                    MainActivity.sortedIDs.get(position)),
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    "");

                                            MainActivity.db.updateOverdue(String.valueOf(
                                                    MainActivity.sortedIDs.get(position)),
                                                    false);

                                            MainActivity.db.updateSnoozedTimestamp(MainActivity
                                                            .sortedIDs.get(position),
                                                    "0");

                                            MainActivity.db.updateManualKill(MainActivity
                                                    .sortedIDs.get(position), true);

                                            MainActivity.taskPropertiesShowing = false;

                                            //Returns the 'add' button
                                            MainActivity.params.height = MainActivity.addHeight;
                                            MainActivity.iconParams.height =
                                                    MainActivity.addIconHeight;

                                            taskView.setLayoutParams(MainActivity.params);
                                            taskView.setLayoutParams(MainActivity.iconParams);

                                            MainActivity.theListView.setAdapter
                                                    (MainActivity.theAdapter[0]);

                                        } else {

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
                                            if ((newAmpm == 0) && (currentDate
                                                    .get(Calendar.HOUR) >= 8)) {
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
                                                } else if (currentDate.get(Calendar.MONTH) == 1
                                                        && (newDay == 28) && (newYear % 4 != 0)) {
                                                    newDay = 1;
                                                    newMonth++;
                                                } else if (currentDate.get(Calendar.MONTH) == 1
                                                        && (newDay == 29) && (newYear % 4 == 0)) {
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

                                            MainActivity.db.updateSnoozeData(String.valueOf(
                                                    MainActivity.sortedIDs
                                                            .get(MainActivity.activeTask)),
                                                    String.valueOf(newHour),
                                                    String.valueOf(currentDate
                                                            .get(Calendar.MINUTE)),
                                                    String.valueOf(newAmpm),
                                                    String.valueOf(newDay),
                                                    String.valueOf(newMonth),
                                                    String.valueOf(newYear));

                                            //setting the name of the task for which
                                            // the notification is being set
                                            MainActivity.alertIntent.putExtra("ToDo", task);
                                            MainActivity.alertIntent.putExtra
                                                    ("broadId", finalDbBroadcast);
                                            MainActivity.alertIntent.putExtra
                                                    ("snoozeStatus", true);

                                            int newBroadcast = finalDbBroadcast + 1000;

                                            //setting new alarm
                                            MainActivity.pendIntent = PendingIntent.getBroadcast(
                                                    getContext(), newBroadcast,
                                                    MainActivity.alertIntent,
                                                    PendingIntent.FLAG_UPDATE_CURRENT);

                                            MainActivity.alarmManager.set(AlarmManager.RTC,
                                                    (currentDate.getTimeInMillis() +
                                                            (AlarmManager.INTERVAL_HOUR * 4)),
                                                    MainActivity.pendIntent);

                                            MainActivity.db.updateSnooze(MainActivity.sortedIDs
                                                    .get(position), true);

                                            MainActivity.db.updateSnoozedTimestamp(MainActivity
                                                            .sortedIDs.get(position),
                                                    String.valueOf((Calendar.getInstance()
                                                            .getTimeInMillis() / 1000) +
                                                            ((AlarmManager.INTERVAL_HOUR * 4) / 1000)));

                                            MainActivity.theListView.setAdapter
                                                    (MainActivity.theAdapter[0]);

                                            //Marks properties as not showing
                                            MainActivity.taskPropertiesShowing = false;

                                            //Returns the 'add' button
                                            MainActivity.params.height = MainActivity.addHeight;
                                            MainActivity.iconParams.height =
                                                    MainActivity.addIconHeight;

                                            taskView.setLayoutParams(MainActivity.params);
                                            taskView.setLayoutParams(MainActivity.iconParams);

                                            MainActivity.repeating = false;

                                            new Reorder();
                                            //Updating the view with the new order
                                            MainActivity.theAdapter = new ListAdapter[]{new MyAdapter(
                                                    getContext(), MainActivity.taskList)};
                                            MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                                            notifyDataSetChanged();

                                        }
                                    }
                                };
                                handler.postDelayed(runnable, 600);
                            }
                        });

                        //Actions to occur if user selects 'tomorrow'
                        tomorrowBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {

                                MainActivity.vibrate.vibrate(50);

                                snoozeRow.startAnimation(AnimationUtils.loadAnimation
                                        (getContext(), R.anim.exit_out_right));

                                final Handler handler = new Handler();

                                final Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {

                                        MainActivity.db.updateInterval(String.valueOf(
                                                MainActivity.sortedIDs.get(position)),
                                                String.valueOf(24));

                                        Calendar dateNow = new GregorianCalendar();

                                        //checking if new repeat is less than a day
                                        boolean dontSnooze = false;
                                        if (finalDbRepeat) {
                                            if (finalDbRepeatInterval.equals("day")) {
                                                dontSnooze = true;
                                            } else if (finalDbRepeatInterval.equals("week")) {
                                                if ((dateNow.getTimeInMillis() / 1000) >= (Integer
                                                        .parseInt(finalDbTimestamp) -
                                                        (AlarmManager.INTERVAL_DAY / 1000))) {
                                                    dontSnooze = true;
                                                }
                                            } else if (finalDbRepeatInterval.equals("month")) {
                                                if ((dateNow.getTimeInMillis() / 1000) >= (Integer
                                                        .parseInt(finalDbTimestamp) -
                                                        (AlarmManager.INTERVAL_DAY / 1000))) {
                                                    dontSnooze = true;
                                                }
                                            }
                                        }

                                        if (dontSnooze) {

                                            MainActivity.toast.setText(R.string
                                                    .taskNotSnoozedBecause);
                                            final Handler handler = new Handler();

                                            final Runnable runnable = new Runnable() {
                                                public void run() {
                                                    if (!MainActivity.mute) {
                                                        MainActivity.sweep.start();
                                                    }
                                                    MainActivity.toastView.startAnimation
                                                            (AnimationUtils.loadAnimation(getContext(),
                                                                    R.anim.enter_from_right_fast));
                                                    MainActivity.toastView.setVisibility(View.VISIBLE);
                                                    final Handler handler2 = new Handler();
                                                    final Runnable runnable2 = new Runnable() {
                                                        public void run() {
                                                            MainActivity.toastView.startAnimation
                                                                    (AnimationUtils.loadAnimation
                                                                            (getContext(), android.R.anim
                                                                                    .fade_out));
                                                            MainActivity.toastView.setVisibility(View.GONE);
                                                        }
                                                    };
                                                    handler2.postDelayed(runnable2, 2500);
                                                }
                                            };

                                            handler.postDelayed(runnable, 500);

                                            int interval = 0;
                                            int newDay;
                                            int newMonth;
                                            int newYear;

                                            int adjustedStamp = 0;

                                            if (finalDbRepeatInterval.equals("day")) {

                                                adjustedStamp = Integer.parseInt
                                                        (finalDbTimestamp) + 86400;

                                            } else if (finalDbRepeatInterval.equals("week")) {

                                                adjustedStamp = Integer.parseInt
                                                        (finalDbTimestamp) + 604800;

                                            } else if (finalDbRepeatInterval.equals("month")) {

                                                //getting interval based on current day and month
                                                int theYear = Integer.parseInt(finalAlarmYear);
                                                int theMonth = Integer.parseInt(finalAlarmMonth);
                                                int theDay = Integer.parseInt(finalAlarmDay);
                                                //Month January and day is 29 non leap year 2592000
                                                if ((theMonth == 0) && (theDay == 29) &&
                                                        (theYear % 4 != 0)) {
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

                                                adjustedStamp = Integer.parseInt
                                                        (finalDbTimestamp) + interval;

                                            }

                                            //App crashes if exact duplicate of timestamp is saved
                                            // in database. Attempting to detect duplicates and then
                                            // adjusting the timestamp on the millisecond level
                                            long futureStamp = adjustedStamp;
                                            String tempTimestamp = "";
                                            for (int i = 0; i < MainActivity.taskList.size(); i++) {
                                                Cursor tempResult = MainActivity.db.getData
                                                        (Integer.parseInt(MainActivity.sortedIDs.get(i)));
                                                while (tempResult.moveToNext()) {
                                                    tempTimestamp = tempResult.getString(3);
                                                }
                                                tempResult.close();
                                                if (futureStamp == Long.parseLong(tempTimestamp)) {
                                                    futureStamp++;
                                                    i = 0;
                                                }

                                            }

                                            int repeatInterval = 0;
                                            if(finalDbRepeatInterval.equals("day")){
                                                repeatInterval = 86400;
                                            }else if(finalDbRepeatInterval.equals("week")){
                                                repeatInterval = (86400 * 7);
                                            }else if(finalDbRepeatInterval.equals("month")){
                                                repeatInterval = interval;
                                            }

                                            //updating timestamp
                                            if(!finalDbOverdue) {
                                                MainActivity.db.updateTimestamp(String.valueOf(
                                                        MainActivity.sortedIDs.get(position)),
                                                        String.valueOf(futureStamp - repeatInterval));
                                            }else{
                                                MainActivity.db.updateTimestamp(String.valueOf(
                                                        MainActivity.sortedIDs.get(position)),
                                                        String.valueOf(finalDbTimestamp));
                                            }

                                            Calendar snoozeCal = Calendar.getInstance();
                                            if(!finalDbOverdue) {
                                                snoozeCal.setTimeInMillis((futureStamp - repeatInterval)
                                                        * 1000);
                                            }else{
                                                snoozeCal.setTimeInMillis(Long
                                                        .parseLong(finalDbTimestamp) * 1000);
                                            }

                                            newYear = snoozeCal.get(Calendar.YEAR);
                                            newMonth = snoozeCal.get(Calendar.MONTH);
                                            newDay = snoozeCal.get(Calendar.DAY_OF_MONTH);

                                            //updating due date in database
                                            MainActivity.db.updateAlarmData(String.valueOf(
                                                    MainActivity.sortedIDs.get(position)),
                                                    finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                                                    String.valueOf(newDay),
                                                    String.valueOf(newMonth),
                                                    String.valueOf(newYear));

                                            //cancelling any snoozed alarm data
                                            MainActivity.db.updateSnoozeData(String.valueOf(
                                                    MainActivity.sortedIDs.get(position)),
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    "");

                                            MainActivity.db.updateOverdue(String.valueOf(
                                                    MainActivity.sortedIDs
                                                            .get(position)), false);

                                            MainActivity.db.updateSnoozedTimestamp(MainActivity
                                                            .sortedIDs.get(position), "0");

                                            MainActivity.db.updateManualKill
                                                    (MainActivity.sortedIDs.get(position), true);

                                            MainActivity.taskPropertiesShowing = false;

                                            //Returns the 'add' button
                                            MainActivity.params.height = MainActivity.addHeight;
                                            MainActivity.iconParams.height =
                                                    MainActivity.addIconHeight;

                                            taskView.setLayoutParams(MainActivity.params);
                                            taskView.setLayoutParams(MainActivity.iconParams);

                                            MainActivity.theListView.setAdapter(MainActivity
                                                    .theAdapter[0]);

                                        } else {

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
                                            } else if (currentDate.get(Calendar.MONTH) == 1
                                                    && (newDay == 28) && (newYear % 4 != 0)) {
                                                newDay = 1;
                                                newMonth++;
                                            } else if (currentDate.get(Calendar.MONTH) == 1
                                                    && (newDay == 29) && (newYear % 4 == 0)) {
                                                newDay = 1;
                                                newMonth++;
                                            } else {
                                                newDay++;
                                            }

                                            //updating snooze data
                                            MainActivity.db.updateSnoozeData(String.valueOf(
                                                    MainActivity.sortedIDs.get(position)),
                                                    String.valueOf(currentDate.get(Calendar.HOUR)),
                                                    String.valueOf(currentDate.get(Calendar.MINUTE)),
                                                    String.valueOf(currentDate.get(Calendar.AM_PM)),
                                                    String.valueOf(newDay),
                                                    String.valueOf(newMonth),
                                                    String.valueOf(newYear));

                                            //setting the name of the task for which
                                            // the notification is being set
                                            MainActivity.alertIntent.putExtra("ToDo", task);
                                            MainActivity.alertIntent.putExtra
                                                    ("broadId", finalDbBroadcast);
                                            MainActivity.alertIntent.putExtra
                                                    ("snoozeStatus", true);

                                            int newBroadcast = finalDbBroadcast + 1000;

                                            //setting alarm
                                            MainActivity.pendIntent = PendingIntent.getBroadcast(
                                                    getContext(), newBroadcast, MainActivity
                                                            .alertIntent, PendingIntent
                                                            .FLAG_UPDATE_CURRENT);

                                            MainActivity.alarmManager.set(AlarmManager.RTC,
                                                    (currentDate.getTimeInMillis() +
                                                            AlarmManager.INTERVAL_DAY),
                                                    MainActivity.pendIntent);

                                            MainActivity.db.updateSnooze(MainActivity
                                                    .sortedIDs.get(position), true);

                                            MainActivity.db.updateSnoozedTimestamp(MainActivity
                                                            .sortedIDs.get(position), String
                                                    .valueOf(((Calendar.getInstance()
                                                            .getTimeInMillis() / 1000) +
                                                            AlarmManager.INTERVAL_DAY) / 1000));

                                            MainActivity.theListView.setAdapter
                                                    (MainActivity.theAdapter[0]);

                                            //Marks properties as not showing
                                            MainActivity.taskPropertiesShowing = false;

                                            //Returns the 'add' button
                                            MainActivity.params.height = MainActivity.addHeight;
                                            MainActivity.iconParams.height =
                                                    MainActivity.addIconHeight;

                                            taskView.setLayoutParams(MainActivity.params);
                                            taskView.setLayoutParams(MainActivity.iconParams);

                                            MainActivity.repeating = false;

                                            new Reorder();
                                            //Updating the view with the new order
                                            MainActivity.theAdapter = new ListAdapter[]
                                                    {new MyAdapter(getContext(),
                                                            MainActivity.taskList)};
                                            MainActivity.theListView.setAdapter
                                                    (MainActivity.theAdapter[0]);

                                            notifyDataSetChanged();

                                        }
                                    }
                                };
                                handler.postDelayed(runnable, 600);
                            }
                        });
                        new Reorder();
                    }
                });

                //Actions to occur if user selects 'Done'
                taskDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        taskOverdueRow.startAnimation(AnimationUtils.loadAnimation(getContext(),
                                R.anim.exit_out_right));

                        MainActivity.vibrate.vibrate(100);

                        if (!MainActivity.mute) {
                            MainActivity.punch.start();
                        }

                        //kill task if not repeating
                        if (!finalDbRepeat) {

                            taskOverdueRow.setVisibility(View.GONE);

                            MainActivity.db.updateOverdue(String.valueOf(
                                    MainActivity.sortedIDs.get(position)), false);

                            MainActivity.db.updateIgnored(MainActivity.sortedIDs
                                    .get(position), false);

                            notifyDataSetChanged();

                            MainActivity.taskPropertiesShowing = false;

                            MainActivity.db.updateKilled(String.valueOf(
                                    MainActivity.sortedIDs.get(position)), true);

                            MainActivity.db.updateSnoozedTimestamp
                                    (MainActivity.sortedIDs.get(position), "0");

                            if (MainActivity.showMotivation) {
                                MainActivity.toast.setText(R.string.youKilledThisTask);
                                final Handler handler = new Handler();

                                final Runnable runnable = new Runnable() {
                                    public void run() {
                                        if (!MainActivity.mute) {
                                            MainActivity.sweep.start();
                                        }
                                        MainActivity.toastView.startAnimation
                                                (AnimationUtils.loadAnimation(getContext(),
                                                        R.anim.enter_from_right_fast));
                                        MainActivity.toastView.setVisibility(View.VISIBLE);
                                        final Handler handler2 = new Handler();
                                        final Runnable runnable2 = new Runnable() {
                                            public void run() {
                                                MainActivity.toastView.startAnimation
                                                        (AnimationUtils.loadAnimation
                                                                (getContext(),
                                                                        android.R.anim.fade_out));
                                                MainActivity.toastView.setVisibility(View.GONE);
                                            }
                                        };
                                        handler2.postDelayed(runnable2, 1500);
                                    }
                                };
                                handler.postDelayed(runnable, 500);
                            }

                            if(!MainActivity.remindersAvailable
                                    && !finalDbTimestamp.equals("0")) {
                                MainActivity.duesSet--;
                                MainActivity.db.updateDuesSet(MainActivity.duesSet);
                            }

                            MainActivity.pendIntent = PendingIntent.getBroadcast
                                    (getContext(), Integer.parseInt(
                                            MainActivity.sortedIDs.get(position) + 1000),
                                    MainActivity.alertIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);

                            MainActivity.alarmManager.cancel(MainActivity.pendIntent);

                            MainActivity.add.setVisibility(View.VISIBLE);
                            MainActivity.addIcon.setVisibility(View.VISIBLE);

                            MainActivity.vibrate.vibrate(50);

                            MainActivity.params.height = MainActivity.addHeight;
                            MainActivity.iconParams.height = MainActivity.addIconHeight;

                            v.setLayoutParams(MainActivity.params);
                            v.setLayoutParams(MainActivity.iconParams);

                        //update repeating task to be due at next available date
                        } else {

                            taskOverdueRow.setVisibility(View.GONE);

                            MainActivity.db.updateOverdue(String.valueOf(
                                    MainActivity.sortedIDs.get(position)), false);

                            MainActivity.db.updateIgnored(MainActivity.sortedIDs
                                    .get(position), false);

                            notifyDataSetChanged();

                            MainActivity.taskPropertiesShowing = false;

                            //cancelling any snooze data
                            MainActivity.db.updateSnoozeData(String.valueOf(
                                    MainActivity.sortedIDs.get(position)),
                                    "", "", "", "",
                                    "", "");

                            if (finalDbRepeatInterval.equals("day")) {

                                //App crashes if exact duplicate of timestamp is saved in database. Attempting to
                                // detect duplicates and then adjusting the timestamp on the second level
                                long futureStamp = Integer.parseInt(finalDbTimestamp) + (AlarmManager.INTERVAL_DAY / 1000);
                                String tempTimestamp = "";
                                for(int i = 0; i < MainActivity.taskList.size(); i++) {
                                    Cursor tempResult = MainActivity.db.getData(Integer.parseInt(
                                            MainActivity.sortedIDs.get(i)));
                                    while (tempResult.moveToNext()) {
                                        tempTimestamp = tempResult.getString(3);
                                    }
                                    tempResult.close();
                                    if(futureStamp == Long.parseLong(tempTimestamp)){
                                        futureStamp++;
                                        i = 0;
                                    }

                                }

                                Calendar currentCal = Calendar.getInstance();
                                Calendar futureCal = Calendar.getInstance();
                                futureCal.setTimeInMillis(futureStamp * 1000);
                                Calendar timestampCal = Calendar.getInstance();
                                timestampCal.setTimeInMillis(Long.parseLong(finalDbTimestamp) * 1000);
                                Long diff = (Long.parseLong(finalDbTimestamp) * 1000) - currentCal.getTimeInMillis();
                                diff = diff / 1000;

                                int repeatInterval = 0;
                                if(finalDbRepeatInterval.equals("day")){
                                    repeatInterval = 86400;
                                }

                                Calendar snoozeCal = Calendar.getInstance();
                                //updating timestamp
                                if(!finalDbOverdue && (Integer.parseInt(finalAlarmDay) != currentCal.get(Calendar.DAY_OF_MONTH))) {
                                    MainActivity.db.updateTimestamp(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            String.valueOf(futureStamp));
                                    snoozeCal.setTimeInMillis(futureStamp * 1000);
                                }else if(!finalDbOverdue && diff < repeatInterval && (Integer.parseInt(finalAlarmDay) == currentCal.get(Calendar.DAY_OF_MONTH))){
                                    Long value = Long.parseLong(finalDbTimestamp) + repeatInterval;
                                    MainActivity.db.updateTimestamp(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            String.valueOf(value));
                                    snoozeCal.setTimeInMillis(value * 1000);
                                }else if(finalDbOverdue && diff < repeatInterval && (Integer.parseInt(finalAlarmDay) == currentCal.get(Calendar.DAY_OF_MONTH))){
                                    Long value = Long.parseLong(finalDbTimestamp);
                                    MainActivity.db.updateTimestamp(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            String.valueOf(value));
                                    snoozeCal.setTimeInMillis(value * 1000);
                                }else if(diff < repeatInterval){
                                    MainActivity.db.updateTimestamp(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            String.valueOf(finalDbTimestamp));
                                    snoozeCal.setTimeInMillis(Long.parseLong(finalDbTimestamp) * 1000);
                                }else if(!finalDbOverdue){
                                    int daysOut = (int) (diff / repeatInterval);
                                    MainActivity.db.updateTimestamp(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            String.valueOf((Integer.parseInt(finalDbTimestamp) - (repeatInterval * daysOut)) + repeatInterval));
                                    snoozeCal.setTimeInMillis(((Long.parseLong(finalDbTimestamp) - (repeatInterval * daysOut)) + repeatInterval)* 1000);
                                }else{
                                    int daysOut = (int) (diff / repeatInterval);
                                    MainActivity.db.updateTimestamp(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            String.valueOf(Integer.parseInt(finalDbTimestamp) - (repeatInterval * daysOut)));
                                    snoozeCal.setTimeInMillis((Long.parseLong(finalDbTimestamp) - (repeatInterval * daysOut)) * 1000);
                                }

                                if(!finalAlarmDay.equals("")) {

                                    int newDay;
                                    int newMonth;
                                    int newYear;

                                    newYear = snoozeCal.get(Calendar.YEAR);
                                    newMonth = snoozeCal.get(Calendar.MONTH);
                                    newDay = snoozeCal.get(Calendar.DAY_OF_MONTH);

                                    //updating due time in database
                                    MainActivity.db.updateAlarmData(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                                            String.valueOf(newDay), String.valueOf(newMonth),
                                            String.valueOf(newYear));

                                    //cancelling any snoozed alarm data
                                    MainActivity.db.updateSnoozeData(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "");

                                    MainActivity.db.updateSnoozedTimestamp(String.valueOf(MainActivity
                                            .sortedIDs.get(position)), "0");

                                    MainActivity.db.updateSnooze(String.valueOf(MainActivity.sortedIDs
                                            .get(position)), false);

                                    MainActivity.db.updateManualKill(String.valueOf(
                                            MainActivity.sortedIDs.get(position)), true);

                                    MainActivity.db.updateShowOnce(
                                            MainActivity.sortedIDs.get(position), true);

                                }

                            } else if (finalDbRepeatInterval.equals("week")) {

                                //App crashes if exact duplicate of timestamp is saved in database.
                                // Attempting to detect duplicates and then adjusting the timestamp
                                // on the second level
                                long futureStamp = Integer.parseInt(finalDbTimestamp) +
                                        ((AlarmManager.INTERVAL_DAY * 7) / 1000);
                                String tempTimestamp = "";
                                for(int i = 0; i < MainActivity.taskList.size(); i++) {
                                    Cursor tempResult = MainActivity.db.getData(Integer.parseInt(
                                            MainActivity.sortedIDs.get(i)));
                                    while (tempResult.moveToNext()) {
                                        tempTimestamp = tempResult.getString(3);
                                    }
                                    tempResult.close();
                                    if(futureStamp == Long.parseLong(tempTimestamp)){
                                        futureStamp++;
                                        i = 0;
                                    }

                                }

                                Calendar currentCal = Calendar.getInstance();
                                Calendar futureCal = Calendar.getInstance();
                                futureCal.setTimeInMillis(futureStamp * 1000);
                                Calendar timestampCal = Calendar.getInstance();
                                timestampCal.setTimeInMillis(Long.parseLong(finalDbTimestamp) * 1000);
                                Long diff = (Long.parseLong(finalDbTimestamp) * 1000) - currentCal.getTimeInMillis();
                                diff = diff / 1000;

                                int repeatInterval = 0;
                                if(finalDbRepeatInterval.equals("week")){
                                    repeatInterval = 86400 * 7;
                                }

                                Calendar snoozeCal = Calendar.getInstance();
                                //updating timestamp
                                if(!finalDbOverdue && (Integer.parseInt(finalAlarmDay) != currentCal.get(Calendar.DAY_OF_MONTH))) {
                                    MainActivity.db.updateTimestamp(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            String.valueOf(futureStamp));
                                    snoozeCal.setTimeInMillis(futureStamp * 1000);
                                }else if(!finalDbOverdue && diff < repeatInterval && (Integer.parseInt(finalAlarmDay) == currentCal.get(Calendar.DAY_OF_MONTH))){
                                    Long value = Long.parseLong(finalDbTimestamp) + repeatInterval;
                                    MainActivity.db.updateTimestamp(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            String.valueOf(value));
                                    snoozeCal.setTimeInMillis(value * 1000);
                                }else if(finalDbOverdue && diff < repeatInterval && (Integer.parseInt(finalAlarmDay) == currentCal.get(Calendar.DAY_OF_MONTH))){
                                    Long value = Long.parseLong(finalDbTimestamp);
                                    MainActivity.db.updateTimestamp(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            String.valueOf(value));
                                    snoozeCal.setTimeInMillis(value * 1000);
                                }else if(diff < repeatInterval){
                                    MainActivity.db.updateTimestamp(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            String.valueOf(finalDbTimestamp));
                                    snoozeCal.setTimeInMillis(Long.parseLong(finalDbTimestamp) * 1000);
                                }else if(!finalDbOverdue){
                                    int daysOut = (int) (diff / repeatInterval);
                                    MainActivity.db.updateTimestamp(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            String.valueOf((Integer.parseInt(finalDbTimestamp) - (repeatInterval * daysOut)) + repeatInterval));
                                    snoozeCal.setTimeInMillis(((Long.parseLong(finalDbTimestamp) - (repeatInterval * daysOut)) + repeatInterval)* 1000);
                                }else{
                                    int daysOut = (int) (diff / repeatInterval);
                                    MainActivity.db.updateTimestamp(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            String.valueOf(Integer.parseInt(finalDbTimestamp) - (repeatInterval * daysOut)));
                                    snoozeCal.setTimeInMillis((Long.parseLong(finalDbTimestamp) - (repeatInterval * daysOut)) * 1000);
                                }

//                                Calendar snoozeCal = Calendar.getInstance();
//                                //updating timestamp
//                                if(!finalDbOverdue && (Integer.parseInt(finalAlarmDay) != currentCal.get(Calendar.DAY_OF_MONTH))) {
//                                    MainActivity.db.updateTimestamp(String.valueOf(
//                                            MainActivity.sortedIDs.get(position)),
//                                            String.valueOf(futureStamp));
//                                    snoozeCal.setTimeInMillis(futureStamp * 1000);
//                                }else if(diff < (86400 * 7)){
//                                    MainActivity.db.updateTimestamp(String.valueOf(
//                                            MainActivity.sortedIDs.get(position)),
//                                            String.valueOf(finalDbTimestamp));
//                                    snoozeCal.setTimeInMillis(Long.parseLong(finalDbTimestamp) * 1000);
//                                }else{
//                                    int daysOut = (int) (diff / (86400 * 7));
//                                    MainActivity.db.updateTimestamp(String.valueOf(
//                                            MainActivity.sortedIDs.get(position)),
//                                            String.valueOf(Integer.parseInt(finalDbTimestamp) - ((86400 * 7) * daysOut)));
//                                    snoozeCal.setTimeInMillis((Long.parseLong(finalDbTimestamp) - ((86400 * 7) * daysOut)) * 1000);
//                                }

                                if(!finalAlarmDay.equals("")) {

                                    int newDay;
                                    int newMonth;
                                    int newYear;

                                    newYear = snoozeCal.get(Calendar.YEAR);
                                    newMonth = snoozeCal.get(Calendar.MONTH);
                                    newDay = snoozeCal.get(Calendar.DAY_OF_MONTH);

                                    //updating due time in database
                                    MainActivity.db.updateAlarmData(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                                            String.valueOf(newDay), String.valueOf(newMonth),
                                            String.valueOf(newYear));

                                    //cancelling any snoozed alarm data
                                    MainActivity.db.updateSnoozeData(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "");

                                    MainActivity.db.updateSnoozedTimestamp(String.valueOf(MainActivity
                                            .sortedIDs.get(position)), "0");

                                    MainActivity.db.updateSnooze(String.valueOf(MainActivity.sortedIDs
                                            .get(position)), false);

                                    MainActivity.db.updateManualKill(String.valueOf(
                                            MainActivity.sortedIDs.get(position)), true);

                                    MainActivity.db.updateShowOnce(
                                            MainActivity.sortedIDs.get(position), true);

//                                    Calendar adjustedCalendar = Calendar.getInstance();
//                                    if(!finalDbOverdue) {
//                                        adjustedCalendar.setTimeInMillis(futureStamp * 1000);
//                                    }else{
//                                        adjustedCalendar.setTimeInMillis(Long.parseLong
//                                                (finalDbTimestamp) * 1000);
//                                    }
//                                    int newDay = adjustedCalendar.get(Calendar.DAY_OF_MONTH);
//                                    int newMonth = adjustedCalendar.get(Calendar.MONTH);
//                                    int newYear = adjustedCalendar.get(Calendar.YEAR);
//
//                                    MainActivity.db.updateAlarmData(String.valueOf(MainActivity
//                                                    .sortedIDs.get(position)), finalAlarmHour,
//                                            finalAlarmMinute, finalAlarmAmpm, String.valueOf(newDay),
//                                            String.valueOf(newMonth), String.valueOf(newYear));
//
//                                    MainActivity.db.updateManualKill(String.valueOf(MainActivity
//                                            .sortedIDs.get(position)), true);

                                }

                            } else if (finalDbRepeatInterval.equals("month")) {

                                long futureStamp;

                                Cursor origResult = MainActivity.db.getData(Integer.parseInt(
                                        MainActivity.sortedIDs.get(position)));
                                String originalDay = "";
                                while (origResult.moveToNext()) {
                                    originalDay = origResult.getString(20);
                                }
                                origResult.close();

                                //Getting interval in seconds based on specific day and month
                                Calendar currentCal = Calendar.getInstance();
                                int monthInterval = 0;
                                int currentYear = currentCal.get(Calendar.YEAR);
                                int currentMonth = currentCal.get(Calendar.MONTH);
                                int currentDay = currentCal.get(Calendar.DAY_OF_MONTH);
                                //Month January and day is 29 non leap year 2592000
                                if((currentMonth == 0) && (currentDay == 29) &&
                                        (currentYear % 4 != 0)){
                                    monthInterval = 2592000;
                                //Month January and day is 30 non leap year 2505600
                                }else if((currentMonth == 0) && (currentDay == 30) &&
                                        (currentYear % 4 != 0)){
                                    monthInterval = 2505600;
                                //Month January and day is 31 non leap year 2419200
                                }else if((currentMonth == 0) && (currentDay == 31) &&
                                        (currentYear % 4 != 0)){
                                    monthInterval = 2419200;
                                //Month January and day is 30 leap year 2592000
                                }else if((currentMonth == 0) && (currentDay == 30)  &&
                                        (currentYear % 4 == 0)){
                                    monthInterval = 2592000;
                                //Month January and day is 31 leap year 2505600
                                }else if((currentMonth == 0) && (currentDay == 31) &&
                                        (currentYear % 4 == 0)){
                                    monthInterval = 2505600;
                                //Month March||May||August||October and day is 31 2592000
                                }else if(((currentMonth == 2) || (currentMonth == 4) ||
                                        (currentMonth == 7)
                                        || (currentMonth == 9)) && (currentDay == 31)){
                                    monthInterval = 2592000;
                                //Month January||March||May||July||August||October||December 2678400
                                }else if((currentMonth == 0) || (currentMonth == 2) ||
                                        (currentMonth == 4)
                                        || (currentMonth == 6) || (currentMonth == 7) ||
                                        (currentMonth == 9)
                                        || (currentMonth == 11)){
                                    monthInterval = 2678400;
                                //Month April||June||September||November 2592000
                                }else if((currentMonth == 3) || (currentMonth == 5) ||
                                        (currentMonth == 8)
                                        || (currentMonth == 10)){
                                    monthInterval = 2592000;
                                //Month February non leap year 2419200
                                }else if((currentMonth == 1) && (currentYear % 4 != 0)){
                                    monthInterval = 2419200;
                                //Month February leap year 2505600
                                }else if((currentMonth == 1) && (currentYear % 4 == 0)){
                                    monthInterval = 2505600;
                                }

                                //App crashes if exact duplicate of timestamp is saved in database.
                                // Attempting to detect duplicates and then adjusting the timestamp
                                // on the millisecond level
                                futureStamp = (Long.parseLong(finalDbTimestamp) + monthInterval);
                                String tempTimestamp = "";
                                for(int i = 0; i < MainActivity.taskList.size(); i++) {
                                    Cursor tempResult = MainActivity.db.getData(Integer.parseInt(
                                            MainActivity.sortedIDs.get(i)));
                                    while (tempResult.moveToNext()) {
                                        tempTimestamp = tempResult.getString(3);
                                    }
                                    tempResult.close();
                                    if(futureStamp == Long.parseLong(tempTimestamp)){
                                        futureStamp++;
                                        i = 0;
                                    }

                                }

                                futureStamp = Long.parseLong(String.valueOf(futureStamp) + "000");
                                Cursor originalResult = MainActivity.db.getData(Integer.parseInt(
                                        MainActivity.sortedIDs.get(position)));
                                String origDay = "";
                                while (originalResult.moveToNext()) {
                                    origDay = originalResult.getString(20);
                                }
                                originalResult.close();

                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis(futureStamp);
                                int day = cal.get(Calendar.DAY_OF_MONTH);
                                int month = cal.get(Calendar.MONTH);
                                if(day != Integer.parseInt(origDay)){
                                    int daysOut;
                                    if(month == 0 && (day == 28 || day == 29 || day == 30)){
                                        daysOut = Integer.parseInt(origDay) - day;
                                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY
                                                * daysOut);
                                    }else if(month == 2 && (day == 28 || day == 29 || day == 30)){
                                        daysOut = Integer.parseInt(origDay) - day;
                                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY
                                                * daysOut);
                                    }else if(month == 3 && (day == 28 || day == 29)){
                                        daysOut = Integer.parseInt(origDay) - day;
                                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY
                                                * daysOut);
                                    }else if(month == 4 && (day == 28 || day == 29 || day == 30)){
                                        daysOut = Integer.parseInt(origDay) - day;
                                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY
                                                * daysOut);
                                    }else if(month == 5 && (day == 28 || day == 29)){
                                        daysOut = Integer.parseInt(origDay) - day;
                                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY
                                                * daysOut);
                                    }else if(month == 6 && (day == 28 || day == 29 || day == 30)){
                                        daysOut = Integer.parseInt(origDay) - day;
                                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY
                                                * daysOut);
                                    }else if(month == 7 && (day == 28 || day == 29 || day == 30)){
                                        daysOut = Integer.parseInt(origDay) - day;
                                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY
                                                * daysOut);
                                    }else if(month == 8 && (day == 28 || day == 29)){
                                        daysOut = Integer.parseInt(origDay) - day;
                                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY
                                                * daysOut);
                                    }else if(month == 9 && (day == 28 || day == 29 || day == 30)){
                                        daysOut = Integer.parseInt(origDay) - day;
                                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY
                                                * daysOut);
                                    }else if(month == 10 && (day == 28 || day == 29)){
                                        daysOut = Integer.parseInt(origDay) - day;
                                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY
                                                * daysOut);
                                    }else if(month == 11 && (day == 28 || day == 29 || day == 30)){
                                        daysOut = Integer.parseInt(origDay) - day;
                                        futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY
                                                * daysOut);
                                    }
                                }
                                futureStamp = futureStamp / 1000;

                                Calendar futureCal = Calendar.getInstance();
                                futureCal.setTimeInMillis(futureStamp * 1000);
                                Calendar timestampCal = Calendar.getInstance();
                                timestampCal.setTimeInMillis(Long.parseLong(finalDbTimestamp) * 1000);
                                Long diff = (Long.parseLong(finalDbTimestamp) * 1000) - currentCal.getTimeInMillis();
                                diff = diff / 1000;

                                int repeatInterval = 0;
                                if(finalDbRepeatInterval.equals("month")){
                                    repeatInterval = monthInterval;
                                }

                                Calendar snoozeCal = Calendar.getInstance();

                                if (!finalDbOverdue && (Integer.parseInt(finalAlarmMonth)
                                        != currentCal.get(Calendar.MONTH))) {
                                    MainActivity.db.updateTimestamp(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            String.valueOf(futureStamp));
                                    snoozeCal.setTimeInMillis(futureStamp * 1000);
                                } else if (!finalDbOverdue && diff < repeatInterval && (Integer.parseInt(finalAlarmMonth)
                                        == currentCal.get(Calendar.MONTH))) {
                                    Long value = Long.parseLong(finalDbTimestamp) + repeatInterval;
                                    MainActivity.db.updateTimestamp(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            String.valueOf(value));
                                    snoozeCal.setTimeInMillis(value * 1000);
                                } else if (finalDbOverdue && diff < repeatInterval && (Integer.parseInt(finalAlarmMonth)
                                        == currentCal.get(Calendar.MONTH))) {
                                    Long value = Long.parseLong(finalDbTimestamp);
                                    MainActivity.db.updateTimestamp(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            String.valueOf(value));
                                    snoozeCal.setTimeInMillis(value * 1000);
                                } else if (diff < repeatInterval) {
                                    MainActivity.db.updateTimestamp(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            String.valueOf(finalDbTimestamp));
                                    snoozeCal.setTimeInMillis(Long.parseLong(finalDbTimestamp) * 1000);
                                } else if (!finalDbOverdue) {
                                    int daysWrong = (int) (diff / repeatInterval);
                                    MainActivity.db.updateTimestamp(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            String.valueOf((Integer.parseInt(finalDbTimestamp)
                                                    - (repeatInterval * daysWrong)) + repeatInterval));
                                    snoozeCal.setTimeInMillis(((Long.parseLong(finalDbTimestamp)
                                            - (repeatInterval * daysWrong)) + repeatInterval) * 1000);
                                } else {
                                    Long value = Long.parseLong(finalDbTimestamp);
                                    MainActivity.db.updateTimestamp(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            String.valueOf(value));
                                    snoozeCal.setTimeInMillis(value * 1000);
                                }

                                if(!finalAlarmDay.equals("")) {

                                    int newDay;
                                    int newMonth;
                                    int newYear;

                                    newYear = snoozeCal.get(Calendar.YEAR);
                                    newMonth = snoozeCal.get(Calendar.MONTH);
                                    newDay = snoozeCal.get(Calendar.DAY_OF_MONTH);

                                    //updating due time in database
                                    MainActivity.db.updateAlarmData(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                                            String.valueOf(newDay), String.valueOf(newMonth),
                                            String.valueOf(newYear));

                                    //cancelling any snoozed alarm data
                                    MainActivity.db.updateSnoozeData(String.valueOf(
                                            MainActivity.sortedIDs.get(position)),
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "");

                                    MainActivity.db.updateSnoozedTimestamp(String.valueOf(MainActivity
                                            .sortedIDs.get(position)), "0");

                                    MainActivity.db.updateSnooze(String.valueOf(MainActivity.sortedIDs
                                            .get(position)), false);

                                    MainActivity.db.updateManualKill(String.valueOf(
                                            MainActivity.sortedIDs.get(position)), true);

                                    MainActivity.db.updateShowOnce(
                                            MainActivity.sortedIDs.get(position), true);

                                }
                            }
                        }

                        new Reorder();
                        //Updating the view with the new order
                        MainActivity.theAdapter = new ListAdapter[]{new MyAdapter(
                                getContext(), MainActivity.taskList)};
                        MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                    }
                });

                //Actions to occur if user selects 'ignore'
                taskIgnore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MainActivity.vibrate.vibrate(50);

                        taskOverdueRow.startAnimation(AnimationUtils.loadAnimation(getContext(),
                                R.anim.exit_out_right));

                        final Handler handler = new Handler();

                        final Runnable runnable = new Runnable() {
                            @Override
                            public void run() {

                        MainActivity.db.updateOverdue(
                                MainActivity.sortedIDs.get(position), false);

                        MainActivity.db.updateShowOnce(
                                MainActivity.sortedIDs.get(position), false);

                        MainActivity.db.updateIgnored(String.valueOf(
                                MainActivity.sortedIDs.get(position)), true);

                        //cancelling any snooze data
                        MainActivity.db.updateSnoozeData(String.valueOf(
                                MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                "",
                                "",
                                "",
                                "",
                                "",
                                "");

                        MainActivity.taskPropertiesShowing = false;

                        //Returns the 'add' button
                        MainActivity.params.height = MainActivity.addHeight;
                        MainActivity.iconParams.height = MainActivity.addIconHeight;

                        MainActivity.add.setLayoutParams(MainActivity.params);
                        MainActivity.addIcon.setLayoutParams(MainActivity.iconParams);

                        //Updates the view
                        MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                            }};

                        handler.postDelayed(runnable, 600);

                    }
                });

            //show tasks properties
            }else{

                ViewGroup.LayoutParams params = alarm.getLayoutParams();
                params.width = MainActivity.deviceWidth / 3;
                alarm.setLayoutParams(params);
                subTasks.setLayoutParams(params);
                note.setLayoutParams(params);

                propertyRow.startAnimation(AnimationUtils.loadAnimation(getContext(),
                        R.anim.enter_from_right));
                propertyRow.setVisibility(View.VISIBLE);

            }

            //Making extra row visible removes clickability. Clickability needs to be reinstated.
            taskNameRow.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {

                    MainActivity.vibrate.vibrate(50);

                    //Updates the view
                    MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                    //Marks properties as not showing
                    MainActivity.taskPropertiesShowing = false;

                    //Returns the 'add' button
                    MainActivity.params.height = MainActivity.addHeight;
                    MainActivity.iconParams.height = MainActivity.addIconHeight;

                    MainActivity.add.setLayoutParams(MainActivity.params);
                    MainActivity.addIcon.setLayoutParams(MainActivity.iconParams);

                }
            });

            //centering the selected item in the view
            MainActivity.listViewHeight = MainActivity.theListView.getMeasuredHeight();
            MainActivity.theListView.smoothScrollToPositionFromTop(position,
                    (MainActivity.listViewHeight / 2) -
                            (MainActivity.toolbarParams.height * 2));

            notifyDataSetChanged();

            //put data in text view
            theTextView.setText(task);

            //Actions to occur if user selects 'complete'
            complete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if(!MainActivity.mute){
                        MainActivity.punch.start();
                    }

                    //task is killed if not repeating
                    if(!finalDbRepeat) {

                        notifyDataSetChanged();

                        MainActivity.taskPropertiesShowing = false;

                        MainActivity.db.updateKilled(String.valueOf(
                                MainActivity.sortedIDs.get
                                        (MainActivity.activeTask)), true);

                        MainActivity.db.updateIgnored(MainActivity.sortedIDs
                                .get(position), false);

                        MainActivity.db.updateSnoozedTimestamp(MainActivity.sortedIDs.get(position),
                                "0");

                        if(MainActivity.showMotivation) {
                            MainActivity.toast.setText(R.string.youKilledThisTask);
                            final Handler handler = new Handler();

                            final Runnable runnable = new Runnable() {
                                public void run() {
                                    if (!MainActivity.mute) {
                                        MainActivity.sweep.start();
                                    }
                                    MainActivity.toastView.startAnimation
                                            (AnimationUtils.loadAnimation
                                            (getContext(), R.anim.enter_from_right_fast));
                                    MainActivity.toastView.setVisibility(View.VISIBLE);
                                    final Handler handler2 = new Handler();
                                    final Runnable runnable2 = new Runnable() {
                                        public void run() {
                                            MainActivity.toastView.startAnimation(AnimationUtils
                                                    .loadAnimation(getContext(),
                                                            android.R.anim.fade_out));
                                            MainActivity.toastView.setVisibility(View.GONE);
                                        }
                                    };
                                    handler2.postDelayed(runnable2, 1500);
                                }
                            };
                            handler.postDelayed(runnable, 500);
                        }

                        if(!MainActivity.remindersAvailable && !finalDbTimestamp.equals("0")) {
                            MainActivity.duesSet--;
                            MainActivity.db.updateDuesSet(MainActivity.duesSet);
                        }

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
                        MainActivity.addIcon.setVisibility(View.VISIBLE);

                        MainActivity.vibrate.vibrate(50);

                        MainActivity.params.height = MainActivity.addHeight;
                        MainActivity.iconParams.height = MainActivity.addIconHeight;

                        v.setLayoutParams(MainActivity.params);
                        v.setLayoutParams(MainActivity.iconParams);

                        new Reorder();
                        //Updating the view with the new order
                        MainActivity.theAdapter = new ListAdapter[]{new MyAdapter(
                                getContext(), MainActivity.taskList)};
                        MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                    //task is updated to be due at next repeat
                    }else{

                        int interval = 0;
                        int newDay;
                        int newMonth;
                        int newYear;
                        long futureStamp = 0;
                        int daysOut = 0;

                        if(finalDbRepeatInterval.equals("day")){

                            //App crashes if exact duplicate of timestamp is saved in database.
                            // Attempting to detect duplicates and then adjusting the timestamp on
                            // the millisecond level
                            futureStamp = Long.parseLong(finalDbTimestamp) + (AlarmManager
                                    .INTERVAL_DAY / 1000);
                            String tempTimestamp = "";
                            for (int i = 0; i < MainActivity.taskList.size(); i++) {
                                Cursor tempResult = MainActivity.db.getData(Integer.parseInt(
                                        MainActivity.sortedIDs.get(i)));
                                while (tempResult.moveToNext()) {
                                    tempTimestamp = tempResult.getString(3);
                                }
                                tempResult.close();
                                if (futureStamp == Long.parseLong(tempTimestamp)) {
                                    futureStamp++;
                                    i = 0;
                                }

                            }

                            futureStamp = futureStamp * 1000;

                        }else if(finalDbRepeatInterval.equals("week")){

                            //App crashes if exact duplicate of timestamp is saved in database.
                            // Attempting to detect duplicates and then adjusting the timestamp on
                            // the millisecond level
                            futureStamp = Long.parseLong(finalDbTimestamp) +
                                    ((AlarmManager.INTERVAL_DAY * 7) / 1000);
                            String tempTimestamp = "";
                            for(int i = 0; i < MainActivity.taskList.size(); i++) {
                                Cursor tempResult = MainActivity.db.getData(Integer.parseInt(
                                        MainActivity.sortedIDs.get(i)));
                                while (tempResult.moveToNext()) {
                                    tempTimestamp = tempResult.getString(3);
                                }
                                tempResult.close();
                                if(futureStamp == Long.parseLong(tempTimestamp)){
                                    futureStamp++;
                                    i = 0;
                                }

                            }

                            futureStamp = futureStamp * 1000;

                        }else if(finalDbRepeatInterval.equals("month")) {

                            //getting interval based on current day and month
                            interval = 0;
                            int theYear = Integer.parseInt(finalAlarmYear);
                            int theMonth = Integer.parseInt(finalAlarmMonth);
                            int theDay = Integer.parseInt(finalAlarmDay);
                            //Month January and day is 29 non leap year 2592000
                            if ((theMonth == 0) && (theDay == 29) && (theYear % 4 != 0)) {
                                interval = 2592000;
                                //Month January and day is 30 non leap year 2505600
                            } else if ((theMonth == 0) && (theDay == 30) && (theYear % 4 != 0)) {
                                interval = 2505600;
                                //Month January and day is 31 non leap year 2419200
                            } else if ((theMonth == 0) && (theDay == 31) && (theYear % 4 != 0)) {
                                interval = 2419200;
                                //Month January and day is 30 leap year 2592000
                            } else if ((theMonth == 0) && (theDay == 30) && (theYear % 4 == 0)) {
                                interval = 2592000;
                                //Month January and day is 31 leap year 2505600
                            } else if ((theMonth == 0) && (theDay == 31) && (theYear % 4 == 0)) {
                                interval = 2505600;
                                //Month March||May||August||October and day is 31 2592000
                            } else if (((theMonth == 2) || (theMonth == 4) || (theMonth == 7)
                                    || (theMonth == 9)) && (theDay == 31)) {
                                interval = 2592000;
                                //Month January||March||May||July||August||October||December 2678400
                            } else if ((theMonth == 0) || (theMonth == 2) || (theMonth == 4)
                                    || (theMonth == 6) || (theMonth == 7) || (theMonth == 9)
                                    || (theMonth == 11)) {
                                interval = 2678400;
                                //Month April||June||September||November 2592000
                            } else if ((theMonth == 3) || (theMonth == 5) || (theMonth == 8)
                                    || (theMonth == 10)) {
                                interval = 2592000;
                                //Month February non leap year 2419200
                            } else if ((theMonth == 1) && (theYear % 4 != 0)) {
                                interval = 2419200;
                                //Month February leap year 2505600
                            } else if ((theMonth == 1) && (theYear % 4 == 0)) {
                                interval = 2505600;
                            }

                            //App crashes if exact duplicate of timestamp is saved in database. Attempting to
                            // detect duplicates and then adjusting the timestamp on the millisecond level
                            futureStamp = (Long.parseLong(finalDbTimestamp) + interval/*monthInterval*/);
                            String tempTimestamp = "";
                            for(int i = 0; i < MainActivity.taskList.size(); i++) {
                                Cursor tempResult = MainActivity.db.getData(Integer.parseInt(
                                        MainActivity.sortedIDs.get(i)));
                                while (tempResult.moveToNext()) {
                                    tempTimestamp = tempResult.getString(3);
                                }
                                tempResult.close();
                                if(futureStamp == Long.parseLong(tempTimestamp)){
                                    futureStamp++;
                                    i = 0;
                                }

                            }

                            futureStamp = Long.parseLong(String.valueOf(futureStamp) + "000");
                            Cursor originalResult = MainActivity.db.getData(Integer.parseInt(
                                    MainActivity.sortedIDs.get(position)));
                            String origDay = "";
                            while (originalResult.moveToNext()) {
                                origDay = originalResult.getString(20);
                            }
                            originalResult.close();

                            Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(futureStamp);
                            int day = cal.get(Calendar.DAY_OF_MONTH);
                            int month = cal.get(Calendar.MONTH);
                            if(day != Integer.parseInt(origDay)){
                                if(month == 0 && (day == 28 || day == 29 || day == 30)){
                                    daysOut = Integer.parseInt(origDay) - day;
                                    futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                                }else if(month == 2 && (day == 28 || day == 29 || day == 30)){
                                    daysOut = Integer.parseInt(origDay) - day;
                                    futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                                }else if(month == 3 && (day == 28 || day == 29)){
                                    daysOut = Integer.parseInt(origDay) - day;
                                    futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                                }else if(month == 4 && (day == 28 || day == 29 || day == 30)){
                                    daysOut = Integer.parseInt(origDay) - day;
                                    futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                                }else if(month == 5 && (day == 28 || day == 29)){
                                    daysOut = Integer.parseInt(origDay) - day;
                                    futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                                }else if(month == 6 && (day == 28 || day == 29 || day == 30)){
                                    daysOut = Integer.parseInt(origDay) - day;
                                    futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                                }else if(month == 7 && (day == 28 || day == 29 || day == 30)){
                                    daysOut = Integer.parseInt(origDay) - day;
                                    futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                                }else if(month == 8 && (day == 28 || day == 29)){
                                    daysOut = Integer.parseInt(origDay) - day;
                                    futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                                }else if(month == 9 && (day == 28 || day == 29 || day == 30)){
                                    daysOut = Integer.parseInt(origDay) - day;
                                    futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                                }else if(month == 10 && (day == 28 || day == 29)){
                                    daysOut = Integer.parseInt(origDay) - day;
                                    futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                                }else if(month == 11 && (day == 28 || day == 29 || day == 30)){
                                    daysOut = Integer.parseInt(origDay) - day;
                                    futureStamp = futureStamp + (AlarmManager.INTERVAL_DAY * daysOut);
                                }
                            }
                        }

                        futureStamp = futureStamp / 1000;

                        Calendar currentCal = Calendar.getInstance();
                        Calendar futureCal = Calendar.getInstance();
                        futureCal.setTimeInMillis(futureStamp * 1000);
                        Calendar timestampCal = Calendar.getInstance();
                        timestampCal.setTimeInMillis(Long.parseLong(finalDbTimestamp) * 1000);
                        Long diff = (Long.parseLong(finalDbTimestamp) * 1000) - currentCal.getTimeInMillis();
                        diff = diff / 1000;

                        int repeatInterval = 0;
                        if(finalDbRepeatInterval.equals("day")){
                            repeatInterval = 86400;
                        }else if(finalDbRepeatInterval.equals("week")){
                            repeatInterval = (86400 * 7);
                        }else if(finalDbRepeatInterval.equals("month")){
                            String bigInterval = String.valueOf(interval + ((AlarmManager.INTERVAL_DAY * daysOut) / 1000));
                            repeatInterval = Integer.parseInt(bigInterval);
                        }

                        Calendar snoozeCal = Calendar.getInstance();
                        //updating timestamp
                        if(!finalDbRepeatInterval.equals("month")) {
                            if (!finalDbOverdue && (Integer.parseInt(finalAlarmDay)
                                    != currentCal.get(Calendar.DAY_OF_MONTH))) {
                                MainActivity.db.updateTimestamp(String.valueOf(
                                        MainActivity.sortedIDs.get(position)),
                                        String.valueOf(futureStamp));
                                snoozeCal.setTimeInMillis(futureStamp * 1000);
                            } else if (!finalDbOverdue && diff < repeatInterval && (Integer.parseInt(finalAlarmDay)
                                    == currentCal.get(Calendar.DAY_OF_MONTH))) {
                                Long value = Long.parseLong(finalDbTimestamp) + repeatInterval;
                                MainActivity.db.updateTimestamp(String.valueOf(
                                        MainActivity.sortedIDs.get(position)),
                                        String.valueOf(value));
                                snoozeCal.setTimeInMillis(value * 1000);
                            } else if (finalDbOverdue && diff < repeatInterval && (Integer.parseInt(finalAlarmDay)
                                    == currentCal.get(Calendar.DAY_OF_MONTH))) {
                                Long value = Long.parseLong(finalDbTimestamp);
                                MainActivity.db.updateTimestamp(String.valueOf(
                                        MainActivity.sortedIDs.get(position)),
                                        String.valueOf(value));
                                snoozeCal.setTimeInMillis(value * 1000);
                            } else if (diff < repeatInterval) {
                                MainActivity.db.updateTimestamp(String.valueOf(
                                        MainActivity.sortedIDs.get(position)),
                                        String.valueOf(finalDbTimestamp));
                                snoozeCal.setTimeInMillis(Long.parseLong(finalDbTimestamp) * 1000);
                            } else if (!finalDbOverdue) {
                                int daysWrong = (int) (diff / repeatInterval);
                                MainActivity.db.updateTimestamp(String.valueOf(
                                        MainActivity.sortedIDs.get(position)),
                                        String.valueOf((Integer.parseInt(finalDbTimestamp)
                                                - (repeatInterval * daysWrong)) + repeatInterval));
                                snoozeCal.setTimeInMillis(((Long.parseLong(finalDbTimestamp)
                                        - (repeatInterval * daysWrong)) + repeatInterval) * 1000);
                            } else {
                                int daysWrong = (int) (diff / repeatInterval);
                                MainActivity.db.updateTimestamp(String.valueOf(
                                        MainActivity.sortedIDs.get(position)),
                                        String.valueOf(Integer.parseInt(finalDbTimestamp)
                                                - (repeatInterval * daysWrong)));
                                snoozeCal.setTimeInMillis((Long.parseLong(finalDbTimestamp)
                                        - (repeatInterval * daysWrong)) * 1000);
                            }
                        }else{
                            if (!finalDbOverdue && (Integer.parseInt(finalAlarmMonth)
                                    != currentCal.get(Calendar.MONTH))) {
                                MainActivity.db.updateTimestamp(String.valueOf(
                                        MainActivity.sortedIDs.get(position)),
                                        String.valueOf(futureStamp));
                                snoozeCal.setTimeInMillis(futureStamp * 1000);
                            } else if (!finalDbOverdue && diff < repeatInterval && (Integer.parseInt(finalAlarmMonth)
                                    == currentCal.get(Calendar.MONTH))) {
                                Long value = Long.parseLong(finalDbTimestamp) + repeatInterval;
                                MainActivity.db.updateTimestamp(String.valueOf(
                                        MainActivity.sortedIDs.get(position)),
                                        String.valueOf(value));
                                snoozeCal.setTimeInMillis(value * 1000);
                            } else if (finalDbOverdue && diff < repeatInterval && (Integer.parseInt(finalAlarmMonth)
                                    == currentCal.get(Calendar.MONTH))) {
                                Long value = Long.parseLong(finalDbTimestamp);
                                MainActivity.db.updateTimestamp(String.valueOf(
                                        MainActivity.sortedIDs.get(position)),
                                        String.valueOf(value));
                                snoozeCal.setTimeInMillis(value * 1000);
                            } else if (diff < repeatInterval) {
                                MainActivity.db.updateTimestamp(String.valueOf(
                                        MainActivity.sortedIDs.get(position)),
                                        String.valueOf(finalDbTimestamp));
                                snoozeCal.setTimeInMillis(Long.parseLong(finalDbTimestamp) * 1000);
                            } else if (!finalDbOverdue) {
                                int daysWrong = (int) (diff / repeatInterval);
                                MainActivity.db.updateTimestamp(String.valueOf(
                                        MainActivity.sortedIDs.get(position)),
                                        String.valueOf((Integer.parseInt(finalDbTimestamp)
                                                - (repeatInterval * daysWrong)) + repeatInterval));
                                snoozeCal.setTimeInMillis(((Long.parseLong(finalDbTimestamp)
                                        - (repeatInterval * daysWrong)) + repeatInterval) * 1000);
                            } else {
//                                int daysWrong = (int) (diff / repeatInterval);
//                                MainActivity.db.updateTimestamp(String.valueOf(
//                                        MainActivity.sortedIDs.get(position)),
//                                        String.valueOf(Integer.parseInt(finalDbTimestamp)
//                                                - (repeatInterval * daysWrong)));
//                                snoozeCal.setTimeInMillis((Long.parseLong(finalDbTimestamp)
//                                        - (repeatInterval * daysWrong)) * 1000);
                                Long value = Long.parseLong(finalDbTimestamp);
                                MainActivity.db.updateTimestamp(String.valueOf(
                                        MainActivity.sortedIDs.get(position)),
                                        String.valueOf(value));
                                snoozeCal.setTimeInMillis(value * 1000);
                            }
                        }

                        if(finalDbSnooze) {
                            diff = snoozeCal.getTimeInMillis() - currentCal.getTimeInMillis();
                            diff = diff / 1000;
                            long daysWrong = diff / repeatInterval;
                            long snoozeMillisForCalculation = snoozeCal.getTimeInMillis();
                            long intervalForCalculation = repeatInterval * daysWrong;
                            snoozeCal.setTimeInMillis(snoozeMillisForCalculation - (intervalForCalculation * 1000));
                        }else if((timestampCal.get(Calendar.DAY_OF_MONTH) != Integer.parseInt(finalAlarmDay))
                                || (finalDbRepeatInterval.equals("month") && (timestampCal.get(Calendar.MONTH)
                                != Integer.parseInt(finalAlarmMonth)))){
                            diff = snoozeCal.getTimeInMillis() - timestampCal.getTimeInMillis();
                            diff = diff / 1000;
                            int daysWrong = (int) (diff / repeatInterval);
                            if(daysWrong >=0) {
                                snoozeCal.setTimeInMillis((snoozeCal.getTimeInMillis()
                                        - ((repeatInterval * daysWrong) * 1000)));
                            }
                        }else if(finalDbOverdue){
                            snoozeCal.setTimeInMillis((snoozeCal.getTimeInMillis()
                                    + (repeatInterval * 1000)));
                        }

                        newYear = snoozeCal.get(Calendar.YEAR);
                        newMonth = snoozeCal.get(Calendar.MONTH);
                        newDay = snoozeCal.get(Calendar.DAY_OF_MONTH);

                        //updating due time in database
                        MainActivity.db.updateAlarmData(String.valueOf(
                                MainActivity.sortedIDs.get(position)),
                                finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                                String.valueOf(newDay), String.valueOf(newMonth),
                                String.valueOf(newYear));

                        //cancelling any snoozed alarm data
                        MainActivity.db.updateSnoozeData(String.valueOf(
                                MainActivity.sortedIDs.get(position)),
                                "",
                                "",
                                "",
                                "",
                                "",
                                "");

                        MainActivity.db.updateSnoozedTimestamp(String.valueOf(MainActivity
                                .sortedIDs.get(position)), "0");

                        MainActivity.db.updateSnooze(String.valueOf(MainActivity.sortedIDs
                                .get(position)), false);

                        MainActivity.db.updateManualKill(String.valueOf(
                                MainActivity.sortedIDs.get(position)), true);

                        if(!finalDbOverdue) {
                            MainActivity.db.updateKilledEarly(String.valueOf(
                                    MainActivity.sortedIDs.get(position)), true);
                        }

                        MainActivity.db.updateShowOnce(
                                MainActivity.sortedIDs.get(MainActivity.activeTask), true);

                        if(MainActivity.repeatHint <= 10) {
                            if((MainActivity.repeatHint == 1) || (MainActivity.repeatHint == 10)) {
                                MainActivity.toast.setText(R.string.youCanCancelRepeat);
                                final Handler handler = new Handler();

                                final Runnable runnable = new Runnable() {
                                    public void run() {
                                        MainActivity.hint.start();
                                        MainActivity.toastView.startAnimation(AnimationUtils
                                                .loadAnimation(getContext(), R.anim
                                                        .enter_from_right_fast));
                                        MainActivity.toastView.setVisibility(View.VISIBLE);
                                        final Handler handler2 = new Handler();
                                        final Runnable runnable2 = new Runnable() {
                                            public void run() {
                                                MainActivity.toastView.startAnimation
                                                        (AnimationUtils.loadAnimation
                                                                (getContext(), android.R.anim
                                                                        .fade_out));
                                                MainActivity.toastView.setVisibility(View.GONE);
                                            }
                                        };
                                        handler2.postDelayed(runnable2, 2500);
                                    }
                                };

                                handler.postDelayed(runnable, 500);
                            }
                            MainActivity.repeatHint++;
                            MainActivity.db.updateRepeatHint(MainActivity.repeatHint);
                        }

                        propertyRow.setVisibility(View.GONE);

                        MainActivity.taskPropertiesShowing = false;

                        MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                    }

                    new Reorder();

                }
            });

            //Actions to occur if user selects 'set due date'
            alarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(MainActivity.duesSet < 5 || finalDbDue) {
                        MainActivity.vibrate.vibrate(50);

                        //actions to occur if alarm not already set
                        if (!finalDbDue) {

                            MainActivity.db.updateActiveTaskTemp(String.valueOf(finalDbID));

                            getContext().startActivity(dueIntent);

                        //actions to occur when viewing alarm properties
                        } else {

                            MainActivity.db.updateActiveTaskTemp(String.valueOf(finalDbID));

                            getContext().startActivity(dueIntent);

                        }
                    }else{

                        MainActivity.purchasesShowing = true;
                        MainActivity.add.setClickable(false);
                        MainActivity.theListView.setOnItemClickListener(null);
                        MainActivity.taskPropertiesShowing = false;
                        MainActivity.purchases.startAnimation(AnimationUtils.loadAnimation
                                (getContext(), R.anim.enter_from_right));

                        final Handler handler = new Handler();

                        final Runnable runnable = new Runnable() {
                            public void run() {
                                MainActivity.purchases.setVisibility(View.VISIBLE);
                            }
                        };

                        handler.postDelayed(runnable, 200);

                    }

                }
            });

            //Actions to occur if user selects 'Sub-Tasks'
            subTasks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.vibrate.vibrate(50);

                    MainActivity.checklistShowing = true;

                    MainActivity.vibrate.vibrate(50);

                    MainActivity.db.updateActiveTaskTemp(String.valueOf(finalDbID));

                    getContext().startActivity(intent);

                }
            });

            //Actions to occur if user selects 'Add Note'
            note.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.vibrate.vibrate(50);

                    MainActivity.vibrate.vibrate(50);

                    MainActivity.db.updateActiveTaskTemp(String.valueOf(finalDbID));

                    getContext().startActivity(noteIntent);

                }
            });

        }

        //put data in text view
        theTextView.setText(task);

        //crossing out completed tasks

        //check if task has to be crossed out
        if (dbKilled) {

            theTextView.setPaintFlags(theTextView.getPaintFlags() |
                    Paint.STRIKE_THRU_TEXT_FLAG);

            if(!MainActivity.lightDark) {
                complete.setVisibility(View.GONE);
                completed.setVisibility(View.VISIBLE);
            }else{
                completeWhite.setVisibility(View.GONE);
                completedWhite.setVisibility(View.VISIBLE);
            }
            completed.setClickable(false);

        }

        //show repeat icon if required
        if(dbRepeat && !dbKilled){
            repeatClear.setBackgroundColor(Color.parseColor(MainActivity.highlight));
            repeatClearWhite.setBackgroundColor(Color.parseColor(MainActivity.highlight));
        }else if(MainActivity.lightDark){
            repeatClearWhite.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }

        //Show checklist/note icon if required
        if(dbChecklistSize != 0){
            if(!dbKilled) {
                checklistClear.setBackgroundColor(Color.parseColor(MainActivity.highlight));
                checklistClearWhite.setBackgroundColor(Color.parseColor(MainActivity.highlight));
            }else{
                checklistClear.setBackgroundColor(ContextCompat
                        .getColor(getContext(), R.color.gray));
                checklistClearWhite.setBackgroundColor(ContextCompat
                        .getColor(getContext(), R.color.black));
            }
        }else if(MainActivity.lightDark){
            checklistClearWhite.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }
        if(!dbNote.equals("")){
            if(!dbKilled) {
                noteClear.setBackgroundColor(Color.parseColor(MainActivity.highlight));
                noteClearWhite.setBackgroundColor(Color.parseColor(MainActivity.highlight));
            }else{
                noteClear.setBackgroundColor(ContextCompat
                        .getColor(getContext(), R.color.gray));
                noteClearWhite.setBackgroundColor(ContextCompat
                        .getColor(getContext(), R.color.black));
            }
        }else if(MainActivity.lightDark){
            noteClearWhite.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }

        //greying out unselected tasks
        if (MainActivity.taskPropertiesShowing && (position != MainActivity.activeTask)) {

            //Attempting to make animations run smoothly by running a separate thread
            final Handler handler = new Handler();

            final Runnable runnable = new Runnable() {
                public void run() {
                    //Fade out inactive taskviews
                    taskView.startAnimation(AnimationUtils.loadAnimation
                            (getContext(), android.R.anim.fade_out));
                    taskView.setVisibility(View.INVISIBLE);
                }
            };

            handler.postDelayed(runnable, 10);

        }

        //Actions to take when editing task
        if (MainActivity.taskBeingEdited && (position == MainActivity.activeTask) &&
                !MainActivity.goToMyAdapter) {

            MainActivity.keyboard.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

            String oldTaskString = theTextView.getText().toString();

            complete.setVisibility(View.GONE);

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
    private void setAlarm(final int position, int year, int month, int day, int hour, int minute,
            int ampm, String uniInterval, String originalDayTemp){

        String dbTask = "";
        Integer dbBroadcast = 0;
        Boolean dbRepeat = false;
        Boolean dbSnooze = false;
        String dbTimestamp = "";
        Cursor dbResult = MainActivity.db.getData(Integer.parseInt(
                MainActivity.sortedIDs.get(position)));
        while (dbResult.moveToNext()) {
            dbTimestamp = dbResult.getString(3);
            dbTask = dbResult.getString(4);
            dbBroadcast = dbResult.getInt(7);
            dbRepeat = dbResult.getInt(8) > 0;
            dbSnooze = dbResult.getInt(10) > 0;
        }
        dbResult.close();

        if (!dbSnooze) {
            MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
                    Integer.parseInt(MainActivity.sortedIDs.get(position)),
                    MainActivity.alertIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
                    Integer.parseInt(
                            MainActivity.sortedIDs.get(position) + 1000),
                    MainActivity.alertIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        MainActivity.alarmManager.cancel(MainActivity.pendIntent);

        Calendar currentDate = new GregorianCalendar();

        int adjustedHour = hour;
        if (ampm == 1) {
            adjustedHour += 12;
        }else if(hour == 12){
             adjustedHour = 0;
        }

        if (currentDate.get(Calendar.YEAR) == year
            && currentDate.get(Calendar.MONTH) == month
            && currentDate.get(Calendar.DAY_OF_MONTH) == day
            && currentDate.get(Calendar.HOUR_OF_DAY) >
            adjustedHour) {

            MainActivity.toast.setText(R.string.cannotSetTask);
            MainActivity.db.updateRepeat(MainActivity.sortedIDs.get(position), false);
            MainActivity.db.updateRepeatInterval
                    (MainActivity.sortedIDs.get(position), "");

            MainActivity.db.updateTimestamp
                    (MainActivity.sortedIDs.get(position), "0");
            dbRepeat = false;
            final Handler handler = new Handler();

            final Runnable runnable = new Runnable() {
                public void run() {

                    MainActivity.hint.start();

                    MainActivity.toastView.startAnimation(AnimationUtils.loadAnimation
                             (getContext(), R.anim.enter_from_right_fast));
                    MainActivity.toastView.setVisibility(View.VISIBLE);
                    final Handler handler2 = new Handler();
                    final Runnable runnable2 = new Runnable() {
                             public void run() {
                            MainActivity.toastView.startAnimation(AnimationUtils.loadAnimation
                                    (getContext(), android.R.anim.fade_out));
                            MainActivity.toastView.setVisibility(View.GONE);
                        }
                    };
                    handler2.postDelayed(runnable2, 2500);
                }
            };

            handler.postDelayed(runnable, 500);

            //negating any increase in due date limit
            if(!MainActivity.remindersAvailable) {
                MainActivity.duesSet--;
                MainActivity.db.updateDuesSet(MainActivity.duesSet);
            }

        } else if (currentDate.get(Calendar.YEAR) == year
                && currentDate.get(Calendar.MONTH) == month
                && currentDate.get(Calendar.DAY_OF_MONTH) == day
                && (currentDate.get(Calendar.HOUR_OF_DAY) == adjustedHour
                || (currentDate.get(Calendar.HOUR_OF_DAY) == 0 && adjustedHour == 12)
                || (currentDate.get(Calendar.HOUR_OF_DAY) == 12 && adjustedHour == 24))
                && currentDate.get(Calendar.MINUTE) > minute) {
            MainActivity.toast.setText(R.string.cannotSetTask);
            MainActivity.db.updateRepeat(MainActivity.sortedIDs.get(position), false);
            MainActivity.db.updateRepeatInterval
                    (MainActivity.sortedIDs.get(position), "");

            MainActivity.db.updateTimestamp
                    (MainActivity.sortedIDs.get(position), "0");
            dbRepeat = false;
            final Handler handler = new Handler();

            final Runnable runnable = new Runnable() {
                public void run() {

                    MainActivity.hint.start();

                    MainActivity.toastView.startAnimation(AnimationUtils.loadAnimation
                            (getContext(), R.anim.enter_from_right_fast));
                    MainActivity.toastView.setVisibility(View.VISIBLE);
                    final Handler handler2 = new Handler();
                    final Runnable runnable2 = new Runnable() {
                             public void run() {
                            MainActivity.toastView.startAnimation(AnimationUtils.loadAnimation
                                    (getContext(), android.R.anim.fade_out));
                             MainActivity.toastView.setVisibility(View.GONE);
                         }
                         };
                    handler2.postDelayed(runnable2, 1500);
                }
            };

            handler.postDelayed(runnable, 500);

            //negating any increase in due date limit
            if(!MainActivity.remindersAvailable) {
                MainActivity.duesSet--;
                MainActivity.db.updateDuesSet(MainActivity.duesSet);
            }

        } else {

            int amPmHour = hour;
            if (ampm == 1 && amPmHour != 12) {
                amPmHour += 12;
            }else if(ampm == 0 && amPmHour == 12){
                amPmHour = 0;
            }

            Calendar futureDate = new GregorianCalendar(year,
                    month, day,
                    amPmHour, minute);

            //App crashes if exact duplicate of timestamp is saved in database. Attempting to
            // detect duplicates and then adjusting the timestamp on the millisecond level
            long futureStamp = futureDate.getTimeInMillis() / 1000;
            String tempTimestamp = "";
            for(int i = 0; i < MainActivity.taskList.size(); i++) {
                Cursor tempResult = MainActivity.db.getData(Integer.parseInt(
                        MainActivity.sortedIDs.get(i)));
                while (tempResult.moveToNext()) {
                    tempTimestamp = tempResult.getString(3);
                }
                tempResult.close();
                if(futureStamp == Long.parseLong(tempTimestamp)){
                    futureStamp++;
                    i = 0;
                }
            }

            //updating timestamp
            MainActivity.db.updateTimestamp(String.valueOf(
                    MainActivity.sortedIDs.get(position)),
                    String.valueOf(futureStamp));

            Calendar tempCal = Calendar.getInstance();
            tempCal.setTimeInMillis(futureStamp * 1000);

            //intention to execute AlertReceiver
            MainActivity.alertIntent = new Intent(getContext(), AlertReceiver.class);

            MainActivity.db.updateAlarmData(String.valueOf(
                    MainActivity.sortedIDs.get(position)),
                    String.valueOf(hour),
                    String.valueOf(minute),
                    String.valueOf(ampm),
                    String.valueOf(day),
                    String.valueOf(month),
                    String.valueOf(year));

            MainActivity.db.updateOriginalDay(String.valueOf(
                    MainActivity.sortedIDs.get(position)), originalDayTemp);

            //setting the name of the task for which the notification is being set
            MainActivity.alertIntent.putExtra("ToDo", dbTask);
            MainActivity.alertIntent.putExtra("broadId", dbBroadcast);

            MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(), dbBroadcast,
                    MainActivity.alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (!dbSnooze) {
                MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
                        Integer.parseInt(MainActivity.sortedIDs
                                .get(position)), MainActivity.alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
            } else {
                MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
                        Integer.parseInt(
                                MainActivity.sortedIDs.get
                                        (position) + 1000),
                        MainActivity.alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
            }

            MainActivity.alarmManager.cancel(MainActivity.pendIntent);

            MainActivity.alarmManager.set(AlarmManager.RTC, futureDate.getTimeInMillis(),
                    MainActivity.pendIntent);

            MainActivity.db.updateDue(
                    MainActivity.sortedIDs.get(position), true);

            MainActivity.db.updateShowOnce(
                    MainActivity.sortedIDs.get(position), true);

        }

        //TODO find out if this delay can be removed
        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {

                //Marks properties as not showing
                MainActivity.taskPropertiesShowing = false;

                MainActivity.repeating = false;

                new Reorder();
                //Updating the view with the new order
                MainActivity.theAdapter = new ListAdapter[]{new MyAdapter(
                        getContext(), MainActivity.taskList)};
                MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                //TODO make animation work
//                MainActivity.alarmAnimation = true;
//                MainActivity.animateID = Integer.parseInt(MainActivity.sortedIDs.get(position));
//                MainActivity.animatePosition = position;
            }
        };

        handler.postDelayed(runnable, 1);//TODO app stuffs up if this handler is removed for some reason

        if (dbRepeat) {

            MainActivity.db.updateRepeat(MainActivity.sortedIDs
                    .get(position), true);

            MainActivity.repeating = false;

        }

    }

}