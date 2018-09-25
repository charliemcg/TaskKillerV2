package com.violenthoboenterprises.taskkiller;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
        TableRow taskNameRow = taskView.findViewById(R.id.taskName);
        //For displaying the date and time pickers
//        final TableRow dateRow = taskView.findViewById(R.id.dateTime);
        //For displaying the alarm options
        final TableRow alarmOptionsRow = taskView.findViewById(R.id.alarmOptions);
        //For displaying the repeat options
        final TableRow repeatRow = taskView.findViewById(R.id.repeat);
        //For displaying the ad
        final TableRow adRow = taskView.findViewById(R.id.adRow);
        //For displaying the snooze options
        final TableRow snoozeRow = taskView.findViewById(R.id.snoozeRow);
        //For displaying the overdue options
        final TableRow taskOverdueRow = taskView.findViewById(R.id.taskIsOverdue);
        //Date and time picker allow user to set due time
//        final DatePicker datePicker = taskView.findViewById(R.id.datePicker);
//        final TimePicker timePicker = taskView.findViewById(R.id.timePicker);
        //Displays the tasks due date
        TextView dueTextView = taskView.findViewById(R.id.dueTextView);
        //Button used for marking task as complete
        ImageView complete = taskView.findViewById(R.id.complete);
        ImageView completeWhite = taskView.findViewById(R.id.completeWhite);
        //Graphically depicts a task as being complete
        ImageView completed = taskView.findViewById(R.id.completed);
        ImageView completedWhite = taskView.findViewById(R.id.completedWhite);
        //Gives user ability to set alarm on click
        final LinearLayout alarm = taskView.findViewById(R.id.alarm);
        //Icon needs to changed based on light/dark mode
        ImageView alarmBtnIcon = taskView.findViewById(R.id.alarmBtnIcon);
        ImageView alarmBtnIconWhite = taskView.findViewById(R.id.alarmBtnIconWhite);
        //The text on this button needs to change depending on the state of the alarm
        final TextView alarmBtnText = taskView.findViewById(R.id.alarmBtnText);
        //Need the following texts for color changing
        final TextView subtasksBtnText = taskView.findViewById(R.id.subtasksBtnText);
        //Icon needs to changed based on light/dark mode
        ImageView subTasksBtnIcon = taskView.findViewById(R.id.subTasksBtnIcon);
        ImageView subTasksBtnIconWhite = taskView.findViewById(R.id.subTasksBtnIconWhite);
        final TextView noteBtnText = taskView.findViewById(R.id.noteBtnText);
        //Icon needs to changed based on light/dark mode
        ImageView noteBtnIcon = taskView.findViewById(R.id.noteBtnIcon);
        ImageView noteBtnIconWhite = taskView.findViewById(R.id.noteBtnIconWhite);
        final TextView killAlarmBtnText = taskView.findViewById(R.id.killAlarmBtnText);
        final TextView resetAlarmBtnText = taskView.findViewById(R.id.resetAlarmBtnText);
        final TextView dailyBtnText = taskView.findViewById(R.id.dailyBtnText);
        final TextView weeklyBtnText = taskView.findViewById(R.id.weeklyBtnText);
        final TextView monthlyBtnText = taskView.findViewById(R.id.monthlyBtnText);
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
        //For setting the due date
//        final Button dateButton = taskView.findViewById(R.id.date);
        //Sets task to repeat daily
        final LinearLayout daily = taskView.findViewById(R.id.daily);
        //Sets task to repeat weekly
        final LinearLayout weekly = taskView.findViewById(R.id.weekly);
        //Sets task to repeat monthly
        final LinearLayout monthly = taskView.findViewById(R.id.monthly);
        //Removes alarm from task
        final LinearLayout killAlarmBtn = taskView.findViewById(R.id.killAlarmBtn);
        //Allows user to change the due date of a task
        final LinearLayout resetAlarmBtn = taskView.findViewById(R.id.resetAlarmBtn);
        //Displays repeat options
        final LinearLayout repeatAlarmBtn = taskView.findViewById(R.id.repeatBtn);
        //The display of status icons
        final LinearLayout statusLayout = taskView.findViewById(R.id.statusLayout);
        //Repeat button text needs to change depending on what state the repeat is in
        final TextView repeatAlarmBtnText = taskView.findViewById(R.id.repeatAlarmBtnText);
        //Task status icons are transparent. This is so the background colour can be
        // changed giving the illusion that the icon image color has changed.
        // Each icon has it's own relative layout.
        final ImageView dueClear = taskView.findViewById(R.id.dueClear);
        final ImageView dueClearWhite = taskView.findViewById(R.id.dueClearWhite);
        RelativeLayout dueLayout = taskView.findViewById(R.id.dueLayout);
        ImageView overdueClear = taskView.findViewById(R.id.overdueClear);
        ImageView overdueClearWhite = taskView.findViewById(R.id.overdueClearWhite);
        RelativeLayout overdueLayout = taskView.findViewById(R.id.overdueLayout);
        final ImageView snoozeClear = taskView.findViewById(R.id.snoozeClear);
        final ImageView snoozeClearWhite = taskView.findViewById(R.id.snoozeClearWhite);
        RelativeLayout snoozeLayout = taskView.findViewById(R.id.snoozeLayout);
        ImageView repeatDayClear = taskView.findViewById(R.id.repeatDayClear);
        ImageView repeatDayClearWhite = taskView.findViewById(R.id.repeatDayClearWhite);
        RelativeLayout repeatDayLayout = taskView.findViewById(R.id.repeatDayLayout);
        ImageView repeatWeekClear = taskView.findViewById(R.id.repeatWeekClear);
        ImageView repeatWeekClearWhite = taskView.findViewById(R.id.repeatWeekClearWhite);
        RelativeLayout repeatWeekLayout = taskView.findViewById(R.id.repeatWeekLayout);
        ImageView repeatMonthClear = taskView.findViewById(R.id.repeatMonthClear);
        ImageView repeatMonthClearWhite = taskView.findViewById(R.id.repeatMonthClearWhite);
        RelativeLayout repeatMonthLayout = taskView.findViewById(R.id.repeatMonthLayout);
        ImageView repeatClear = taskView.findViewById(R.id.repeatClear);
        ImageView repeatClearWhite = taskView.findViewById(R.id.repeatClearWhite);
        RelativeLayout repeatLayout = taskView.findViewById(R.id.repeatLayout);
        ImageView noteClear = taskView.findViewById(R.id.noteClear);
        ImageView noteClearWhite = taskView.findViewById(R.id.noteClearWhite);
        ImageView checklistClear = taskView.findViewById(R.id.checklistClear);
        ImageView checklistClearWhite = taskView.findViewById(R.id.checklistClearWhite);
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
        Boolean dbShowOnce = false;
        int dbInterval = 0;
        String dbRepeatInterval = "";
        Boolean dbIgnored = false;
        String dbTimeCreated = "";
        int dbSortedIndex = 0;
        int dbChecklistSize = 0;
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
            dbShowOnce = dbResult.getInt(11) > 0;
            dbInterval = dbResult.getInt(12);
            dbRepeatInterval = dbResult.getString(13);
            dbIgnored = dbResult.getInt(14) > 0;
            dbTimeCreated = dbResult.getString(15);
            dbSortedIndex = dbResult.getInt(16);
            dbChecklistSize = dbResult.getInt(17);
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
        while(uniResult.moveToNext()){
            uniSetAlarm = uniResult.getInt(10) > 0;
            uniYear = uniResult.getInt(11);
            uniMonth = uniResult.getInt(12);
            uniDay = uniResult.getInt(13);
            uniHour = uniResult.getInt(14);
            uniMinute = uniResult.getInt(15);
        }
        uniResult.close();

        final int finalDbID = dbID;
        final String finalDbNote = dbNote;
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
        final String finalDbTimeCreated = dbTimeCreated;
        final int finalDbSortedIndex = dbSortedIndex;
        final int finalDbChecklistSize = dbChecklistSize;

        final String finalAlarmHour = alarmHour;
        final String finalAlarmMinute = alarmMinute;
        final String finalAlarmAmpm = alarmAmpm;
        final String finalAlarmDay = alarmDay;
        final String finalAlarmMonth = alarmMonth;
        final String finalAlarmYear = alarmYear;

        final Boolean finalUniSetAlarm = uniSetAlarm;
        final int finalUniYear = uniYear;
        final int finalUniMonth = uniMonth;
        final int finalUniDay = uniDay;
        final int finalUniHour = uniHour;
        final int finalUniMinute = uniMinute;

        if(MainActivity.mute){
            complete.setSoundEffectsEnabled(false);
            alarm.setSoundEffectsEnabled(false);
            subTasks.setSoundEffectsEnabled(false);
            note.setSoundEffectsEnabled(false);
//            dateButton.setSoundEffectsEnabled(false);
            daily.setSoundEffectsEnabled(false);
            weekly.setSoundEffectsEnabled(false);
            monthly.setSoundEffectsEnabled(false);
            killAlarmBtn.setSoundEffectsEnabled(false);
            resetAlarmBtn.setSoundEffectsEnabled(false);
            repeatAlarmBtn.setSoundEffectsEnabled(false);
            snoozeTask.setSoundEffectsEnabled(false);
            taskDone.setSoundEffectsEnabled(false);
            taskIgnore.setSoundEffectsEnabled(false);
            oneHourBtn.setSoundEffectsEnabled(false);
            fourHourBtn.setSoundEffectsEnabled(false);
            tomorrowBtn.setSoundEffectsEnabled(false);
            taskView.setSoundEffectsEnabled(false);
            theTextView.setSoundEffectsEnabled(false);
            taskNameRow.setSoundEffectsEnabled(false);
        }

        if(!MainActivity.lightDark){
            taskView.setBackgroundColor(Color.parseColor("#333333"));
            propertyRow.setBackgroundColor(Color.parseColor("#333333"));
            dueTextView.setBackgroundColor(Color.parseColor("#333333"));
            statusLayout.setBackgroundColor(Color.parseColor("#333333"));
            theTextView.setBackgroundColor(Color.parseColor("#333333"));
//            dateButton.setBackgroundColor(Color.parseColor("#333333"));
            alarmOptionsRow.setBackgroundColor(Color.parseColor("#333333"));
            snoozeRow.setBackgroundColor(Color.parseColor("#333333"));
            taskOverdueRow.setBackgroundColor(Color.parseColor("#333333"));
            repeatRow.setBackgroundColor(Color.parseColor("#333333"));
//            dateRow.setBackgroundColor(Color.parseColor("#333333"));
//            datePicker.setBackgroundColor(Color.parseColor("#333333"));
//            timePicker.setBackgroundColor(Color.parseColor("#333333"));
            theTextView.setTextColor(Color.parseColor("#AAAAAA"));
            dueTextView.setTextColor(Color.parseColor("#AAAAAA"));
//            dateButton.setTextColor(Color.parseColor("#AAAAAA"));
            alarmBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            subtasksBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            noteBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            killAlarmBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            resetAlarmBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            repeatAlarmBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            dailyBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            weeklyBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            monthlyBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            oneHourBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            fourHoursBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            tomorrowBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            snoozeTaskBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            taskDoneBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            taskIgnoreBtnText.setTextColor(Color.parseColor("#AAAAAA"));
            checklistClear.setVisibility(View.VISIBLE);
            noteClear.setVisibility(View.VISIBLE);
            repeatClear.setVisibility(View.VISIBLE);
            repeatDayClear.setVisibility(View.VISIBLE);
            repeatWeekClear.setVisibility(View.VISIBLE);
            repeatMonthClear.setVisibility(View.VISIBLE);
            dueClear.setVisibility(View.VISIBLE);
            overdueClear.setVisibility(View.VISIBLE);
            snoozeClear.setVisibility(View.VISIBLE);
            checklistClearWhite.setVisibility(View.GONE);
            noteClearWhite.setVisibility(View.GONE);
            repeatClearWhite.setVisibility(View.GONE);
            repeatDayClearWhite.setVisibility(View.GONE);
            repeatWeekClearWhite.setVisibility(View.GONE);
            repeatMonthClearWhite.setVisibility(View.GONE);
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
//            dateButton.setBackgroundColor(Color.parseColor("#FFFFFF"));
            alarmOptionsRow.setBackgroundColor(Color.parseColor("#FFFFFF"));
            snoozeRow.setBackgroundColor(Color.parseColor("#FFFFFF"));
            taskOverdueRow.setBackgroundColor(Color.parseColor("#FFFFFF"));
            repeatRow.setBackgroundColor(Color.parseColor("#FFFFFF"));
//            dateRow.setBackgroundColor(Color.parseColor("#FFFFFF"));
//            datePicker.setBackgroundColor(Color.parseColor("#FFFFFF"));
//            timePicker.setBackgroundColor(Color.parseColor("#FFFFFF"));
            theTextView.setTextColor(Color.parseColor("#000000"));
            dueTextView.setTextColor(Color.parseColor("#000000"));
//            dateButton.setTextColor(Color.parseColor("#000000"));
            alarmBtnText.setTextColor(Color.parseColor("#000000"));
            subtasksBtnText.setTextColor(Color.parseColor("#000000"));
            noteBtnText.setTextColor(Color.parseColor("#000000"));
            killAlarmBtnText.setTextColor(Color.parseColor("#000000"));
            resetAlarmBtnText.setTextColor(Color.parseColor("#000000"));
            repeatAlarmBtnText.setTextColor(Color.parseColor("#000000"));
            dailyBtnText.setTextColor(Color.parseColor("#000000"));
            weeklyBtnText.setTextColor(Color.parseColor("#000000"));
            monthlyBtnText.setTextColor(Color.parseColor("#000000"));
            oneHourBtnText.setTextColor(Color.parseColor("#000000"));
            fourHoursBtnText.setTextColor(Color.parseColor("#000000"));
            tomorrowBtnText.setTextColor(Color.parseColor("#000000"));
            snoozeTaskBtnText.setTextColor(Color.parseColor("#000000"));
            taskDoneBtnText.setTextColor(Color.parseColor("#000000"));
            taskIgnoreBtnText.setTextColor(Color.parseColor("#000000"));
            checklistClear.setVisibility(View.GONE);
            noteClear.setVisibility(View.GONE);
            repeatClear.setVisibility(View.GONE);
            repeatDayClear.setVisibility(View.GONE);
            repeatWeekClear.setVisibility(View.GONE);
            repeatMonthClear.setVisibility(View.GONE);
            dueClear.setVisibility(View.GONE);
            overdueClear.setVisibility(View.GONE);
            snoozeClear.setVisibility(View.GONE);
            checklistClearWhite.setVisibility(View.VISIBLE);
            noteClearWhite.setVisibility(View.VISIBLE);
            repeatClearWhite.setVisibility(View.VISIBLE);
            repeatDayClearWhite.setVisibility(View.VISIBLE);
            repeatWeekClearWhite.setVisibility(View.VISIBLE);
            repeatMonthClearWhite.setVisibility(View.VISIBLE);
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
            alarm.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.layout_border_white));
            subTasks.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.layout_border_white));
            note.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.layout_border_white));
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
//                    handler.postDelayed(runnable, 50);
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
//            Log.i(TAG, "Position: " + position);
//            if(position == (MainActivity.taskList.size() - 1)){
//                MainActivity.theListView.setSelection(MainActivity.animatePosition);
//                //TODO make sure to get correct resting position
//                if(MainActivity.animatePosition == (MainActivity.taskList.size() - 1)){
//                    Log.i(TAG, "Animation complete");
//                    MainActivity.alarmAnimation = false;
//                }else{
//                    Log.i(TAG, "Reorder");
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

        //Displaying ad if there are five or more tasks
//        if(position == 4) {
//            adRow.setVisibility(View.VISIBLE);
//            boolean networkAvailable = false;
//            ConnectivityManager connectivityManager = (ConnectivityManager)
//                    getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
//                networkAvailable = true;
//            }
//
//            //Initialising banner ad
//            final AdView adView = taskView.findViewById(R.id.adView);
//            ImageView banner = taskView.findViewById(R.id.banner);
//
//            if (networkAvailable) {
//                adView.setVisibility(View.VISIBLE);
//                final AdRequest banRequest = new AdRequest.Builder()
//        //TODO probably need a new ID
//                        .addTestDevice("7A57C74D0EDE338C302869CB538CD3AC")/*.addTestDevice
//                    (AdRequest.DEVICE_ID_EMULATOR)*/.build();//TODO remove .addTestDevice()
//                adView.loadAd(banRequest);
//            } else {
//                banner.setVisibility(View.VISIBLE);
//            }
//        }

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

        //implementing exit animations if required
        if(MainActivity.exitTaskProperties && (position == MainActivity.activeTask)){
            if(!dbOverdue){
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
        }else if(MainActivity.exitAlarmOptions && (position == MainActivity.activeTask)){
            alarmOptionsRow.setVisibility(View.VISIBLE);
            alarmOptionsRow.startAnimation(AnimationUtils.loadAnimation
                    (getContext(), R.anim.exit_out_left));

            final Handler handler = new Handler();

            final Runnable runnable = new Runnable() {
                public void run() {
                    alarmOptionsRow.setVisibility(View.GONE);
                }
            };

            handler.postDelayed(runnable, 400);
        }else if(MainActivity.exitDatePicker && (position == MainActivity.activeTask)){
//            dateRow.setVisibility(View.VISIBLE);
//            dateRow.startAnimation(AnimationUtils.loadAnimation
//                    (getContext(), R.anim.exit_out_left));

            final Handler handler = new Handler();

            final Runnable runnable = new Runnable() {
                public void run() {
//                    dateRow.setVisibility(View.GONE);
                }
            };

            handler.postDelayed(runnable, 400);
        }else if(MainActivity.exitTimePicker && (position == MainActivity.activeTask)){

//            dateRow.setVisibility(View.VISIBLE);
//            datePicker.setVisibility(View.GONE);
//            timePicker.setVisibility(View.VISIBLE);
//            timePicker.startAnimation(AnimationUtils.loadAnimation
//                    (getContext(), R.anim.exit_out_left));

            final Handler handler = new Handler();

            final Runnable runnable = new Runnable() {
                public void run() {
//                    timePicker.setVisibility(View.GONE);
                }
            };

            handler.postDelayed(runnable, 400);
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
                MainActivity.db.updateTimestamp(MainActivity.sortedIDs
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
                    MainActivity.db.updateAlarmData(String.valueOf(
                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                            alarmHour, alarmMinute, alarmAmpm,
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
                //setting new timestamp to a week from previous
                int newStamp = Integer.parseInt(dbTimestamp) + 604800;
                MainActivity.db.updateTimestamp(MainActivity.sortedIDs
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
                    MainActivity.db.updateAlarmData(String.valueOf(
                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                            alarmHour, alarmMinute, alarmAmpm,
                            String.valueOf(theDay), String.valueOf(theMonth),
                            String.valueOf(theYear));

                    //cancelling any snooze data
                    MainActivity.db.updateSnoozeData(String.valueOf(
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
                MainActivity.db.updateTimestamp(MainActivity.sortedIDs
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
                    MainActivity.db.updateAlarmData(String.valueOf(
                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                            alarmHour, alarmMinute, alarmAmpm,
                            String.valueOf(newDay), String.valueOf(newMonth),
                            String.valueOf(newYear));

                    //cancelling any snooze data
                    MainActivity.db.updateSnoozeData(String.valueOf(
                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                            "", "", "", "", "", "");

                }
            }
        }

        if(uniSetAlarm && (position == MainActivity.activeTask)){
            setAlarm(position, uniYear, uniMonth, uniDay, uniHour, uniMinute);
            MainActivity.db.updateSetAlarm(false);
            MainActivity.db.updateYear(0);
            MainActivity.db.updateMonth(0);
            MainActivity.db.updateDay(0);
            MainActivity.db.updateHour(0);
            MainActivity.db.updateMinute(0);
//            MainActivity.selectedYear = 0;
//            MainActivity.selectedMonth = 0;
//            MainActivity.selectedDay = 0;
//            MainActivity.selectedHour = 0;
//            MainActivity.selectedMinute = 0;
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

        //Show due icon and due date if required
        if (dbDue) {

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

            //Checking for overdue tasks
            String formattedTime;
            Boolean sameDay = false;
            Boolean tomorrow = false;
            Boolean markAsOverdue = false;
            if(!dbKilled) {
                //Overdue
                if (currentYear > Integer.valueOf(year)) {
                    dueClear.setVisibility(View.GONE);
                    dueLayout.setVisibility(View.GONE);
                    overdueClear.setVisibility(View.VISIBLE);
                    overdueLayout.setVisibility(View.VISIBLE);
                    if(MainActivity.lightDark) {
                        overdueClear.setBackgroundColor(ContextCompat
                                .getColor(getContext(), R.color.darkRed));
                        overdueClearWhite.setBackgroundColor(ContextCompat
                                .getColor(getContext(), R.color.darkRed));
                        dueTextView.setTextColor(ContextCompat.getColor
                                (getContext(), R.color.darkRed));
                    }else{
                        overdueClear.setBackgroundColor(ContextCompat
                                .getColor(getContext(), R.color.lightRed));
                        overdueClearWhite.setBackgroundColor(ContextCompat
                                .getColor(getContext(), R.color.lightRed));
                        dueTextView.setTextColor(ContextCompat.getColor
                                (getContext(), R.color.lightRed));
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
                    if(MainActivity.lightDark) {
                        overdueClear.setBackgroundColor(ContextCompat
                                .getColor(getContext(), R.color.darkRed));
                        overdueClearWhite.setBackgroundColor(ContextCompat
                                .getColor(getContext(), R.color.darkRed));
                        dueTextView.setTextColor(ContextCompat.getColor
                                (getContext(), R.color.darkRed));
                    }else{
                        overdueClear.setBackgroundColor(ContextCompat
                                .getColor(getContext(), R.color.lightRed));
                        overdueClearWhite.setBackgroundColor(ContextCompat
                                .getColor(getContext(), R.color.lightRed));
                        dueTextView.setTextColor(ContextCompat.getColor
                                (getContext(), R.color.lightRed));
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
                    if(MainActivity.lightDark) {
                        overdueClear.setBackgroundColor(ContextCompat
                                .getColor(getContext(), R.color.darkRed));
                        overdueClearWhite.setBackgroundColor(ContextCompat
                                .getColor(getContext(), R.color.darkRed));
                        dueTextView.setTextColor(ContextCompat.getColor
                                (getContext(), R.color.darkRed));
                    }else{
                        overdueClear.setBackgroundColor(ContextCompat
                                .getColor(getContext(), R.color.lightRed));
                        overdueClearWhite.setBackgroundColor(ContextCompat
                                .getColor(getContext(), R.color.lightRed));
                        dueTextView.setTextColor(ContextCompat.getColor
                                (getContext(), R.color.lightRed));
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
                        if(MainActivity.lightDark) {
                            overdueClear.setBackgroundColor(ContextCompat
                                    .getColor(getContext(), R.color.darkRed));
                            overdueClearWhite.setBackgroundColor(ContextCompat
                                    .getColor(getContext(), R.color.darkRed));
                            dueTextView.setTextColor(ContextCompat.getColor
                                    (getContext(), R.color.darkRed));
                        }else{
                            overdueClear.setBackgroundColor(ContextCompat
                                    .getColor(getContext(), R.color.lightRed));
                            overdueClearWhite.setBackgroundColor(ContextCompat
                                    .getColor(getContext(), R.color.lightRed));
                            dueTextView.setTextColor(ContextCompat.getColor
                                    (getContext(), R.color.lightRed));
                        }
                        markAsOverdue = true;
                    //Overdue
                    } else if (currentHour == adjustedHour
                            && currentMinute >= Integer.valueOf(minute)) {
                        dueClear.setVisibility(View.GONE);
                        dueLayout.setVisibility(View.GONE);
                        overdueClear.setVisibility(View.VISIBLE);
                        overdueLayout.setVisibility(View.VISIBLE);
                        dueTextView.setTextColor(Color.parseColor("#FF0000"));
                        if(MainActivity.lightDark) {
                            overdueClear.setBackgroundColor(ContextCompat
                                    .getColor(getContext(), R.color.darkRed));
                            overdueClearWhite.setBackgroundColor(ContextCompat
                                    .getColor(getContext(), R.color.darkRed));
                            dueTextView.setTextColor(ContextCompat.getColor
                                    (getContext(), R.color.darkRed));
                        }else{
                            overdueClear.setBackgroundColor(ContextCompat
                                    .getColor(getContext(), R.color.lightRed));
                            overdueClearWhite.setBackgroundColor(ContextCompat
                                    .getColor(getContext(), R.color.lightRed));
                            dueTextView.setTextColor(ContextCompat.getColor
                                    (getContext(), R.color.lightRed));
                        }
                        markAsOverdue = true;
                        //Not overdue
                    } else {
                        dueClear.setBackgroundColor(Color.parseColor(MainActivity.highlight));
                        dueClearWhite.setBackgroundColor(Color.parseColor(MainActivity.highlight));
                    }
                    //Not overdue
                } else {
                    //Checking if date due tomorrow
                    //Incrementing day
                    if (((currentMonth == 0) || (currentMonth == 2)
                            || (currentMonth == 4) || (currentMonth == 6)
                            || (currentMonth == 7) || (currentMonth == 9))
                            && (currentDay == 31) && (Integer.valueOf(day) == 1)) {
                        tomorrow = true;
                    } else if (((currentMonth == 1) || (currentMonth == 3)
                            || (currentMonth == 5) || (currentMonth == 8)
                            || (currentMonth == 10)) && (currentDay == 30)
                            && (Integer.valueOf(day) == 1)) {
                        tomorrow = true;
                    } else if ((currentMonth == 11) && (currentDay == 31)
                            && (Integer.valueOf(day) == 1)) {
                        tomorrow = true;
                    } else if ((currentMonth == 1) && (currentDay == 28)
                            && (currentYear % 4 != 0) && (Integer.valueOf(day) == 1)) {
                        tomorrow = true;
                    } else if ((currentMonth == 1) && (currentDay == 29)
                            && (currentYear % 4 == 0) && (Integer.valueOf(day) == 1)) {
                        tomorrow = true;
                    } else if (currentDay == (Integer.valueOf(day) - 1)){
                        tomorrow = true;
                    }
                    dueClear.setBackgroundColor(Color.parseColor(MainActivity.highlight));
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
                    //show snooze icon
                } else {
                    snoozeClear.setVisibility(View.VISIBLE);
                    snoozeLayout.setVisibility(View.VISIBLE);
                    overdueClear.setVisibility(View.GONE);
                    overdueLayout.setVisibility(View.GONE);
                }

                //Show the once off overdue options
//            }else if(markAsOverdue && dbShowOnce){
//
//                MainActivity.db.updateOverdue(
//                        MainActivity.sortedIDs.get(position), true);
//
//                MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);
//
            }else if(markAsOverdue){

                //if ignored task is ignored beyond the next repeat then
                // the due time is updated to that new repeat time
//                if((currentDate.get(Calendar.MINUTE) >= (Integer
//                        .parseInt(hour) + dbInterval)) && !dbRepeat){
//
//                    alarmHour = String.valueOf(Integer.parseInt(alarmHour) + dbInterval);
//
//                    MainActivity.noteDb.updateAlarmData(String.valueOf(
//                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
//                            alarmHour, alarmMinute, alarmAmpm, alarmDay, alarmMonth, alarmYear);
//
//                }

                MainActivity.db.updateOverdue(String.valueOf(
                        MainActivity.sortedIDs.get(position)), true);

            }

            //If task due on following day say "Tomorrow"
            if (tomorrow){

                dueTextView.setText(R.string.tomorrow);

                //If task due on same day show the due date
            } else if(!sameDay){
                //TODO account for MM/DD/YYYY https://en.wikipedia.org/wiki/Date_format_by_country
                //Formatting date
                String formattedMonth = "";
                String formattedDate;

                //TODO account for all these numbers in different languages
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

                formattedDate = day + " " + formattedMonth;

                dueTextView.setText(formattedDate);

                //If task due on different day show the due time
            }else{

                if(Integer.valueOf(hour) == 0){
                    hour = getContext().getString(R.string.twelveNumerals);
                }
                if(Integer.valueOf(minute) < 10){
                    if(Integer.valueOf(ampm) == 0) {
                        formattedTime = hour + getContext().getString(R.string.colonZero) + minute + getContext().getString(R.string.am);
                    }else{
                        formattedTime = hour + getContext().getString(R.string.colonZero) + minute + getContext().getString(R.string.pm);
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

//                MainActivity.killedAnimation = true;
//                MainActivity.animateID = Integer.parseInt(MainActivity.sortedIDs.get(position));
//                MainActivity.animatePosition = position;

                notifyDataSetChanged();

                MainActivity.taskPropertiesShowing = false;

                MainActivity.db.updateKilled(String.valueOf(
                        MainActivity.sortedIDs.get(position)), true);

                MainActivity.db.updateIgnored(MainActivity.sortedIDs
                        .get(position), false);

                MainActivity.toast.setText(R.string.youKilledThisTask);
                final Handler handler = new Handler();

                final Runnable runnable = new Runnable() {
                    public void run() {
                        MainActivity.sweep.start();
                        MainActivity.toast.startAnimation(AnimationUtils.loadAnimation
                                (getContext(), R.anim.enter_from_right_fast));
                        MainActivity.toast.setVisibility(View.VISIBLE);
                        final Handler handler2 = new Handler();
                        final Runnable runnable2 = new Runnable(){
                            public void run(){
                                MainActivity.toast.startAnimation(AnimationUtils.loadAnimation
                                        (getContext(), android.R.anim.fade_out));
                                MainActivity.toast.setVisibility(View.GONE);
                            }
                        };
                        handler2.postDelayed(runnable2, 1500);
                    }
                };

                handler.postDelayed(runnable, 500);

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
                MainActivity.db.updateTimestamp(String.valueOf(MainActivity
                        .sortedIDs.get(position)), String.valueOf(adjustedStamp));

                //updating due time in database
                MainActivity.db.updateAlarmData(String.valueOf(
                        MainActivity.sortedIDs.get(position)),
                        finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                        String.valueOf(newDay), String.valueOf(newMonth),
                        String.valueOf(newYear));

                MainActivity.db.updateShowOnce(
                        MainActivity.sortedIDs.get(MainActivity.activeTask), true);

                //TODO Show this only when necessary
                MainActivity.toast.setText(R.string.youCanCancelRepeat);
                final Handler handler = new Handler();

                final Runnable runnable = new Runnable() {
                    public void run() {
                        MainActivity.sweep.start();
                        MainActivity.toast.startAnimation(AnimationUtils.loadAnimation
                                (getContext(), R.anim.enter_from_right_fast));
                        MainActivity.toast.setVisibility(View.VISIBLE);
                        final Handler handler2 = new Handler();
                        final Runnable runnable2 = new Runnable(){
                            public void run(){
                                MainActivity.toast.startAnimation(AnimationUtils.loadAnimation
                                        (getContext(), android.R.anim.fade_out));
                                MainActivity.toast.setVisibility(View.GONE);
                            }
                        };
                        handler2.postDelayed(runnable2, 1500);
                    }
                };

                handler.postDelayed(runnable, 500);

                propertyRow.setVisibility(View.GONE);

                MainActivity.taskPropertiesShowing = false;

                MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

            }

        }else if(MainActivity.taskPropertiesShowing && position == MainActivity.activeTask){

            //Determine whether to show datepicker
            if(MainActivity.datePickerShowing) {

                if(!MainActivity.exitTimePicker) {

                    propertyRow.setVisibility(View.VISIBLE);
                    propertyRow.startAnimation(AnimationUtils.loadAnimation
                            (getContext(), R.anim.exit_out_right));

                    final Handler handler = new Handler();

                    final Runnable runnable = new Runnable() {
                        public void run() {

                            propertyRow.setVisibility(View.GONE);
//                            dateRow.startAnimation(AnimationUtils.loadAnimation
//                                    (getContext(), R.anim.enter_from_right));
//                            dateRow.setVisibility(View.VISIBLE);

                        }
                    };

                    handler.postDelayed(runnable, 300);

                    MainActivity.dateOrTime = true;

                //run exit animation on timepicker and reinstate the date picker
                }else{

                    final Handler handler = new Handler();

                    final Runnable runnable = new Runnable() {
                        public void run() {
//                            datePicker.startAnimation(AnimationUtils.loadAnimation
//                                    (getContext(), R.anim.enter_from_right));
//                            datePicker.setVisibility(View.VISIBLE);
                        }
                    };

                    handler.postDelayed(runnable, 400);

                    MainActivity.exitTimePicker = false;

                    MainActivity.dateOrTime = false;

                }
            //Show alarm options
            }else if(MainActivity.alarmOptionsShowing) {

                if (!MainActivity.exitRepeat){
//                    dateRow.setVisibility(View.VISIBLE);
//                    dateRow.startAnimation(AnimationUtils.loadAnimation
//                            (getContext(), R.anim.exit_out_left));
                    MainActivity.exitChangeDueDate = false;
                }else {
                    repeatRow.setVisibility(View.VISIBLE);
                    repeatRow.startAnimation(AnimationUtils.loadAnimation
                            (getContext(), R.anim.exit_out_left));
                    MainActivity.exitRepeat = false;
                }

                ViewGroup.LayoutParams params = killAlarmBtn.getLayoutParams();
                params.width = MainActivity.deviceWidthPortrait / 3;
                killAlarmBtn.setLayoutParams(params);
                resetAlarmBtn.setLayoutParams(params);
                repeatAlarmBtn.setLayoutParams(params);

                final Handler handler = new Handler();

                final Runnable runnable = new Runnable() {
                    public void run() {
//                        dateRow.setVisibility(View.GONE);
                        repeatRow.setVisibility(View.GONE);
                        alarmOptionsRow.startAnimation(AnimationUtils.loadAnimation
                                (getContext(), R.anim.enter_from_right));

                        alarmOptionsRow.setVisibility(View.VISIBLE);
                    }
                };

                handler.postDelayed(runnable, 400);

                MainActivity.alarmOptionsShowing = true;

                //Actions to occur if user selects 'remove alarm'
                killAlarmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MainActivity.vibrate.vibrate(50);

                        if(!MainActivity.mute){
                            MainActivity.blip.start();
                        }

                        MainActivity.db.updateDue(String.valueOf(MainActivity
                                .sortedIDs.get(MainActivity.activeTask)), false);

                        MainActivity.db.updateRepeat(MainActivity.sortedIDs
                                .get(position), false);

                        MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
                                Integer.parseInt(MainActivity.sortedIDs.get(position)),
                                MainActivity.alertIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                        MainActivity.alarmManager.cancel(MainActivity.pendIntent);

                        MainActivity.db.updateAlarmData(String.valueOf(MainActivity.activeTask),
                                "", "", "", "", "", "");

                        MainActivity.alarmOptionsShowing = false;

                        notifyDataSetChanged();

                    }
                });

                //Actions to occur if user selects 'change due date'
                resetAlarmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MainActivity.vibrate.vibrate(50);

                        if(!MainActivity.mute){
                            MainActivity.blip.start();
                        }

                        MainActivity.datePickerShowing = true;

                        MainActivity.dateRowShowing = true;

                        notifyDataSetChanged();

                    }
                });

                //Actions to occur if user selects 'repeat alarm'
                repeatAlarmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MainActivity.vibrate.vibrate(50);

                        if(!MainActivity.mute){
                            MainActivity.blip.start();
                        }

                        alarmOptionsRow.setVisibility(View.GONE);

                        repeatRow.startAnimation(AnimationUtils.loadAnimation
                                (getContext(), android.R.anim.slide_in_left));
                        repeatRow.setVisibility(View.VISIBLE);

                        MainActivity.alarmOptionsShowing = false;

                    }
                });

            //show the overdue properties
            }else if(dbOverdue && !dbSnooze && !dbIgnored){

                ViewGroup.LayoutParams params = snoozeTask.getLayoutParams();
                params.width = MainActivity.deviceWidthPortrait / 3;
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

                        if(!MainActivity.mute){
                            MainActivity.blip.start();
                        }

                        taskOverdueRow.startAnimation(AnimationUtils.loadAnimation
                                (getContext(), R.anim.exit_out_right));

                        ViewGroup.LayoutParams params = oneHourBtn.getLayoutParams();
                        params.width = MainActivity.deviceWidthPortrait / 3;
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

                                if(!MainActivity.mute){
                                    MainActivity.blip.start();
                                }

                                snoozeRow.startAnimation(AnimationUtils.loadAnimation
                                        (getContext(), R.anim.exit_out_right));

                                final Handler handler = new Handler();

                                final Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {

                                MainActivity.db.updateInterval(String.valueOf(MainActivity
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

                                    MainActivity.toast.setText
                                            (R.string.taskNotSnoozedBecause);
                                    final Handler handler = new Handler();

                                    final Runnable runnable = new Runnable() {
                                        public void run() {
                                            MainActivity.sweep.start();
                                            MainActivity.toast.startAnimation
                                                    (AnimationUtils.loadAnimation(getContext(),
                                                            R.anim.enter_from_right_fast));
                                            MainActivity.toast.setVisibility(View.VISIBLE);
                                            final Handler handler2 = new Handler();
                                            final Runnable runnable2 = new Runnable(){
                                                public void run(){
                                                    MainActivity.toast.startAnimation
                                                            (AnimationUtils.loadAnimation
                                                                    (getContext(),
                                                                            android.R.anim
                                                                                    .fade_out));
                                                    MainActivity.toast.setVisibility(View.GONE);
                                                }
                                            };
                                            handler2.postDelayed(runnable2, 1500);
                                        }
                                    };

                                    handler.postDelayed(runnable, 500);

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
                                    MainActivity.db.updateAlarmData(String.valueOf(
                                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                            finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                                            String.valueOf(newDay),
                                            String.valueOf(newMonth),
                                            String.valueOf(newYear));

                                    //cancelling any snoozed alarm data
                                    MainActivity.db.updateSnoozeData(String.valueOf(
                                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "");

                                    MainActivity.db.updateTimestamp(String.valueOf(MainActivity
                                            .sortedIDs.get(position)),
                                            String.valueOf(adjustedStamp));

                                    MainActivity.db.updateOverdue(String.valueOf(
                                            MainActivity.sortedIDs.get(position)), false);

                                    MainActivity.taskPropertiesShowing = false;

                                    //Returns the 'add' button
                                    MainActivity.params.height = MainActivity.addHeight;
                                    MainActivity.iconParams.height = MainActivity.addIconHeight;

                                    taskView.setLayoutParams(MainActivity.params);
                                    taskView.setLayoutParams(MainActivity.iconParams);

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
                                    if (finalDbRepeatInterval.equals("day")) {
                                        interval = 86400;
                                    } else if (finalDbRepeatInterval.equals("week")) {
                                        interval = 604800;
                                    } else if (finalDbRepeatInterval.equals("month")) {
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

                                    MainActivity.db.updateSnooze(MainActivity.sortedIDs
                                            .get(position), true);

//                                    datePicker.setVisibility(View.VISIBLE);

//                                    timePicker.setVisibility(View.GONE);

                                    MainActivity.dateOrTime = false;

                                    MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                                    //Marks properties as not showing
                                    MainActivity.taskPropertiesShowing = false;

                                    //Returns the 'add' button
                                    MainActivity.params.height = MainActivity.addHeight;
                                    MainActivity.iconParams.height = MainActivity.addIconHeight;

                                    taskView.setLayoutParams(MainActivity.params);
                                    taskView.setLayoutParams(MainActivity.iconParams);

                                    MainActivity.dateRowShowing = false;

                                    MainActivity.repeating = false;

                                    MainActivity.timePickerShowing = false;

                                    reorderList();

                                    notifyDataSetChanged();
                                }}
                                };

                                handler.postDelayed(runnable, 600);

                            }
                        });

                        //Actions to occur if user selects '4 hours'
                        fourHourBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {

                                MainActivity.vibrate.vibrate(50);

                                if(!MainActivity.mute){
                                    MainActivity.blip.start();
                                }

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
                                                if ((theMonth == 0) && (theDay == 29)
                                                        && (theYear % 4 != 0)) {
                                                    interval = 2577600;
                                                    //Month January and day is 30
                                                    // non leap year 2491200
                                                } else if ((theMonth == 0) && (theDay == 30)
                                                        && (theYear % 4 != 0)) {
                                                    interval = 2491200;
                                                    //Month January and day is 31
                                                    // non leap year 2404800
                                                } else if ((theMonth == 0) && (theDay == 31)
                                                        && (theYear % 4 != 0)) {
                                                    interval = 2404800;
                                                    //Month January and day is 30 leap year 2577600
                                                } else if ((theMonth == 0) && (theDay == 30)
                                                        && (theYear % 4 == 0)) {
                                                    interval = 2577600;
                                                    //Month January and day is 31 leap year 2491200
                                                } else if ((theMonth == 0) && (theDay == 31)
                                                        && (theYear % 4 == 0)) {
                                                    interval = 2491200;
                                                    //Month March||May||August||October
                                                    // and day is 31 2577600
                                                } else if (((theMonth == 2) || (theMonth == 4)
                                                        || (theMonth == 7) || (theMonth == 9))
                                                        && (theDay == 31)) {
                                                    interval = 2577600;
                                                    //Month January||March||May||July||August
                                                    // ||October||December 2664000
                                                } else if ((theMonth == 0) || (theMonth == 2)
                                                        || (theMonth == 4) || (theMonth == 6)
                                                        || (theMonth == 7) || (theMonth == 9)
                                                        || (theMonth == 11)) {
                                                    interval = 2664000;
                                                    //Month April||June||September||November 2577600
                                                } else if ((theMonth == 3) || (theMonth == 5)
                                                        || (theMonth == 8) || (theMonth == 10)) {
                                                    interval = 2577600;
                                                    //Month February non leap year 2404800
                                                } else if ((theMonth == 1) && (theYear % 4 != 0)) {
                                                    interval = 2404800;
                                                    //Month February leap year 2491200
                                                } else if ((theMonth == 1) && (theYear % 4 == 0)) {
                                                    interval = 2491200;
                                                }
                                                if ((dateNow.getTimeInMillis() / 1000) >= (Integer
                                                        .parseInt(finalDbTimestamp) + interval)) {
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
                                                    MainActivity.sweep.start();
                                                    MainActivity.toast.startAnimation
                                                            (AnimationUtils.loadAnimation
                                                                    (getContext(), R.anim
                                                                            .enter_from_right_fast));
                                                    MainActivity.toast.setVisibility(View.VISIBLE);
                                                    final Handler handler2 = new Handler();
                                                    final Runnable runnable2 = new Runnable() {
                                                        public void run() {
                                                            MainActivity.toast.startAnimation
                                                                    (AnimationUtils.loadAnimation
                                                                            (getContext(),
                                                                                    android.R.anim
                                                                                            .fade_out));
                                                            MainActivity.toast
                                                                    .setVisibility(View.GONE);
                                                        }
                                                    };
                                                    handler2.postDelayed(runnable2, 1500);
                                                }
                                            };

                                            handler.postDelayed(runnable, 500);

                                            int newDay = Integer.parseInt(finalAlarmDay);
                                            int newMonth = Integer.parseInt(finalAlarmMonth);
                                            int newYear = Integer.parseInt(finalAlarmYear);

                                            int adjustedStamp = 0;

                                            if (finalDbRepeatInterval.equals("day")) {

                                                adjustedStamp = Integer.parseInt
                                                        (finalDbTimestamp) + 86400;

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
                                                } else if (newMonth == 1
                                                        && (newDay == 28) && (newYear % 4 != 0)) {
                                                    newDay = 1;
                                                    newMonth++;
                                                } else if (newMonth == 1
                                                        && (newDay == 29) && (newYear % 4 == 0)) {
                                                    newDay = 1;
                                                    newMonth++;
                                                } else {
                                                    newDay++;
                                                }

                                            } else if (finalDbRepeatInterval.equals("week")) {

                                                adjustedStamp = Integer.parseInt
                                                        (finalDbTimestamp) + 604800;
                                                newDay += 7;

                                                //incrementing week
                                                if (((newMonth == 0) || (newMonth == 2)
                                                        || (newMonth == 4) || (newMonth == 6)
                                                        || (newMonth == 7) || (newMonth == 9))
                                                        && (newDay >= 25)) {
                                                    newDay -= 31;
                                                    newMonth++;
                                                } else if (((newMonth == 3) || (newMonth == 5)
                                                        || (newMonth == 8) || (newMonth == 10))
                                                        && (newDay >= 24)) {
                                                    newDay -= 30;
                                                    newMonth++;
                                                } else if ((newMonth == 11) && (newDay >= 25)) {
                                                    newDay -= 31;
                                                    newMonth++;
                                                    newYear++;
                                                } else if ((newMonth == 1) && (newDay >= 22)
                                                        && (newYear % 4 != 0)) {
                                                    newDay -= 28;
                                                    newMonth++;
                                                } else if ((newMonth == 1) && (newDay >= 22)
                                                        && (newYear % 4 == 0)) {
                                                    newDay -= 29;
                                                    newMonth++;
                                                }

                                            } else if (finalDbRepeatInterval.equals("month")) {

                                                //getting interval based on current day and month
                                                int interval = 0;
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

                                                //setting next alarm because monthly
                                                // repeats cannot be done automatically
                                                MainActivity.pendIntent = PendingIntent
                                                        .getBroadcast(getContext(),
                                                                Integer.parseInt(MainActivity
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
                                            MainActivity.db.updateAlarmData(String.valueOf(
                                                    MainActivity.sortedIDs.get(MainActivity
                                                            .activeTask)), finalAlarmHour,
                                                    finalAlarmMinute, finalAlarmAmpm,
                                                    String.valueOf(newDay),
                                                    String.valueOf(newMonth),
                                                    String.valueOf(newYear));

                                            //cancelling any snooze data
                                            MainActivity.db.updateSnoozeData(String.valueOf(
                                                    MainActivity.sortedIDs.get
                                                            (MainActivity.activeTask)),
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    "",
                                                    "");

                                            MainActivity.db.updateTimestamp(String.valueOf
                                                            (MainActivity.sortedIDs.get(position)),
                                                    String.valueOf(adjustedStamp));

                                            MainActivity.db.updateOverdue(String.valueOf(
                                                    MainActivity.sortedIDs.get(position)),
                                                    false);

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

                                            MainActivity.pendIntent = PendingIntent.getBroadcast(
                                                    getContext(), Integer.parseInt(
                                                            MainActivity.sortedIDs
                                                                    .get(position) + 1000),
                                                    MainActivity.alertIntent,
                                                    PendingIntent.FLAG_UPDATE_CURRENT);

                                            MainActivity.alarmManager.cancel
                                                    (MainActivity.pendIntent);

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

                                            int newBroadcast = finalDbBroadcast + 1000;

                                            int interval = 0;
                                            //getting interval to add to timestamp
                                            //TODO is this stuff needed? shouldn't it be incremented by four hours regardless of repeat interval?
                                            if (finalDbRepeatInterval.equals("day")) {
                                                interval = 86400;
                                            } else if (finalDbRepeatInterval.equals("week")) {
                                                interval = 604800;
                                            } else if (finalDbRepeatInterval.equals("month")) {
                                                int theYear = Integer.parseInt(finalAlarmYear);
                                                int theMonth = Integer.parseInt(finalAlarmMonth);
                                                int theDay = Integer.parseInt(finalAlarmDay);
                                                //Month January and day is 29 non leap year 2592000
                                                if ((theMonth == 0) && (theDay == 29)
                                                        && (theYear % 4 != 0)) {
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
                                            }

                                            //setting new alarm
                                            MainActivity.pendIntent = PendingIntent.getBroadcast(
                                                    getContext(), newBroadcast,
                                                    MainActivity.alertIntent,
                                                    PendingIntent.FLAG_UPDATE_CURRENT);

                                            MainActivity.alarmManager.set(AlarmManager.RTC,
                                                    (currentDate.getTimeInMillis() + interval),
                                                    MainActivity.pendIntent);

                                            MainActivity.db.updateSnooze(MainActivity.sortedIDs
                                                    .get(position), true);

//                                            datePicker.setVisibility(View.VISIBLE);

//                                            timePicker.setVisibility(View.GONE);

                                            MainActivity.dateOrTime = false;

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

                                            MainActivity.dateRowShowing = false;

                                            MainActivity.repeating = false;

                                            MainActivity.timePickerShowing = false;

                                            reorderList();

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

                                if(!MainActivity.mute){
                                    MainActivity.blip.start();
                                }

                                snoozeRow.startAnimation(AnimationUtils.loadAnimation
                                        (getContext(), R.anim.exit_out_right));

                                final Handler handler = new Handler();

                                final Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {


                                        MainActivity.db.updateInterval(String.valueOf(
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

                                    MainActivity.toast.setText(R.string.taskNotSnoozedBecause);
                                    final Handler handler = new Handler();

                                    final Runnable runnable = new Runnable() {
                                        public void run() {
                                            MainActivity.sweep.start();
                                            MainActivity.toast.startAnimation
                                                    (AnimationUtils.loadAnimation(getContext(),
                                                            R.anim.enter_from_right_fast));
                                            MainActivity.toast.setVisibility(View.VISIBLE);
                                            final Handler handler2 = new Handler();
                                            final Runnable runnable2 = new Runnable(){
                                                public void run(){
                                                    MainActivity.toast.startAnimation
                                                            (AnimationUtils.loadAnimation
                                                                    (getContext(), android.R.anim
                                                                            .fade_out));
                                                    MainActivity.toast.setVisibility(View.GONE);
                                                }
                                            };
                                            handler2.postDelayed(runnable2, 1500);
                                        }
                                    };

                                    handler.postDelayed(runnable, 500);

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
                                    MainActivity.db.updateAlarmData(String.valueOf(
                                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                            finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                                            String.valueOf(newDay),
                                            String.valueOf(newMonth),
                                            String.valueOf(newYear));

                                    //cancelling any snooze data
                                    MainActivity.db.updateSnoozeData(String.valueOf(
                                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "");

                                    MainActivity.db.updateTimestamp(String.valueOf(MainActivity
                                                    .sortedIDs.get(position)),
                                            String.valueOf(adjustedStamp));

                                    MainActivity.db.updateOverdue(String.valueOf(
                                            MainActivity.sortedIDs.get(position)), false);

                                    MainActivity.taskPropertiesShowing = false;

                                    //Returns the 'add' button
                                    MainActivity.params.height = MainActivity.addHeight;
                                    MainActivity.iconParams.height = MainActivity.addIconHeight;

                                    taskView.setLayoutParams(MainActivity.params);
                                    taskView.setLayoutParams(MainActivity.iconParams);

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
                                    MainActivity.db.updateSnoozeData(String.valueOf(
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

                                    MainActivity.db.updateSnooze(MainActivity
                                            .sortedIDs.get(position), true);

//                                    datePicker.setVisibility(View.VISIBLE);

//                                    timePicker.setVisibility(View.GONE);

                                    MainActivity.dateOrTime = false;

                                    MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                                    //Marks properties as not showing
                                    MainActivity.taskPropertiesShowing = false;

                                    //Returns the 'add' button
                                    MainActivity.params.height = MainActivity.addHeight;
                                    MainActivity.iconParams.height = MainActivity.addIconHeight;

                                    taskView.setLayoutParams(MainActivity.params);
                                    taskView.setLayoutParams(MainActivity.iconParams);

                                    MainActivity.dateRowShowing = false;

                                    MainActivity.repeating = false;

                                    MainActivity.timePickerShowing = false;

                                    reorderList();

                                    notifyDataSetChanged();

                                }}};
                                handler.postDelayed(runnable, 600);

                            }
                        });

                    }
                });

                //Actions to occur if user selects 'Done'
                taskDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        taskOverdueRow.startAnimation(AnimationUtils.loadAnimation(getContext(),
                                R.anim.exit_out_right));

//                        final Handler handler = new Handler();

//                        final Runnable runnable = new Runnable() {
//                            @Override
//                            public void run() {

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
                                            MainActivity.sortedIDs.get(
                                                    MainActivity.activeTask)), true);

                                    MainActivity.toast.setText(R.string.youKilledThisTask);
                                    final Handler handler = new Handler();

                                    final Runnable runnable = new Runnable() {
                                        public void run() {
                                            MainActivity.sweep.start();
                                            MainActivity.toast.startAnimation
                                                    (AnimationUtils.loadAnimation(getContext(),
                                                            R.anim.enter_from_right_fast));
                                            MainActivity.toast.setVisibility(View.VISIBLE);
                                            final Handler handler2 = new Handler();
                                            final Runnable runnable2 = new Runnable(){
                                                public void run(){
                                                    MainActivity.toast.startAnimation
                                                            (AnimationUtils.loadAnimation
                                                                    (getContext(), android.R.anim
                                                                            .fade_out));
                                                    MainActivity.toast.setVisibility(View.GONE);
                                                }
                                            };
                                            handler2.postDelayed(runnable2, 1500);
                                        }
                                    };

                                    handler.postDelayed(runnable, 500);

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

                                    //cancelling any snooze data
                                    MainActivity.db.updateSnoozeData(String.valueOf(
                                            MainActivity.sortedIDs.get(MainActivity.activeTask)),
                                            "", "", "", "",
                                            "", "");

                                    Calendar currentDate = new GregorianCalendar();

                                    if (finalDbRepeatInterval.equals("day")) {

                                        //adding one day to timestamp
                                        int adjustedStamp = Integer.parseInt
                                                (finalDbTimestamp) + 86400;
                                        MainActivity.db.updateTimestamp(String.valueOf(MainActivity
                                                .sortedIDs.get(position)), String.valueOf
                                                (adjustedStamp));

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
                                        } else if ((Integer.parseInt(finalAlarmMonth) == 1)
                                                && (newDay == 28) && (newYear % 4 != 0)) {
                                            newDay = 1;
                                            newMonth++;
                                        } else if ((Integer.parseInt(finalAlarmMonth) == 1)
                                                && (newDay == 29) && (newYear % 4 == 0)) {
                                            newDay = 1;
                                            newMonth++;
                                        } else {
                                            newDay++;
                                        }

                                        MainActivity.db.updateAlarmData(String.valueOf(
                                                MainActivity.sortedIDs.get
                                                        (MainActivity.activeTask)),
                                                finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                                                String.valueOf(newDay), String.valueOf(newMonth),
                                                String.valueOf(newYear));

                                    } else if (finalDbRepeatInterval.equals("week")) {

                                        //adding one week to timestamp
                                        int adjustedStamp = Integer.parseInt
                                                (finalDbTimestamp) + 604800;
                                        MainActivity.db.updateTimestamp(String.valueOf(MainActivity
                                                .sortedIDs.get(position)), String.valueOf
                                                (adjustedStamp));

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
                                        } else if (currentDate.get(Calendar.MONTH) == 1
                                                && (newDay == 28) && (newYear % 4 != 0)) {
                                            newDay = 1;
                                            newMonth++;
                                        } else if (currentDate.get(Calendar.MONTH) == 1
                                                && (newDay == 29) && (newYear % 4 == 0)) {
                                            newDay = 1;
                                            newMonth++;
                                        }

                                        MainActivity.db.updateAlarmData(String.valueOf(
                                                MainActivity.sortedIDs.get(MainActivity
                                                        .activeTask)), finalAlarmHour,
                                                finalAlarmMinute, finalAlarmAmpm,
                                                String.valueOf(newDay), String.valueOf(newMonth),
                                                String.valueOf(newYear));

                                    } else if (finalDbRepeatInterval.equals("month")) {

                                        //getting interval based on current day and month
                                        int interval = 0;
                                        int theYear = Integer.parseInt(finalAlarmYear);
                                        int theMonth = Integer.parseInt(finalAlarmMonth);
                                        int theDay = Integer.parseInt(finalAlarmDay);
                                        //Month January and day is 29 non leap year 2592000
                                        if ((theMonth == 0) && (theDay == 29) &&
                                                (theYear % 4 != 0)) {
                                            interval = 2592000;
                                            //Month January and day is 30 non leap year 2505600
                                        } else if ((theMonth == 0) && (theDay == 30) &&
                                                (theYear % 4 != 0)) {
                                            interval = 2505600;
                                            //Month January and day is 31 non leap year 2419200
                                        } else if ((theMonth == 0) && (theDay == 31) &&
                                                (theYear % 4 != 0)) {
                                            interval = 2419200;
                                            //Month January and day is 30 leap year 2592000
                                        } else if ((theMonth == 0) && (theDay == 30) &&
                                                (theYear % 4 == 0)) {
                                            interval = 2592000;
                                            //Month January and day is 31 leap year 2505600
                                        } else if ((theMonth == 0) && (theDay == 31) &&
                                                (theYear % 4 == 0)) {
                                            interval = 2505600;
                                            //Month March||May||August||October and day is 31 2592000
                                        } else if (((theMonth == 2) || (theMonth == 4) ||
                                                (theMonth == 7) || (theMonth == 9)) &&
                                                (theDay == 31)) {
                                            interval = 2592000;
                                            //Month January||March||May||July||August
                                            // ||October||December 2678400
                                        } else if ((theMonth == 0) || (theMonth == 2) ||
                                                (theMonth == 4) || (theMonth == 6) ||
                                                (theMonth == 7) || (theMonth == 9)
                                                || (theMonth == 11)) {
                                            interval = 2678400;
                                            //Month April||June||September||November 2592000
                                        } else if ((theMonth == 3) || (theMonth == 5) ||
                                                (theMonth == 8) || (theMonth == 10)) {
                                            interval = 2592000;
                                            //Month February non leap year 2419200
                                        } else if ((theMonth == 1) && (theYear % 4 != 0)) {
                                            interval = 2419200;
                                            //Month February leap year 2505600
                                        } else if ((theMonth == 1) && (theYear % 4 == 0)) {
                                            interval = 2505600;
                                        }

                                        //adding one month to timestamp
                                        int adjustedStamp = Integer.parseInt
                                                (finalDbTimestamp) + interval;
                                        MainActivity.db.updateTimestamp(String.valueOf(MainActivity
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
                                        } else if ((newMonth == 1) && (newDay > 28) &&
                                                (newYear % 4 != 0)) {
                                            newDay = 28;
                                            newMonth++;
                                        } else if (newMonth == 1
                                                && (newDay > 29) && (newYear % 4 == 0)) {
                                            newDay = 28;
                                            newMonth++;
                                        } else {
                                            newMonth++;
                                        }

                                        //setting new due time in database
                                        MainActivity.db.updateAlarmData(String.valueOf(
                                                MainActivity.sortedIDs.get
                                                        (MainActivity.activeTask)),
                                                finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                                                String.valueOf(newDay),
                                                String.valueOf(newMonth),
                                                String.valueOf(newYear));

                                    }

                                    taskOverdueRow.setVisibility(View.GONE);

                                    MainActivity.taskPropertiesShowing = false;

                                    MainActivity.db.updateOverdue(
                                            MainActivity.sortedIDs.get(position), false);

                                    MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                                }
//                            }};
//                        handler.postDelayed(runnable, 600);
                    }
                });

                //Actions to occur if user selects 'ignore'
                taskIgnore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MainActivity.vibrate.vibrate(50);

                        if(!MainActivity.mute){
                            MainActivity.blip.start();
                        }

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

                if(MainActivity.exitAlarmOptions && (position == MainActivity.activeTask)){

                    final Handler handler = new Handler();

                    final Runnable runnable = new Runnable() {
                        public void run() {
                            propertyRow.startAnimation(AnimationUtils.loadAnimation(getContext(),
                                    R.anim.enter_from_right));
                            propertyRow.setVisibility(View.VISIBLE);
                        }
                    };

                    handler.postDelayed(runnable, 400);

                    MainActivity.exitAlarmOptions = false;
                }else if (MainActivity.exitDatePicker){

                    final Handler handler = new Handler();

                    final Runnable runnable = new Runnable() {
                        public void run() {
                            propertyRow.startAnimation(AnimationUtils.loadAnimation(getContext(),
                                    R.anim.enter_from_right));
                            propertyRow.setVisibility(View.VISIBLE);
                        }
                    };

                    handler.postDelayed(runnable, 400);

                    MainActivity.exitDatePicker = false;
                }else{
                    propertyRow.startAnimation(AnimationUtils.loadAnimation(getContext(),
                            R.anim.enter_from_right));
                    propertyRow.setVisibility(View.VISIBLE);
                }

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
                    (MainActivity.listViewHeight / 2));

            //put data in text view
            theTextView.setText(task);

            if(dbDue){

                alarmBtnText.setText(R.string.alarmOptions);

            }

            //Actions to occur if user selects 'complete'
            complete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //task is killed if not repeating
                    if(!finalDbRepeat) {

                        notifyDataSetChanged();

                        MainActivity.taskPropertiesShowing = false;

                        MainActivity.db.updateKilled(String.valueOf(
                                MainActivity.sortedIDs.get(MainActivity.activeTask)), true);

                        MainActivity.db.updateIgnored(MainActivity.sortedIDs
                                .get(position), false);

                        MainActivity.toast.setText(R.string.youKilledThisTask);
                        final Handler handler = new Handler();

                        final Runnable runnable = new Runnable() {
                            public void run() {
                                MainActivity.sweep.start();
                                MainActivity.toast.startAnimation(AnimationUtils.loadAnimation
                                        (getContext(), R.anim.enter_from_right_fast));
                                MainActivity.toast.setVisibility(View.VISIBLE);
                                final Handler handler2 = new Handler();
                                final Runnable runnable2 = new Runnable(){
                                    public void run(){
                                        MainActivity.toast.startAnimation(AnimationUtils
                                                .loadAnimation(getContext(),
                                                        android.R.anim.fade_out));
                                        MainActivity.toast.setVisibility(View.GONE);
                                    }
                                };
                                handler2.postDelayed(runnable2, 1500);
                            }
                        };

                        handler.postDelayed(runnable, 500);

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
                        MainActivity.db.updateTimestamp(String.valueOf(MainActivity
                                .sortedIDs.get(position)), String.valueOf(adjustedStamp));

                        //updating due time in database
                        MainActivity.db.updateAlarmData(String.valueOf(
                                MainActivity.sortedIDs.get(position)),
                                finalAlarmHour, finalAlarmMinute, finalAlarmAmpm,
                                String.valueOf(newDay), String.valueOf(newMonth),
                                String.valueOf(newYear));

                        MainActivity.db.updateShowOnce(
                                MainActivity.sortedIDs.get(MainActivity.activeTask), true);

                        //TODO Show this only when necessary
                        MainActivity.toast.setText(R.string.youCanCancelRepeat);
                        final Handler handler = new Handler();

                        final Runnable runnable = new Runnable() {
                            public void run() {
                                MainActivity.sweep.start();
                                MainActivity.toast.startAnimation(AnimationUtils
                                        .loadAnimation(getContext(), R.anim.enter_from_right_fast));
                                MainActivity.toast.setVisibility(View.VISIBLE);
                                final Handler handler2 = new Handler();
                                final Runnable runnable2 = new Runnable(){
                                    public void run(){
                                        MainActivity.toast.startAnimation(AnimationUtils
                                                .loadAnimation(getContext(),
                                                        android.R.anim.fade_out));
                                        MainActivity.toast.setVisibility(View.GONE);
                                    }
                                };
                                handler2.postDelayed(runnable2, 1500);
                            }
                        };

                        handler.postDelayed(runnable, 500);

                        propertyRow.setVisibility(View.GONE);

                        MainActivity.taskPropertiesShowing = false;

                        MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                    }

                }

            });

            //Actions to occur if user selects 'set due date'
            alarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.vibrate.vibrate(50);

                    //clear out all data related to alarm
//                    if(finalDbIgnored){
//
//                        MainActivity.db.updateDue(String.valueOf(MainActivity
//                                .sortedIDs.get(MainActivity.activeTask)), false);
//
//                        MainActivity.db.removeTimestamp(String.valueOf(MainActivity
//                                .sortedIDs.get(MainActivity.activeTask)));
//
//                        MainActivity.db.updateRepeat(MainActivity.sortedIDs
//                                .get(position), false);
//
//                        MainActivity.db.updateIgnored(MainActivity.sortedIDs
//                                .get(position), false);
//
//                        MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
//                                Integer.parseInt(MainActivity.sortedIDs.get(position)),
//                                MainActivity.alertIntent,
//                                PendingIntent.FLAG_UPDATE_CURRENT);
//
//                        MainActivity.alarmManager.cancel(MainActivity.pendIntent);
//
//                        MainActivity.db.updateAlarmData
//                                (String.valueOf(MainActivity.sortedIDs.get(position)),
//                                        "", "", "",
//                                        "", "", "");
//
//                        MainActivity.alarmOptionsShowing = false;
//
//                        reorderList();
//
//                        MainActivity.taskPropertiesShowing = false;
//
////                        MainActivity.activityRootView
////                                .setBackgroundColor(Color.parseColor("#FFFFFF"));
//
//                        MainActivity.add.setVisibility(View.VISIBLE);
//                        MainActivity.addIcon.setVisibility(View.VISIBLE);
//
////                        MainActivity.vibrate.vibrate(50);
//
//                        alarmBtnText.setText(R.string.setDueDate);
//
//                        MainActivity.params.height = MainActivity.addHeight;
//                        MainActivity.iconParams.height = MainActivity.addIconHeight;
//
//                        v.setLayoutParams(MainActivity.params);
//                        v.setLayoutParams(MainActivity.iconParams);
//
//                        notifyDataSetChanged();
//
//                    }else {
                        //TODO reword this
//                    Toast.makeText(v.getContext(), "Upgrade to the Pro version to" +
//                                    " get this feature", Toast.LENGTH_SHORT).show();
//                        MainActivity.toast.setText("Upgrade to pro to get this feature");
//                        final Handler handler = new Handler();
//
//                        final Runnable runnable = new Runnable() {
//                            public void run() {
//                                MainActivity.toast.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.enter_from_right));
//                                MainActivity.toast.setVisibility(View.VISIBLE);
//                                final Handler handler2 = new Handler();
//                                final Runnable runnable2 = new Runnable(){
//                                    public void run(){
//                                        MainActivity.toast.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
//                                        MainActivity.toast.setVisibility(View.GONE);
//                                    }
//                                };
//                                handler2.postDelayed(runnable2, 1500);
//                            }
//                        };
//
//                        handler.postDelayed(runnable, 500);

                    if(!MainActivity.mute){
                        MainActivity.blip.start();
                    }

                        //actions to occur if alarm not already set
                        if (!finalDbDue) {

                            MainActivity.db.updateActiveTaskTemp(String.valueOf(finalDbID));

                            getContext().startActivity(dueIntent);

//                            MainActivity.dateRowShowing = true;

//                            MainActivity.datePickerShowing = true;

//                            notifyDataSetChanged();

                        //actions to occur when cancelling snooze
//                        } else if (finalDbSnooze) {
//
//                            //remove any associated snooze
//                            MainActivity.db.updateSnooze(String.valueOf(MainActivity.sortedIDs
//                                    .get(position)), false);
//
//                            //marks showonce as false
//                            MainActivity.db.updateShowOnce(String.valueOf(MainActivity
//                                    .sortedIDs.get(position)), false);
//
//                            //remove snooze time data
//                            MainActivity.db.updateSnoozeData
//                                    (String.valueOf(MainActivity.sortedIDs.get(position)),
//                                            "", "", "",
//                                            "", "", "");
//
//                            MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
//                                    Integer.parseInt(
//                                            MainActivity.sortedIDs.get(position) + 1000),
//                                    MainActivity.alertIntent,
//                                    PendingIntent.FLAG_UPDATE_CURRENT);
//
//                            MainActivity.alarmManager.cancel(MainActivity.pendIntent);
//
//                            alarmBtnText.setText(R.string.alarmOptions);
//                            MainActivity.taskPropertiesShowing = false;
////                            MainActivity.activityRootView
////                                    .setBackgroundColor(Color.parseColor("#FFFFFF"));
//
//                            MainActivity.params.height = MainActivity.addHeight;
//                            MainActivity.iconParams.height = MainActivity.addIconHeight;
//
//                            v.setLayoutParams(MainActivity.params);
//                            v.setLayoutParams(MainActivity.iconParams);
//
//                            MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

                        //actions to occur when viewing alarm properties
                        } else {

                            MainActivity.db.updateActiveTaskTemp(String.valueOf(finalDbID));

                            getContext().startActivity(dueIntent);
//
//                            propertyRow.startAnimation(AnimationUtils.loadAnimation(getContext(),
//                                    R.anim.exit_out_right));
//
//                            ViewGroup.LayoutParams params = killAlarmBtn.getLayoutParams();
//                            params.width = MainActivity.deviceWidthPortrait / 3;
//                            killAlarmBtn.setLayoutParams(params);
//                            resetAlarmBtn.setLayoutParams(params);
//                            repeatAlarmBtn.setLayoutParams(params);
//
//                            final Handler handler = new Handler();
//
//                            final Runnable runnable = new Runnable() {
//                                public void run() {
//                                    propertyRow.setVisibility(View.GONE);
//                                    alarmOptionsRow.startAnimation(AnimationUtils.loadAnimation
//                                            (getContext(), R.anim.enter_from_right));
//                                    alarmOptionsRow.setVisibility(View.VISIBLE);
//                                }
//                            };
//
//                            handler.postDelayed(runnable, 600);
//
//                            MainActivity.alarmOptionsShowing = true;
//
//                            if (finalDbRepeat) {
//
//                                repeatAlarmBtnText.setText(R.string.cancelRepeat);
//
//                            }
//
//                            //Actions to occur if user selects 'remove alarm'
//                            killAlarmBtn.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(final View v) {
//
//                                    MainActivity.vibrate.vibrate(50);
//
//                                    if(!MainActivity.mute){
//                                        MainActivity.blip.start();
//                                    }
//
//                                    alarmOptionsRow.startAnimation(AnimationUtils.loadAnimation
//                                            (getContext(), R.anim.exit_out_right));
//
//                                    final Handler handler = new Handler();
//
//                                    final Runnable runnable = new Runnable() {
//                                        @Override
//                                        public void run() {
////                                    MainActivity.noteDb.updateDue(String.valueOf(MainActivity
////                                            .sortedIDs.get(MainActivity.activeTask)), false);
////                                    MainActivity.noteDb.removeTimestamp(String.valueOf(MainActivity
////                                            .sortedIDs.get(MainActivity.activeTask)));
//                                            MainActivity.db.updateDue(String.valueOf(MainActivity
//                                                    .sortedIDs.get(position)), false);
//                                            MainActivity.db.removeTimestamp(String.valueOf
//                                                    (MainActivity.sortedIDs.get(position)));
//
//                                            MainActivity.db.updateRepeat(MainActivity.sortedIDs
//                                                    .get(position), false);
//
//                                            MainActivity.pendIntent = PendingIntent.getBroadcast
//                                                    (getContext(), Integer.parseInt(MainActivity
//                                                                    .sortedIDs.get(position)),
//                                                            MainActivity.alertIntent,
//                                                            PendingIntent.FLAG_UPDATE_CURRENT);
//
//                                            MainActivity.alarmManager.cancel
//                                                    (MainActivity.pendIntent);
//
//                                            MainActivity.db.updateAlarmData
//                                                    (String.valueOf(MainActivity.sortedIDs
//                                                                    .get(position)),
//                                                            "", "", "",
//                                                            "", "", "");
//
//                                            MainActivity.alarmOptionsShowing = false;
//
//                                            reorderList();
//
//                                            MainActivity.taskPropertiesShowing = false;
//
//                                            MainActivity.add.setVisibility(View.VISIBLE);
//                                            MainActivity.addIcon.setVisibility(View.VISIBLE);
//
//                                            MainActivity.params.height = MainActivity.addHeight;
//                                            MainActivity.iconParams.height =
//                                                    MainActivity.addIconHeight;
//
//                                            v.setLayoutParams(MainActivity.params);
//                                            v.setLayoutParams(MainActivity.iconParams);
//
//                                            notifyDataSetChanged();
//
//                                        }};
//                                    handler.postDelayed(runnable, 600);
//
//                                }
//                            });
//
//                            //Actions to occur if user selects 'change due date'
//                            resetAlarmBtn.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                    MainActivity.vibrate.vibrate(50);
//
//                                    if(!MainActivity.mute){
//                                        MainActivity.blip.start();
//                                    }
//
//                                    getContext().startActivity(dueIntent);
//
////                                    MainActivity.datePickerShowing = true;
////
////                                    MainActivity.dateRowShowing = true;
////
////                                    notifyDataSetChanged();
//
//                                }
//                            });
//
//                            //Actions to occur if user selects 'repeat alarm'
//                            repeatAlarmBtn.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                    MainActivity.vibrate.vibrate(50);
//
//                                    if(!MainActivity.mute){
//                                        MainActivity.blip.start();
//                                    }
//
//                                    if (finalDbRepeat) {
//
//                                        alarmOptionsRow.startAnimation(
//                                                AnimationUtils.loadAnimation(getContext(),
//                                                        R.anim.exit_out_right));
//
//                                        final Handler handler = new Handler();
//
//                                        final Runnable runnable = new Runnable() {
//                                            @Override
//                                            public void run() {
//
//                                        MainActivity.db.updateRepeat(MainActivity.sortedIDs
//                                                .get(MainActivity.activeTask), false);
//
//                                        MainActivity.pendIntent = PendingIntent.getBroadcast(
//                                                getContext(), Integer.parseInt(MainActivity
//                                                        .sortedIDs.get(position) + 1000),
//                                                MainActivity.alertIntent, PendingIntent
//                                                        .FLAG_UPDATE_CURRENT);
//
//                                        MainActivity.alarmManager.cancel(MainActivity.pendIntent);
//
//                                        Calendar prevCalendar = new GregorianCalendar();
//                                        String newHour = "";
//                                        if (finalAlarmAmpm.equals("1")) {
//                                            int tempHour = Integer.parseInt(finalAlarmHour) + 12;
//                                            newHour = String.valueOf(tempHour);
//                                        }
//                                        if (!finalAlarmHour.equals("")) {
//                                            prevCalendar.set(Integer.parseInt(finalAlarmYear),
//                                                    Integer.parseInt(finalAlarmMonth),
//                                                    Integer.parseInt(finalAlarmDay),
//                                                    Integer.parseInt(newHour),
//                                                    Integer.parseInt(finalAlarmMinute));
//                                        }
//
//                                        MainActivity.alarmManager.set(AlarmManager.RTC,
//                                                prevCalendar.getTimeInMillis(),
//                                                MainActivity.pendIntent);
//
//                                        alarmOptionsRow.setVisibility(View.GONE);
//
//                                        MainActivity.repeatShowing = false;
//                                        MainActivity.repeating = false;
//                                        MainActivity.alarmOptionsShowing = false;
//                                        MainActivity.taskPropertiesShowing = false;
//
//                                        MainActivity.theListView.setAdapter
//                                                (MainActivity.theAdapter[0]);
//
//                                        //Returns the 'add' button
//                                        MainActivity.params.height = MainActivity.addHeight;
//                                        MainActivity.iconParams.height = MainActivity.addIconHeight;
//
//                                        MainActivity.add.setLayoutParams(MainActivity.params);
//                                        MainActivity.addIcon.setLayoutParams
//                                                (MainActivity.iconParams);
//
//                                            }};
//
//                                        handler.postDelayed(runnable, 600);
//
//                                    //show repeat row
//                                    } else {
//
//                                        alarmOptionsRow.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.exit_out_right));
//
//                                        ViewGroup.LayoutParams params = daily.getLayoutParams();
//                                        params.width = MainActivity.deviceWidthPortrait / 3;
//                                        daily.setLayoutParams(params);
//                                        weekly.setLayoutParams(params);
//                                        monthly.setLayoutParams(params);
//
//                                        final Handler handler = new Handler();
//
//                                        final Runnable runnable = new Runnable() {
//                                            public void run() {
//                                                alarmOptionsRow.setVisibility(View.GONE);
//                                                repeatRow.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.enter_from_right));
//                                                repeatRow.setVisibility(View.VISIBLE);
//                                            }
//                                        };
//
//                                        handler.postDelayed(runnable, 600);
//
//                                        MainActivity.repeatShowing = true;
//
//                                    }
//
//                                }
//                            });
//
//                        }
                    }

                }
            });

            //Actions to occur if user selects 'Sub-Tasks'
            subTasks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.vibrate.vibrate(50);

                    if(!MainActivity.mute){
                        MainActivity.blip.start();
                    }

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

                    if(!MainActivity.mute){
                        MainActivity.blip.start();
                    }

                    MainActivity.vibrate.vibrate(50);

                    MainActivity.db.updateActiveTaskTemp(String.valueOf(finalDbID));

                    getContext().startActivity(noteIntent);

                }
            });

            //Actions to occur if user selects 'Set Time'
//            dateButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    MainActivity.vibrate.vibrate(50);
//
//                    if(!MainActivity.mute){
//                        MainActivity.blip.start();
//                    }
//
//                    dateButton.setText(R.string.setTime);
//
//                    setAlarm(dateRow, datePicker, timePicker, position);
//
//                }
//            });

            //Actions to occur if user selects to repeat daily
            daily.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.vibrate.vibrate(50);

                    if(!MainActivity.mute){
                        MainActivity.blip.start();
                    }

                    repeatRow.startAnimation(AnimationUtils.loadAnimation(getContext(),
                            R.anim.exit_out_right));

                    final Handler handler = new Handler();

                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {

                        MainActivity.dateRowShowing =true;

                        MainActivity.repeatInterval =AlarmManager.INTERVAL_DAY;

                    MainActivity.db.updateRepeatInterval(String.valueOf(
                            MainActivity.sortedIDs.get(position)),"day");

                        MainActivity.repeating =true;

                        MainActivity.taskPropertiesShowing =false;

                        setAlarm(/*dateRow, datePicker, timePicker, */position, finalUniYear,
                                finalUniMonth, finalUniDay, finalUniHour, finalUniMinute);

                        //Returns the 'add' button
                        MainActivity.params.height =MainActivity.addHeight;
                        MainActivity.iconParams.height =MainActivity.addIconHeight;

                        MainActivity.add.setLayoutParams(MainActivity.params);
                        MainActivity.addIcon.setLayoutParams(MainActivity.iconParams);

                        notifyDataSetChanged();

                    }};

                    handler.postDelayed(runnable, 600);

                }
            });

            //Actions to occur if user selects to repeat weekly
            weekly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.vibrate.vibrate(50);

                    if(!MainActivity.mute){
                        MainActivity.blip.start();
                    }

                    repeatRow.startAnimation(AnimationUtils.loadAnimation(getContext(),
                            R.anim.exit_out_right));

                    final Handler handler = new Handler();

                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {

                    MainActivity.dateRowShowing = true;

                    MainActivity.repeatInterval = (AlarmManager.INTERVAL_DAY * 7);

                    MainActivity.db.updateRepeatInterval(String.valueOf(
                            MainActivity.sortedIDs.get(position)), "week");

                    MainActivity.repeating = true;

                    MainActivity.taskPropertiesShowing = false;

                    setAlarm(/*dateRow, datePicker, timePicker, */position, finalUniYear,
                            finalUniMonth, finalUniDay, finalUniHour, finalUniMinute);

                    //Returns the 'add' button
                    MainActivity.params.height = MainActivity.addHeight;
                    MainActivity.iconParams.height = MainActivity.addIconHeight;

                    MainActivity.add.setLayoutParams(MainActivity.params);
                    MainActivity.addIcon.setLayoutParams(MainActivity.iconParams);

                    notifyDataSetChanged();

                        }};

                    handler.postDelayed(runnable, 600);

                }
            });

            //Actions to occur if user selects to repeat monthly
            monthly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MainActivity.vibrate.vibrate(50);

                    if(!MainActivity.mute){
                        MainActivity.blip.start();
                    }

                    repeatRow.startAnimation(AnimationUtils.loadAnimation(getContext(),
                            R.anim.exit_out_right));

                    final Handler handler = new Handler();

                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {

                    MainActivity.dateRowShowing = true;

                    MainActivity.db.updateRepeatInterval(String.valueOf(
                            MainActivity.sortedIDs.get(position)), "month");

                    MainActivity.taskPropertiesShowing = false;

                    setAlarm(/*dateRow, datePicker, timePicker, */position, finalUniYear,
                            finalUniMonth, finalUniDay, finalUniHour, finalUniMinute);

                    //Returns the 'add' button
                    MainActivity.params.height = MainActivity.addHeight;
                    MainActivity.iconParams.height = MainActivity.addIconHeight;

                    MainActivity.add.setLayoutParams(MainActivity.params);
                    MainActivity.addIcon.setLayoutParams(MainActivity.iconParams);

                    notifyDataSetChanged();

                        }};

                    handler.postDelayed(runnable, 600);

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

            repeatClear.setVisibility(View.GONE);
            repeatLayout.setVisibility(View.GONE);
            if(dbRepeatInterval.equals("day")){
                repeatDayClear.setVisibility(View.VISIBLE);
                repeatDayLayout.setVisibility(View.VISIBLE);
                repeatDayClear.setBackgroundColor(Color.parseColor(MainActivity.highlight));
                repeatDayClearWhite.setBackgroundColor(Color.parseColor(MainActivity.highlight));
            }else if(dbRepeatInterval.equals("week")){
                repeatWeekClear.setVisibility(View.VISIBLE);
                repeatWeekLayout.setVisibility(View.VISIBLE);
                repeatWeekClear.setBackgroundColor(Color.parseColor(MainActivity.highlight));
                repeatWeekClearWhite.setBackgroundColor(Color.parseColor(MainActivity.highlight));
            }else if(dbRepeatInterval.equals("month")){
                repeatMonthClear.setVisibility(View.VISIBLE);
                repeatMonthLayout.setVisibility(View.VISIBLE);
                repeatMonthClear.setBackgroundColor(Color.parseColor(MainActivity.highlight));
                repeatMonthClearWhite.setBackgroundColor(Color.parseColor(MainActivity.highlight));
            }

        }else if(MainActivity.lightDark){
            repeatClearWhite.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }

        //Show checklist/note icon if required
        if(dbChecklistSize != 0){
            checklistClear.setBackgroundColor(Color.parseColor(MainActivity.highlight));
            checklistClearWhite.setBackgroundColor(Color.parseColor(MainActivity.highlight));
        }else if(MainActivity.lightDark){
            checklistClearWhite.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }
        if(!dbNote.equals("")){
            noteClear.setBackgroundColor(Color.parseColor(MainActivity.highlight));
            noteClearWhite.setBackgroundColor(Color.parseColor(MainActivity.highlight));
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
    private void setAlarm(/*TableRow dateRow, final DatePicker datePicker,
                          final TimePicker timePicker, */final int position, int year, int month,
                                                         int day, int hour, int minute){

//        //getting task data
//        String dbTask = "";
//        Integer dbBroadcast = 0;
//        Boolean dbSnooze = false;
//        String dbRepeatInterval = "";
//        Cursor dbResult = MainActivity.db.getData(Integer.parseInt(
//                MainActivity.sortedIDs.get(position)));
//        while (dbResult.moveToNext()) {
//            dbTask = dbResult.getString(4);
//            dbBroadcast = dbResult.getInt(7);
//            dbSnooze = dbResult.getInt(10) > 0;
//            dbRepeatInterval = dbResult.getString(13);
//        }
//        dbResult.close();
//
//        //getting alarm data
//        Cursor alarmResult = MainActivity.db.getAlarmData(
//                Integer.parseInt(MainActivity.sortedIDs.get(position)));
//        String alarmHour = "";
//        String alarmMinute = "";
//        String alarmAmpm = "";
//        String alarmDay = "";
//        String alarmMonth = "";
//        String alarmYear = "";
//        while(alarmResult.moveToNext()){
//            alarmHour = alarmResult.getString(1);
//            alarmMinute = alarmResult.getString(2);
//            alarmAmpm = alarmResult.getString(3);
//            alarmDay = alarmResult.getString(4);
//            alarmMonth = alarmResult.getString(5);
//            alarmYear = alarmResult.getString(6);
//        }
//        alarmResult.close();
//
//        //Show time picker
//        if(MainActivity.dateOrTime) {
//
//            datePicker.startAnimation(AnimationUtils.loadAnimation(getContext(),
//                    R.anim.exit_out_right));
//
//            final Handler handler = new Handler();
//
//            final Runnable runnable = new Runnable() {
//                public void run() {
//                    datePicker.setVisibility(View.GONE);
//                    timePicker.startAnimation(AnimationUtils.loadAnimation(getContext(),
//                            R.anim.enter_from_right));
//                    timePicker.setVisibility(View.VISIBLE);
//                }
//            };
//
//            handler.postDelayed(runnable, 600);
//
//            dateRow.setVisibility(View.VISIBLE);
//            MainActivity.dateOrTime = false;
//            MainActivity.datePickerShowing = false;
//            MainActivity.timePickerShowing = true;
//
//        //actions to occur when setting a repeating task
//        }else if(MainActivity.repeating){
//
//            Calendar prevCalendar = new GregorianCalendar();
//            if(alarmAmpm.equals("1")){
//                int tempHour = Integer.parseInt(alarmHour) + 12;
//                alarmHour = String.valueOf(tempHour);
//            }
//            if(!alarmHour.equals("")) {
//                prevCalendar.set(Integer.parseInt(alarmYear), Integer.parseInt(alarmMonth),
//                        Integer.parseInt(alarmDay), Integer.parseInt(alarmHour),
//                        Integer.parseInt(alarmMinute));
//            }
//
//            MainActivity.alarmManager.setInexactRepeating(AlarmManager.RTC,
//                    prevCalendar.getTimeInMillis(),
//                    MainActivity.repeatInterval, MainActivity.pendIntent);
//
//            MainActivity.db.updateRepeat(MainActivity.sortedIDs
//                    .get(position), true);
//
//            MainActivity.repeatShowing = false;
//            MainActivity.repeating = false;
//
//        //actions to occur when setting a normal alarm
//        }else{
//
//            if (!dbSnooze) {
//                MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
//                        Integer.parseInt(MainActivity.sortedIDs.get(position)),
//                        MainActivity.alertIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT);
//            } else {
//                MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
//                        Integer.parseInt(
//                                MainActivity.sortedIDs.get(position) + 1000),
//                        MainActivity.alertIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT);
//            }
//
//            //actions specific to monthly repeating task
//            if(dbRepeatInterval.equals("month")){
//
//                MainActivity.db.updateRepeat(MainActivity.sortedIDs
//                        .get(position), true);
//
//                MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);
//
//            }else {
//
//                MainActivity.alarmManager.cancel(MainActivity.pendIntent);
//
//                Calendar calendar = Calendar.getInstance();
//
//                //setting alarm
//                calendar.set(Calendar.YEAR, datePicker.getYear());
//                calendar.set(Calendar.MONTH, datePicker.getMonth());
//                calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
//                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
//                calendar.set(Calendar.MINUTE, timePicker.getMinute());
//
//                Calendar currentDate = new GregorianCalendar();
//
//                //Checking that task due date is in the future
//                if (currentDate.get(Calendar.YEAR) > datePicker.getYear()) {
//                    MainActivity.toast.setText(R.string.cannotSetTask);
//                    final Handler handler = new Handler();
//
//                    final Runnable runnable = new Runnable() {
//                        public void run() {
//                            MainActivity.sweep.start();
//                            MainActivity.toast.startAnimation(AnimationUtils.loadAnimation
//                                    (getContext(), R.anim.enter_from_right_fast));
//                            MainActivity.toast.setVisibility(View.VISIBLE);
//                            final Handler handler2 = new Handler();
//                            final Runnable runnable2 = new Runnable(){
//                                public void run(){
//                                    MainActivity.toast.startAnimation
//                                            (AnimationUtils.loadAnimation(getContext(),
//                                                    android.R.anim.fade_out));
//                                    MainActivity.toast.setVisibility(View.GONE);
//                                }
//                            };
//                            handler2.postDelayed(runnable2, 1500);
//                        }
//                    };
//
//                    handler.postDelayed(runnable, 500);
//                } else if (currentDate.get(Calendar.YEAR) == datePicker.getYear()
//                        && currentDate.get(Calendar.MONTH) > datePicker.getMonth()) {
//                    MainActivity.toast.setText(R.string.cannotSetTask);
//                    final Handler handler = new Handler();
//
//                    final Runnable runnable = new Runnable() {
//                        public void run() {
//                            MainActivity.sweep.start();
//                            MainActivity.toast.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.enter_from_right_fast));
//                            MainActivity.toast.setVisibility(View.VISIBLE);
//                            final Handler handler2 = new Handler();
//                            final Runnable runnable2 = new Runnable(){
//                                public void run(){
//                                    MainActivity.toast.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
//                                    MainActivity.toast.setVisibility(View.GONE);
//                                }
//                            };
//                            handler2.postDelayed(runnable2, 1500);
//                        }
//                    };
//
//                    handler.postDelayed(runnable, 500);
//                } else if (currentDate.get(Calendar.YEAR) == datePicker.getYear()
//                        && currentDate.get(Calendar.MONTH) == datePicker.getMonth()
//                        && currentDate.get(Calendar.DAY_OF_MONTH) >
//                        datePicker.getDayOfMonth()) {
//                    MainActivity.toast.setText(R.string.cannotSetTask);
//                    final Handler handler = new Handler();
//
//                    final Runnable runnable = new Runnable() {
//                        public void run() {
//                            MainActivity.sweep.start();
//                            MainActivity.toast.startAnimation(AnimationUtils.loadAnimation
//                                    (getContext(), R.anim.enter_from_right_fast));
//                            MainActivity.toast.setVisibility(View.VISIBLE);
//                            final Handler handler2 = new Handler();
//                            final Runnable runnable2 = new Runnable(){
//                                public void run(){
//                                    MainActivity.toast.startAnimation(AnimationUtils.loadAnimation
//                                            (getContext(), android.R.anim.fade_out));
//                                    MainActivity.toast.setVisibility(View.GONE);
//                                }
//                            };
//                            handler2.postDelayed(runnable2, 1500);
//                        }
//                    };
//
//                    handler.postDelayed(runnable, 500);
//                } else if (currentDate.get(Calendar.YEAR) == datePicker.getYear()
//                        && currentDate.get(Calendar.MONTH) == datePicker.getMonth()
//                        && currentDate.get(Calendar.DAY_OF_MONTH) ==
//                        datePicker.getDayOfMonth()
//                        && currentDate.get(Calendar.HOUR_OF_DAY) >
//                        timePicker.getHour()) {
//                    MainActivity.toast.setText(R.string.cannotSetTask);
//                    final Handler handler = new Handler();
//
//                    final Runnable runnable = new Runnable() {
//                        public void run() {
//                            MainActivity.sweep.start();
//                            MainActivity.toast.startAnimation(AnimationUtils.loadAnimation
//                                    (getContext(), R.anim.enter_from_right_fast));
//                            MainActivity.toast.setVisibility(View.VISIBLE);
//                            final Handler handler2 = new Handler();
//                            final Runnable runnable2 = new Runnable(){
//                                public void run(){
//                                    MainActivity.toast.startAnimation(AnimationUtils.loadAnimation
//                                            (getContext(), android.R.anim.fade_out));
//                                    MainActivity.toast.setVisibility(View.GONE);
//                                }
//                            };
//                            handler2.postDelayed(runnable2, 1500);
//                        }
//                    };
//
//                    handler.postDelayed(runnable, 500);
//                } else if (currentDate.get(Calendar.YEAR) == datePicker.getYear()
//                        && currentDate.get(Calendar.MONTH) == datePicker.getMonth()
//                        && currentDate.get(Calendar.DAY_OF_MONTH) ==
//                        datePicker.getDayOfMonth()
//                        && currentDate.get(Calendar.HOUR_OF_DAY) ==
//                        timePicker.getHour()
//                        && currentDate.get(Calendar.MINUTE) > timePicker.getMinute()) {
//                    MainActivity.toast.setText(R.string.cannotSetTask);
//                    final Handler handler = new Handler();
//
//                    final Runnable runnable = new Runnable() {
//                        public void run() {
//                            MainActivity.sweep.start();
//                            MainActivity.toast.startAnimation(AnimationUtils.loadAnimation
//                                    (getContext(), R.anim.enter_from_right_fast));
//                            MainActivity.toast.setVisibility(View.VISIBLE);
//                            final Handler handler2 = new Handler();
//                            final Runnable runnable2 = new Runnable(){
//                                public void run(){
//                                    MainActivity.toast.startAnimation(AnimationUtils.loadAnimation
//                                            (getContext(), android.R.anim.fade_out));
//                                    MainActivity.toast.setVisibility(View.GONE);
//                                }
//                            };
//                            handler2.postDelayed(runnable2, 1500);
//                        }
//                    };
//
//                    handler.postDelayed(runnable, 500);
//                } else {
//
//                    Calendar futureDate = new GregorianCalendar(datePicker.getYear(),
//                            datePicker.getMonth(), datePicker.getDayOfMonth(),
//                            timePicker.getHour(), timePicker.getMinute());
//
//                    //updating timestamp
//                    MainActivity.db.updateTimestamp(String.valueOf(
//                            MainActivity.sortedIDs.get(position)),
//                            String.valueOf(futureDate.getTimeInMillis() / 1000));
//
//                    //intention to execute AlertReceiver
//                    MainActivity.alertIntent = new Intent(getContext(), AlertReceiver.class);
//
//                    //updating due time in database
//                    MainActivity.db.updateAlarmData(String.valueOf(
//                            MainActivity.sortedIDs.get(position)),
//                            String.valueOf(calendar.get(calendar.HOUR)),
//                            String.valueOf(calendar.get(calendar.MINUTE)),
//                            String.valueOf(calendar.get(calendar.AM_PM)),
//                            String.valueOf(calendar.get(calendar.DAY_OF_MONTH)),
//                            String.valueOf(calendar.get(calendar.MONTH)),
//                            String.valueOf(calendar.get(calendar.YEAR)));
//
//                    //setting the name of the task for which the notification is being set
//                    MainActivity.alertIntent.putExtra("ToDo", dbTask);
//
//                    MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(), dbBroadcast,
//                            MainActivity.alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                    if (!dbSnooze) {
//                        MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
//                                Integer.parseInt(MainActivity.sortedIDs
//                                        .get(position)), MainActivity.alertIntent,
//                                PendingIntent.FLAG_UPDATE_CURRENT);
//                    } else {
//                        MainActivity.pendIntent = PendingIntent.getBroadcast(getContext(),
//                                Integer.parseInt(
//                                        MainActivity.sortedIDs.get
//                                                (position) + 1000),
//                                MainActivity.alertIntent,
//                                PendingIntent.FLAG_UPDATE_CURRENT);
//                    }
//
//                    MainActivity.alarmManager.cancel(MainActivity.pendIntent);
//
//                    MainActivity.alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(),
//                            MainActivity.pendIntent);
//
//                    MainActivity.db.updateDue(
//                            MainActivity.sortedIDs.get(position), true);
//
//                    MainActivity.db.updateShowOnce(
//                            MainActivity.sortedIDs.get(position), true);
//
//                }
//
//                dateRow.startAnimation(AnimationUtils.loadAnimation(getContext(),
//                        R.anim.exit_out_right));
//
//                final Handler handler = new Handler();
//
//                final Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
//
//                datePicker.setVisibility(View.VISIBLE);
//
//                timePicker.setVisibility(View.GONE);
//
//                MainActivity.dateOrTime = false;
//
//                //Marks properties as not showing
//                MainActivity.taskPropertiesShowing = false;
//
//                //Returns the 'add' button
//                MainActivity.params.height = MainActivity.addHeight;
//                MainActivity.iconParams.height = MainActivity.addIconHeight;
//
//                MainActivity.add.setLayoutParams(MainActivity.params);
//                MainActivity.addIcon.setLayoutParams(MainActivity.iconParams);
//
//                MainActivity.dateRowShowing = false;
//
//                MainActivity.repeating = false;
//
//                MainActivity.timePickerShowing = false;
//
//                reorderList();
//
//                  //TODO make animation work
////                MainActivity.alarmAnimation = true;
////                MainActivity.animateID = Integer.parseInt(MainActivity.sortedIDs.get(position));
////                MainActivity.animatePosition = position;
//
////                final Handler handler = new Handler();
////
////                final Runnable r = new Runnable() {
////                    public void run() {
////                        notifyDataSetChanged();
////                        Log.i(TAG, "I'm in here");
////                    }
////                };
//
////                handler.postDelayed(r, 6000);
////                notifyDataSetChanged();
//                    }
//                };
//                handler.postDelayed(runnable, 600);
//            }
//
//        }


        String dbTask = "";
        Integer dbBroadcast = 0;
        Boolean dbSnooze = false;
        String dbRepeatInterval = "";
        Cursor dbResult = MainActivity.db.getData(Integer.parseInt(
                MainActivity.sortedIDs.get(position)));
        while (dbResult.moveToNext()) {
            dbTask = dbResult.getString(4);
            dbBroadcast = dbResult.getInt(7);
            dbSnooze = dbResult.getInt(10) > 0;
            dbRepeatInterval = dbResult.getString(13);
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

        if(MainActivity.repeating){

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

            MainActivity.db.updateRepeat(MainActivity.sortedIDs
                    .get(position), true);

            MainActivity.repeatShowing = false;
            MainActivity.repeating = false;

        //actions to occur when setting a normal alarm
        }else{

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

            //actions specific to monthly repeating task
            if(dbRepeatInterval.equals("month")){

                MainActivity.db.updateRepeat(MainActivity.sortedIDs
                        .get(position), true);

                MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

            }else {

                MainActivity.alarmManager.cancel(MainActivity.pendIntent);

                Calendar calendar = Calendar.getInstance();

                Calendar currentDate = new GregorianCalendar();

                //Checking that task due date is in the future
                if (currentDate.get(Calendar.YEAR) > year) {
                    MainActivity.toast.setText(R.string.cannotSetTask);
                    final Handler handler = new Handler();

                    final Runnable runnable = new Runnable() {
                        public void run() {
                            MainActivity.sweep.start();
                            MainActivity.toast.startAnimation(AnimationUtils.loadAnimation
                                    (getContext(), R.anim.enter_from_right_fast));
                            MainActivity.toast.setVisibility(View.VISIBLE);
                            final Handler handler2 = new Handler();
                            final Runnable runnable2 = new Runnable(){
                                public void run(){
                                    MainActivity.toast.startAnimation
                                            (AnimationUtils.loadAnimation(getContext(),
                                                    android.R.anim.fade_out));
                                    MainActivity.toast.setVisibility(View.GONE);
                                }
                            };
                            handler2.postDelayed(runnable2, 1500);
                        }
                    };

                    handler.postDelayed(runnable, 500);
                } else if (currentDate.get(Calendar.YEAR) == year
                        && currentDate.get(Calendar.MONTH) > month) {
                    MainActivity.toast.setText(R.string.cannotSetTask);
                    final Handler handler = new Handler();

                    final Runnable runnable = new Runnable() {
                        public void run() {
                            MainActivity.sweep.start();
                            MainActivity.toast.startAnimation(AnimationUtils
                                    .loadAnimation(getContext(), R.anim.enter_from_right_fast));
                            MainActivity.toast.setVisibility(View.VISIBLE);
                            final Handler handler2 = new Handler();
                            final Runnable runnable2 = new Runnable(){
                                public void run(){
                                    MainActivity.toast.startAnimation(AnimationUtils
                                            .loadAnimation(getContext(), android.R.anim.fade_out));
                                    MainActivity.toast.setVisibility(View.GONE);
                                }
                            };
                            handler2.postDelayed(runnable2, 1500);
                        }
                    };

                    handler.postDelayed(runnable, 500);
                } else if (currentDate.get(Calendar.YEAR) == year
                        && currentDate.get(Calendar.MONTH) == month
                        && currentDate.get(Calendar.DAY_OF_MONTH) >
                        day) {
                    MainActivity.toast.setText(R.string.cannotSetTask);
                    final Handler handler = new Handler();

                    final Runnable runnable = new Runnable() {
                        public void run() {
                            MainActivity.sweep.start();
                            MainActivity.toast.startAnimation(AnimationUtils.loadAnimation
                                    (getContext(), R.anim.enter_from_right_fast));
                            MainActivity.toast.setVisibility(View.VISIBLE);
                            final Handler handler2 = new Handler();
                            final Runnable runnable2 = new Runnable(){
                                public void run(){
                                    MainActivity.toast.startAnimation(AnimationUtils.loadAnimation
                                            (getContext(), android.R.anim.fade_out));
                                    MainActivity.toast.setVisibility(View.GONE);
                                }
                            };
                            handler2.postDelayed(runnable2, 1500);
                        }
                    };

                    handler.postDelayed(runnable, 500);
                } else if (currentDate.get(Calendar.YEAR) == year
                        && currentDate.get(Calendar.MONTH) == month
                        && currentDate.get(Calendar.DAY_OF_MONTH) ==
                        day
                        && currentDate.get(Calendar.HOUR_OF_DAY) >
                        hour) {
                    MainActivity.toast.setText(R.string.cannotSetTask);
                    final Handler handler = new Handler();

                    final Runnable runnable = new Runnable() {
                        public void run() {
                            MainActivity.sweep.start();
                            MainActivity.toast.startAnimation(AnimationUtils.loadAnimation
                                    (getContext(), R.anim.enter_from_right_fast));
                            MainActivity.toast.setVisibility(View.VISIBLE);
                            final Handler handler2 = new Handler();
                            final Runnable runnable2 = new Runnable(){
                                public void run(){
                                    MainActivity.toast.startAnimation(AnimationUtils.loadAnimation
                                            (getContext(), android.R.anim.fade_out));
                                    MainActivity.toast.setVisibility(View.GONE);
                                }
                            };
                            handler2.postDelayed(runnable2, 1500);
                        }
                    };

                    handler.postDelayed(runnable, 500);
                } else if (currentDate.get(Calendar.YEAR) == year
                        && currentDate.get(Calendar.MONTH) == month
                        && currentDate.get(Calendar.DAY_OF_MONTH) ==
                        day
                        && currentDate.get(Calendar.HOUR_OF_DAY) ==
                        hour
                        && currentDate.get(Calendar.MINUTE) > minute) {
                    MainActivity.toast.setText(R.string.cannotSetTask);
                    final Handler handler = new Handler();

                    final Runnable runnable = new Runnable() {
                        public void run() {
                            MainActivity.sweep.start();
                            MainActivity.toast.startAnimation(AnimationUtils.loadAnimation
                                    (getContext(), R.anim.enter_from_right_fast));
                            MainActivity.toast.setVisibility(View.VISIBLE);
                            final Handler handler2 = new Handler();
                            final Runnable runnable2 = new Runnable(){
                                public void run(){
                                    MainActivity.toast.startAnimation(AnimationUtils.loadAnimation
                                            (getContext(), android.R.anim.fade_out));
                                    MainActivity.toast.setVisibility(View.GONE);
                                }
                            };
                            handler2.postDelayed(runnable2, 1500);
                        }
                    };

                    handler.postDelayed(runnable, 500);
                } else {

                    Calendar futureDate = new GregorianCalendar(year,
                            month, day,
                            hour, minute);

                    //updating timestamp
                    MainActivity.db.updateTimestamp(String.valueOf(
                            MainActivity.sortedIDs.get(position)),
                            String.valueOf(futureDate.getTimeInMillis() / 1000));

                    //intention to execute AlertReceiver
                    MainActivity.alertIntent = new Intent(getContext(), AlertReceiver.class);

                    //updating due time in database
//                    MainActivity.db.updateAlarmData(String.valueOf(
//                            MainActivity.sortedIDs.get(position)),
//                            String.valueOf(calendar.get(calendar.HOUR)),
//                            String.valueOf(calendar.get(calendar.MINUTE)),
//                            String.valueOf(calendar.get(calendar.AM_PM)),
//                            String.valueOf(calendar.get(calendar.DAY_OF_MONTH)),
//                            String.valueOf(calendar.get(calendar.MONTH)),
//                            String.valueOf(calendar.get(calendar.YEAR)));

                    MainActivity.db.updateAlarmData(String.valueOf(
                            MainActivity.sortedIDs.get(position)),
                            String.valueOf(hour),
                            String.valueOf(minute),
                            //TODO get actual ampm
                            String.valueOf(1),
                            String.valueOf(day),
                            String.valueOf(month),
                            String.valueOf(year));

                    //setting the name of the task for which the notification is being set
                    MainActivity.alertIntent.putExtra("ToDo", dbTask);

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

                    MainActivity.alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(),
                            MainActivity.pendIntent);

                    MainActivity.db.updateDue(
                            MainActivity.sortedIDs.get(position), true);

                    MainActivity.db.updateShowOnce(
                            MainActivity.sortedIDs.get(position), true);

                }

                final Handler handler = new Handler();

                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
;

                MainActivity.dateOrTime = false;

                //Marks properties as not showing
                MainActivity.taskPropertiesShowing = false;

                //Returns the 'add' button
                MainActivity.params.height = MainActivity.addHeight;
                MainActivity.iconParams.height = MainActivity.addIconHeight;

                MainActivity.add.setLayoutParams(MainActivity.params);
                MainActivity.addIcon.setLayoutParams(MainActivity.iconParams);

                MainActivity.dateRowShowing = false;

                MainActivity.repeating = false;

                MainActivity.timePickerShowing = false;

                reorderList();

                  //TODO make animation work
//                MainActivity.alarmAnimation = true;
//                MainActivity.animateID = Integer.parseInt(MainActivity.sortedIDs.get(position));
//                MainActivity.animatePosition = position;

//                final Handler handler = new Handler();
//
//                final Runnable r = new Runnable() {
//                    public void run() {
//                        notifyDataSetChanged();
//                        Log.i(TAG, "I'm in here");
//                    }
//                };

//                handler.postDelayed(r, 6000);
//                notifyDataSetChanged();
                    }
                };
                handler.postDelayed(runnable, 600);
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
            Cursor dbResult = MainActivity.db.getData(Integer.parseInt(
                    MainActivity.sortedIDs.get(i)));
            while (dbResult.moveToNext()) {
                dbTimestamp = dbResult.getString(3);
            }
            dbResult.close();

            tempList.add(Integer.valueOf(dbTimestamp));

        }

        //Ordering list by time task was created
        ArrayList<String> whenTaskCreated = new ArrayList<>();
        for(int i = 0; i < MainActivity.taskListSize; i++){
            String created = "";
            Cursor createdResult = MainActivity.db.getData(Integer.parseInt
                    (MainActivity.sortedIDs.get(i)));
            while (createdResult.moveToNext()) {
                created = createdResult.getString(15);
            }
            createdResult.close();
            whenTaskCreated.add(created);
        }
        Collections.sort(whenTaskCreated);
        Collections.reverse(whenTaskCreated);

        ArrayList<String> tempIdsList = new ArrayList<>();
        ArrayList<String> tempTaskList = new ArrayList<>();
        ArrayList<String> tempKilledIdsList = new ArrayList<>();
        ArrayList<String> tempKilledTaskList = new ArrayList<>();

        //getting tasks which have no due date
        for(int i = 0; i < MainActivity.taskListSize; i++){

            //getting task data
            int dbId = 0;
            String dbTimestamp = "";
            String dbTask = "";
            Boolean dbKilled = false;
            Cursor dbResult = MainActivity.db.getDataByTimestamp(
                    whenTaskCreated.get(i));
            while (dbResult.moveToNext()) {
                dbId = dbResult.getInt(0);
                dbTimestamp = dbResult.getString(3);
                dbTask = dbResult.getString(4);
                dbKilled = dbResult.getInt(6) > 0;
            }
            dbResult.close();

            //Getting tasks with no due time and not killed
            if((Integer.parseInt(dbTimestamp) == 0) && (!dbKilled)){
                tempIdsList.add(String.valueOf(dbId));
                tempTaskList.add(dbTask);
            //Getting tasks with no due time and killed
            }else if((Integer.parseInt(dbTimestamp) == 0) && (dbKilled)){
                tempKilledIdsList.add(String.valueOf(dbId));
                tempKilledTaskList.add(dbTask);
            }

        }

        Collections.sort(tempList);

        //Adding due tasks which aren't killed to middle of task list
        for(int i = 0; i < MainActivity.taskListSize; i++){

            //getting task data
            int dbId = 0;
            String dbTask = "";
            boolean dbKilled = false;
            Cursor dbResult = MainActivity.db.getDataByDueTime(
                    String.valueOf(tempList.get(i)));
            while (dbResult.moveToNext()) {
                dbId = dbResult.getInt(0);
                dbTask = dbResult.getString(4);
                dbKilled = dbResult.getInt(6) > 0;
            }
            dbResult.close();

            if((tempList.get(i) != 0) && !dbKilled){
                tempIdsList.add(String.valueOf(dbId));
                tempTaskList.add(dbTask);
            }

        }

        //Adding killed tasks to end of task list
        for(int i = 0; i < tempKilledIdsList.size(); i++){

            tempTaskList.add(tempKilledTaskList.get(i));
            tempIdsList.add(tempKilledIdsList.get(i));

        }

        //Adding killed tasks with due dates to middle of task list
        for(int i = 0; i < MainActivity.taskListSize; i++){

            //getting task data
            int dbId = 0;
            String dbTask = "";
            boolean dbKilled = false;
            Cursor dbResult = MainActivity.db.getDataByDueTime(
                    String.valueOf(tempList.get(i)));
            while (dbResult.moveToNext()) {
                dbId = dbResult.getInt(0);
                dbTask = dbResult.getString(4);
                dbKilled = dbResult.getInt(6) > 0;
            }
            dbResult.close();

            if((tempList.get(i) != 0) && dbKilled){
                tempIdsList.add(String.valueOf(dbId));
                tempTaskList.add(dbTask);
            }

        }

        for(int i = 0; i < MainActivity.taskListSize; i++){

            MainActivity.db.updateSortedIndex(String.valueOf(i), Integer.parseInt
                    (tempIdsList.get(i)));

        }

        if(MainActivity.killedAnimation) {

            int tempId = 0;
            String tempTask = "";

            for (int i = 0; i < MainActivity.taskListSize; i++) {

                if (Integer.parseInt(tempIdsList.get(i)) == MainActivity.animateID) {
                    tempId = Integer.parseInt(tempIdsList.get(i));
                    tempTask = tempTaskList.get(i);
                    tempIdsList.remove(i);
                    tempTaskList.remove(i);
                    break;
                }

            }

            tempIdsList.add(MainActivity.animatePosition, String.valueOf(tempId));
            tempTaskList.add(MainActivity.animatePosition, tempTask);
        }

        if(MainActivity.reinstateAnimation) {

            int tempId = 0;
            String tempTask = "";

            for (int i = 0; i < MainActivity.taskListSize; i++) {

                if (Integer.parseInt(tempIdsList.get(i)) == MainActivity.animateID) {
                    tempId = Integer.parseInt(tempIdsList.get(i));
                    tempTask = tempTaskList.get(i);
                    tempIdsList.remove(i);
                    tempTaskList.remove(i);
                    break;
                }

            }

            tempIdsList.add(MainActivity.animatePosition, String.valueOf(tempId));
            tempTaskList.add(MainActivity.animatePosition, tempTask);
        }

        if(MainActivity.alarmAnimation) {

            int tempId = 0;
            String tempTask = "";

            for (int i = 0; i < MainActivity.taskListSize; i++) {

                if (Integer.parseInt(tempIdsList.get(i)) == MainActivity.animateID) {
                    tempId = Integer.parseInt(tempIdsList.get(i));
                    tempTask = tempTaskList.get(i);
                    tempIdsList.remove(i);
                    tempTaskList.remove(i);
                    break;
                }

            }

            tempIdsList.add(MainActivity.animatePosition, String.valueOf(tempId));
            tempTaskList.add(MainActivity.animatePosition, tempTask);
        }

        MainActivity.sortedIDs = tempIdsList;
        MainActivity.taskList = tempTaskList;

        //Updating the view with the new order
        MainActivity.theAdapter = new ListAdapter[]{new MyAdapter(
                getContext(), MainActivity.taskList)};
        MainActivity.theListView.setAdapter(MainActivity.theAdapter[0]);

    }

}